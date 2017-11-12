package re.adjustme.de.readjustme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Semmel on 12.11.2017.
 */

public class BluetoothActivity extends AppCompatActivity {
    private TextView tvDevice;
    private TextView tvStatus;
    private TextView tvSensor;
    private TextView tvX;
    private TextView tvY;
    private TextView tvZ;
    private TextView outStream;

    private BroadcastReceiver mReceiver;
    private BluetoothService mBluetoothService;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set fields
        super.onCreate(savedInstanceState);
        tvDevice=(TextView) findViewById(R.id.tvDevice);
        tvStatus=(TextView) findViewById(R.id.tvStatus);
        tvSensor=(TextView) findViewById(R.id.tvSensor);
        tvX=(TextView) findViewById(R.id.tvSensor);
        tvY=(TextView) findViewById(R.id.tvSensor);
        tvZ=(TextView) findViewById(R.id.tvSensor);
        outStream=(TextView) findViewById(R.id.textView);
        // check permissions
        checkPermissions();
        checkBluethoothActive();

        // set up specific classes
        this.mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        this.setHandler();
        this.setReceiver();

        discoverDevice();
    }
// start discovery Mode
private void discoverDevice(){
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
        this.mBluetoothService = new BluetoothService(device, mHandler, mBluetoothAdapter);
        mBluetoothService.start();
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private void setReceiver() {
        mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                        // if it is our Shirt device, try to connect
                        if (deviceName.equals(Configuration.BT_DEVICE_NAME)) {
                            Toast.makeText(context, "Try Connection to" + deviceName, Toast.LENGTH_SHORT).show();
                            connectToDevice(device);
                            // stop discovering
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    }

            }

        };
    }
    // Create the specifc Handler to handle BT data
    private void setHandler(){
       this.mHandler  = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case Configuration.MESSAGE_READ:
                        String singleData=msg.getData().getString(Configuration.SENSOR_DATA);
                        int endOfLineIndex = singleData.indexOf("\r\n");
                        String[] data=new String[5];
                        // write on debug console Textfield
                        outStream.setText(singleData);
                        // split the single String into its data
                        for(int i=0;i<5;i++){
                            data[i]=singleData.substring(0,singleData.indexOf(" "));
                            singleData=singleData.substring(singleData.indexOf(" "),singleData.length());
                        }
                        // write data to GUI
                        if (endOfLineIndex > 0) {
                            tvStatus.setText(data[0]);
                            tvSensor.setText(data[1]);
                            tvX.setText(data[2]);
                            tvY.setText(data[3]);
                            tvZ.setText(data[4]);
                        }
                        break;
                }
            }

            ;
        };
    }
    // check App permissions
    private void checkPermissions() {
        for (String currentPerm : Configuration.permissionsToRequest) {
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
