package re.adjustme.de.readjustme;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends MyNavigationActivity {
    Button bluttoothButton, b2;
    private TextView posture;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mPostureReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("Posture")) {
                String s = intent.getStringExtra("PostureName");
                posture.setText(s);
                //Toast.makeText(context,posture, Toast.LENGTH_SHORT).show();
            }
        }

    };
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<String> arrayOfFoundBTDevices = new ArrayList<String>();
    private ListView lv;
    private String[] permissionsToRequest =
            {
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void connectToDevice(BluetoothDevice device) {
        // let the Bluetooth service make his work

//        this.mBluetoothService = new BluetoothService(device, mHandler, BA);
//        mBluetoothService.startConnection();
        // switch to BT pageS


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluttoothButton = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        BA = BluetoothAdapter.getDefaultAdapter();
        checkBluethoothActive();
        checkPermissions();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        posture=(TextView)findViewById(R.id.textView2);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("Posture"));
    }


    private void checkBluethoothActive() {
        // Check and Log BT
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            Log.i("info", "Enabled Bluetooth!");
        }
    }


    // start BT service
    public void startService(View v) {
        try {
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            startService(intent);
            Toast.makeText(getApplicationContext(), "started BT Service", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already started", Toast.LENGTH_LONG).show();
        }
    }

    public void StartEvalService(View v) {
        try {
            Intent intent = new Intent(this, EvaluationBackgroundService.class);
            startService(intent);
            Toast.makeText(getApplicationContext(), "started Evaluation", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already started", Toast.LENGTH_LONG).show();
        }
    }

    public void calculateClassifier(View v) {
        Intent intent = new Intent(this, EvaluationBackgroundService.class);
        startService(intent);
    }

    public void calibrate(View v) {
        mPersistenceService.calibrate();
    }

    public void stopService(View v) {
        try {
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            stopService(intent);
            Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already stopped", Toast.LENGTH_LONG).show();
        }
    }


    public void stopEvalService(View v) {
        try {
            Intent intent = new Intent(this, EvaluationBackgroundService.class);
            stopService(intent);
            Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already stopped", Toast.LENGTH_LONG).show();
        }
    }

    private void displayListOfFoundDevices() {

        // startConnection looking for bluetooth devices C:\Users\Semmel\
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mPostureReceiver, filter);
        if (BA.startDiscovery()) {
            Log.i("info", "Start Discovery.");
        } else {
            Log.i("info", "Cannot startConnection Discovery.");
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPostureReceiver);

    }


    private void checkPermissions() {

        for (String currentPerm : permissionsToRequest) {
// Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    currentPerm)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        currentPerm)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.
                    Log.i("info", "Have to enable Permission: " + currentPerm);
                    ActivityCompat.requestPermissions(this,
                            new String[]{currentPerm},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }


}