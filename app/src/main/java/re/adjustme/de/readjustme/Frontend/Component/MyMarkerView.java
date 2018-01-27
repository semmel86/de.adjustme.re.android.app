package re.adjustme.de.readjustme.Frontend.Component;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;

import re.adjustme.de.readjustme.Bean.Posture;
import re.adjustme.de.readjustme.R;
import re.adjustme.de.readjustme.Util.Duration;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private MPPointF mOffset;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        // find your layout components
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long millis = (long) e.getY();
        String s = "";
        if (e.getData() instanceof Posture) {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM - HH:mm");
            s = format.format(((Posture) e.getData()).getBegin());
            s += " - ";
        }
        s += Duration.millisToDuration(millis);
        tvContent.setText(s);

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }


    @Override
    public MPPointF getOffset() {

        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}
