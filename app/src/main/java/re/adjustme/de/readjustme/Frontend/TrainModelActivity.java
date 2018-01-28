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

    private Button mSetLabelButton;
    private EditText mLabelTxt;
    private TextView mLabelView;
    private ToggleButton mIsInLabeledPostionBtn;


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
        if (mDataAccessService != null) {
            Log.i("Info", "set mLabelView text");
            mLabelView.setText(mDataAccessService.getLabel());
            mIsInLabeledPostionBtn.setChecked(mDataAccessService.getIsInLabeledPosition());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set fields
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        // set navigation bar
        setNavigationBar();

        mLabelTxt = (EditText) findViewById(R.id.editText2);
        mIsInLabeledPostionBtn = (ToggleButton) findViewById(R.id.toggleButton2);
        mLabelView = (TextView) findViewById(R.id.editText3);
        mSetLabelButton = (Button) findViewById(R.id.button4);

    }

    public void onToggleButtonClick(View v) {
        // switch true/false
        if (mIsInLabeledPostionBtn.getText().equals("False - not in Position")) {
            //set false
            mDataAccessService.setIsInLabeledPosition(false);
        } else {
            // set true
            mDataAccessService.setIsInLabeledPosition(true);
        }
    }

    public void setmLabelTxt(View v) {
        // is labeled = false
        mDataAccessService.setIsInLabeledPosition(false);
        // set new mLabelView
        if (mLabelTxt.getText() != null) {
            mDataAccessService.setLabel(mLabelTxt.getText().toString());
            mLabelView.setText(mLabelTxt.getText());
            mLabelTxt.setText("");
        }
    }

}
