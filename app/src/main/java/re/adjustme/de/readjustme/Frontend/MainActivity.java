package re.adjustme.de.readjustme.Frontend;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;
import re.adjustme.de.readjustme.R;

public class MainActivity extends GenericBaseActivity {
    private Button startServiceBtn;
    private Button stopServiceBtn;
    private Button calibrateBtn;
    private EditText usernameInput;
    private ProgressBar progressBar;
    private TextView usernameLabel;
    private TextView hws_posture;
    private TextView shoulder_posture;
    private TextView bws_posture;
    private TextView lws_posture;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mPostureReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("Posture")) {
                String s = intent.getStringExtra("PostureName");
                String area = intent.getStringExtra("Area");
                switch (area) {
                    case "HWS":
                        hws_posture.setText(s);
                        break;
                    case "SHOULDER":
                        shoulder_posture.setText(s);
                        break;
                    case "SPLINE":
                        bws_posture.setText(s);
                        break;
                    case "LWS":
                        lws_posture.setText(s);
                        break;
                }
            } else if (action.equals("AppEvent")) {
                String running = intent.getStringExtra("Running");
                String starting = intent.getStringExtra("Starting");
                setButtons(running.equals("true"),starting.equals("true"));
            }
        }

    };
    private LinearLayout mainLayout;
    private BluetoothAdapter BA;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameInput = (EditText) findViewById(R.id.editUsername);
        usernameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setNewUsername(s.toString());
            }
        });
        usernameLabel = (TextView) findViewById(R.id.usernameTextview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.INVISIBLE);
        calibrateBtn = (Button) findViewById(R.id.btnCalibrate);
        startServiceBtn = (Button) findViewById(R.id.startService);
        stopServiceBtn = (Button) findViewById(R.id.stopService);
        BA = BluetoothAdapter.getDefaultAdapter();
        startServiceBtn.setEnabled(true);
        changeStartStopBtns(mPersistenceService != null && mPersistenceService.getIsRunning());
        checkBluethoothActive();
        checkPermissions();
        // set navigation bar
        setNavigationBar();

        hws_posture = (TextView) findViewById(R.id.hws_posture);
        shoulder_posture = (TextView) findViewById(R.id.shoulder_posture);
        bws_posture = (TextView) findViewById(R.id.bws_posture);
        lws_posture = (TextView) findViewById(R.id.lws_posture);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("Posture"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("AppEvent"));

    }

    protected void afterServiceConnection() {
        progressBar.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
        if(mPersistenceService.getUsername()!=null){
            usernameInput.setText(mPersistenceService.getUsername());
        }
        setButtons(mPersistenceService.getIsRunning(),mPersistenceService.getTryStarting());
    }

    private void setButtons (boolean running, boolean starting) {
        if (running) {
            mPersistenceService.setTryStarting(false);
            changeUsernameEditable(false);
            startServiceBtn.setEnabled(true);
        }else
        if (!starting){
            changeUsernameEditable(true);
            startServiceBtn.setEnabled(true);
        }
        if (starting) {
            changeUsernameEditable(false);
            startServiceBtn.setEnabled(false);
        }
        changeStartStopBtns(running);
    }

    private void setNewUsername(String name) {
        if (mPersistenceService != null) {
            if (name.equals("") || name.equals(" ")) {
                startServiceBtn.setEnabled(false);
            } else if (!mPersistenceService.getTryStarting()) {
                startServiceBtn.setEnabled(true);
            }
            mPersistenceService.setUsername(name);
        }
    }

    private void changeUsernameEditable(boolean editable) {
        usernameLabel.setText(usernameInput.getText());
        if (editable) {
            usernameLabel.setVisibility(View.INVISIBLE);
            usernameInput.setVisibility(View.VISIBLE);
        }else{
            usernameLabel.setVisibility(View.VISIBLE);
            usernameInput.setVisibility(View.INVISIBLE);
        }
    }

    private void checkBluethoothActive() {
        // Check and Log BT
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            Log.d("info", "Enabled Bluetooth!");
        }
    }


    // old:start BT service
    // new: stop all Running Services
    public void startService(View v) {
        mPersistenceService.setTryStarting(true);
        try {
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            Intent intent2 = new Intent(this, EvaluationBackgroundService.class);
            InitThread init = new InitThread(intent, intent2);
            init.start();
            Toast.makeText(getApplicationContext(), "Start Services", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            mPersistenceService.setTryStarting(false);
            Toast.makeText(getApplicationContext(), "Cannot start Services", Toast.LENGTH_LONG).show();
        }
    }

    @Deprecated
    public void StartEvalService(View v) {
        try {
            Intent intent = new Intent(this, EvaluationBackgroundService.class);
            startService(intent);
            Toast.makeText(getApplicationContext(), "started Evaluation", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already started", Toast.LENGTH_LONG).show();
        }
    }


    // set current posture as (0,0,0) for each sensor
    public void calibrate(View v) {
        new AlertDialog.Builder(this)
                .setMessage("re.adjustme wirklich kalibrieren?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPersistenceService.calibrate();
                    }
                })
                .setNegativeButton("Nein", null) // nothing to do
                .show();

    }

    // stop all Running Services
    public void stopService(View v) {
        mPersistenceService.setTryStarting(false);
        try {
            // stop BT
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            stopService(intent);
            // stop Evaluation
            Intent intent2 = new Intent(this, EvaluationBackgroundService.class);
            stopService(intent2);

            Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already stopped", Toast.LENGTH_LONG).show();
        }
        mPersistenceService.setIsRunning(false);
    }

    @Deprecated
    public void stopEvalService(View v) {
        try {
            Intent intent = new Intent(this, EvaluationBackgroundService.class);
            stopService(intent);
            Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already stopped", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("re.adjustme wirklich schließen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // unbind service
                        unbindService(mPersistenceConnection);
                        // finish activity
                        finish();

                    }
                })
                .setNegativeButton("Nein", null) // nothing to do
                .show();


    }
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
                    Log.d("info", "Have to enable Permission: " + currentPerm);
                    ActivityCompat.requestPermissions(this,
                            new String[]{currentPerm},
                            1);

                }
            }
        }
    }

    private void changeStartStopBtns (boolean isRunning) {
        if (isRunning) {
            startServiceBtn.setVisibility(View.INVISIBLE);
            stopServiceBtn.setVisibility(View.VISIBLE);
            calibrateBtn.setEnabled(true);
        } else {
            startServiceBtn.setVisibility(View.VISIBLE);
            stopServiceBtn.setVisibility(View.INVISIBLE);
            calibrateBtn.setEnabled(false);
        }
    }

    // Wrap initialization in a single thread to avoid UI delays
    private class InitThread extends Thread {
        Intent btIntent;
        Intent evalIntent;

        public InitThread(Intent bt, Intent eval) {
            this.btIntent = bt;
            this.evalIntent = eval;
        }

        @Override
        public void run() {
            // init BlueTooth first
            startService(btIntent);

            // wait until the data comes in
            while (!mPersistenceService.receivesLiveData()) {
                try {
                    sleep(1000);
                } catch (InterruptedException e1) {

                }
            }
            // and init Evaluation last
            startService(evalIntent);

        }
    }
}