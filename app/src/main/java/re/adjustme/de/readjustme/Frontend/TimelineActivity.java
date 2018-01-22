package re.adjustme.de.readjustme.Frontend;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.TimeZone;

import re.adjustme.de.readjustme.Bean.DashboardData;
import re.adjustme.de.readjustme.Bean.LabelData;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Frontend.Component.TimelineBarChartRenderer;
import re.adjustme.de.readjustme.Frontend.Component.TimelineBarChartValueFormatter;
import re.adjustme.de.readjustme.Frontend.Component.MyMarkerView;
import re.adjustme.de.readjustme.R;
import re.adjustme.de.readjustme.Util.Duration;

public class TimelineActivity extends GenericBaseActivity {

    private DashboardData dashboardData = new DashboardData();
    private BarChart splineBar;
    private BarChart shoulderBar;
    private BarChart hwsBar;
    private BarChart lwsBar;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_dashboard);
        setContentView(R.layout.activity_timeline_day);
        splineBar = (BarChart) findViewById(R.id.splineBarChart);
        shoulderBar = (BarChart) findViewById(R.id.shoulderBarChart);
        hwsBar = (BarChart) findViewById(R.id.hwsBarChart);
        lwsBar = (BarChart) findViewById(R.id.lwsBarChart);

        // set navigation bar
        setNavigationBar();

        addDataToBarCharts();
    }

    @Override
    protected void afterServiceConnection() {
        dashboardData = mPersistenceService.getDashboardData();
        addDataToBarCharts();
    }

    private void addDataToBarCharts() {
        String splineBarTitle =  getResources().getString(R.string.spline_dashboard);
        String shoulderBarTitle =  getResources().getString(R.string.shoulder_dashboard);
        String hwsBarTitle =  getResources().getString(R.string.hws_dashboard);
        String lwsBarTitle =  getResources().getString(R.string.lws_dashboard);
        if (dashboardData == null) {
            addDataToChart(splineBar, new Stack<LabelData>(), splineBarTitle);
            addDataToChart(shoulderBar, new Stack<LabelData>(), shoulderBarTitle);
            addDataToChart(hwsBar, new Stack<LabelData>(), hwsBarTitle);
            addDataToChart(lwsBar, new Stack<LabelData>(), lwsBarTitle);
            return;
        }
        addDataToChart(splineBar, dashboardData.getBws_timeline(), splineBarTitle);
        addDataToChart(shoulderBar, dashboardData.getShoulder_timeline(), shoulderBarTitle);
        addDataToChart(hwsBar, dashboardData.getHws_timeline(), hwsBarTitle);
        addDataToChart(lwsBar, dashboardData.getLws_timeline(), lwsBarTitle);

    }
    private void addDataToChart(BarChart barChart, Stack<LabelData> timeline, String label) {
//        Stack<LabelData> timeline = new Stack<>();
      //  Stack<LabelData> adjustedTimelineHelper = new Stack<>();
//        if (timeline != null) {
//            for (LabelData l : timeline) {
//                if (l.getDuration() > PersistenceConfiguration.MIN_POSTURE_DURATION) {
//                    // >2.5 sec
//                    timeline.add(l);
//                }
//            }
//        }
//        while (!timeline.empty()) {
//            LabelData currentData = timeline.peek();
//            if (adjustedTimelineHelper.size() > 0 && adjustedTimelineHelper.peek().getLabel().getLabel().equals(currentData.getLabel().getLabel())){
//                LabelData dataHelper = adjustedTimelineHelper.peek();
//                dataHelper.setDuration(dataHelper.getDuration() + currentData.getDuration());
//                timeline.pop();
//            }else{
//                adjustedTimelineHelper.add(timeline.pop());
//            }
//        }
//
//        for (int i= adjustedTimelineHelper.size() - 1; i>-1; i--){
//            timeline.add(adjustedTimelineHelper.get(i));
//        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        Long xAxisCounter = 0L;
        for (LabelData l : timeline) {
            entries.add(new BarEntry(xAxisCounter, l.getDuration(), l));

            //add color
            long duration = l.getDuration();
            if (duration > 3600000) {
                // >60min
                colors.add(ResourcesCompat.getColor(getResources(), R.color.colorError, null));
            }else if (duration > 600000) {
                // >10min
                colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorHint, null));
            } else {
                // <10min
                colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorHighlight, null));
            }
            xAxisCounter++;
        }



        BarDataSet dataset = new BarDataSet(entries, label);
        //ToDo fix labels
        String[] labels = new String[timeline.size()];
        Timestamp lastTime = null;
        int currentPosition = timeline.size() - 1;
        for (LabelData l : timeline){
            if (lastTime == null){
                labels[currentPosition] = getDate(l);
                lastTime = l.getEnd();
            } else if (isNewDate(lastTime, l.getEnd())){
                labels[currentPosition] = getDate(l);
                lastTime = l.getEnd();
            }else{
                labels[currentPosition] = " ";
            }
            currentPosition--;
        }

        dataset.setColors(colors);
        ViewPortHandler vph = barChart.getViewPortHandler();
        barChart.setRenderer(new TimelineBarChartRenderer(barChart, barChart.getAnimator(), vph));
        dataset.setValueFormatter(new TimelineBarChartValueFormatter(labels));
        BarData data = new BarData(dataset);
        barChart.setData(data);
        barChart.getXAxis().setDrawGridLines(false);

        //remove following line if labels are correct
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setValueFormatter(new DefaultAxisValueFormatter(0) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String time = Duration.millisToDuration((long) value);
                return time;
            }
        });
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setScaleYEnabled(false);
        barChart.setVisibleXRangeMaximum(10);
        barChart.setVisibleXRangeMinimum(4);
        barChart.moveViewToX(timeline.size());
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        IMarker marker = new MyMarkerView(this, R.layout.custom_marker_view);
        barChart.setMarker(marker);
        barChart.invalidate();
    }

    private boolean isNewDate(Timestamp lastTime, Timestamp currentEnd) {
        return lastTime.getTime() - currentEnd.getTime() > 3600000;
    }

    private String getDate(LabelData l) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(l.getBegin());
        SimpleDateFormat format = new SimpleDateFormat("d MMMM - HH");
        return format.format(cal.getTime()) + " Uhr";
    }
}
