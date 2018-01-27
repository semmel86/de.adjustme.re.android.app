package re.adjustme.de.readjustme.Frontend;

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
import android.view.View;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.R;

/**
 * Created by Semmel on 13.11.2017.
 */

public abstract class GenericBaseActivity extends AppCompatActivity {

    protected DataAccessService mDataAccessService = null;
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
                case R.id.navigation_train:
                    if (!this.getClass().equals(TrainModelActivity.class)) {
                        intent = new Intent(getApplicationContext(), TrainModelActivity.class);
                        startActivity(intent);
                    }
                    return true;
                case R.id.navigation_timeline:
                    if (!this.getClass().equals(TimelineActivity.class)) {
                        intent = new Intent(getApplicationContext(), TimelineActivity.class);
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
                DataAccessService.PersistenceServiceBinder b = (DataAccessService.PersistenceServiceBinder) iBinder;
                mDataAccessService = b.getService();
                afterServiceConnection();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mDataAccessService = null;
            }
        };
    }

    protected void afterServiceConnection() {
        // declare methods that should be called after persitence connection here
        // in derived classes
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unbind service
        unbindService(mPersistenceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_logo_nuricon);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        addPersistence();
    }

    protected void addPersistence() {
        PersistenceConfiguration.setPersistenceDirectory(this.getApplicationContext().getFilesDir());
        // get Persistence Service Binder
        setPersistenceConnection();
        Intent intent = new Intent(this, DataAccessService.class);
        boolean b = bindService(intent, mPersistenceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void setNavigationBar() {
        BottomNavigationView navigation = null;
        BottomNavigationView deactivatedNavigation = null;
        if (PersistenceConfiguration.MODE_DEVELOPMENT) {
            navigation = (BottomNavigationView) findViewById(R.id.navigation_developer);
            navigation.setVisibility(View.VISIBLE);
            deactivatedNavigation = (BottomNavigationView) findViewById(R.id.navigation);
            deactivatedNavigation.setVisibility(View.INVISIBLE);
        } else {
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setVisibility(View.VISIBLE);
            deactivatedNavigation = (BottomNavigationView) findViewById(R.id.navigation_developer);
            deactivatedNavigation.setVisibility(View.INVISIBLE);
        }

        if (this.getClass().equals(MainActivity.class)) {
            navigation.setSelectedItemId(R.id.navigation_home);
        } else if (this.getClass().equals(DashboardDayActivity.class)) {
            navigation.setSelectedItemId(R.id.navigation_dashboard);
        } else if (this.getClass().equals(TrainModelActivity.class)) {
            navigation.setSelectedItemId(R.id.navigation_train);
        } else if (this.getClass().equals(TimelineActivity.class)) {
            navigation.setSelectedItemId(R.id.navigation_timeline);
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
