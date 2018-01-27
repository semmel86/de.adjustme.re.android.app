package re.adjustme.de.readjustme.Persistence.Entity;

import android.util.Log;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;
import java.util.TimeZone;

import re.adjustme.de.readjustme.Bean.Posture;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;
import re.adjustme.de.readjustme.Predefined.PersistenceType;

/**
 * Wrapper for the Dashboard shown data.
 * <p>
 * Created by semmel on 06.01.2018.
 */
@Persistence(name = "DashboardDataEntity", type = PersistenceType.OBJECT)
public class DashboardDataEntity implements Serializable {

    // aggregated lable-sum (duration)
    HashMap<BodyArea, HashMap<Label, Long>> posture_sum;
    // Timeline: sorted List of Lables with duration
    Stack<Posture> bws_timeline;
    Stack<Posture> hws_timeline;
    Stack<Posture> lws_timeline;
    Stack<Posture> shoulder_timeline;
    private Date date;

    public DashboardDataEntity() {
        this.date = new Date();
        posture_sum = new HashMap<>();
        bws_timeline = new Stack<>();
        hws_timeline = new Stack<>();
        lws_timeline = new Stack<>();
        shoulder_timeline = new Stack<>();
    }

    private void checkDurationRestriction(Posture l) {
        // check if the duration of the last lable > minDuration
        // if not, delete it from stack
        Posture lastLabel = this.getlast(l.getArea());
        if (lastLabel != null) {
            if (lastLabel.getDuration() < PersistenceConfiguration.MIN_POSTURE_DURATION) {
                deleteLast(l.getArea());
            }
        }
        ;
    }

    public void addLabelData(Posture l) {
        BodyArea area = l.getArea();
        if (posture_sum.get(area) == null) {
            posture_sum.put(area, new HashMap<Label, Long>());
        }

        checkDurationRestriction(l);

        switch (area) {
            case SHOULDER:
                addArea(area, shoulder_timeline, l);
                break;
            case SPINE:
                addArea(area, bws_timeline, l);
                break;
            case HWS:
                addArea(area, hws_timeline, l);
                break;
            case LWS:
                addArea(area, lws_timeline, l);
                break;
            default:
                Log.e("Dashboard", "Unkown body area.");

        }
    }

    private void addArea(BodyArea b, Stack<Posture> stack, Posture l) {
        if (posture_sum.get(b).get(l.getLabel()) != null) {
            // accumulate old to sum
            Long currentDuration = posture_sum.get(b).get(l.getLabel());
            currentDuration = currentDuration == null ? 0L : currentDuration;
            Long sumDuration = currentDuration + l.getDuration();
            posture_sum.get(b).put(l.getLabel(), sumDuration);
        } else {
            posture_sum.get(b).put(l.getLabel(), l.getDuration());
        }
        if (stack.size() > 0 && stack.peek().getLabel().getLabel().equals(l.getLabel().getLabel())) {
            stack.peek().setDuration(stack.peek().getDuration() + l.getDuration());
        } else {
            // add new
            stack.push(l);
        }
    }

    public HashMap<Label, Long> getSum(BodyArea b) {
        return posture_sum.get(b);
    }

    public void setSum(HashMap<Label, Long> sum, BodyArea b) {
        this.posture_sum.put(b, sum);
    }

    public Stack<Posture> getShoulder_timeline() {
        return shoulder_timeline;
    }

    public void setShoulder_timeline(Stack<Posture> shoulder_timeline) {
        this.shoulder_timeline = shoulder_timeline;
    }

    public Stack<Posture> getBws_timeline() {
        return bws_timeline;
    }

    public void setBws_timeline(Stack<Posture> bws_timeline) {
        this.bws_timeline = bws_timeline;
    }

    public Stack<Posture> getHws_timeline() {
        return hws_timeline;
    }

    public void setHws_timeline(Stack<Posture> hws_timeline) {
        this.hws_timeline = hws_timeline;
    }

    public Stack<Posture> getLws_timeline() {
        return lws_timeline;
    }

    public void setLws_timeline(Stack<Posture> lws_timeline) {
        this.lws_timeline = lws_timeline;
    }

