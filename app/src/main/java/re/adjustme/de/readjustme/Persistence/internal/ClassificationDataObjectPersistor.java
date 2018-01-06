package re.adjustme.de.readjustme.Persistence.internal;

import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionClassificator;
import re.adjustme.de.readjustme.Persistence.ClassificationDataPersistor;

/**
 * Really slow for many single load and save options.
 * Convenient for persisting larger objects without ORM.
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
}
