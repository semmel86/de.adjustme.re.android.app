package re.adjustme.de.readjustme.Configuration;

import java.io.File;

/**
 * Created by Semmel on 18.11.2017.
 */

public class PersistenceConfiguration {

    public final static boolean MODE_DEVELOPMENT = true;

    // DEFINES THE MINIMAL DURATION FOR A POSTURE, ALL SMALLER Postures where dismissed
    public static final long MIN_POSTURE_DURATION = 5000L;
    public static final String CSV_SEPARATOR = ";";

    public static final boolean SAVE_LOCAL = true;//MODE_DEVELOPMENT;
    public static final boolean SAVE_BACKEND = true;
    public static final boolean ENABLE_CALIBRATION = true;
    private static String PERSISTENCE_DIRECTORY = System.getProperty("user.dir") + "/persistence/";

    public static String getPersistenceDirectory() {
        File file = new File(PERSISTENCE_DIRECTORY);
        // create if missing
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return PERSISTENCE_DIRECTORY;
    }

    public static void setPersistenceDirectory(File persistenceDirectory) {
        PERSISTENCE_DIRECTORY = persistenceDirectory.getAbsolutePath() + "/persistence/";
    }
}
