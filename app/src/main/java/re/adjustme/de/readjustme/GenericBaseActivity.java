package re.adjustme.de.readjustme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;

/**
 * Created by Semmel on 13.11.2017.
 */

public abstract class GenericBaseActivity extends AppCompatActivity {

    protected PersistenceService mPersistenceService = null;
    protected EvaluationBackgroundService mEvaluationBackgroundService = null;
    protected ServiceConnection mEvaluationConnection = null;
    protected ServiceConnection mPersistenceConnection = null;

    protected BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    if (!this.getClass().equals(MainActivity.class)) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    return true;
                case R.id.navigation_dashboard:
                    if (!this.getClass().equals(DashboardDayActivity.class)) {
                        intent = new Intent(getApplicationContext(), DashboardDayActivity.class);
                        startActivity(intent);
                    }
                    return true;
            }
            return false;
        }
    };

    protected void setPersistenceConnection() {
        mPersistenceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PersistenceService.PersistenceServiceBinder b = (PersistenceService.PersistenceServiceBinder) iBinder;
                mPersistenceService = b.getService();
                afterServiceConnection();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mPersistenceService = null;
            }
        };
    }

    protected void setClassificationConnection() {
        mEvaluationConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                EvaluationBackgroundService.EvaluationBackgroundServiceBinder b = (EvaluationBackgroundService.EvaluationBackgroundServiceBinder) iBinder;
                mEvaluationBackgroundService = b.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mPersistenceService = null;
            }
        };
    }


    protected void afterServiceConnection() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_logo_nuricon);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        PersistenceConfiguration.setPersistenceDirectory(this.getApplicationContext().getFilesDir());
        // get Persistence Service Binder
        setPersistenceConnection();
        Intent intent = new Intent(this, PersistenceService.class);
        boolean b = bindService(intent, mPersistenceConnection, Context.BIND_AUTO_CREATE);

        setClassificationConnection();
        Intent intent2 = new Intent(this, EvaluationBackgroundService.class);
        boolean d = bindService(intent2, mEvaluationConnection, Context.BIND_AUTO_CREATE);
    }

}
