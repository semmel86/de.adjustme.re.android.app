package re.adjustme.de.readjustme.Persistance;

import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.Sensor;

/**
 * Created by Semmel on 18.11.2017.
 */

public interface MotionDataPersistenceInterface {

    /**
     * Add this MotionData to  Persistence
     */
    public boolean saveMotion(MotionData data);

    /**
     * Get the whole data for the given Sensor
     */
    public List<MotionData> getMotionDataForSensor(Sensor sensor);

    /**
     * Get the last saved MotionData entry
     */
    public MotionData getLastMotionData(Sensor sensor);

}
