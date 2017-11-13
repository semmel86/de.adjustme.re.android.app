package re.adjustme.de.readjustme;

import android.content.Intent;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DashboardDayActivity extends MyNavigationActivity {

    private TextView mTextMessage;
    private ProgressBar pB1;
    private ProgressBar pB2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_day);


        pB1 = (ProgressBar) findViewById(R.id.progressBar);
        pB2 = (ProgressBar) findViewById(R.id.progressBar2);
        pB1.setMax(100);
        pB1.setProgress(65);
        pB2.setMax(100);

        pB2.setProgress(65);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
