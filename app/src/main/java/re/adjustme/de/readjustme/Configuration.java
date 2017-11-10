package re.adjustme.de.readjustme;

import java.util.UUID;

/**
 * Contains the local configuration
 *
 * Created by Semmel on 10.11.2017.
 */

public class Configuration {

    /*
        The UUID needed to connect to the hc-05/06 divece
    */
    public static final UUID BT_DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /*
     Final BT Message values.
     */
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;

}
