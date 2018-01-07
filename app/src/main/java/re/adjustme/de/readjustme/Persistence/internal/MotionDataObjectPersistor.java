package re.adjustme.de.readjustme.Persistence.internal;

import java.util.ArrayList;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Configuration.Sensor;
import re.adjustme.de.readjustme.Persistence.MotionDataPersistor;

/**
 * Really slow for many single load and save options.
 * Convenient for persisting larger objects without ORM.
 * <p>
 * Created by Semmel on 18.11.2017.
 */
public class MotionDataObjectPersistor extends ObjectPersistor implements MotionDataPersistor {


    @Override
    public void saveMotion(MotionData data) {

        List<MotionData> currentMotions = (List<MotionData>) this.load(data.getSensor().name());
        if (currentMotions == null) {
            currentMotions = new ArrayList<MotionData>();
        }
        currentMotions.add(data);
        this.save(currentMotions, data.getSensor().name());
    }

    @Override
    public List<MotionData> getMotionDataForSensor(Sensor sensor) {
        return (List<MotionData>) this.load(sensor.name());
    }

    @Override
    public MotionData getLastMotionData(Sensor sensor) {
        List<MotionData> currentMotions = (List<MotionData>) this.load(sensor.name());
        return currentMotions.get(currentMotions.size() - 1);
    }

    @Override
    public void saveMotionSet(MotionDataSetDto m) {
        this.save(m, "FullMotionDataSet.csv");
    }
}
