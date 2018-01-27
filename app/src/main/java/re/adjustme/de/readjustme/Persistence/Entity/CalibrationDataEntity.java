package re.adjustme.de.readjustme.Persistence.Entity;

import java.util.HashMap;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.PersistenceType;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Entity for persisting the calibration settings
 * Created by semmel on 27.01.2018.
 */
@Persistence(name = "CalibrationDataEntity", type = PersistenceType.OBJECT)
public class CalibrationDataEntity {

    private HashMap<Sensor, MotionData> data;

    public HashMap<Sensor, MotionData> getData() {
        return data;
    }

    public void setData(HashMap<Sensor, MotionData> data) {
        this.data = data;
    }
}
