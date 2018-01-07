package re.adjustme.de.readjustme;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;
import re.adjustme.de.readjustme.Configuration.HardwareFailures;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;

import static android.content.ContentValues.TAG;

/**
 * TODO
 * Created by Semmel on 18.11.2017.
 */

public class BluetoothBackgroundService extends Service {

    private static Handler mHandler;
    // current MotionData used for aggregation via equals
    // private static HashMap<Sensor, MotionData> currentRawMotionData;
    private BluetoothAdapter mBluetoothAdapter;
    private PersistenceService mPersistenceService = null;
    private ServiceConnection mConnection = null;
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private boolean mConnected = false;
    private boolean destroyed = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        this.init();
        // startConnection Bluetooth listener
        getBondedDevice();
        // connect to device
        startConnection();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // save the last data before shutdown !!!
//        for (MotionData m : currentRawMotionData.values()) {
//            mPersistenceService.save(m);
//        }
        destroyed = true;
        if (mSocket!=null && mSocket.isConnected()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        PersistenceConfiguration.setPersistenceDirectory(this.getApplicationContext().getFilesDir());
    }

    private void init() {

        // get Persistence Service Binder
        setConnection();
        Intent intent = new Intent(this, PersistenceService.class);
        boolean b = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        // set up specific classes
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setHandler();
    }


    public void startConnection() {
        if (!destroyed && mDevice!=null) {
            if (BluetoothConfiguration.SERVER_MODE) {
                AcceptThread a = new AcceptThread(mDevice.getName(), BluetoothConfiguration.BT_DEVICE_UUID);
                a.start();
            } else {
                ThreadConnectBTdevice b = new ThreadConnectBTdevice(mDevice);
                b.start();
            }
            Log.i("Info", "Started Connecting to Device");
        }

    }

