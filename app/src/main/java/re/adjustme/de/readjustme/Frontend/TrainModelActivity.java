package re.adjustme.de.readjustme.Frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import re.adjustme.de.readjustme.R;

/**
 * Created by Semmel on 12.11.2017.
 */

public class TrainModelActivity extends GenericBaseActivity {

    private Button setLabelButton;
    private EditText labelTxt;
    private TextView label;
    private ToggleButton isInLabeledPostion;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void afterServiceConnection() {
        if (mPersistenceService != null) {
            Log.i("Info", "set label text");
            label.setText(mPersistenceService.getLabel());
            isInLabeledPostion.setChecked(mPersistenceService.getIsInLabeledPosition());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set fields
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        // set navigation bar
         setNavigationBar();

        labelTxt = (EditText) findViewById(R.id.editText2);
        isInLabeledPostion = (ToggleButton) findViewById(R.id.toggleButton2);
        label = (TextView) findViewById(R.id.editText3);
        setLabelButton = (Button) findViewById(R.id.button4);

    }

    public void onToggleButtonClick(View v) {
        // switch true/false
        if (isInLabeledPostion.getText().equals("False - not in Position")) {
            //set false
            mPersistenceService.setIsInLabeledPosition(false);
        } else {
            // set true
            mPersistenceService.setIsInLabeledPosition(true);
        }
    }

    public void setLabelTxt(View v) {
        // is labeled = false
        mPersistenceService.setIsInLabeledPosition(false);
        // set new label
        if (labelTxt.getText() != null) {
            mPersistenceService.setLabel(labelTxt.getText().toString());
            label.setText(labelTxt.getText());
            labelTxt.setText("");
        }
    }

}
