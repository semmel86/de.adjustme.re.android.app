package re.adjustme.de.readjustme.Bean;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;

/**
 * Wrapper for the Dashboard shown data.
 *
 * Created by semmel on 06.01.2018.
 */
public class DashboardData implements Serializable {

    // aggregated lable-sum (duration)
    HashMap<BodyArea, HashMap<Label, Long>> posture_sum;
    // Timeline: sorted List of Lables with duration
    Stack<LabelData> shoulder_timeline;
    Stack<LabelData> spline_timeline;
    private Date date;


    public DashboardData() {
        this.date = new Date();
        posture_sum = new HashMap<>();
        shoulder_timeline = new Stack<>();
        spline_timeline = new Stack<>();
    }

    public void addLabelData(LabelData l) {
        Label old = null;
        Long oldDuration = null;
        BodyArea area = l.getArea();
        if (posture_sum.get(area) == null) {
            posture_sum.put(area, new HashMap<Label, Long>());
        }
        switch (area) {
            case SHOULDER:
                // accumulate old to sum
                if (shoulder_timeline.size() > 0) {
                    old = shoulder_timeline.peek().getLabel();
                    oldDuration = posture_sum.get(area).get(old);
                    posture_sum.get(area).put(old, oldDuration + shoulder_timeline.peek().getDuration());
                }
                // add new
                this.shoulder_timeline.push(l);
                this.posture_sum.get(area).put(l.getLabel(), 0L);
                break;
            case SPLINE:
                // accumulate old to sum
                if (spline_timeline.size() > 0) {
                    old = spline_timeline.peek().getLabel();
                    oldDuration = posture_sum.get(area).get(old);
                    posture_sum.get(area).put(old, oldDuration + shoulder_timeline.peek().getDuration());
                }
                // add new
                this.spline_timeline.push(l);
                this.posture_sum.get(area).put(l.getLabel(), 0L);
                break;
            default:
                Log.e("Dashboard", "Unkown body area.");

        }
    }

    public HashMap<Label, Long> getShoulder_sum() {
        return posture_sum.get(BodyArea.SHOULDER);
    }

    public void setShoulder_sum(HashMap<Label, Long> shoulder_sum) {
        this.posture_sum.put(BodyArea.SHOULDER, shoulder_sum);
    }

    public HashMap<Label, Long> getSpline_sum() {
        return posture_sum.get(BodyArea.SPLINE);
    }

    public void setSpline_sum(HashMap<Label, Long> spline_sum) {
        this.posture_sum.put(BodyArea.SPLINE, spline_sum);
    }

    public Stack<LabelData> getShoulder_timeline() {
        return shoulder_timeline;
    }

    public Stack<LabelData> getSpline_timeline() {
        return spline_timeline;
    }


    public LabelData getlast(BodyArea b) {
        switch (b) {
            case SHOULDER:
                if (shoulder_timeline.size() > 0) {
                    return shoulder_timeline.peek();
                } else {
                    return null;
                }
            case SPLINE:
                if (spline_timeline.size() > 0) {
                    return spline_timeline.peek();
                } else {
                    return null;
                }
        }
        return null;
    }
}
