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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.ClassificationData;
import re.adjustme.de.readjustme.Bean.MotionClassificator;
import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Predefined.PersistenceType;
import re.adjustme.de.readjustme.Predefined.Sensor;
import re.adjustme.de.readjustme.Persistence.ClassificationDataPersistor;
import re.adjustme.de.readjustme.Persistence.PersistorFactory;

/**
 * Connected to the Persistence Service
 * Evaluates the current Posture and
 * (1) gives Notifications for bad Postures
 * (2) Broadcasts the current Posture to GUI
 * Created by semmel on 03.12.2017.
 */

public class EvaluationBackgroundService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new EvaluationBackgroundServiceBinder();
    // Contains a specific Classifier for each Area
    HashMap<BodyArea, List<MotionClassificator>> motionclassifier;
    private PersistenceService mPersistenceService = null;
    private ServiceConnection mPersistenceConnection = null;
    private EvalThread mEvalThread;
    private ClassificationDataPersistor persistor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        persistor = PersistorFactory.getClassificationDataPersistor(PersistenceType.OBJECT);
        motionclassifier = persistor.loadClassificationMap();
    }

    @Override
    public void onDestroy() {
        mEvalThread.killMe();
        mEvalThread = null;
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
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
    private void loadClassifier(){
        persistor = PersistorFactory.getClassificationDataPersistor(PersistenceType.OBJECT);
        motionclassifier = persistor.loadClassificationMap();
        if (motionclassifier == null || motionclassifier.isEmpty()) {
            unzipClassificator();
            motionclassifier = persistor.loadClassificationMap();
        }
    }

    private void startEvalThread() {
        // start only if there is any data
        if(mPersistenceService.receivesLiveData()) {
            // load classifier first
            loadClassifier();
            if (mEvalThread == null) {
                mEvalThread = new EvalThread();
            }
            // start the thread
            mEvalThread.start();
        }
    }
    // write the classificator object to /data/..persistence to load it from there
    private void unzipClassificator(){
        File f = new File(PersistenceConfiguration.getPersistenceDirectory()+"classificationHashMap.md");
        if (!f.exists()) try {

            InputStream is = getAssets().open("classificationHashMap.md");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
            Log.i("Info","Unzipped classifiactionHashMap.md from assets.");
        } catch (Exception e) {
            Log.e("Error","Cannot unzip classifiactionHashMap.md from assets.");
        }
    }
    // only used on development, to calculate the Classification object
    public void calculateModel() {
        Toast.makeText(this, "Start Calculation", Toast.LENGTH_SHORT).show();
        if (mPersistenceService == null) {
            setConnection();
        }
        HashMap<Sensor, List<MotionData>> raw = mPersistenceService.getMotionData();
        List<MotionData> allMotions = new ArrayList<>();
        HashMap<String, MotionClassificator> motions = new HashMap<>();

        // loop over all entrys and get Motions
        for (Sensor s : Sensor.values()) {


            for (MotionData md : raw.get(s)) {
                allMotions.add(md);
                if (md.getLabel() != null && !motions.containsKey(md.getLabel())) {
                    motions.put(md.getLabel(), new MotionClassificator(md.getLabel()));

                }
            }
        }
        // save ram, delete reference
        raw = null;

        if (motions.size() > 0) {
            // now aggregate all data with the same label && inLabeldPosition == true
            // to a classifier
            // if unknown Motion, create new MotionClassificator Object
            for (String label : motions.keySet()) {
                MotionClassificator currClassificator = motions.get(label);

                for (MotionData md : allMotions) {
                    if (md.getLabel().equals(label) && md.getInLabeledPosition()) {

                        if (!currClassificator.containsSensor(md.getSensor().getSensorNumber())) {
                            currClassificator.putClassificationData(md.getSensor().getSensorNumber(), new ClassificationData());
                        }
                        // get reference to data
                        ClassificationData c = currClassificator.getClassificationData(md.getSensor().getSensorNumber());
                        // Maximum
                        if (c.getMaxX() < md.getX()) c.setMaxX(md.getX());
                        if (c.getMaxY() < md.getY()) c.setMaxY(md.getY());
                        if (c.getMaxZ() < md.getZ()) c.setMaxZ(md.getZ());
                        // Minimum
                        if (c.getMinX() > md.getX()) c.setMinX(md.getX());
                        if (c.getMinY() > md.getY()) c.setMinY(md.getY());
                        if (c.getMinZ() > md.getZ()) c.setMinZ(md.getZ());
                        // update Mean
                        long d = md.getDuration() == 0 ? 1 : md.getDuration();
                        c.setMeanX((c.getMeanX() * c.getDur() + md.getX() * d) / (c.getDur() + d));
                        c.setMeanY((c.getMeanY() * c.getDur() + md.getY() * d) / (c.getDur() + d));
                        c.setMeanZ((c.getMeanZ() * c.getDur() + md.getZ() * d) / (c.getDur() + d));
                        c.setDur(c.getDur() + d);
                        // TODO update Max distance
                        double distance = 180;
                        c.setMaxDistance(180);

                    }
                }
            }
        }

        HashMap<BodyArea, List<MotionClassificator>> result = new HashMap<>();

        // Build classifier for each area
        for (BodyArea b : BodyArea.values()) {
            // if area contains lable, add to classifier
            for (MotionClassificator c : motions.values()) {
                if (b.contains(c.getName())) {
                    if (result.containsKey(b)) {
                        result.get(b).add(c);
                    } else {
                        List<MotionClassificator> l = new ArrayList<>();
                        l.add(c);
                        result.put(b, l);
                    }
                }
            }
        }


        persistor.save(result);
        motionclassifier = result;
        Toast.makeText(this, "Classification Model Calculated", Toast.LENGTH_SHORT).show();
    }

    private void evaluateMotionData(MotionDataSetDto motionDataSet) {
        if(mPersistenceService.receivesLiveData()){
        for (BodyArea area : motionclassifier.keySet()) {
            double probability = 0;
            MotionClassificator classificator = new MotionClassificator("");

            for (MotionClassificator m : motionclassifier.get(area)) {
                double currProbability = m.getProbability(motionDataSet);
                if (Double.compare(currProbability, probability) > 0) {
                    classificator = m;
                    probability = currProbability;
                    //  Log.i("Info", "Classification: " + classificator.getName() + " " + currProbability);
                }
            }
            if(probability > ClassificationConfiguration.MIN_PROBABILITY) {
                this.sendPostureBroadcast(classificator.getName(), area.name());
            }else{
                this.sendPostureBroadcast(ClassificationConfiguration.UNKNOWN_POSITION, area.name());
            }
        }}else{
            this.stopSelf();
        }
    }

    private void sendPostureBroadcast(String posture, String area) {

        Intent intent = new Intent("Posture");
        // You can also include some extra data.

        intent.putExtra("PostureName", posture);
        intent.putExtra("Area", area);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private class EvalThread extends Thread {

        private MotionDataSetDto motionDataSet;
        public void killMe(){
            this.destroy();
        }

        @Override
        public void run() {

            // Evaluate Posture
            while (true) {
                try {

                    sleep(ClassificationConfiguration.EVALUATION_TIME);
                    motionDataSet = mPersistenceService.getMotionDataSet();
                    evaluateMotionData(motionDataSet);

                } catch (Exception e) {

                }
            }
        }
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
