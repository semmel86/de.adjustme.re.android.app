package re.adjustme.de.readjustme.Bean;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import re.adjustme.de.readjustme.Configuration.BodyAreas;

/**
 * Created by semmel on 06.01.2018.
 */
public class DashboardData implements Serializable {

    // aggregated lable-sum (duration)
    HashMap<LabelData, Long> shoulder_sum;
    HashMap<LabelData, Long> spline_sum;
    // Timeline: sorted List of Lables with duration
    Stack<LabelData> shoulder_timeline;
    Stack<LabelData> spline_timeline;
    private Date date;


    public DashboardData() {
        this.date = new Date();
        shoulder_sum = new HashMap<>();
        spline_sum = new HashMap<>();
        shoulder_timeline = new Stack<>();
        spline_timeline = new Stack<>();
    }

    public void addLabelData(LabelData l) {
        LabelData old = null;
        Long oldDuration = null;
        switch (l.getArea()) {
            case SHOULDER:
                // accumulate old to sum
                if (shoulder_timeline.size() > 0) {
                    old = shoulder_timeline.peek();
                    oldDuration = shoulder_sum.get(old);
                    shoulder_sum.put(old, oldDuration + old.getDuration());
                }
                // add new
                this.shoulder_timeline.push(l);
                this.shoulder_sum.put(l, 0L);
            case SPLINE:
                // accumulate old to sum
                if (spline_timeline.size() > 0) {
                    old = spline_timeline.peek();
                    oldDuration = spline_sum.get(old);
                    spline_sum.put(old, oldDuration + old.getDuration());
                }
                // add new
                this.spline_timeline.push(l);
                this.spline_sum.put(l, 0L);
            default:
                Log.e("Dashboard", "Unkown body area.");

        }
    }

    public HashMap<LabelData, Long> getShoulder_sum() {
        return shoulder_sum;
    }

    public void setShoulder_sum(HashMap<LabelData, Long> shoulder_sum) {
        this.shoulder_sum = shoulder_sum;
    }

    public HashMap<LabelData, Long> getSpline_sum() {
        return spline_sum;
    }

    public void setSpline_sum(HashMap<LabelData, Long> spline_sum) {
        this.spline_sum = spline_sum;
    }

    public Stack<LabelData> getShoulder_timeline() {
        return shoulder_timeline;
    }

    public Stack<LabelData> getSpline_timeline() {
        return spline_timeline;
    }


    public LabelData getlast(BodyAreas b) {
        switch (b) {
            case SHOULDER:
                if (shoulder_timeline.size() > 0) {
                    return shoulder_timeline.peek();
                } else {
                    return null;
                }
            case SPLINE:
                if (shoulder_timeline.size() > 0) {
                    return spline_timeline.peek();
                } else {
                    return null;
                }
        }
        return null;
    }
}
