package re.adjustme.de.readjustme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import re.adjustme.de.readjustme.Bean.DashboardData;
import re.adjustme.de.readjustme.Bean.LabelData;
import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Persistence.BackendConnection;
import re.adjustme.de.readjustme.Persistence.MotionDataPersistor;
import re.adjustme.de.readjustme.Persistence.PersistorFactory;
import re.adjustme.de.readjustme.Persistence.internal.ObjectPersistor;
import re.adjustme.de.readjustme.Persistence.internal.TextFilePersistor;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Created by semmel on 25.11.2017.
 */

public class PersistenceService extends Service {
    // calibration values
    private static HashMap<Sensor, MotionData> currentRawMotionData;
    private static HashMap<Sensor, MotionData> calibrationMotionData;
    // Binder given to clients
    private final IBinder mBinder = new PersistenceServiceBinder();
    // current data
    private MotionDataSetDto motionDataSet;
    private MotionDataPersistor persistor;
    private String label = "";
    private boolean isInLabeledPosition = false;
    private HashMap<Sensor, List<MotionData>> fullMotionData;
    private boolean receivesLiveData = false;
    private BackendConnection backend = new BackendConnection();
    private DashboardData dashboardData;
    private String username;
    private boolean isRunning = false;
    private boolean tryStarting = false;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mPostureReceiver = new BroadcastReceiver() {
        // simple counter for sendet Notification, to avoid thousands of notifications if the position
        // doesn't change after the first notification
        private int sendNotification = 1;

        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("Posture")) {
                if (dashboardData == null) {
                    dashboardData = new DashboardData();
                }
                String s = intent.getStringExtra("PostureName");
                BodyArea bodyarea = BodyArea.valueOf(intent.getStringExtra("Area"));
                Label label = bodyarea.getLableByDescription(intent.getStringExtra("PostureName"));
                // put to Dashboard
                LabelData lastLabel = dashboardData.getlast(bodyarea);
                if (label != null) {
                    if (lastLabel != null && lastLabel.getLabel().equals(label)) {
                        // same Label -> accumulate duration
                        Timestamp current = new Timestamp(new Date().getTime());
                        HashMap<Label, Long> dashboardDataSum = dashboardData.getSum(bodyarea);
                        Long sumDuration = 0L;
                        if (dashboardDataSum != null) {
                             sumDuration = dashboardDataSum.get(lastLabel.getLabel());
                             sumDuration = sumDuration == null ? 0L : sumDuration;
                        }
                        if (lastLabel.getDuration().compareTo(0L) > 0) {
                            sumDuration = sumDuration - lastLabel.getDuration();
                        }
                        Long dur = current.getTime() - lastLabel.getBegin().getTime();
                        lastLabel.setDuration(dur);
                        if (sumDuration.compareTo(0L) > 0) {
                            sumDuration = sumDuration + dur;
                        } else{
                            sumDuration = dur;
                        }
                        dashboardData.getSum(bodyarea).put(lastLabel.getLabel(), sumDuration);
                        if (dur >= lastLabel.getArea().getMaxDuration() * sendNotification) {
                            sendNotification(lastLabel.getArea().getAreaName(),lastLabel.getLabel().getDescription(),dur);
                            sendNotification++;
                        }
                    } else {
                        // other label -> new LabelData object
                        Log.i("New Posture detected", bodyarea.name() + " - " + label.getDescription());
                        LabelData newLabel = new LabelData(label, bodyarea);
                        dashboardData.addLabelData(newLabel);
                        save(dashboardData);
                    }
                }
            }
        }

    };


    public PersistenceService() {
        super();
        intiPersistenceService();
    }

    private void save(DashboardData dashboardData) {
        ObjectPersistor helper = new ObjectPersistor();
        helper.save(dashboardData, "DashboardData");
    }

    private void saveUserName(String username){
        ObjectPersistor helper = new ObjectPersistor();
        helper.save(username, "User");
    }

    private String loadUserName(){
        ObjectPersistor helper = new ObjectPersistor();
        return (String) helper.load("User");
    }

    private void intiPersistenceService() {
        ObjectPersistor helper = new ObjectPersistor();
        // helper.load("calibrationMotionData");
        // load Dashboard Object if possible
        dashboardData = (DashboardData) helper.load("DashboardData");
        if (dashboardData == null) {
            dashboardData = new DashboardData();
            helper.save(dashboardData, "DashboardData");
        }
        this.username =(String) helper.load("User");
        motionDataSet = new MotionDataSetDto();
        calibrationMotionData = (HashMap<Sensor, MotionData>) helper.load("calibrationMotionData"); // new HashMap<>();
        if (calibrationMotionData == null) {
            // for restets, start with a new entity
            calibrationMotionData = new HashMap<>();
        }
        currentRawMotionData = new HashMap<>();
        // get Persistor
        this.persistor = PersistorFactory.getMotionDataPersistor(PersistenceConfiguration.DEFAULT_PERSISTOR);


        fullMotionData = new HashMap<>();
//        // load current data foreach Sensor
        for (Sensor s : Sensor.values()) {
            // initiate calibration with (x,y,z)->(0,0,0)
            MotionData calibration = new MotionData();
            calibration.setDuration(0);
            calibration.setX(0);
            calibration.setY(0);
            calibration.setZ(0);

            List<MotionData> list = persistor.getMotionDataForSensor(s);
            if (!calibrationMotionData.containsKey(s)) {
                calibration.setSensor(s);
                calibrationMotionData.put(s, calibration);
            }
            fullMotionData.put(s, list);

        }
        // Register BroadcastReceiver to receive current Motions
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("Posture"));
    }

    public HashMap<Sensor, List<MotionData>> getMotionData() {
        return fullMotionData;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intiPersistenceService();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // save the pending raw data
        for (MotionData m : currentRawMotionData.values()) {
            save(m);
        }
        setIsRunning(false);
        setTryStarting(false);
        save(dashboardData);
    }

    public void setIsRunning(Boolean running) {
        if (running != this.isRunning) {
            sendAppEventBroadcast(running, this.tryStarting);
        }
        this.isRunning = running;
    }

    public boolean getIsRunning() {
        return this.isRunning;
    }

    public void setTryStarting(Boolean tryStarting) {
        if (tryStarting != this.tryStarting) {
            sendAppEventBroadcast(this.isRunning, tryStarting);
        }
        this.tryStarting = tryStarting;
    }

    public boolean getTryStarting() {
        return this.tryStarting;
    }

    // sets the current raw Motion Data as calibrated 0-values and all following
    // motions are calculated related to these
    public void calibrate() {
        for (Sensor s : Sensor.values()) {
            if (currentRawMotionData.containsKey(s)) {
                MotionData oldCalibration = calibrationMotionData.get(s);
                MotionData newMd = currentRawMotionData.get(s);
                // Recalculate calibration, as it was already calibrated before
                // x1 = x1 - calibrationX == calibrate(x1) + calibrationX
                if (!oldCalibration.equals(newMd)) {
                    oldCalibration.setX(newMd.getX());// + oldCalibration.getX());
                    oldCalibration.setY(newMd.getY());// + oldCalibration.getY());
                    oldCalibration.setZ(newMd.getZ());// + oldCalibration.getZ());
                    Log.i("Info", "Calibrated to" + newMd.toString());
                    // set as new calibration
                }
            }
        }
        ObjectPersistor helper = new ObjectPersistor();
        helper.save(calibrationMotionData, "calibrationMotionData");
    }

    private void doCalibration(MotionData md) {
        MotionData calibration = calibrationMotionData.get(md.getSensor());
        Log.i("Info Persistence", "Before Calibration: " + md.toString());
        // CALIBRATION
        md.setX(md.getX() - calibration.getX());
        md.setY(md.getY() - calibration.getY());
        md.setZ(md.getZ() - calibration.getZ());
    }

    // save persist immediately the object
    // and adds to the cached map after
    public void save(MotionData md) {
        this.receivesLiveData = true;
        if (PersistenceConfiguration.ENABLE_CALIBRATION) {
            doCalibration(md);
        }
        // SAVE
        if (PersistenceConfiguration.SAVE_LOCAL) {
            labelMotionData(md);
            persistor.saveMotion(md);
            motionDataSet.update(md);
            persistor.saveMotionSet(motionDataSet);
            Log.i("Info Persistence", "Saved Motion Data: " + md.toString());
        }
        if (PersistenceConfiguration.SAVE_BACKEND) {
            JSONObject motionData = motionDataSet.getJson();
            try {
                motionData.put("user", this.username.toString());
                for (BodyArea b : BodyArea.values()) {
                    if(dashboardData.getlast(b)!=null) {
                        motionData.put(b.name(), dashboardData.getlast(b).getLabel().getLabel());
                    }
                }
            } catch (Exception e) {
                //Log.e("Error Persistence", "Error on parsing Json");
            }
            Log.i("SEND-JSON",motionData.toString());
            backend.sendRequest(motionData, this.getApplicationContext());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void processNewMotionData(MotionData md) {
        // compare to current MotionData of the same sensor
        if (currentRawMotionData.containsKey(md.getSensor())) {
            if (md.compareTo(currentRawMotionData.get(md.getSensor())) == 0) {
                // no change, we only count up the duration
                long duration = currentRawMotionData.get(md.getSensor()).getDuration();
                duration = md.getBegin().getTime() - currentRawMotionData.get(md.getSensor()).getBegin().getTime();
                currentRawMotionData.get(md.getSensor()).setDuration(duration);
                // Log.i("Info", "Enlarged duration to: " + duration + " it's " + duration / 1000 + " sek");
            } else {
                // there is a difference, change md to current and persist old current
                save(currentRawMotionData.get(md.getSensor()));
                currentRawMotionData.put(md.getSensor(), md);
            }

        } else {
            Log.i("Info", "Set first entry for this Sensor as current");
            currentRawMotionData.put(md.getSensor(), md);
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
        saveUserName(username);
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean getIsInLabeledPosition() {
        return this.isInLabeledPosition;
    }

    public void setIsInLabeledPosition(boolean b) {
        this.isInLabeledPosition = b;
    }

    private void labelMotionData(MotionData md) {
        md.setInLabeledPosition(isInLabeledPosition);
        md.setLabel(label);
    }

    // get reference to current MotionDataSet object
    public MotionDataSetDto getMotionDataSet() {
        return motionDataSet;
    }

    // get the data from cached Map
    public MotionData getLast(Sensor s) {
        return motionDataSet.getMotion(s);
    }

    // get reference to current DashboardData object
    public DashboardData getDashboardData() {
        return this.dashboardData;
    }

    // indicates whether the loose coupled Bluetooth service brings in some data
    public boolean receivesLiveData() {
        return this.receivesLiveData;
    }

    public void unsetReceivesLiveData() {
        this.receivesLiveData = false;
    }

    protected void sendNotification(String area,String posture,long dur) {
        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(this, NotifyServiceReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String s = "Wir haben dich in den letzen "+Long.toString(dur/60000L)+" Minuten sehr lange in " +
                area.toString()+" - "+posture.toString()+" gesehen. Du solltest dich bewegen oder deine Position ändern um Verspannungen vorzubeugen.";
//        s = String.format(s, ,area.toString(),posture.toString());
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(this)
                .setContentTitle("re.adjustme")

                // Du bist in der letzten zeit sehr lange (time)%d in folgender Haltung gewesen:
                //  @ Area%s - Haltung%s
                // Es wird zeit dich zu bewegen
                .setContentText(s)
                .setSmallIcon(R.drawable.ic_logo_nuricon)
                .setContentIntent(pIntent)
                .setVibrate(new long[]{200L, 200L, 200L, 200L, 200L})
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PersistenceServiceBinder extends Binder {
        PersistenceService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PersistenceService.this;
        }
    }

    public class NotifyServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == 1) {
                stopSelf();
            }
        }
    }

    private void sendAppEventBroadcast(boolean running, boolean tryStarting) {
        Intent intent = new Intent("AppEvent");
        intent.putExtra("Running", running ? "true" : "false");
        intent.putExtra("Starting", tryStarting? "true" : "false");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
