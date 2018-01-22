package re.adjustme.de.readjustme.Frontend;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
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
import re.adjustme.de.readjustme.Frontend.Component.MyMarkerView;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;
import re.adjustme.de.readjustme.R;

public class DashboardDayActivity extends GenericBaseActivity {

    private DashboardData dashboardData = new DashboardData();
    private PieChart splinePie;
    private PieChart shoulderPie;
    private PieChart hwsPie;
    private PieChart lwsPie;
    private RadioGroup radioGroup;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_dashboard);
        setContentView(R.layout.activity_dashboard_day);
        splinePie = (PieChart) findViewById(R.id.splinePieChart);
        shoulderPie = (PieChart) findViewById(R.id.shoulderPieChart);
        hwsPie = (PieChart) findViewById(R.id.hwsPieChart);
        lwsPie = (PieChart) findViewById(R.id.lwsPieChart);

        radioGroup = (RadioGroup) findViewById(R.id.postureProfileRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                radioGroupCheckedChanged();
            }
        });
        radioGroup.check(R.id.radio_all);

        // set navigation bar
        setNavigationBar();
    }

    private void radioGroupCheckedChanged() {
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
        addDataToChart(splinePie, newDashboardData.getSum(BodyArea.SPLINE), getResources().getString(R.string.spline_dashboard));
        //shoulder
        addDataToChart(shoulderPie, newDashboardData.getSum(BodyArea.SHOULDER), getResources().getString(R.string.shoulder_dashboard));
        //hws
        addDataToChart(hwsPie, newDashboardData.getSum(BodyArea.HWS), getResources().getString(R.string.hws_dashboard));
        //lws
        addDataToChart(lwsPie, newDashboardData.getSum(BodyArea.LWS), getResources().getString(R.string.lws_dashboard));
    }

    @Override
    protected void afterServiceConnection() {
        dashboardData = mPersistenceService.getDashboardData();
        radioGroupCheckedChanged();
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

        IMarker marker = new MyMarkerView(this, R.layout.custom_marker_view);
        pieChart.setMarker(marker);
    }
}