    public Posture getlast(BodyArea b) {
        switch (b) {
            case SHOULDER:
                if (shoulder_timeline.size() > 0) {
                    return shoulder_timeline.peek();
                } else {
                    return null;
                }
            case SPINE:
                if (bws_timeline.size() > 0) {
                    return bws_timeline.peek();
                } else {
                    return null;
                }
            case HWS:
                if (hws_timeline.size() > 0) {
                    return hws_timeline.peek();
                } else {
                    return null;
                }
            case LWS:
                if (lws_timeline.size() > 0) {
                    return lws_timeline.peek();
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    public void deleteLast(BodyArea b) {
        switch (b) {
            case SHOULDER:
                if (shoulder_timeline.size() > 0) {
                    shoulder_timeline.pop();
                    break;
                }
            case SPINE:
                if (bws_timeline.size() > 0) {
                    bws_timeline.pop();
                    break;
                }
            case HWS:
                if (hws_timeline.size() > 0) {
                    hws_timeline.pop();
                    break;
                }
            case LWS:
                if (lws_timeline.size() > 0) {
                    lws_timeline.pop();
                    break;
                }
        }
    }

    public DashboardDataEntity getDashboardDataSubset(TimeSpan timeSpan) {
        DashboardDataEntity newDashboardData = new DashboardDataEntity();
        newDashboardData.setBws_timeline(adjusteTimeline(this.getBws_timeline(), timeSpan));
        newDashboardData.setShoulder_timeline(adjusteTimeline(this.getShoulder_timeline(), timeSpan));
        newDashboardData.setHws_timeline(adjusteTimeline(this.getHws_timeline(), timeSpan));
        newDashboardData.setLws_timeline(adjusteTimeline(this.getLws_timeline(), timeSpan));
        newDashboardData.setSum(createSum(newDashboardData.getBws_timeline(), BodyArea.SPINE), BodyArea.SPINE);
        newDashboardData.setSum(createSum(newDashboardData.getShoulder_timeline(), BodyArea.SHOULDER), BodyArea.SHOULDER);
        newDashboardData.setSum(createSum(newDashboardData.getHws_timeline(), BodyArea.HWS), BodyArea.HWS);
        newDashboardData.setSum(createSum(newDashboardData.getLws_timeline(), BodyArea.LWS), BodyArea.LWS);
        return newDashboardData;
    }

    private HashMap<Label, Long> createSum(Stack<Posture> timeline, BodyArea b) {
        HashMap<Label, Long> map = new HashMap<>();
        for (Posture l : timeline) {
            if (map.containsKey(l.getLabel())) {
                map.put(l.getLabel(), l.getDuration() + map.get(l.getLabel()));
            } else {
                map.put(l.getLabel(), l.getDuration());
            }
        }
        return map;
    }

    private Stack<Posture> adjusteTimeline(Stack<Posture> timeline, TimeSpan timeSpan) {
        Stack<Posture> helperStack = new Stack<>();
        Stack<Posture> stack = (Stack<Posture>) timeline.clone();
        Timestamp relevantTimestamp = getRelevantTimestamp(timeSpan);
        for (Posture data : stack) {
            if (data.getEnd().after(relevantTimestamp)) {
                helperStack.push(adjustLabelDataDuration(data, relevantTimestamp));
            }
        }

        Stack<Posture> helperStack2 = new Stack<>();
        for (Posture data : helperStack) {
            helperStack2.push(data);
        }
        return helperStack2;
    }

    private Posture adjustLabelDataDuration(Posture data, Timestamp timestamp) {
        if ((data.getEnd().before(timestamp)) || (data.getBegin().after(timestamp))) {
            return data;
        }
        data.setDuration(data.getEnd().getTime() - timestamp.getTime());
        return data;
    }

    private Timestamp getRelevantTimestamp(TimeSpan timeSpan) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(new Date()); // compute start of the day for the timestamp
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        switch (timeSpan) {
            case DAY:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case HOUR:
                break;
            case WEEK:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                break;
        }
        return new Timestamp(cal.getTime().getTime());
    }

    public enum TimeSpan {
        HOUR,
        DAY,
        WEEK
    }
}
