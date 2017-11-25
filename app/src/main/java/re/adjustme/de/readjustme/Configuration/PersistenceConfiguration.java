package re.adjustme.de.readjustme.Configuration;

import java.io.File;

/**
 * Created by Semmel on 18.11.2017.
 */

public class PersistenceConfiguration {

    public static final String CSV_SEPARATOR = ";";
    public static final PersistenceType DEFAULT_PERSISTOR = PersistenceType.FILE;
    private static final String PERSITANCE_DIRECTORY = System.getProperty("user.dir") + "/persistence/";

    public static String getPersitenceDirectory() {
        File file = new File(PERSITANCE_DIRECTORY);
        // create if missing
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return PERSITANCE_DIRECTORY;
    }
}
