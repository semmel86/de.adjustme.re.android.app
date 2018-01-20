package re.adjustme.de.readjustme.Bean;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Sensor;
import re.adjustme.de.readjustme.Prediction.internal.svm;
import re.adjustme.de.readjustme.Prediction.internal.svm_node;
import re.adjustme.de.readjustme.Util.Distance;

/**
 * Transfer object for Motion data.
 * <p>
 * Created by semmel on 03.12.2017.
 */

public class MotionDataSetDto implements Serializable {
    public double probability = 0;
    public String predictedLable = "";
    private MotionData[] motionDataSet;
    private String label = "";
    private String svmClass = "0";
    private boolean isInLabeledPostion = false;
    private final static String sep = " ";
    private final static String pair = ":";

    public MotionDataSetDto() {
        // at least on place for each last sensors MotionData
        motionDataSet = new MotionData[Sensor.values().length];
        for (Sensor s : Sensor.values()) {
            // fill with dummy data
            motionDataSet[s.getSensorNumber() - 1] = new MotionData();
        }

    }

    public void update(MotionData md) {
        // update MotionData
        motionDataSet[md.getSensor().getSensorNumber() - 1] = md;
        //update Label information
        label = md.getLabel();
        isInLabeledPostion = md.getInLabeledPosition();
    }

    public MotionData[] getMotionDataSet() {
        MotionData[] motionDataSetCopy = Arrays.copyOf(this.motionDataSet, this.motionDataSet.length);

        return motionDataSetCopy;
    }

    public void setSvmClass(String classNum) {
        this.svmClass = classNum;
    }

    public MotionData getMotion(Sensor s) {
        return this.motionDataSet[s.getSensorNumber() - 1].clone();
    }

