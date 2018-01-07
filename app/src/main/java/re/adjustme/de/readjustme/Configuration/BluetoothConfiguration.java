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
    // Semmel test & debug Settings
    // public static final String BT_DEVICE_NAME = "P023"; // Semmels Tablet
    // public static boolean SERVER_MODE = true;
    public static final String[] BT_DEVICE_NAME = {"HC-06","HC-05"}; // Unser Bluetooth Modul
    public static final long CONNECTION_DELAY = 500;
    /*
     Final BT Message values.
     */
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final String BLUETOOTH_CONNECTED = "Connected";
    public static final String BLUETOOTH_DISCONNECTED = "Disconnected";
    public static final String MESSAGE_LINE_SEPERATOR = "\r\n";
    public static final String MESSAGE_SEPARATOR = " ";
    public static final String SENSOR_STATUS_OK = "100";
    // global App permissions
    public static final String[] permissionsToRequest =
            {
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
    // switch, for HC-06 server-mode=false is required
    public static boolean SERVER_MODE = false;
}
