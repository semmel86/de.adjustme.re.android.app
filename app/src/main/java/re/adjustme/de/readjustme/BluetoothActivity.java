package re.adjustme.de.readjustme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.BluetoothConfiguration;
import re.adjustme.de.readjustme.Service.BluetoothService;

/**
 * Created by Semmel on 12.11.2017.
 */

public class BluetoothActivity extends MyNavigationActivity {

    private Button setLabelButton;
    private EditText labelTxt;
    private TextView label;
    private ToggleButton isInLabeledPostion;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume(){
        super.onResume();
    }
@Override
    protected void afterServiceConnection(){
    if(mPersistenceService!=null) {
        Log.i("Info","set label text");
        label.setText(mPersistenceService.getLabel());
        isInLabeledPostion.setChecked(mPersistenceService.getIsInLabeledPosition());
    }
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set fields
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_data);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        labelTxt = (EditText) findViewById(R.id.editText2);
        isInLabeledPostion = (ToggleButton) findViewById(R.id.toggleButton2);
        label = (TextView) findViewById(R.id.editText3);
        setLabelButton = (Button) findViewById(R.id.button4);

    }

    public void onToggleButtonClick(View v){
        // switch true/false
        if(isInLabeledPostion.getText().equals("False - not in Position")){
            //set false
            mPersistenceService.setIsInLabeledPosition(false);
        }else{
            // set true
            mPersistenceService.setIsInLabeledPosition(true);
        }
    }

    public void setLabelTxt(View v){
        // is labeled = false
        mPersistenceService.setIsInLabeledPosition(false);
        // set new label
        if(labelTxt.getText()!=null) {
            mPersistenceService.setLabel(labelTxt.getText().toString());
            label.setText(labelTxt.getText());
            labelTxt.setText("");
        }
    }

}
