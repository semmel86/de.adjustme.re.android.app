package re.adjustme.de.readjustme;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DashboardDayActivity extends MyNavigationActivity {

    private TextView mTextMessage;
    private ProgressBar pB1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_day);


        pB1 = (ProgressBar) findViewById(R.id.progressBar);
        pB1.setMax(100);
        pB1.setProgress(65);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
