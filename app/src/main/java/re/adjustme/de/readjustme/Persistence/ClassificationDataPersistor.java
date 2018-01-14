package re.adjustme.de.readjustme.Persistence;

import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionClassificator;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Prediction.SvmPredictor;

/**
 * Created by semmel on 16.12.2017.
 */

public interface ClassificationDataPersistor {

    public List<MotionClassificator> load();

    public void save(List<MotionClassificator> data);

    public HashMap<BodyArea, List<MotionClassificator>> loadClassificationMap();

    public void save(HashMap<BodyArea, List<MotionClassificator>> data);

    public void saveSVM(HashMap<BodyArea, SvmPredictor> data);

    public HashMap<BodyArea, SvmPredictor> loadSVM();
}
