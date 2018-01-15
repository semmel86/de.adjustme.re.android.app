package re.adjustme.de.readjustme;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;

public class MainActivity extends GenericBaseActivity {
    Button bluttoothButton, b2;
    private EditText usernameInput;
    private ProgressBar progressBar;
    private TextView posture;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mPostureReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("Posture")) {
                String s = intent.getStringExtra("PostureName");
                posture.setText(s);
                //Toast.makeText(context,posture, Toast.LENGTH_SHORT).show();
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
        PersistenceConfiguration.setPersistenceDirectory(this.getApplicationContext().getFilesDir());
        // get Persistence Service Binder
        setPersistenceConnection();
        Intent intent = new Intent(this, PersistenceService.class);
        boolean b = bindService(intent, mPersistenceConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_main);
        usernameInput = (EditText) findViewById(R.id.editText);
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
                if(s.length() != 0)
                    setNewUsername(s.toString());
            }
        });
        bluttoothButton = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.INVISIBLE);
        b2 = (Button) findViewById(R.id.button2);
        BA = BluetoothAdapter.getDefaultAdapter();
        checkBluethoothActive();
        checkPermissions();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        posture = (TextView) findViewById(R.id.textView2);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPostureReceiver, new IntentFilter("Posture"));
    }


    protected void afterServiceConnection() {
        progressBar.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
        if(mPersistenceService.getUsername()!=null){
            usernameInput.setText(mPersistenceService.getUsername());
        }
    }

    private void setNewUsername(String name){
        if (mPersistenceService != null) {
            mPersistenceService.setUsername(usernameInput.getText().toString());
           }
        }

    private void checkBluethoothActive() {
        // Check and Log BT
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            Log.i("info", "Enabled Bluetooth!");
        }
    }


    // old:start BT service
    // new: stop all Running Services
    public void startService(View v) {
        try {
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            Intent intent2 = new Intent(this, EvaluationBackgroundService.class);
            InitThread init = new InitThread(intent, intent2);
            init.start();
            Toast.makeText(getApplicationContext(), "Start Services", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
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
        mPersistenceService.calibrate();
    }

    // stop all Running Services
    public void stopService(View v) {
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
        unregisterReceiver(mPostureReceiver);

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
                    Log.i("info", "Have to enable Permission: " + currentPerm);
                    ActivityCompat.requestPermissions(this,
                            new String[]{currentPerm},
                            1);

                }
            }
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