package re.adjustme.de.readjustme.Bean;

import android.support.v7.util.SortedList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

/**
 * Created by semmel on 06.01.2018.
 */
public class DashboardData {
    private Date date;

    // aggregated lable-sum (duration)
    HashMap<LabelData,Long> shoulder_sum;
    HashMap<LabelData,Long> spline_sum;

    // Timeline: sorted List of Lables with duration
    Queue<LabelData> shoulder_timeline;
    Queue<LabelData> spline_timeline;



    public DashboardData(){
        this.date=new Date();
        shoulder_sum=new HashMap<>();
        spline_sum=new HashMap<>();
        shoulder_timeline=new ArrayDeque<>();
        spline_timeline=new ArrayDeque<>();
    }

    public void addLabelData(LabelData l){
        if(l.getArea()=="Shoulder"){
            // TODO
        }else{
            // TODO
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

    public Queue<LabelData> getShoulder_timeline() {
        return shoulder_timeline;
    }

    public void setShoulder_timeline(Queue<LabelData> shoulder_timeline) {
        this.shoulder_timeline = shoulder_timeline;
    }

    public Queue<LabelData> getSpline_timeline() {
        return spline_timeline;
    }

    public void setSpline_timeline(Queue<LabelData> spline_timeline) {
        this.spline_timeline = spline_timeline;
    }
}
