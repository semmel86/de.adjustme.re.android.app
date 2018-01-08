package re.adjustme.de.readjustme.Persistence;

import re.adjustme.de.readjustme.Predefined.PersistenceType;
import re.adjustme.de.readjustme.Persistence.internal.ClassificationDataObjectPersistor;
import re.adjustme.de.readjustme.Persistence.internal.MotionDataObjectPersistor;
import re.adjustme.de.readjustme.Persistence.internal.MotionDataTextFilePersistor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class PersistorFactory {


    static public MotionDataPersistor getMotionDataPersistor(PersistenceType type) {
        switch (type) {
            case OBJECT:
                return new MotionDataObjectPersistor();
            case FILE:
                return new MotionDataTextFilePersistor();
            case BACKEND:
                return null;
            default:
                return null;
        }
    }

    static public ClassificationDataPersistor getClassificationDataPersistor(PersistenceType type) {
        return new ClassificationDataObjectPersistor();
    }
}
