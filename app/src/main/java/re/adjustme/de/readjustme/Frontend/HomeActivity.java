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
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;
import re.adjustme.de.readjustme.Frontend.Service.BluetoothBackgroundService;
import re.adjustme.de.readjustme.Frontend.Service.ClassificationBackgroundService;
import re.adjustme.de.readjustme.R;

public class HomeActivity extends GenericBaseActivity {
    private Button mStartServiceBtn;
    private Button mStopServiceBtn;
    private Button mCalibrateBtn;
    private EditText mUsernameInput;
    private ProgressBar mProgressBar;
    private TextView mUsernameLabel;
    private TextView mHwsPosture;
    private TextView mShoulderPosture;
    private TextView mBwsPosture;
    private TextView mLwsPosture;
    private AlertDialog mAlertDialog;
    private TextView mCalibrationTextView;
    private LinearLayout mMainLayout;
    private BluetoothAdapter mBluetoothAdapter;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mPostureReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("PostureBean")) {
                String s = intent.getStringExtra("PostureName");
                String area = intent.getStringExtra("Area");
                switch (area) {
                    case "HWS":
                        mHwsPosture.setText(s);
                        break;
                    case "SHOULDER":
                        mShoulderPosture.setText(s);
                        break;
                    case "SPINE":
                        mBwsPosture.setText(s);
                        break;
                    case "LWS":
                        mLwsPosture.setText(s);
                        break;
                }
            } else if (action.equals("AppEvent")) {
                String running = intent.getStringExtra("Running");
                String starting = intent.getStringExtra("Starting");
                setButtons(running.equals("true"), starting.equals("true"));
            }
        }

    };


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsernameInput = (EditText) findViewById(R.id.editUsername);
        mUsernameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                setNewUsername(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        mUsernameLabel = (TextView) findViewById(R.id.usernameTextview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mMainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mMainLayout.setVisibility(View.INVISIBLE);
        mCalibrateBtn = (Button) findViewById(R.id.btnCalibrate);
        mStartServiceBtn = (Button) findViewById(R.id.startService);
        mStopServiceBtn = (Button) findViewById(R.id.stopService);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableButton(mStartServiceBtn, true);
        changeStartStopBtns(mDataAccessService != null && mDataAccessService.getIsRunning());
        checkBluethoothActive();
        checkPermissions();
        // set navigation bar
        setNavigationBar();

        mHwsPosture = (TextView) findViewById(R.id.hws_posture);
        mShoulderPosture = (TextView) findViewById(R.id.shoulder_posture);
        mBwsPosture = (TextView) findViewById(R.id.bws_posture);
        mLwsPosture = (TextView) findViewById(R.id.lws_posture);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("PostureBean"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("AppEvent"));

    }

    protected void afterServiceConnection() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMainLayout.setVisibility(View.VISIBLE);
        if (mDataAccessService.getUsername() != null) {
            mUsernameInput.setText(mDataAccessService.getUsername());
        }
        setButtons(mDataAccessService.getIsRunning(), mDataAccessService.getTryStarting());
    }

    private void setButtons(boolean running, boolean starting) {
        if (running) {
            mDataAccessService.setTryStarting(false);
            changeUsernameEditable(false);
            enableButton(mStartServiceBtn, true);
        } else if (!starting) {
            changeUsernameEditable(true);
            enableButton(mStartServiceBtn, true);
        }
        if (starting) {
            changeUsernameEditable(false);
            enableButton(mStartServiceBtn, false);
        }
        changeStartStopBtns(running);
    }

    private void setNewUsername(String name) {
        if (mDataAccessService != null) {
            if (name.equals("") || name.equals(" ")) {
                enableButton(mStartServiceBtn, false);
            } else if (!mDataAccessService.getTryStarting()) {
                enableButton(mStartServiceBtn, true);

            }
            mDataAccessService.setUsername(name);
        }
    }

    private void changeUsernameEditable(boolean editable) {
        mUsernameLabel.setText(mUsernameInput.getText());
        if (editable) {
            mUsernameLabel.setVisibility(View.INVISIBLE);
            mUsernameInput.setVisibility(View.VISIBLE);
        } else {
            mUsernameLabel.setVisibility(View.VISIBLE);
            mUsernameInput.setVisibility(View.INVISIBLE);
        }
    }

    private void enableButton(Button btn, Boolean enabled) {
        btn.setAlpha(enabled ? 1 : .5f);
        btn.setClickable(enabled);
    }

    private void checkBluethoothActive() {
        // Check and Log BT
        if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            Log.d("info", "Enabled Bluetooth!");
        }
    }


    // old:start BT service
    // new: stop all Running Services
    public void startService(View v) {
        mDataAccessService.setTryStarting(true);
        try {
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            Intent intent2 = new Intent(this, ClassificationBackgroundService.class);
            InitThread init = new InitThread(intent, intent2);
            init.start();
            Toast.makeText(getApplicationContext(), "Start Services", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            mDataAccessService.setTryStarting(false);
            Toast.makeText(getApplicationContext(), "Cannot start Services", Toast.LENGTH_LONG).show();
        }
    }

    // set current posture as (0,0,0) for each sensor
    public void calibrate(View v) {
        new AlertDialog.Builder(this)
                .setMessage("re.adjustme wirklich kalibrieren?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startCalibrationAlert();
                    }
                })
                .setNegativeButton("Nein", null) // nothing to do
                .show();

    }

    private void startCalibrationAlert() {
        mAlertDialog = new AlertDialog.Builder(this).setMessage("5").show();   //
        mCalibrationTextView = (TextView) mAlertDialog.findViewById(android.R.id.message);
        mCalibrationTextView.setTextSize(30);
        mCalibrationTextView.setGravity(Gravity.CENTER);

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished > 7000) {
                    mAlertDialog.setMessage(getResources().getString(R.string.calibrationMessage));
                } else {
                    mCalibrationTextView.setTextSize(60);
                    mAlertDialog.setMessage("" + ((millisUntilFinished / 1000) - 1));
                }
            }

            @Override
            public void onFinish() {
                mDataAccessService.setCalibrationData();
                mAlertDialog.hide();
            }
        }.start();
    }

    // stop all Running Services
    public void stopService(View v) {
        mDataAccessService.setTryStarting(false);
        try {
            // stop BT
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            stopService(intent);
            // stop Evaluation
            Intent intent2 = new Intent(this, ClassificationBackgroundService.class);
            stopService(intent2);

            Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "already stopped", Toast.LENGTH_LONG).show();
        }
        mDataAccessService.setIsRunning(false);
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
        for (String currentPerm : BluetoothConfiguration.REQUESTED_PERMISSIONS) {

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

    private void changeStartStopBtns(boolean isRunning) {
        if (isRunning) {
            mStartServiceBtn.setVisibility(View.INVISIBLE);
            mStopServiceBtn.setVisibility(View.VISIBLE);
            enableButton(mCalibrateBtn, true);
        } else {
            mStartServiceBtn.setVisibility(View.VISIBLE);
            mStopServiceBtn.setVisibility(View.INVISIBLE);
            enableButton(mCalibrateBtn, false);
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
        public void run()  {
            // init BlueTooth first
            startService(btIntent);
            long start= System.currentTimeMillis();
            long curr=0L;
            // wait until the data comes in
            while (!mDataAccessService.receivesLiveData() && curr<BluetoothConfiguration.CONNECTION_TIMEOUT) {
                curr= System.currentTimeMillis()-start;

                try {
                    sleep(1000);
                } catch (InterruptedException e1) {

                }
            }
            if(curr>=BluetoothConfiguration.CONNECTION_TIMEOUT){
                mDataAccessService.setTryStarting(false);

            }
    if( mDataAccessService.getTryStarting()){
                // and init Evaluation last
                startService(evalIntent);

        }
    }
}}