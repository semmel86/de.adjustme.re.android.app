package re.adjustme.de.readjustme.Frontend.Component;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Stefan on 21.01.2018.
 */

public class TimelineBarChartRenderer extends BarChartRenderer {


    public TimelineBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
        mValuePaint.setColor(color);
        mValuePaint.setTextSize(28);
        MPPointF pointF = MPPointF.getInstance(0, 0);
        String text = formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler);
        if (text.contains("\n")) {
            String[] separated = text.split("\n");
            int currentPosition = 0;
            for (String s : separated) {
                Utils.drawXAxisValue(c, s.trim(), x + (currentPosition * 30), 100, mValuePaint, pointF, 90);
                currentPosition++;
            }
        } else {
            Utils.drawXAxisValue(c, text, x, 120, mValuePaint, pointF, 90);
        }
    }
}