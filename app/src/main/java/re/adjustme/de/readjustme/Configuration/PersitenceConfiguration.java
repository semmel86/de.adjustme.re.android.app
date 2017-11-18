package re.adjustme.de.readjustme.Configuration;

import java.io.File;

/**
 * Created by Semmel on 18.11.2017.
 */

public class PersitenceConfiguration {

    private static final File PERSITANCE_DIRECTORY = new File(System.getProperty("user.dir") + "persistance/");
    public static final String CSV_SEPARATOR = " ; ";

    public static File getPersitanceDirectory() {
        // create if missing
        if (!PERSITANCE_DIRECTORY.exists() || !PERSITANCE_DIRECTORY.isDirectory()) {
            PERSITANCE_DIRECTORY.mkdirs();
        }
        return
                PERSITANCE_DIRECTORY;
    }
}
