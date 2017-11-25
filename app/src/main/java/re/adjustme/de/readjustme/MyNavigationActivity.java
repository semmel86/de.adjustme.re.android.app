package re.adjustme.de.readjustme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by Semmel on 13.11.2017.
 */

public abstract class MyNavigationActivity extends AppCompatActivity {

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

}
