package re.adjustme.de.readjustme.Frontend.Service;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Persistence.Entity.MotionDataSetEntity;
import re.adjustme.de.readjustme.Persistence.Entity.SVMClassificationEntity;
import re.adjustme.de.readjustme.Persistence.GenericPersistenceProvider;
import re.adjustme.de.readjustme.Persistence.internal.MotionDataTextFilePersistor;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;
import re.adjustme.de.readjustme.Prediction.SvmPredictor;

/**
 * Connected to the Persistence Service
 * Evaluates the current Posture and
 * (1) gives Notifications for bad Postures
 * (2) Broadcasts the current Posture to GUI
 * Created by semmel on 03.12.2017.
 */

public class EvaluationBackgroundService extends Service {

    // Contains a specific Classifier for each Area
    SVMClassificationEntity svmMotionclassifier;
    GenericPersistenceProvider mPersistenceProvider;
    private DataAccessService mDataAccessService = null;
    private ServiceConnection mPersistenceConnection = null;
    private EvalThread mEvalThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        svmMotionclassifier = (SVMClassificationEntity) mPersistenceProvider.load(svmMotionclassifier.getClass());
    }

    @Override
    public void onDestroy() {
        try {
            mEvalThread.killMe();
            unbindService(mPersistenceConnection);
            sendStatusToPersistenceService(false);
            mEvalThread = null;
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.init();
        return START_STICKY;
    }

    private void init() {
        // get Persistence Service Binder
        setConnection();
        Intent intent = new Intent(this, DataAccessService.class);
        boolean b = bindService(intent, mPersistenceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setConnection() {
        mPersistenceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                DataAccessService.PersistenceServiceBinder b = (DataAccessService.PersistenceServiceBinder) iBinder;
                mDataAccessService = b.getService();
                // we git the connection, so start the Evaluation Tread
                startEvalThread();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mDataAccessService = null;
            }
        };
    }

    private void loadClassifier() {

        // load svm
        svmMotionclassifier = (SVMClassificationEntity) mPersistenceProvider.load(svmMotionclassifier.getClass());
        if (svmMotionclassifier == null || svmMotionclassifier.getSvmMotionclassifier().isEmpty()) {
            unzipClassificator("svmClassificationMap.md");
            unzipClassificator("FullMotionDataSet.csv");
            svmMotionclassifier = (SVMClassificationEntity) mPersistenceProvider.load(svmMotionclassifier.getClass());
        }
    }

    private void startEvalThread() {
        // start only if there is any data
        if (mDataAccessService.receivesLiveData()) {
            // load classifier first
            if (ClassificationConfiguration.CALCULATE_MODEL) {
                this.calculateSVMModel();
                {
                    this.loadClassifier();
                }

                if (mEvalThread == null) {
                    mEvalThread = new EvalThread();
                }
                // start the thread
                mEvalThread.start();
                sendStatusToPersistenceService(true);
            }
        }
    }

    private void sendStatusToPersistenceService(boolean status) {
        if (mDataAccessService != null) {
            mDataAccessService.setIsRunning(status);
        }
    }

    // write the classificator object to /data/..persistence to load it from there
    private void unzipClassificator(String name) {
        File f = new File(PersistenceConfiguration.getPersistenceDirectory() + name);
        if (!f.exists()) try {
            InputStream is = getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
            Log.d("Info", "Unzipped " + name + " from assets.");
        } catch (Exception e) {
            Log.e("Error", "Cannot unzip " + name + " from assets.");
        }
    }

    public void calculateSVMModel() {
        // 1-load the data (Full Motion data set objects)
        MotionDataTextFilePersistor csvPersistor = new MotionDataTextFilePersistor();
        List<MotionDataSetEntity> raw = csvPersistor.getMotionDataSetDtos();
        HashMap<BodyArea, List<MotionDataSetEntity>> sortedMap = new HashMap<BodyArea, List<MotionDataSetEntity>>();
        // init sortedMap with empty lists
        for (BodyArea area : BodyArea.values()) {
            sortedMap.put(area, new ArrayList<MotionDataSetEntity>());
        }
        // 2- assign each line with label & inPosition to a Bodyarea
        for (MotionDataSetEntity curr : raw) {
            if (curr.getInPosition() && curr.getLabel() != null) {
                for (BodyArea area : BodyArea.values()) {
                    if (area.contains(curr.getLabel())) {
                        // performs not so nice, but we don't build the model that often...
                        curr.setSvmClass(area.getLable(curr.getLabel()).getSVMClass());
                        sortedMap.get(area).add(curr);
                    }
                }
            }
        }
        svmMotionclassifier = new SVMClassificationEntity();
        svmMotionclassifier.setSvmMotionclassifier(new HashMap<BodyArea, SvmPredictor>());
        // 3- build the model for each Bodyarea
        for (BodyArea area : BodyArea.values()) {
            SvmPredictor predictor = new SvmPredictor();
            predictor.trainModel(sortedMap.get(area), area);
            svmMotionclassifier.getSvmMotionclassifier().put(area, predictor);
        }

        // 4 - save the classifierMap
        mPersistenceProvider.save(svmMotionclassifier);
    }

    private void evaluateMotionData(MotionDataSetEntity motionDataSet) {
        if (mDataAccessService.receivesLiveData()) {
            for (BodyArea area : BodyArea.values()) {
                double classification = svmMotionclassifier.getSvmMotionclassifier().get(area).predict(motionDataSet, area);
                Label l = area.getLable(Math.round(classification));
                this.sendPostureBroadcast(l.getDescription(), area.name());
                Log.d("Info", "Classification: " + area.name() + "  " + l.getDescription());
            }
        } else {
            this.stopSelf();
        }
    }

    private void sendPostureBroadcast(String posture, String area) {

        Intent intent = new Intent("Posture");
        intent.putExtra("PostureName", posture);
        intent.putExtra("Area", area);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private class EvalThread extends Thread {

        private MotionDataSetEntity motionDataSet;
        private boolean runThreadrun = true;

        public void killMe() {
            this.runThreadrun = false;
        }

        @Override
        public void run() {

            // Evaluate Posture
            while (runThreadrun) {
                try {
                    sleep(ClassificationConfiguration.EVALUATION_TIME);
                    motionDataSet = mDataAccessService.getMotionDataSet();
                    evaluateMotionData(motionDataSet);

                } catch (Exception e) {
                    e.printStackTrace();
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
