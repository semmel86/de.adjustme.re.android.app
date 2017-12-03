package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;
import java.util.Arrays;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;

/**
 * Created by semmel on 03.12.2017.
 */

public class MotionDataSetDto implements Serializable {
    private MotionData[] motionDataSet;
    private String label = "";
    private boolean isInLabeledPostion = false;

    public MotionDataSetDto() {
        // at least on place for each last sensors MotionData
        motionDataSet = new MotionData[Sensor.values().length];
        for(Sensor s:Sensor.values()){
            // fill with dummy data
            motionDataSet[s.getSensorNumber()-1]=new MotionData();
    }}

    public void update(MotionData md) {
        // update MotionData
        motionDataSet[md.getSensor().getSensorNumber()-1]=md;
        //update Label information
        label=md.getLabel();
        isInLabeledPostion=md.getInLabeledPosition();
    }

    public MotionData[] getMotionDataSet(){
        MotionData[] motionDataSetCopy= Arrays.copyOf(this.motionDataSet,this.motionDataSet.length);

        return  motionDataSetCopy;
    }

    public MotionData getMotion(Sensor s){
        return this.motionDataSet[s.getSensorNumber()-1].clone();
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
}
