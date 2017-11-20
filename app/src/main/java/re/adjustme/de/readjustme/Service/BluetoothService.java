package re.adjustme.de.readjustme.Service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;
import re.adjustme.de.readjustme.Bean.MotionData;

/**
 * Created by Semmel on 31.10.2017.
 */

public class BluetoothService {

    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;


    public BluetoothService(BluetoothDevice device, Handler handler, BluetoothAdapter bluetoothAdapter) {
        this.device = device;
        this.mHandler = handler;
        this.mBluetoothAdapter = bluetoothAdapter;
    }

    public void start() {
    if(BluetoothConfiguration.SERVER_MODE) {
        AcceptThread a = new AcceptThread(device.getName(), BluetoothConfiguration.BT_DEVICE_UUID);
        a.start();
    }else{
        ThreadConnectBTdevice b =new ThreadConnectBTdevice(device,BluetoothConfiguration.BT_DEVICE_UUID);
        b.start();
    }
    }

    private void listenOnConnectedSocket(BluetoothSocket socket) {
        Log.i("INFO", "Start listening");
        ConnectedThread conn = null;
        try {
            if (!socket.isConnected()) {
                socket.connect();
            }
            conn = new ConnectedThread(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.start();
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
                    Log.e("info", "Socket's accept() method failed", e);
                    System.out.println("Socket's accept() method failed");

                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
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
            int  numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            BluetoothConfiguration.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d("Info", "Input stream was disconnected", e);
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

        private final BluetoothDevice bluetoothDevice;
        private BluetoothSocket bluetoothSocket = null;


        private ThreadConnectBTdevice(BluetoothDevice device, UUID uuid) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(success){
                listenOnConnectedSocket(bluetoothSocket);
            }
        }
}}