    // special format for saving as csv file
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Sensor sensor : Sensor.values()) {
            MotionData md = motionDataSet[sensor.getSensorNumber() - 1];
            s.append(sensor.getSensorNumber());
            s.append(PersistenceConfiguration.CSV_SEPARATOR);
            s.append(md.getBegin());
            s.append(PersistenceConfiguration.CSV_SEPARATOR);
            s.append(md.getDuration());
            s.append(PersistenceConfiguration.CSV_SEPARATOR);
            s.append(md.getX());
            s.append(PersistenceConfiguration.CSV_SEPARATOR);
            s.append(md.getY());
            s.append(PersistenceConfiguration.CSV_SEPARATOR);
            s.append(md.getZ());
            s.append(PersistenceConfiguration.CSV_SEPARATOR);
        }
        s.append(label);
        s.append(PersistenceConfiguration.CSV_SEPARATOR);
        s.append(isInLabeledPostion);

        return s.toString();
    }

    private String getDistanceFeatures(BodyArea area, int i) {
        StringBuilder svmLightString = new StringBuilder();

        // just cache of Sensors we already calculated with
        List<Sensor> markedSensors = new ArrayList<>();
        for (Sensor s : Sensor.values()) {
            // only calculate distances, if this sensor is part
            // of the current area!
            MotionData curr = motionDataSet[s.getSensorNumber() - 1];
            if (area.containsSensors(s)) {
                // calc the distance to every already marked sensor
                for (Sensor m : markedSensors) {

                    MotionData marked = motionDataSet[m.getSensorNumber() - 1];
                    double distance = getDistance(curr, marked, m);
                    // append "i:dist "
                    svmLightString.append(i);
                    svmLightString.append(pair);
                    svmLightString.append(distance);
                    svmLightString.append(sep);

                    i++;
                }
                markedSensors.add(s);
            }
        }
        return svmLightString.toString();
    }

    private double getDistance(MotionData curr, MotionData marked, Sensor s) {
        double distance = 0;
        // if z is excluded, distance (x,y)
        if (s.isExclude_z()) {
            distance = Distance.getEuclideanDistance(curr.getX(), marked.getX(), curr.getY(), marked.getY());
        }
        // else distance (x,y,z)
        else {
            distance = Distance.getEuclideanDistance(curr.getX(), marked.getX(), curr.getY(), marked.getY(), curr.getZ(), marked.getZ());
        }
        return distance;
    }

    // this method sets the features for svm training!
    public String toSVMLightStr(BodyArea area) {
        // only if we are in position!
        if (this.isInLabeledPostion) {

            final StringBuilder s = new StringBuilder();
            s.append(svmClass);
            s.append(sep);
            int i = 1;
            if(ClassificationConfiguration.RAW_VALUES) {
                for (final Sensor sensor : Sensor.values()) {
                    if (area.containsSensors(sensor)) {
                        final MotionData md = this.motionDataSet[sensor.getSensorNumber() - 1];
                        if (!sensor.isExclude_x()) {
                            // x
                            s.append(i);
                            s.append(pair);
                            s.append(md.getX());
                            s.append(sep);
                            i++;
                        }
                        if (!sensor.isExclude_y()) {
                            // y
                            s.append(i);
                            s.append(pair);
                            s.append(md.getY());
                            s.append(sep);
                            i++;
                        }
                        if (!sensor.isExclude_z()) {
                            // z
                            s.append(i);
                            s.append(pair);
                            s.append(md.getZ());
                            s.append(sep);
                            i++;
                        }
                    }
                }
            }

            if(ClassificationConfiguration.DISTANCE_VALUES){
                s.append(getDistanceFeatures(area,i));
            }
            return s.toString();
        } else {
            return "";
        }
    }

    public svm_node[] getsvmNodes(BodyArea area){
        // calc amount of features -> one node for each feature
        int c = 0;
        if(ClassificationConfiguration.RAW_VALUES) {
            for (Sensor s : Sensor.values()) {
                if (area.containsSensors(s)) {
                    if (!s.isExclude_x()) {
                        c++;
                    }
                    if (!s.isExclude_y()) {
                        c++;
                    }
                    if (!s.isExclude_z()) {
                        c++;
                    }
                }
            }
        }
        if(ClassificationConfiguration.DISTANCE_VALUES){
            switch (area.getSensorSet().length) {
                case 2:
                    c += 1;
                    break;
                case 3:
                    c += 3;
                    break;
                case 4:
                    c += 6;
                    break;
                case 5:
                    c += 10;
                    break;
            }

        }
        // init node array
        final svm_node[] x = new svm_node[c];
        c = 0;
        // fill nodes with features first raw
        if(ClassificationConfiguration.RAW_VALUES) {
        for (Sensor s : Sensor.values()) {
            // x
            if (area.containsSensors(s)) {
                if (!s.isExclude_x()) {
                    x[c] = new svm_node();
                    x[c].index = c;
                    x[c].value = getMotion(s).getX();
                    c++;
                }
                // y
                if (!s.isExclude_y()) {
                    x[c] = new svm_node();
                    x[c].index = c;
                    x[c].value = getMotion(s).getY();
                    c++;
                }
                // z
                if (!s.isExclude_z()) {
                    x[c] = new svm_node();
                    x[c].index = c;
                    x[c].value = getMotion(s).getZ();
                    c++;
                }
            }
        }}
        // then distance
        if(ClassificationConfiguration.DISTANCE_VALUES){
            List<Sensor> markedSensors = new ArrayList<>();
            for (Sensor s : Sensor.values()) {
                // only calculate distances, if this sensor is part
                // of the current area!
                MotionData curr = motionDataSet[s.getSensorNumber() - 1];
                if (area.containsSensors(s)) {
                    // calc the distance to every already marked sensor
                    for (Sensor m : markedSensors) {

                        MotionData marked = motionDataSet[m.getSensorNumber() - 1];
                        double distance = getDistance(curr, marked, m);
                        x[c] = new svm_node();
                        x[c].index = c;
                        x[c].value = distance;
                        c++;
                    }
                    markedSensors.add(s);
                }

            }
        }

        return x;
    }
    public void addMotionData(MotionData[] md) {
        motionDataSet = md;
    }

    public void setLable(String lable) {
        this.label = lable;
    }

    public void setInLabledPos(boolean b) {
        this.isInLabeledPostion = b;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean getInPosition() {
        return this.isInLabeledPostion;
    }

    public JSONObject getJson() {

        // Here we convert Java Object to JSON
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("label", this.getLabel());
            jsonObj.put("prediction", this.predictedLable);
            jsonObj.put("inPosition", this.isInLabeledPostion);
            int i = 1;
            for (Sensor s : Sensor.values()) {
                MotionData md = motionDataSet[s.getSensorNumber() - 1];
                jsonObj.put("s" + i, s.getSensorNumber());
                jsonObj.put("t" + i, md.getBegin());
                jsonObj.put("d" + i, md.getDuration());
                jsonObj.put("x" + i, md.getX());
                jsonObj.put("y" + i, md.getY());
                jsonObj.put("z" + i, md.getZ());
                i++;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return jsonObj;
    }
}
