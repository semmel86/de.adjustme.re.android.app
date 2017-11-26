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

public abstract class MyNavigationActivity extends AppCompatActivity {

    protected PersistenceService mPersistenceService = null;
    protected ServiceConnection mConnection = null;
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
                case R.id.navigation_bluetooth:
                    if (!this.getClass().equals(BluetoothActivity.class)) {
                        intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                        startActivity(intent);
                    }
                    return true;
            }
            return false;
        }
    };

    private void setConnection() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PersistenceService.PersistenceServiceBinder b = (PersistenceService.PersistenceServiceBinder) iBinder;
                mPersistenceService = b.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mPersistenceService = null;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PersistenceConfiguration.setPersistenceDirectory(this.getApplicationContext().getFilesDir());
        // get Persistence Service Binder
        setConnection();
        Intent intent = new Intent(this, PersistenceService.class);
        boolean b = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

}