    private void getBondedDevice() {

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = this.mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0) {

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                for(String btModul:BluetoothConfiguration.BT_DEVICE_NAME){
                 if (!(device.getName() == null) && device.getName().equals(btModul)) {
                    mDevice = device;
                    Log.i("Info", "Found a known Bluetooth device: " + device.getName() + " " + device.getType());
                    break;
                }}
            }
         }
            Toast.makeText(getApplicationContext(), "Please pair the HC-05/06", Toast.LENGTH_SHORT).show();



    }


    /**
     * *****************************
     * PRIVATE Bluetooth Threads
     * *****************************
     */
    private void listenOnConnectedSocket(BluetoothSocket socket) {
        mSocket = socket;
        Log.i("INFO", "Start listening");
        ConnectedThread conn = null;
        try {
            if (!socket.isConnected()) {
                socket.connect();
            }
            conn = new ConnectedThread(socket);
        } catch (Exception e) {
            Log.i("info", "Failure on listenOnConnectedSocket");
            e.printStackTrace();
        }
        sendConnected();
        conn.start();
    }

    /**
     * *****************************
     * PRIVATE INITIAL SETUP METHODS
     * *****************************
     */


    private void setHandler() {
        this.mHandler = new DataHandler(mPersistenceService);
    }

    private void setConnection() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PersistenceService.PersistenceServiceBinder b = (PersistenceService.PersistenceServiceBinder) iBinder;
                mPersistenceService = b.getService();
                // ensures the handler got a persistence service instance!!!
                setHandler();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mPersistenceService = null;
            }
        };
    }

    private void sendConnected() {
        Log.d("sender", "BT Connected");
        Intent intent = new Intent(BluetoothConfiguration.BLUETOOTH_CONNECTED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendDisonnected() {
        Log.d("sender", "BT Disconnected");
        Intent intent = new Intent(BluetoothConfiguration.BLUETOOTH_DISCONNECTED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private static class DataHandler extends Handler {
        private PersistenceService mPersistenceService = null;
        private StringBuilder mReceivedData;

        public DataHandler(PersistenceService p) {
            this.mPersistenceService = p;
            this.mReceivedData = new StringBuilder();
        }

        public synchronized void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case BluetoothConfiguration.MESSAGE_READ:

                    String received = (String) msg.obj;
                    mReceivedData.append(received);

                    if (mReceivedData.length() > 25) {
                        String fullData = mReceivedData.toString();
                        mReceivedData = new StringBuilder();
                        // save the last snipped for the next iteration, so we avoid loosing information
                        mReceivedData.append(fullData.substring(fullData.lastIndexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR)));

                        if (fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR) >= 0) {
                            fullData = fullData.substring(fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR) + BluetoothConfiguration.MESSAGE_LINE_SEPERATOR.length());
                            List<MotionData> data = new ArrayList<>();
                            while (fullData.length() > 0 && fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR) > 0) {
                                // Send the obtained bytes to the UI activity.
                                String singleData = fullData.substring(0, fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR));
                                if (singleData.length() > 0) {
                                    // get an motion data object from String
                                    MotionData md = getMotionDataObjectFromString(singleData);
                                    if (md != null) {
                                        mPersistenceService.processNewMotionData(md);
                                    }
                                }
                                fullData = fullData.substring(fullData.indexOf(BluetoothConfiguration.MESSAGE_LINE_SEPERATOR) + BluetoothConfiguration.MESSAGE_LINE_SEPERATOR.length());

                            }
                        }
                    }
            }
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
                    } else {

                        md = null;

                        if (Integer.valueOf(data[0]) == HardwareFailures.INITIATION_FAILURE.getCode()) {
                            Log.i("Info", "Hardware-Failure: " + HardwareFailures.getFailure(Integer.valueOf(data[0])).getMessage() + " " + Integer.valueOf(data[1]));
                        } else {
                            Log.i("Info", "Hardware-Failure: " + HardwareFailures.getFailure(Integer.valueOf(data[0])).getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                md = null; //ignore Exceptions on Parsing
            }
            return md;
        }
    }

    // SERVER MODE
    // This Class is used for a single tread to get a connection
    // returns a connected Socket on success
    // , Failure otherwise
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(String name, UUID uuid) {
            Log.i("info", "Init AcceptThread");
            System.out.println("Waiting for incomming Connection.");

            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);

            } catch (IOException e) {
                Log.i("info", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            System.out.println("Accept Thread Runs !");
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    mConnected = false;
                    Log.e("info", "Socket's accept() method failed", e);
                    System.out.println("Socket's accept() method failed");
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    mConnected = true;
                    listenOnConnectedSocket(socket);
                    break;
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

    // This Class is used for a single tread to receive Messages from
    // the connected device
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private BluetoothSocket mmSocket;
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

        public synchronized void run() {
            Log.i("Info", "Connection status:" + mmSocket.isConnected());
            mmBuffer = new byte[1024];
            Integer numBytes = null; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    if (mmSocket.isConnected()) {
                        if (mmInStream.available() > 0) {
                            // Read from the InputStream.
                            numBytes = mmInStream.read(mmBuffer);
                            // Send the obtained bytes to the UI activity.
                            String s2 = new String(mmBuffer, 0, numBytes);
                            Message readMsg = mHandler.obtainMessage(
                                    BluetoothConfiguration.MESSAGE_READ, numBytes, -1,
                                    s2);
                            readMsg.sendToTarget();
//                            Log.i("Debug", "BT received Input.");
                        } else {
                            try {
//                                Log.i("Debug", "BT Inputstream empty.");
                                sleep(BluetoothConfiguration.CONNECTION_DELAY);
                            } catch (Exception e) {
                            }
                            ;
                        }
                    } else {
                        // get in the except block and restart
                        throw new IOException();
                    }
                } catch (IOException e) {
                    Log.d("Info", "Input stream was disconnected", e);
                    if (!mmSocket.isConnected()) {
                        // lost connection, close and destroy this socket,
                        mConnected = false;
                        try {
                            mmSocket.close();
                            mmSocket = null;
                        } catch (IOException e2) {
                        }
                        // restart connection
                        startConnection();
                    }
                    break;
                }

            }
        }

        // Call this from the main activity to send data to the remote device.
        // NOT USED
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        BluetoothConfiguration.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e("info", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(BluetoothConfiguration.MESSAGE_TOAST);
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

    // CLIENT MODE
    /*
        ThreadConnectBTdevice:
        Background Thread to handle BlueTooth connecting
        */
    private class ThreadConnectBTdevice extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ThreadConnectBTdevice(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(BluetoothConfiguration.BT_DEVICE_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();
            while (!mConnected) {
                try {
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    mmSocket.connect();

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    mConnected = true;
                    listenOnConnectedSocket(mmSocket);

                } catch (IOException connectException) {
                    // Unable to connect; close the socket and return.
                    mConnected = false;
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                    //wait for 30 seconds and try to connect again
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}
