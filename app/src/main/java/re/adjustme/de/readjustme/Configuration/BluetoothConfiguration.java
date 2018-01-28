package re.adjustme.de.readjustme.Configuration;

import android.Manifest;

import java.util.UUID;


/**
 * Contains the local bluetooth configuration params
 * <p>
 * Created by Semmel on 10.11.2017.
 */

public class BluetoothConfiguration {

    /*
        The UUID for SPP Service needed to connect to the hc-05/06 device
    */
    public static final UUID BT_DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String[] BT_DEVICE_NAME = {"HC-06", "HC-05"}; // Unser Bluetooth Modul
    public static final long CONNECTION_DELAY = 300;
    /*
     Final BT Message values.
     */
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;

    // Parsing
    public static final String MESSAGE_LINE_SEPERATOR = "\r\n";
    public static final String MESSAGE_SEPARATOR = " ";
    public static final String SENSOR_STATUS_OK = "100";

    // global App permissions
    public static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    // Bluetooth connection can be established as Server or Client
    // false is default
    public static boolean SERVER_MODE = false;
    public static long CONNECTION_TIMEOUT =60000L; // 60000L = 1Min
}
