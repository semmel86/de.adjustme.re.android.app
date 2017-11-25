package re.adjustme.de.readjustme.Persistance.internal;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.Sensor;
import re.adjustme.de.readjustme.Persistance.MotionDataPersistor;
import re.adjustme.de.readjustme.Persistance.internal.RoomDatabaseAccess.AppDatabase;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionDataInternalBasePersistor extends IntentService implements MotionDataPersistor {

    AppDatabase db =
            Room.databaseBuilder(this,
                    AppDatabase.class, "app_db").build();

    public MotionDataInternalBasePersistor(String name) {
        super(name);
    }


    @Override
    public boolean saveMotion(MotionData data) {
        return false;
    }

    @Override
    public List<MotionData> getMotionDataForSensor(Sensor sensor) {
        return null;
    }

    @Override
    public MotionData getLastMotionData(Sensor sensor) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
