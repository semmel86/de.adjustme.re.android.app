package re.adjustme.de.readjustme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;
import re.adjustme.de.readjustme.Service.BluetoothService;

/**
 * Created by Semmel on 12.11.2017.
 */

public class BluetoothActivity extends MyNavigationActivity {
    private TextView tvDevice;
    private TextView tvStatus;
    private TextView tvSensor;
    private TextView tvX;
    private TextView tvY;
    private TextView tvZ;
    private TextView outStream;
    private StringBuilder mReceivedData;
    private BroadcastReceiver mReceiver;
    private BluetoothService mBluetoothService;
    private BluetoothAdapter mBluetoothAdapter;
    private static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set fields
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_data);
        tvDevice = (TextView) findViewById(R.id.tvDevice);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvSensor = (TextView) findViewById(R.id.tvSensor);
        tvX = (TextView) findViewById(R.id.tvSensor);
        tvY = (TextView) findViewById(R.id.tvSensor);
        tvZ = (TextView) findViewById(R.id.tvSensor);
        outStream = (TextView) findViewById(R.id.textView);
        mReceivedData= new StringBuilder();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        tvDevice.setText("none");
        tvSensor.setText("none");
        tvStatus.setText("none");
        tvX.setText("x");
        tvY.setText("y");
        tvZ.setText("z");


        // check permissions
        checkPermissions();

        // set up specific classes
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.setHandler();

        checkBluethoothActive();

        connectToBondedDevice();


    }

    private void connectToBondedDevice() {

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = this.mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0) {

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (!(device.getName() == null) && device.getName().equals(BluetoothConfiguration.BT_DEVICE_NAME)) {
                    Toast.makeText(this.getApplicationContext(), "Try Connection to" + device.getName(), Toast.LENGTH_SHORT).show();
                    tvDevice.setText(device.getName());
                    connectToDevice(device);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "No paired devices", Toast.LENGTH_SHORT).show();
        }


    }

    // start discovery Mode
    private void discoverDevice() {
        // start looking for bluetooth devices C:\Users\Semmel\
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        if (mBluetoothAdapter.startDiscovery()) {
            Log.i("info", "Start Discovery.");
        } else {
            Log.i("info", "Cannot start Discovery.");
        }
    }

    private void connectToDevice(BluetoothDevice device) {
        // let the Bluetooth service make his work
        Log.i("info", "Try to Connect to: " + device.getAddress());
        System.out.println("Start BluetoothService");
        this.mBluetoothService = new BluetoothService(device, mHandler, mBluetoothAdapter);
        mBluetoothService.start();
    }


    // Create the specific Handler to handle BT data received by BT service
    private void setHandler() {
        this.mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case BluetoothConfiguration.MESSAGE_READ:

                        byte[] bytes = (byte[]) msg.obj;
                        StringBuilder s = new StringBuilder();

                        for (Byte b : bytes) {
                            if (b < 1 || b == null) {
                                break;
                            }
                            s.append((char) b.intValue());
                        }

                        // aggregate BT-Snippets
                        if(s.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR)>0) {
                            mReceivedData.append(s.substring(0, s.lastIndexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR)+BluetoothConfiguration.MESSAGE_LINE_SEPERATOR.length()));
                        }
                        if(mReceivedData.length()>200){

                        String fullData = mReceivedData.toString();
                        Log.i("INfo",fullData);
                        mReceivedData= new StringBuilder();

                        if(fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR)>0){
                        fullData = fullData.substring(fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR)+BluetoothConfiguration.MESSAGE_LINE_SEPERATOR.length());
                        List<MotionData> data = new ArrayList<>();
                        while (fullData.length() > 0 && fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR) > 0) {
                            // Send the obtained bytes to the UI activity.
                            String singleData = fullData.substring(0, fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR));
                            if (singleData.length() > 0) {
                                MotionData md=getMotionDataObjectFromString(singleData);
                                if(md!=null){
                                    data.add(md);
                                }

                            }
                            fullData = fullData.substring(fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR) + BluetoothConfiguration.MESSAGE_LINE_SEPERATOR.length());

                        }
                            outStream.setText("");
//                        for (MotionData m : data) {
//                            tvSensor.setText(m.getSensor().name());
//                            tvStatus.setText("OK");
//                            tvX.setText(m.getX());
//                            tvY.setText(m.getY());
//                            tvZ.setText(m.getZ());
//                            outStream.setText(outStream.getText()+m.toString()+"\n");
//                        }
                }}}
            }

            private MotionData getMotionDataObjectFromString(String input) {
                String[] data = new String[5];
                MotionData md = new MotionData();
                // split the single String into its data
                try {
                    for (int i = 0; i < 5; i++) {
                        if (input.indexOf(BluetoothConfiguration.MESSAGE_SEPARATOR) > 0) {
                            data[i] = input.substring(0, input.indexOf(BluetoothConfiguration.MESSAGE_SEPARATOR));
                            input = input.substring(input.indexOf(BluetoothConfiguration.MESSAGE_SEPARATOR) + BluetoothConfiguration.MESSAGE_SEPARATOR.length());
                        } else {
                            data[i] = input;
                        }
                    }
                    // init Object

                    try {
                        if (data[0].equalsIgnoreCase(BluetoothConfiguration.SENSOR_STATUS_OK)) {
                            md.setBegin(new Timestamp(System.currentTimeMillis()));
                            md.setSensor(Integer.valueOf(data[1]));
                            md.setX(Integer.valueOf(data[2]));
                            md.setY(Integer.valueOf(data[3]));
                            md.setZ(Integer.valueOf(data[4]));
                        }else{
                            md=null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch(Exception e){
                   md=null; //ignore Exceptions on Parsing
                }
                return md;
            }


        };
    }

    // check App permissions
    private void checkPermissions() {
        for (String currentPerm : BluetoothConfiguration.permissionsToRequest) {
            if (ContextCompat.checkSelfPermission(this,
                    currentPerm)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        currentPerm)) {
                } else {
                    // No explanation needed, we can request the permission.
                    Log.i("info", "Have to enable Permission: " + currentPerm);
                    ActivityCompat.requestPermissions(this,
                            new String[]{currentPerm},
                            1);
                }
            }
        }
    }

    // enable BT if disabled
    private void checkBluethoothActive() {
        // Check and Log BT
        if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            Log.i("info", "Enabled Bluetooth!");
        }
    }

}
