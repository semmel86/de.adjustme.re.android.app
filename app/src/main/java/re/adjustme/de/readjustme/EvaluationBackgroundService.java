package re.adjustme.de.readjustme;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;

/**
 *
 *  Connected to the Persistence Service
 *  Evaluates the current Posture and
 *      (1) gives Notifications for bad Postures
 *      (2) Broadcasts the current Posture to GUI
 * Created by semmel on 03.12.2017.
 */

public class EvaluationBackgroundService extends Service {

    private PersistenceService mPersistenceService = null;
    private ServiceConnection mPersistenceConnection = null;
    private EvalThread mEvalThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){

    }

    @Override
    public void onDestroy() {
        mEvalThread.stop();
        stopSelf();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        this.init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init(){
        // get Persistence Service Binder
        setConnection();
        Intent intent = new Intent(this, PersistenceService.class);
        boolean b = bindService(intent, mPersistenceConnection, Context.BIND_AUTO_CREATE);
    }


    private void setConnection() {
        mPersistenceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PersistenceService.PersistenceServiceBinder b = (PersistenceService.PersistenceServiceBinder) iBinder;
                mPersistenceService = b.getService();
                // we git the connection, so start the Evaluation Tread
                startEvalThread();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mPersistenceService = null;
            }
        };
    }

    private void startEvalThread() {
        if(mEvalThread==null) {
            mEvalThread = new EvalThread();
        }
        mEvalThread.start();
    }

    private class EvalThread extends Thread{

        private MotionData[] motionDataSet;

        @Override
        public void run() {

            // Evaluate Posture
            while (true) {
                try {

                    sleep(100);// we wait a few seconds
                    motionDataSet=mPersistenceService.getMotionDataSet();
                    evaluateMotionData(motionDataSet);

                }catch(Exception e){

                }
            }
        }
    }

    private void evaluateMotionData(MotionData[] motionDataSet) {
        MotionData md=motionDataSet[0];
        int x=md.getY();
        int y=md.getZ();

        String pos="";

        if(x < 15 && x > -15 && y < 15 && y > -15 )  pos="liegt gerade";
        // y achse
        if(x < 15 && x > -15 && y > 15 && y < 75 ) pos="nach vorn geneigt";
        if(x < 15 && x > -15 && y > 75 && y < 105 ) pos="liegt auf der vorderen Kante";
        if(x < 15 && x > -15 && y < -15 && y > -75 )pos="nach hinten geneigt";
        if(x < 15 && x > -15 && y < -75 && y > -105 )pos="liegt auf der hinteren Kante";
        // x achse
        if(x < -15 && x > -75 && y < 15 && y > -15 )  pos="nach links geneigt";
        if(x < -75 && x > -105 && y < 15 && y > -15 )  pos="liegt auf der linken Kante";
        if(x > 15 && x < 75 && y < 15 && y > -15 )  pos="nach rechts geneigt";
        if(x > 75 && x < 105 && y < 15 && y > -15 )  pos="liegt auf der Rechten Kante";

        if(pos.length()>0){
            this.sendPostureBroadcast(pos);
        }
    }

    private void sendPostureBroadcast(String posture) {

        Intent intent = new Intent("Posture");
        // You can also include some extra data.

        intent.putExtra("PostureName", posture);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
