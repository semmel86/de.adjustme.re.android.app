package re.adjustme.de.readjustme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import re.adjustme.de.readjustme.Bean.DashboardData;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Predefined.Classification.Label;

public class DashboardDayActivity extends MyNavigationActivity {

    private DashboardData dashboardData = new DashboardData();
    private PieChart splinePie;
    private PieChart shoulderPie;
    private RadioGroup radioGroup;
    private PersistenceService mPersistenceService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PersistenceConfiguration.setPersistenceDirectory(this.getApplicationContext().getFilesDir());
        // get Persistence Service Binder
        setPersistenceConnection();
        Intent intent = new Intent(this, PersistenceService.class);
        boolean b = bindService(intent, mPersistenceConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_dashboard_day);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        splinePie = (PieChart) findViewById(R.id.splinePieChart);
        shoulderPie = (PieChart) findViewById(R.id.shoulderPieChart);

        radioGroup = (RadioGroup) findViewById(R.id.postureProfileRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                radioGroupCheckedChanged();
            }
        });
        RadioButton all = (RadioButton) findViewById(R.id.radio_all);
        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioButtonChangedListener(compoundButton, b);
            }
        });
        RadioButton week = (RadioButton) findViewById(R.id.radio_week);
        week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioButtonChangedListener(compoundButton, b);
            }
        });
        RadioButton day = (RadioButton) findViewById(R.id.radio_day);
        day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioButtonChangedListener(compoundButton, b);
            }
        });
        RadioButton hour = (RadioButton) findViewById(R.id.radio_hour);
        hour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioButtonChangedListener(compoundButton, b);
            }
        });

        radioGroup.check(R.id.radio_all);

    }

    private void radioGroupCheckedChanged() {
        if (mPersistenceService != null) {
            dashboardData = mPersistenceService.getDashboardData();
        }
        DashboardData newDashboardData = new DashboardData();
        //find selected radio button
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_all:
                newDashboardData = dashboardData;
                break;
            case R.id.radio_week:
                newDashboardData = dashboardData.getDashboardDataSubset(DashboardData.TimeSpan.WEEK);
                break;
            case R.id.radio_day:
                newDashboardData = dashboardData.getDashboardDataSubset(DashboardData.TimeSpan.DAY);
                break;
            case R.id.radio_hour:
                newDashboardData = dashboardData.getDashboardDataSubset(DashboardData.TimeSpan.HOUR);
                break;
        }


        //spline
        addDataToChart(splinePie, newDashboardData.getSpline_sum(), getResources().getString(R.string.spline_dashboard));
        //shoulder
        addDataToChart(shoulderPie, newDashboardData.getShoulder_sum(), getResources().getString(R.string.shoulder_dashboard));
    }

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

    protected void afterServiceConnection() {
        radioGroupCheckedChanged();
    }

    private void radioButtonChangedListener(CompoundButton button, boolean isChecked) {
        if (isChecked) {

            //Make the text underlined
            SpannableString content = new SpannableString(button.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            button.setText(content);
        } else {
            //Change the color here and make the Text bold
            SpannableString content = new SpannableString(button.getText().toString());
            content.setSpan(null, 0, content.length(), 0);
            button.setText(content);
        }
    }

    private void addDataToChart(PieChart pieChart, HashMap<Label, Long> hashMap, String label) {
        List<PieEntry> entries = new ArrayList<>();

        if (hashMap != null && !hashMap.isEmpty()) {
            //sort HashMap
            Set<Map.Entry<Label, Long>> set = hashMap.entrySet();
            List<Map.Entry<Label, Long>> list = new ArrayList<Map.Entry<Label, Long>>(set);
            Collections.sort(list, new Comparator<Map.Entry<Label, Long>>() {
                public int compare(Map.Entry<Label, Long> o1, Map.Entry<Label, Long> o2) {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            });

            for (Map.Entry<Label, Long> l : list) {
                if (l.getValue() > 0) {
                    entries.add(new PieEntry(l.getValue(), l.getKey().getDescription()));
                }
            }
        }

        //create pie data set
        PieDataSet pieDataSet = new PieDataSet(entries, label);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setValueFormatter(new PercentFormatter());

        //add colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorHighlight, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.keyTextColor, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorPrimary, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorTertiary, null));
        //TODO add list
        pieDataSet.setColors(colors);

        pieChart.setEntryLabelTextSize(7f);
        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(ResourcesCompat.getColor(getResources(), R.color.textColorPrimary, null));
        pieChart.setData(new PieData(pieDataSet));
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterText(label);
        pieChart.invalidate();
    }
}
