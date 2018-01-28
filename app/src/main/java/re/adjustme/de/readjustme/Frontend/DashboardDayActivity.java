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

import re.adjustme.de.readjustme.Bean.PersistedEntity.DashboardDataEntity;
import re.adjustme.de.readjustme.Frontend.Component.ChartMarkerView;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;
import re.adjustme.de.readjustme.R;

public class DashboardDayActivity extends GenericBaseActivity {

    private DashboardDataEntity mDashboardDataEntity = new DashboardDataEntity();
    private PieChart mSpinePieChart;
    private PieChart mShoulderPieChart;
    private PieChart mHwsPieChart;
    private PieChart mLwsPieChart;
    private RadioGroup mRadioGroup;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_dashboard);
        setContentView(R.layout.activity_dashboard_day);
        mSpinePieChart = (PieChart) findViewById(R.id.spinePieChart);
        mShoulderPieChart = (PieChart) findViewById(R.id.shoulderPieChart);
        mHwsPieChart = (PieChart) findViewById(R.id.hwsPieChart);
        mLwsPieChart = (PieChart) findViewById(R.id.lwsPieChart);

        mRadioGroup = (RadioGroup) findViewById(R.id.postureProfileRadioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                radioGroupCheckedChanged();
            }
        });
        mRadioGroup.check(R.id.radio_all);

        // set navigation bar
        setNavigationBar();
    }

    private void radioGroupCheckedChanged() {
        DashboardDataEntity newDashboardData = new DashboardDataEntity();
        //find selected radio button
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_all:
                newDashboardData = mDashboardDataEntity;
                break;
            case R.id.radio_week:
                newDashboardData = mDashboardDataEntity.getDashboardDataSubset(DashboardDataEntity.TimeSpan.WEEK);
                break;
            case R.id.radio_day:
                newDashboardData = mDashboardDataEntity.getDashboardDataSubset(DashboardDataEntity.TimeSpan.DAY);
                break;
            case R.id.radio_hour:
                newDashboardData = mDashboardDataEntity.getDashboardDataSubset(DashboardDataEntity.TimeSpan.HOUR);
                break;
        }

        //spline
        addDataToChart(mSpinePieChart, newDashboardData.getSum(BodyArea.SPINE), getResources().getString(R.string.spline_dashboard));
        //shoulder
        addDataToChart(mShoulderPieChart, newDashboardData.getSum(BodyArea.SHOULDER), getResources().getString(R.string.shoulder_dashboard));
        //hws
        addDataToChart(mHwsPieChart, newDashboardData.getSum(BodyArea.HWS), getResources().getString(R.string.hws_dashboard));
        //lws
        addDataToChart(mLwsPieChart, newDashboardData.getSum(BodyArea.LWS), getResources().getString(R.string.lws_dashboard));
    }

    @Override
    protected void afterServiceConnection() {
        mDashboardDataEntity = mDataAccessService.getmDashboardDataEntity();
        radioGroupCheckedChanged();
    }

    private void addDataToChart(PieChart pieChart, HashMap<Label, Long> hashMap, String label) {
        pieChart.highlightValue(0, -1);
        List<PieEntry> entries = new ArrayList<>();
        if (hashMap != null && !hashMap.isEmpty()) {
            //sort HashMap
            Set<Map.Entry<Label, Long>> set = hashMap.entrySet();
            List<Map.Entry<Label, Long>> list = new ArrayList<Map.Entry<Label, Long>>(set);
            Long overallDuration = 0L;
            for (Map.Entry<Label, Long> l : list) {
                overallDuration += l.getValue();
            }
            Collections.sort(list, new Comparator<Map.Entry<Label, Long>>() {
                public int compare(Map.Entry<Label, Long> o1, Map.Entry<Label, Long> o2) {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            });


            Long restDuration = 0L;
            for (Map.Entry<Label, Long> l : list) {
                if (l.getValue() > ((overallDuration * 5) / 100)) {
                    entries.add(new PieEntry(l.getValue(), l.getKey().getDescription()));
                } else {
                    restDuration += l.getValue();
                }
            }
            if (restDuration.compareTo(0L) > 0) {
                entries.add(new PieEntry(restDuration, getResources().getString(R.string.dashboard_rest)));
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
        colors.add(ResourcesCompat.getColor(getResources(), R.color.activatedHighlight, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorSecondary, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorTertiary, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.calendarTextColor, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.colorForeground, null));
        colors.add(ResourcesCompat.getColor(getResources(), R.color.activatedHighlight, null));

        pieDataSet.setColors(colors);

        IMarker marker = new ChartMarkerView(this, R.layout.custom_marker_view);
        pieChart.setMarker(marker);

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
