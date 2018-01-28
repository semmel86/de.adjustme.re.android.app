package re.adjustme.de.readjustme.Bean.Entity;

import java.util.HashMap;

import re.adjustme.de.readjustme.Bean.MotionDataBean;
import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.PersistenceType;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Entity for persisting the calibration settings
 * Created by semmel on 27.01.2018.
 */
@Persistence(name = "CalibrationDataEntity", type = PersistenceType.OBJECT)
public class CalibrationDataEntity {

    private HashMap<Sensor, MotionDataBean> data;

    public HashMap<Sensor, MotionDataBean> getData() {
        return data;
    }

    public void setData(HashMap<Sensor, MotionDataBean> data) {
        this.data = data;
    }
}
