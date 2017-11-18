package re.adjustme.de.readjustme.Service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;

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
        AcceptThread a = new AcceptThread(device.getName(), BluetoothConfiguration.BT_DEVICE_UUID);
       a.start();
//       try {
//            BluetoothSocket socket=createBluetoothSocket(this.device);
//            listenOnConnectedSocket(socket);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void listenOnConnectedSocket(BluetoothSocket socket) {
        Log.i("INFO", "Start listening");
        ConnectedThread conn = null;
        try {
            if(!socket.isConnected()) {
                socket.connect();
            }
            conn = new ConnectedThread(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.run();
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
       // if (
//                Build.VERSION.SDK_INT >= 10) {
//            try {
////                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
////                return (BluetoothSocket) m.invoke(device, BluetoothConfiguration.BT_DEVICE_UUID);
//            } catch (Exception e) {
//                Log.e("INFO", "Could not create Insecure RFComm Connection", e);
//            }
//        }
        BluetoothSocket bts= device.createRfcommSocketToServiceRecord(BluetoothConfiguration.BT_DEVICE_UUID);
        if(bts==null) bts=device.createInsecureRfcommSocketToServiceRecord(BluetoothConfiguration.BT_DEVICE_UUID);
        Log.i("INFO", "Tryed to create Socket"+ bts.toString());
        return bts;
    }

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
                        listenOnConnectedSocket(socket);
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
            int cbyte; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    //TODO Status check
                    StringBuilder sb = new StringBuilder();
                    while ((cbyte = mmInStream.read(mmBuffer)) != -1) {
                        sb.append((char) cbyte);
                    }
                    // split into single packages
                    String fullData=sb.toString();
                    do{
                        // Send the obtained bytes to the UI activity.
                        String singleData=fullData.substring(0,fullData.indexOf(BluetoothConfiguration.MESSAGE_SEPERATOR));
                        Bundle data= new Bundle();
                        data.putString(BluetoothConfiguration.SENSOR_DATA,singleData);
                        Message m=new Message();
                        m.setData(data);
                        mHandler.sendMessage(m);
                        Log.i("info", "Read Data " + singleData);
                        //  readMsg.sendToTarget();
                        mHandler.sendMessage(m);
                    }while(fullData.indexOf(BluetoothConfiguration.MESSAGE_SEPERATOR)!=fullData.lastIndexOf(BluetoothConfiguration.MESSAGE_SEPERATOR));

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


}
