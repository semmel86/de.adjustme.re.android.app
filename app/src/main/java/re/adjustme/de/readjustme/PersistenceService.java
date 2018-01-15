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

import re.adjustme.de.readjustme.Bean.DashboardData;
import re.adjustme.de.readjustme.Bean.LabelData;
import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Predefined.Sensor;
import re.adjustme.de.readjustme.Persistence.BackendConnection;
import re.adjustme.de.readjustme.Persistence.MotionDataPersistor;
import re.adjustme.de.readjustme.Persistence.PersistorFactory;
import re.adjustme.de.readjustme.Persistence.internal.ObjectPersistor;

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
    private boolean receivesLiveData=false;
    private BackendConnection backend = new BackendConnection();
    private DashboardData dashboardData;
    private String username;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mPostureReceiver = new BroadcastReceiver() {
        // simple counter for sendet Notification, to avoid thousands of notifications if the position
        // doesn't change after the first notification
        private int sendNotification=1;
        public void onReceive(final Context context, Intent intent) {

            String action = intent.getAction();
            if (dashboardData == null) {
                dashboardData = new DashboardData();
            }
            if (action.equals("Posture")) {
                String s = intent.getStringExtra("PostureName");
                BodyArea bodyarea = BodyArea.valueOf(intent.getStringExtra("Area"));
                Label label = bodyarea.getLable(intent.getStringExtra("PostureName"));
                // put to Dashboard
                LabelData lastLabel = dashboardData.getlast(bodyarea);
                if (label != null) {
                    if (lastLabel != null && lastLabel.getLabel().equals(label)) {
                        // same Label -> accumulate duration
                        Timestamp current = new Timestamp(new Date().getTime());
                        long dur=current.getTime() - lastLabel.getBegin().getTime();
                        lastLabel.setDuration(dur);
                        if(dur>=lastLabel.getArea().getMaxDuration()*sendNotification){
                            sendNotification(lastLabel.getArea().name());
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

    private void intiPersistenceService() {
        ObjectPersistor helper = new ObjectPersistor();
        // helper.load("calibrationMotionData");
        // load Dashboard Object if possible
        dashboardData = (DashboardData) helper.load("DashboardData");
        if (dashboardData == null) {
            dashboardData = new DashboardData();
        }

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
        save(dashboardData);
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
        this.receivesLiveData=true;
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
            for (BodyArea b : BodyArea.values()) {
                try {
                    motionData.put(b.name(), dashboardData.getlast(b).getLabel().getDescription());
                } catch (Exception e) {
                    //Log.e("Error Persistence", "Error on parsing Json");
                }
            }
            //Log.i("SEND-JSON",motionData.toString());
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
    public boolean receivesLiveData(){
        return this.receivesLiveData;
    }

    public void  unsetReceivesLiveData(){
        this.receivesLiveData=false;
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

    protected void sendNotification(String text){
        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(this, NotifyServiceReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle("Re.adjustme - Haltungs notification "+text)
                .setContentText("Ändere deinen Haltung....bitte.")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
               // .addAction(R.drawable.icon, "Call", pIntent)
               // .addAction(R.drawable.icon, "More", pIntent)
               // .addAction(R.drawable.icon, "And more", pIntent).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    public class NotifyServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == 1){
                stopSelf();
            }
        }
    }
}
