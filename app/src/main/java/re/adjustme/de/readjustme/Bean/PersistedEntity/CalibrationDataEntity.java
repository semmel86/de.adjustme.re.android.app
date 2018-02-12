package re.adjustme.de.readjustme.Bean.PersistedEntity;

import java.util.HashMap;

import re.adjustme.de.readjustme.Bean.MotionDataBean;
import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.PersistenceType;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Entity for persisting the calibration settings
 * Created on 27.01.2018.
 * @author Sebastian Selmke
 * @version 1.0
 * @since 1.0
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
