package re.adjustme.de.readjustme;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;
import re.adjustme.de.readjustme.Service.BluetoothService;

public class MainActivity extends MyNavigationActivity {
    Button bluttoothButton, b2;
    BluetoothService mBluetoothService;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<String> arrayOfFoundBTDevices = new ArrayList<String>();
    private ListView lv;
    private TextView tv;
    private String testChange;
    private StringBuilder sb;
    private String[] permissionsToRequest =
            {
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                for (BluetoothDevice d : BA.getBondedDevices()) {
                    if (d.getName().equals(device.getName()) && d.getAddress().equals(device.getAddress())) {
                        Toast.makeText(context, "Try Connection " + deviceName, Toast.LENGTH_SHORT).show();
                        connectToDevice(device);
                    }
                }
                arrayOfFoundBTDevices.add(deviceName + " - " + deviceHardwareAddress);
                final ArrayAdapter mAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, arrayOfFoundBTDevices);
                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // info on item
                        Toast.makeText(context, lv.getAdapter().getItem(arg2).toString(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            tv.setText(msg.what);
            switch (msg.what) {
                case BluetoothConfiguration.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);
                    sb.append(strIncom);
                    int endOfLineIndex = sb.indexOf("\r\n");
                    if (endOfLineIndex > 0) {
                        String sbprint = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        tv.setText("Data from Arduino: " + sbprint);

                    }
                    break;
            }
        }

        ;
    };

    // handler that gets info from Bluetooth service


    private void connectToDevice(BluetoothDevice device) {
        // let the Bluetooth service make his work

        this.mBluetoothService = new BluetoothService(device, mHandler, BA);
        mBluetoothService.start();
        // switch to BT pageS


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView2);
        bluttoothButton = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        BA = BluetoothAdapter.getDefaultAdapter();
        checkPermissions();
        lv = (ListView) findViewById(R.id.listView);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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


    // list active paired devices
    public void list2(View v) {
        this.checkBluethoothActive();
        ArrayList list = new ArrayList();
        displayListOfFoundDevices();
        list.addAll(arrayOfFoundBTDevices);
        //Toast.makeText(getApplicationContext(), "Try to find Devices", Toast.LENGTH_SHORT).show();

    }

    public void getArrayOfAlreadyPairedBluetoothDevices(View v) {
        tv.setText("Paired Devices:");
        this.checkBluethoothActive();
        ArrayList<String> arrayOfAlreadyPairedBTDevices = new ArrayList<>();

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0) {
            arrayOfAlreadyPairedBTDevices = new ArrayList<String>();

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                arrayOfAlreadyPairedBTDevices.add(device.getName() + " - " + device.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No paired devices", Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOfAlreadyPairedBTDevices);
        lv.setAdapter(adapter);
    }

    private void displayListOfFoundDevices() {
        tv.setText("Found Devices:");
        // start looking for bluetooth devices C:\Users\Semmel\
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        if (BA.startDiscovery()) {
            Log.i("info", "Start Discovery.");
        } else {
            Log.i("info", "Cannot start Discovery.");
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

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