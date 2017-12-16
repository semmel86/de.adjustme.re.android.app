package re.adjustme.de.readjustme.Persistence;

import java.util.List;

import re.adjustme.de.readjustme.Bean.ClassificationData;
import re.adjustme.de.readjustme.Bean.MotionClassificator;

/**
 * Created by semmel on 16.12.2017.
 */

public interface ClassificationDataPersistor {

    public List<MotionClassificator> load();

    public void save(List<MotionClassificator> data);
}
