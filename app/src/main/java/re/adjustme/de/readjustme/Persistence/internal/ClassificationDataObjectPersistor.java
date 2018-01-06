package re.adjustme.de.readjustme.Persistence.internal;

import java.util.HashMap;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionClassificator;
import re.adjustme.de.readjustme.Configuration.BodyAreas;
import re.adjustme.de.readjustme.Persistence.ClassificationDataPersistor;

/**
 * Really slow for many single load and save options.
 * Convenient for persisting smaller objects with ORM.
 * <p>
 * Created by Semmel on 18.11.2017.
 */
public class ClassificationDataObjectPersistor extends ObjectPersistor implements ClassificationDataPersistor {


    @Override
    public List<MotionClassificator> load() {
        return (List<MotionClassificator>) this.load("classificationData");
    }

    @Override
    public void save(List<MotionClassificator> data) {
        this.save(data,"classificationData");
    }

    @Override
    public HashMap<BodyAreas, List<MotionClassificator>> loadClassificationMap() {
        return (HashMap<BodyAreas, List<MotionClassificator>>) this.load("classificationHashMap");
    }

    @Override
    public void save(HashMap<BodyAreas, List<MotionClassificator>> data) {

            this.save(data,"classificationHashMap");
    }
}
