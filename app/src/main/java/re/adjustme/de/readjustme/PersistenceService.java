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
import re.adjustme.de.readjustme.Persistance.MotionDataPersistor;
import re.adjustme.de.readjustme.Persistance.PersistorFactory;

/**
 * Created by semmel on 25.11.2017.
 */

public class PersistenceService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new PersistenceServiceBinder();
    private HashMap<Sensor, List<MotionData>> sensorData;
    private MotionDataPersistor persistor;

    public PersistenceService() {
        // get Persistor
        sensorData=new HashMap<Sensor, List<MotionData>>();
        this.persistor = PersistorFactory.getMotionDataPersistor(PersistenceConfiguration.DEFAULT_PERSISTOR);

        // load current data foreach Sensor
        for (Sensor s : Sensor.values()) {
            List<MotionData> list = persistor.getMotionDataForSensor(s);
            sensorData.put(s, list);
        }
    }
    // save persist immediately the object
    // and adds to the cached map after
    public void save(MotionData md) {
        persistor.saveMotion(md);
        sensorData.get(md.getSensor()).add(md);
        Log.i("Info Persistence","Saved Motion Data: "+md.toString() );

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
