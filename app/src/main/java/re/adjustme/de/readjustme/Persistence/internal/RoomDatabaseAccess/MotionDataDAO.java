package re.adjustme.de.readjustme.Persistence.internal.RoomDatabaseAccess;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import re.adjustme.de.readjustme.Configuration.Sensor;
import re.adjustme.de.readjustme.Entity.MotionData;

/**
 * Created by Semmel on 18.11.2017.
 */
@Dao
public interface MotionDataDAO {

    @Query("SELECT * FROM motion_data WHERE sensor=s")
    List<MotionData> getAll(Sensor s);

    @Query("SELECT * FROM motion_data WHERE sensor=s ORDER BY id DESC LIMIT 1")
    MotionData getLast(Sensor s);

    @Insert
    void instert(MotionData motionData);

}
