package re.adjustme.de.readjustme.Persistence;

import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Created by Semmel on 18.11.2017.
 */

public interface MotionDataPersistor {

    /**
     * Add this MotionData to  Persistence
     */
    public void saveMotion(MotionData data);

    /**
     * Get the whole data for the given Sensor
     */
    public List<MotionData> getMotionDataForSensor(Sensor sensor);

    /**
     * Get the last saved MotionData entry
     */
    public MotionData getLastMotionData(Sensor sensor);

    /**
     * Save the full MotionDataSet
     */
    public void saveMotionSet(MotionDataSetDto m);

}
