package re.adjustme.de.readjustme.Persistence.internal.RoomDatabaseAccess;

import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Semmel on 18.11.2017.
 */
//@Database(entities = {MotionData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";

    // public abstract MotionDataDAO motionDataDao();


}
