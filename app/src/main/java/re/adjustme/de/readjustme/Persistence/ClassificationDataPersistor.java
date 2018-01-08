package re.adjustme.de.readjustme.Persistence;

import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionClassificator;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;

/**
 * Created by semmel on 16.12.2017.
 */

public interface ClassificationDataPersistor {

    public List<MotionClassificator> load();

    public void save(List<MotionClassificator> data);

    public HashMap<BodyArea, List<MotionClassificator>> loadClassificationMap();

    public void save(HashMap<BodyArea, List<MotionClassificator>> data);
}
