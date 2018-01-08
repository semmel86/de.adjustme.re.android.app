package re.adjustme.de.readjustme;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import re.adjustme.de.readjustme.Bean.DashboardData;
import re.adjustme.de.readjustme.Bean.LabelData;
import re.adjustme.de.readjustme.Persistence.internal.ObjectPersistor;

public class DashboardDayActivity extends MyNavigationActivity {

    private TextView mTextMessage;
    private ProgressBar pB1;
    private DashboardData dashboardData;


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

        ObjectPersistor helper = new ObjectPersistor();
        // load Dashboard Object if possible
        dashboardData = (DashboardData) helper.load("DashboardData");
        if (dashboardData == null) {
            dashboardData = new DashboardData();
        }

        //spline


        //shoulder



    }

}
