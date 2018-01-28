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

import re.adjustme.de.readjustme.Bean.PostureBean;
import re.adjustme.de.readjustme.Bean.PersistedEntity.DashboardDataEntity;
import re.adjustme.de.readjustme.Frontend.Component.ChartMarkerView;
import re.adjustme.de.readjustme.Frontend.Component.TimelineBarChartRenderer;
import re.adjustme.de.readjustme.Frontend.Component.TimelineBarChartValueFormatter;
import re.adjustme.de.readjustme.R;
import re.adjustme.de.readjustme.Util.Duration;

public class TimelineActivity extends GenericBaseActivity {

    private DashboardDataEntity dashboardDataEntity = new DashboardDataEntity();
    private BarChart mSpineBarChart;
    private BarChart mShoulderBarChart;
    private BarChart mHwsBarChart;
    private BarChart mLwsBarChart;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_timeline);
        setContentView(R.layout.activity_timeline_day);
        mSpineBarChart = (BarChart) findViewById(R.id.splineBarChart);
        mShoulderBarChart = (BarChart) findViewById(R.id.shoulderBarChart);
        mHwsBarChart = (BarChart) findViewById(R.id.hwsBarChart);
        mLwsBarChart = (BarChart) findViewById(R.id.lwsBarChart);

        // set navigation bar
        setNavigationBar();

        addDataToBarCharts();
    }

    @Override
    protected void afterServiceConnection() {
        dashboardDataEntity = mDataAccessService.getmDashboardDataEntity();
        addDataToBarCharts();
    }

    private void addDataToBarCharts() {
        String splineBarTitle = getResources().getString(R.string.spline_dashboard);
        String shoulderBarTitle = getResources().getString(R.string.shoulder_dashboard);
        String hwsBarTitle = getResources().getString(R.string.hws_dashboard);
        String lwsBarTitle = getResources().getString(R.string.lws_dashboard);
        if (dashboardDataEntity == null) {
            addDataToChart(mSpineBarChart, new Stack<PostureBean>(), splineBarTitle);
            addDataToChart(mShoulderBarChart, new Stack<PostureBean>(), shoulderBarTitle);
            addDataToChart(mHwsBarChart, new Stack<PostureBean>(), hwsBarTitle);
            addDataToChart(mLwsBarChart, new Stack<PostureBean>(), lwsBarTitle);
            return;
        }
        addDataToChart(mSpineBarChart, dashboardDataEntity.getBws_timeline(), splineBarTitle);
        addDataToChart(mShoulderBarChart, dashboardDataEntity.getShoulder_timeline(), shoulderBarTitle);
        addDataToChart(mHwsBarChart, dashboardDataEntity.getHws_timeline(), hwsBarTitle);
        addDataToChart(mLwsBarChart, dashboardDataEntity.getLws_timeline(), lwsBarTitle);

    }

    private void addDataToChart(BarChart barChart, Stack<PostureBean> timeline, String label) {
        Stack<PostureBean> adjustedTimelineHelper = new Stack<>();

        adjustedTimelineHelper = timeline;
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        Long xAxisCounter = 0L;
        for (PostureBean l : adjustedTimelineHelper) {
            entries.add(new BarEntry(xAxisCounter, l.getDuration(), l));

            //add color
            long duration = l.getDuration();
            if (duration > 3600000) {
                // >60min
                colors.add(ResourcesCompat.getColor(getResources(), R.color.colorError, null));
            } else if (duration > 600000) {
                // >10min
                colors.add(ResourcesCompat.getColor(getResources(), R.color.calendarTextColor, null));
            } else {
                // <10min
                colors.add(ResourcesCompat.getColor(getResources(), R.color.textColorHighlight, null));
            }
            xAxisCounter++;
        }


        BarDataSet dataset = new BarDataSet(entries, label);
        //ToDo fix labels
        String[] labels = new String[adjustedTimelineHelper.size()];
        Timestamp lastTime = null;
        int currentPosition = adjustedTimelineHelper.size() - 1;
        for (PostureBean l : adjustedTimelineHelper) {
            if (lastTime == null) {
                labels[currentPosition] = getDate(l);
                lastTime = l.getEnd();
            } else if (isNewDate(lastTime, l.getEnd())) {
                labels[currentPosition] = getDate(l);
                lastTime = l.getEnd();
            } else {
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
        barChart.moveViewToX(adjustedTimelineHelper.size());
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        IMarker marker = new ChartMarkerView(this, R.layout.custom_marker_view);
        barChart.setMarker(marker);
        barChart.invalidate();
    }

    private boolean isNewDate(Timestamp lastTime, Timestamp currentEnd) {
        return lastTime.getTime() - currentEnd.getTime() > 3600000;
    }

    private String getDate(PostureBean l) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(l.getBegin());
        SimpleDateFormat format = new SimpleDateFormat("d MMMM - HH");
        return format.format(cal.getTime()) + " Uhr";
    }
}
