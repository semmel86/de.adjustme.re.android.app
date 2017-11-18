package re.adjustme.de.readjustme.Persistance.internal.RoomDatabaseAccess;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import re.adjustme.de.readjustme.Entity.MotionData;

/**
 * Created by Semmel on 18.11.2017.
 */
@Database(entities = {MotionData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";

    public abstract MotionDataDAO motionDataDao();


}
