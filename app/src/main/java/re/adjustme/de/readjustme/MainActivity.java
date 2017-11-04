package re.adjustme.de.readjustme;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


import static android.Manifest.*;

public class MainActivity extends AppCompatActivity {
    Button bluttoothButton, b2;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<String> arrayOfFoundBTDevices = new ArrayList<String>();
    private ArrayAdapter mAdapter = null;
    private ListView lv;
    private TextView tv;

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

    private void connectToDevice(BluetoothDevice device) {
        // init connection and listen
        AcceptThread a = new AcceptThread(device.getName(), UUID.randomUUID());
        a.start();
    }

    // Create Navigation Menu Listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    Intent intent = new Intent(getApplicationContext(), DashboardDayActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };


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

    public void manageMyConnectedSocket(BluetoothSocket socket) {
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        ConnectedThread conn = new ConnectedThread(socket);


    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(String name, UUID uuid) {
            Log.i("info", "Init AcceptThread");
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = BA.listenUsingRfcommWithServiceRecord(name, uuid);
            } catch (IOException e) {
                Log.i("info", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("info", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    try {
                        manageMyConnectedSocket(socket);
                        mmServerSocket.close();
                        Log.i("info", "Close Thread after connection.");
                        break;
                    } catch (IOException e) {
                        Log.e("info", " Failure on close", e);
                        break;
                    }
                }
                Log.i("info", " Socket is unexpectedly NULL");
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("info", "Could not close the connect socket", e);
            }
        }
    }

    private Handler mHandler; // handler that gets info from Bluetooth service

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            Log.i("info", "Init ConnectThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("info", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("info", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    Log.i("info", "Read Message " + readMsg.toString());
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d("info", "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e("info", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("info", "Could not close the connect socket", e);
            }
        }
    }
}