package re.adjustme.de.readjustme.Configuration;

import android.util.Log;

import java.io.File;
import java.net.URL;

/**
 * Created by Semmel on 18.11.2017.
 */

public class PersistenceConfiguration {

    public static final String CSV_SEPARATOR = ";";
    public static final PersistenceType DEFAULT_PERSISTOR = PersistenceType.FILE;
    public static final boolean SAVE_LOCAL = true;
    public static final boolean SAVE_BACKEND = true;
    public static final boolean ENABEL_CALIBRATION = false;
    private static String PERSISTENCE_DIRECTORY = System.getProperty("user.dir") + "/persistence/";

    public static String getPersistenceDirectory() {
        File file = new File(PERSISTENCE_DIRECTORY);
        // create if missing
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource("PersistenceConfiguration.java");
        if(url!=null) {
            Log.i("PATH", url.getPath());
        }
        return PERSISTENCE_DIRECTORY;
    }

    public static void setPersistenceDirectory(File persistenceDirectory) {
        PERSISTENCE_DIRECTORY = persistenceDirectory.getAbsolutePath() + "/persistence/";
    }
}
