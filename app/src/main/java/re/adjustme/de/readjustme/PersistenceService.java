package re.adjustme.de.readjustme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;
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
    private String label="";
    private boolean isInLabeledPosition=false;
    private HashMap<Sensor, List<MotionData>> fullMotionData;

    public PersistenceService() {

        ObjectPersistor helper=new ObjectPersistor();
        helper.load("calibrationMotionData");
        motionDataSet= new MotionDataSetDto();
        calibrationMotionData = (HashMap<Sensor, MotionData>) helper.load("calibrationMotionData"); // new HashMap<>();
       if(calibrationMotionData==null){
           // for restets, start with a new entity
            calibrationMotionData = new HashMap<>();
       }
        currentRawMotionData = new HashMap<>();
        // get Persistor
        this.persistor = PersistorFactory.getMotionDataPersistor(PersistenceConfiguration.DEFAULT_PERSISTOR);

        // initiate calibration with (x,y,z)->(0,0,0)
        MotionData calibration = new MotionData();
        calibration.setDuration(0);
        calibration.setX(0);
        calibration.setY(0);
        calibration.setZ(0);

        fullMotionData=new HashMap<>();
//        // load current data foreach Sensor
        for (Sensor s : Sensor.values()) {
            List<MotionData> list = persistor.getMotionDataForSensor(s);
            if(!calibrationMotionData.containsKey(s)) {
                calibration.setSensor(s);
                calibrationMotionData.put(s, calibration);
            }
            fullMotionData.put(s,list);

       }
    }

    public HashMap<Sensor, List<MotionData>> getMotionData(){
        return  fullMotionData;
    }
    @Override
    public void onDestroy(){
        // save the pending raw data
        for (MotionData m : currentRawMotionData.values()) {
            save(m);
        }
    }

    // sets the current raw Motion Data as calibrated 0-values and all following
    // motions are calculated related to these
    public void calibrate() {
        for (Sensor s : Sensor.values()) {
            if(currentRawMotionData.containsKey(s)){
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
            }}
        }
        ObjectPersistor helper=new ObjectPersistor();
        helper.save(calibrationMotionData,"calibrationMotionData");
    }

    private void doCalibration(MotionData md){
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
        //doCalibration(md);
        // SAVE
        labelMotionData(md);
        persistor.saveMotion(md);
        motionDataSet.update(md);
        persistor.saveMotionSet(motionDataSet);
        Log.i("Info Persistence", "Saved Motion Data: " + md.toString());
    }

    // get the data from cached Map
    public MotionData getLast(Sensor s) {
        return  motionDataSet.getMotion(s);
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
    public void setLabel(String label) {
        this.label = label;
    }

    public void setIsInLabeledPosition(boolean b) {
        this.isInLabeledPosition = b;
    }

    public String getLabel() {
        return this.label ;
    }

    public boolean getIsInLabeledPosition() {
        return this.isInLabeledPosition ;
    }

    private void labelMotionData(MotionData md){
        md.setInLabeledPosition(isInLabeledPosition);
        md.setLabel(label);
    }

    public MotionDataSetDto getMotionDataSet() {
        return motionDataSet;
    }
}
