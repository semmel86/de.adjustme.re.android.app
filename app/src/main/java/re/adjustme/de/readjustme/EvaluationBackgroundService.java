package re.adjustme.de.readjustme;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.ClassificationData;
import re.adjustme.de.readjustme.Bean.MotionClassificator;
import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Configuration.PersistenceType;
import re.adjustme.de.readjustme.Configuration.Sensor;
import re.adjustme.de.readjustme.Persistence.ClassificationDataPersistor;
import re.adjustme.de.readjustme.Persistence.PersistorFactory;

import static java.lang.Thread.sleep;

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
    private List<MotionClassificator> classifier;
    private ClassificationDataPersistor persistor;
    // Binder given to clients
    private final IBinder mBinder = new EvaluationBackgroundServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
    persistor=PersistorFactory.getClassificationDataPersistor(PersistenceType.OBJECT);
    classifier=persistor.load();
    }

    @Override
    public void onDestroy() {
        mEvalThread.stop();
        mEvalThread=null;
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
        persistor=PersistorFactory.getClassificationDataPersistor(PersistenceType.OBJECT);
        classifier=persistor.load();
        if(classifier==null || classifier.isEmpty()){
            calculateModel();
       }
        if(mEvalThread==null) {
            mEvalThread = new EvalThread();
        }
        mEvalThread.start();
    }


   public void calculateModel(){
        Toast.makeText(this, "Start Calculation", Toast.LENGTH_SHORT).show();
        if(mPersistenceService==null){
            setConnection();

        }
        HashMap<Sensor, List<MotionData>> raw= mPersistenceService.getMotionData();
        List<MotionData> allMotions = new ArrayList<>();
        HashMap<String,MotionClassificator> motions = new HashMap<>();
        //List<String> motions=new ArrayList<String>();
        // loop over all entrys and get Motions
        for(Sensor s:Sensor.values()){


        for(MotionData md:raw.get(s)){
            allMotions.add(md);
            if(md.getLabel()!=null && !motions.containsKey(md.getLabel())){
                motions.put(md.getLabel(),new MotionClassificator(md.getLabel()));

            }
        }}
        // save ram, delete reference
        raw=null;
        if(motions.size()>0){
            // now aggregate all data with the same label && inLabeldPosition == true
            // to a classifier
            // if unknown Motion, create new MotionClassificator Object
            for(String label:motions.keySet()){
                MotionClassificator currClassificator=motions.get(label);

                for(MotionData md:allMotions){
                    if(md.getLabel().equals(label) && md.getInLabeledPosition()){

                        if(!currClassificator.containsSensor(md.getSensor().getSensorNumber())){
                            currClassificator.putClassificationData(md.getSensor().getSensorNumber(),new ClassificationData());
                        }
                        // get reference to data
                        ClassificationData c=currClassificator.getClassificationData(md.getSensor().getSensorNumber());
                        // Maximum
                        if(c.getMaxX()<md.getX()) c.setMaxX(md.getX());
                        if(c.getMaxY()<md.getY()) c.setMaxY(md.getY());
                        if(c.getMaxZ()<md.getZ()) c.setMaxZ(md.getZ());
                        // Minimum
                        if(c.getMinX()>md.getX()) c.setMinX(md.getX());
                        if(c.getMinY()>md.getY()) c.setMinY(md.getY());
                        if(c.getMinZ()>md.getZ()) c.setMinZ(md.getZ());
                        // update Mean
                        long d=md.getDuration()==0?1:md.getDuration();
                        c.setMeanX((c.getMeanX()*c.getDur()+md.getX()*d)/(c.getDur()+d));
                        c.setMeanY((c.getMeanY()*c.getDur()+md.getY()*d)/(c.getDur()+d));
                        c.setMeanZ((c.getMeanZ()*c.getDur()+md.getZ()*d)/(c.getDur()+d));
                        c.setDur(c.getDur()+d);
                        // TODO update Max distance
                        double distance = 180;
                        c.setMaxDistance(180);

                    }
                }
            }


        }

       // List<String> motions=new ArrayList<String>();
       // loop over all entrys and get Motions
       List<String> exclude = new ArrayList<>();
       exclude.add("Test Arbeitsplatz 1");
       exclude.add("Test Arbeitsplatz 2");
       exclude.add("Test Arbeitsplatz 3");
       exclude.add("Test Arbeitsplatz 4");
       exclude.add("Test Arbeitplatz 4");
       exclude.add("Test Arbeitsplatz 5");
       exclude.add("Test Nacken");
       exclude.add("Test Schulter ");
       exclude.add("Test RÃ¼cken lateral-rotation links");
       exclude.add("AAA");
       exclude.add("AAB");
       exclude.add("");
       exclude.add("I");
       exclude.add("I ");
       exclude.add(" I");
       List<MotionClassificator> forSave = new ArrayList<>();
       for (MotionClassificator c : motions.values()) {
           boolean ex = false;
           // test exclusion
           for (String st : exclude) {
               if (st.equals(c.getName())) {
                   ex = true;
               }
           }
           if (!ex) {
               forSave.add(c);
           }
       }



        persistor.save(forSave);
        classifier=forSave;
        Toast.makeText(this, "Classification Model Calculated", Toast.LENGTH_SHORT).show();
    }

    private class EvalThread extends Thread{

        private MotionDataSetDto motionDataSet;

        @Override
        public void run() {

            // Evaluate Posture
            while (true) {
                try {

                    sleep(5000);
                    motionDataSet=mPersistenceService.getMotionDataSet();
                    evaluateMotionData(motionDataSet);

                }catch(Exception e){

                }
            }
        }
    }

    private void evaluateMotionData(MotionDataSetDto motionDataSet) {
        if(classifier==null || classifier.isEmpty()){
            calculateModel();
        }
        double probability=0;
        MotionClassificator classificator=new MotionClassificator("");
        for(MotionClassificator m:classifier){
            double currProbability=m.getProbability(motionDataSet);
            if(Double.compare(currProbability,probability)>0){
                classificator=m;
                probability=currProbability;
                Log.i("Info", "Classification: "+ classificator.getName() + " "+currProbability);
            }

        }

            this.sendPostureBroadcast(classificator.getName());

    }

    private void sendPostureBroadcast(String posture) {

        Intent intent = new Intent("Posture");
        // You can also include some extra data.

        intent.putExtra("PostureName", posture);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class EvaluationBackgroundServiceBinder extends Binder {
        EvaluationBackgroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return EvaluationBackgroundService.this;
        }


    }
}
