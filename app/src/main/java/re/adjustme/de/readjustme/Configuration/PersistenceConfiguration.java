package re.adjustme.de.readjustme.Configuration;

import java.io.File;

/**
 * Created by Semmel on 18.11.2017.
 */

public class PersistenceConfiguration {

    public static final String CSV_SEPARATOR = ";";
    public static final PersistenceType DEFAULT_PERSISTOR = PersistenceType.FILE;
    public static final boolean SAVE_LOCAL = true;
    public static final boolean SAVE_BACKEND = true;
    public static final boolean ENABEL_CALIBRATION = false;
    private static String PERSITENCE_DIRECTORY = System.getProperty("user.dir") + "/persistence/";

    public static String getPersitenceDirectory() {
        File file = new File(PERSITENCE_DIRECTORY);
        // create if missing
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return PERSITENCE_DIRECTORY;
    }

    public static void setPersistenceDirectory(File persistenceDirectory) {
        PERSITENCE_DIRECTORY = persistenceDirectory.getAbsolutePath() + "/persistence/";
    }
}
