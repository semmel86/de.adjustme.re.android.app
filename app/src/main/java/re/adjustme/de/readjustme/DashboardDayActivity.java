package re.adjustme.de.readjustme;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.DashboardData;
import re.adjustme.de.readjustme.Persistence.internal.ObjectPersistor;
import re.adjustme.de.readjustme.Predefined.Classification.Label;

public class DashboardDayActivity extends MyNavigationActivity {

    private DashboardData dashboardData;
    private PieChart splinePie;
    private PieChart shoulderPie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_day);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ObjectPersistor helper = new ObjectPersistor();
        // load Dashboard Object if possible
        dashboardData = (DashboardData) helper.load("DashboardData");
        if (dashboardData == null) {
            dashboardData = new DashboardData();
        }

        splinePie = (PieChart) findViewById(R.id.splinePieChart);
        shoulderPie = (PieChart) findViewById(R.id.shoulderPieChart);

        //spline
        addDataToChart(splinePie, dashboardData.getSpline_sum(), getResources().getString(R.string.spline_dashboard));

        //shoulder
        addDataToChart(shoulderPie, dashboardData.getShoulder_sum(), getResources().getString(R.string.shoulder_dashboard));

    }

    private void addDataToChart(PieChart pieChart, HashMap<Label, Long> hashMap, String label) {
        List<PieEntry> entries = new ArrayList<>();

        if (hashMap == null || hashMap.isEmpty()) {
            return;
        }

        for (Label l : hashMap.keySet()) {
            // turn data into Entry objects
            if (hashMap.get(l) > 0) {
                entries.add(new PieEntry(hashMap.get(l), l.getDescription()));
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
        colors.add(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
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
