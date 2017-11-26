package re.adjustme.de.readjustme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;
import re.adjustme.de.readjustme.Persistence.MotionDataPersistor;
import re.adjustme.de.readjustme.Persistence.PersistorFactory;

/**
 * Created by semmel on 25.11.2017.
 */

public class PersistenceService extends Service {
    private static HashMap<Sensor, MotionData> currentRawMotionData; // redundant
    private static HashMap<Sensor, MotionData> calibrationMotionData;
    // Binder given to clients
    private final IBinder mBinder = new PersistenceServiceBinder();
    private HashMap<Sensor, List<MotionData>> sensorData;
    private MotionDataPersistor persistor;

    public PersistenceService() {
        // get Persistor
        sensorData = new HashMap<Sensor, List<MotionData>>();
        calibrationMotionData = new HashMap<>();
        currentRawMotionData = new HashMap<>();
        this.persistor = PersistorFactory.getMotionDataPersistor(PersistenceConfiguration.DEFAULT_PERSISTOR);

        // initiate calibration with (x,y,z)->(0,0,0)
        MotionData calibration = new MotionData();
        calibration.setDuration(0);
        calibration.setX(0);
        calibration.setY(0);
        calibration.setZ(0);

        // load current data foreach Sensor
        for (Sensor s : Sensor.values()) {
            List<MotionData> list = persistor.getMotionDataForSensor(s);
            sensorData.put(s, list);
            calibration.setSensor(s);
            calibrationMotionData.put(s, calibration);
            currentRawMotionData.put(s, calibration);
        }
    }

    // sets the current raw Motion Data as calibrated 0-values and all following
    // motions are calculated related to these
    public void calibrate() {
        for (Sensor s : Sensor.values()) {
            MotionData oldCalibration = calibrationMotionData.get(s);
            MotionData newMd = currentRawMotionData.get(s);
            // Recalculate calibration, as it was already calibrated before
            // x1 = x1 - calibrationX == calibrate(x1) + calibrationX
            if (!oldCalibration.equals(newMd)) {
                newMd.setX(newMd.getX() + oldCalibration.getX());
                newMd.setY(newMd.getY() + oldCalibration.getY());
                newMd.setZ(newMd.getZ() + oldCalibration.getZ());
                Log.i("Info", "Calibrated to" + newMd.toString());
                // set as new calibration
            }
            calibrationMotionData.put(s, newMd);
        }
    }

    // save persist immediately the object
    // and adds to the cached map after
    public void save(MotionData md) {
        currentRawMotionData.put(md.getSensor(), md);
        MotionData calibration = calibrationMotionData.get(md.getSensor());
        Log.i("Info Persistence", "RAW Motion Data: " + md.toString());
        // TODO CALIBRATION
        md.setX(md.getX() - calibration.getX());
        md.setY(md.getY() - calibration.getY());
        md.setZ(md.getZ() - calibration.getZ());
        // SAVE
        persistor.saveMotion(md);
        sensorData.get(md.getSensor()).add(md);
        Log.i("Info Persistence", "Saved calibrated Motion Data: " + md.toString());

    }

    // get the data from cached Map
    public MotionData getLast(Sensor s) {
        return this.sensorData.get(s).get(0);
    }

    // get the data from cached Map
    public List<MotionData> getList(Sensor s) {
        return this.sensorData.get(s);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
}
