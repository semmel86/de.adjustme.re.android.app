package re.adjustme.de.readjustme.Service;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import re.adjustme.de.readjustme.Configuration.PersistenceType;
import re.adjustme.de.readjustme.Persistance.MotionDataPersistor;
import re.adjustme.de.readjustme.Persistance.PersistorFactory;

/**
 * TODO
 * Created by Semmel on 18.11.2017.
 */

public class BluetoothBackgroundIntent extends IntentService {

    private MotionDataPersistor myPersistor = PersistorFactory.getMotionDataPersistor(PersistenceType.FILE);


    public BluetoothBackgroundIntent(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // start Bluetooth listener
    }


}
