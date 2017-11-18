package re.adjustme.de.readjustme.Persistance;

import re.adjustme.de.readjustme.Configuration.PersistenceType;
import re.adjustme.de.readjustme.Persistance.internal.MotionDataObjectPersistor;
import re.adjustme.de.readjustme.Persistance.internal.MotionDataTextFilePersistor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class PersistorFactory {


    static public MotionDataPersistor getMotionDataPersistor(PersistenceType type) {
        switch (type) {
            case OBJECT:
                return new MotionDataObjectPersistor();
            case DB:
                return null;
            case FILE:
                return new MotionDataTextFilePersistor();
            case BACKEND:
                return null;
            default:
                return null;
        }


    }
}
