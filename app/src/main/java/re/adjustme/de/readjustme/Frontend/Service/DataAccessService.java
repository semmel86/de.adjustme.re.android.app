package re.adjustme.de.readjustme.Frontend.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
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

import re.adjustme.de.readjustme.Bean.Posture;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Frontend.DashboardDayActivity;
import re.adjustme.de.readjustme.Persistence.Entity.BackendDataEntity;
import re.adjustme.de.readjustme.Persistence.Entity.CalibrationDataEntity;
import re.adjustme.de.readjustme.Persistence.Entity.DashboardDataEntity;
import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Persistence.Entity.MotionDataSetEntity;
import re.adjustme.de.readjustme.Persistence.Entity.UserEntity;
import re.adjustme.de.readjustme.Persistence.GenericPersistenceProvider;
import re.adjustme.de.readjustme.Persistence.internal.ObjectPersistor;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;
import re.adjustme.de.readjustme.Predefined.Sensor;
import re.adjustme.de.readjustme.R;
import re.adjustme.de.readjustme.Util.Calibration;

/**
 * Class providing access to all data needed by different services/Activities.
 * Therefore it is possible to bind this Service, which exists along with the live time of all binding Services.
 * <p>
 * Created by semmel on 25.11.2017.
 */

public class DataAccessService extends Service {
    private static HashMap<Sensor, MotionData> currentRawMotionData;
    private static CalibrationDataEntity calibrationMotionData;
    // Binder given to clients
    private final IBinder mBinder = new PersistenceServiceBinder();
    GenericPersistenceProvider mPersistenceProvider;
    // data existing on runtime
    private boolean receivesLiveData = false;
    private String label = "";
    private boolean isInLabeledPosition = false;
    private boolean isRunning = false;
    private boolean tryStarting = false;
    // persisted entities
    private UserEntity user;
    private MotionDataSetEntity motionDataSet;
    private DashboardDataEntity dashboardData;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mPostureReceiver = new BroadcastReceiver() {
        // simple counter for sended Notification, to avoid thousands of notifications if the position
        // doesn't change after the first notification
        private int sendNotification = 1;

        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("Posture")) {
                if (dashboardData == null) {
                    dashboardData = new DashboardDataEntity();
                }
                String s = intent.getStringExtra("PostureName");
                BodyArea bodyarea = BodyArea.valueOf(intent.getStringExtra("Area"));
                Label label = bodyarea.getLableByDescription(intent.getStringExtra("PostureName"));
                // put on Dashboard stack
                // get last stacked object
                Posture lastLabel = dashboardData.getlast(bodyarea);
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
                        } else {
                            sumDuration = dur;
                        }
                        dashboardData.getSum(bodyarea).put(lastLabel.getLabel(), sumDuration);
                        if (dur >= lastLabel.getArea().getMaxDuration() * sendNotification) {
                            sendNotification(lastLabel.getArea().getAreaName(), lastLabel.getLabel().getDescription(), dur);
                            sendNotification++;
                        }
                    } else {
                        // other label -> new Posture object
                        Log.d("New Posture detected", bodyarea.name() + " - " + label.getDescription());
                        Posture newLabel = new Posture(label, bodyarea);
                        dashboardData.addLabelData(newLabel);
                        save(dashboardData);
                    }
                }
            }
        }

    };


    public DataAccessService() {
        super();
        intiDataAccessService();
    }

    private void save(DashboardDataEntity dashboardData) {
        mPersistenceProvider.save(dashboardData);
    }


    private void intiDataAccessService() {
        mPersistenceProvider = new GenericPersistenceProvider();
        // load Dashboard Object if possible
        dashboardData = (DashboardDataEntity) mPersistenceProvider.load(DashboardDataEntity.class);
        if (dashboardData == null) {
            dashboardData = new DashboardDataEntity();
        }
        user = (UserEntity) mPersistenceProvider.load(UserEntity.class);
        if(user==null){
            user=new UserEntity();
        }
        motionDataSet = new MotionDataSetEntity();
        calibrationMotionData = (CalibrationDataEntity) mPersistenceProvider.load(CalibrationDataEntity.class); // new HashMap<>();
        if (calibrationMotionData == null) {
            // for restets, start with a new entity
            calibrationMotionData = new CalibrationDataEntity();
            calibrationMotionData.setData(new HashMap<Sensor, MotionData>());

            for (Sensor s : Sensor.values()) {
                // initiate calibration with (x,y,z)->(0,0,0)
                MotionData calibration = new MotionData();
                calibration.setDuration(0);
                calibration.setX(0);
                calibration.setY(0);
                calibration.setZ(0);

                if (!calibrationMotionData.getData().containsKey(s)) {
                    calibration.setSensor(s);
                    calibrationMotionData.getData().put(s, calibration);
                }
            }
        }

        currentRawMotionData = new HashMap<>();

        // Register BroadcastReceiver to receive current Motions
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("Posture"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intiDataAccessService();
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

    public boolean getIsRunning() {
        return this.isRunning;
    }

    public void setIsRunning(Boolean running) {
        if (running != this.isRunning) {
            sendAppEventBroadcast(running, this.tryStarting);
        }
        this.isRunning = running;
    }

    public boolean getTryStarting() {
        return this.tryStarting;
    }

    public void setTryStarting(Boolean tryStarting) {
        if (tryStarting != this.tryStarting) {
            sendAppEventBroadcast(this.isRunning, tryStarting);
        }
        this.tryStarting = tryStarting;
    }

    // sets the current raw Motion Data as calibrated 0-values and all following
    // motions are calculated related to these
    public void setCalibrationData() {
        for (Sensor s : Sensor.values()) {
            if (currentRawMotionData.containsKey(s)) {
                MotionData oldCalibration = calibrationMotionData.getData().get(s);
                MotionData newMd = currentRawMotionData.get(s);
                // Recalculate calibration, as it was already calibrated before
                // x1 = x1 - calibrationX == setCalibrationData(x1) + calibrationX
                if (!oldCalibration.equals(newMd)) {
                    oldCalibration.setX(newMd.getX());
                    oldCalibration.setY(newMd.getY());
                    oldCalibration.setZ(newMd.getZ());
                    Log.d("Info", "Calibrated to" + newMd.toString());
                    // set as new calibration
                }
            }
        }
        ObjectPersistor helper = new ObjectPersistor();
        helper.save(calibrationMotionData, "calibrationMotionData");
    }

    private void calibration(MotionData md) {
        MotionData calibration = calibrationMotionData.getData().get(md.getSensor());
        Log.d("Info Persistence", "Before Calibration: " + md.toString());

         // Z
        md.setX(Calibration.calibrate(md.getX(), calibration.getX()));
        md.setY(Calibration.calibrate(md.getY(), calibration.getY()));
        md.setZ(Calibration.calibrate(md.getZ(), calibration.getZ()));
    }

    // save persist immediately the object
    // and adds to the cached map after
    public void save(MotionData md) {
        this.receivesLiveData = true;
        if (PersistenceConfiguration.ENABLE_CALIBRATION) {
            calibration(md);
        }
        labelMotionData(md);
        motionDataSet.update(md); // add md to MotionDataSetEntity where z is adjusted !
        mPersistenceProvider.save(motionDataSet);


        JSONObject motionData = motionDataSet.getJson();
        try {
            motionData.put("user", this.user.getName());
            for (BodyArea b : BodyArea.values()) {
                if (dashboardData.getlast(b) != null) {
                    motionData.put(b.name(), dashboardData.getlast(b).getLabel().getLabel());
                }
            }
        } catch (Exception e) {
            // nothing to do here
        }

        mPersistenceProvider.save(new BackendDataEntity(motionData, this.getApplicationContext()));
        Log.d("Info Persistence", "Saved Motion Data: " + md.toString());

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

            } else {

                // there is a difference, change md to current and persist old current
                save(currentRawMotionData.get(md.getSensor()));
                currentRawMotionData.put(md.getSensor(), md);
            }

        } else {
            Log.d("Info", "Set first entry for this Sensor as current");
            currentRawMotionData.put(md.getSensor(), md);
        }
    }

    public String getUsername() {
        return this.user.getName();
    }

    public void setUsername(String username) {
        user.setName(username);
        mPersistenceProvider.save(user);
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
    public MotionDataSetEntity getMotionDataSet() {
        return motionDataSet;
    }


    // get reference to current DashboardDataEntity object
    public DashboardDataEntity getDashboardData() {
        return this.dashboardData;
    }

    // indicates whether the loose coupled Bluetooth service brings in some data
    public boolean receivesLiveData() {
        return this.receivesLiveData;
    }

    public void unsetReceivesLiveData() {
        this.receivesLiveData = false;
    }

    protected void sendNotification(String area, String posture, long dur) {
        // prepare intent which is triggered if the
        // notification is selected

        Intent dash = new Intent(this, DashboardDayActivity.class);
        Intent parent = new Intent(this, DashboardDayActivity.class);
        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntent(parent)
                        .addNextIntentWithParentStack(dash)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //  PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String s = "Zeit zu handeln.";
        // large notification layout
        Notification.InboxStyle inboxStyle =
                new Notification.InboxStyle();
        inboxStyle.addLine("Du warst jetzt schon über " + Long.toString(dur / 60000L) + " Minuten in");
//        inboxStyle.addLine(" ");
        inboxStyle.addLine("dieser Haltung:     " + area.toString() + " - " + posture.toString());
//        inboxStyle.addLine(" ");
        inboxStyle.addLine("Du solltest dich bewegen oder deine Position");
        inboxStyle.addLine("ändern um Verspannungen vorzubeugen.");


        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Haltungswarnung");

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(this)
                .setContentTitle("re.adjustme")
                .setContentText(s)
                .setSmallIcon(R.drawable.ic_logo_nuricon)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{200L, 200L, 200L, 200L, 200L})
                .setStyle(inboxStyle)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);

    }

    private void sendAppEventBroadcast(boolean running, boolean tryStarting) {
        Intent intent = new Intent("AppEvent");
        intent.putExtra("Running", running ? "true" : "false");
        intent.putExtra("Starting", tryStarting ? "true" : "false");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PersistenceServiceBinder extends Binder {
        DataAccessService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DataAccessService.this;
        }
    }
}
