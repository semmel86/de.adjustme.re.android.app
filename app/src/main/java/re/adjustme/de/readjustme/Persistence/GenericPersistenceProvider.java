package re.adjustme.de.readjustme.Persistence;

import android.util.Log;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Persistence.Entity.BackendDataEntity;
import re.adjustme.de.readjustme.Persistence.Entity.MotionDataSetDto;
import re.adjustme.de.readjustme.Persistence.internal.BackendPersistor;
import re.adjustme.de.readjustme.Persistence.internal.MotionDataTextFilePersistor;
import re.adjustme.de.readjustme.Persistence.internal.ObjectPersistor;

/**
 * Class wrap's all possible methods to save and load objects.
 * <p>
 * Created by semmel on 27.01.2018.
 */

public class GenericPersistenceProvider {
    private ObjectPersistor mObjectPersistor;
    private MotionDataTextFilePersistor mTextFilePersistor;
    private BackendPersistor mBackendPersistor;

    // Constructor
    public GenericPersistenceProvider() {
        mObjectPersistor = new ObjectPersistor();
        mTextFilePersistor = new MotionDataTextFilePersistor();
        mBackendPersistor = new BackendPersistor();
    }

    public void save(Object object) {
        // Check if persistence is enabled
        if (object.getClass().isAnnotationPresent(Persistence.class)) {
            Persistence p = (Persistence) getClass().getAnnotation(Persistence.class);
            switch (p.type()) {
                case BACKEND:
                    if (PersistenceConfiguration.SAVE_BACKEND) {
                        try {
                            BackendDataEntity bde = (BackendDataEntity) object;
                            mBackendPersistor.sendRequest(bde.getPayload(), bde.getContext());
                        } catch (ClassCastException e) {
                            Log.e("ClassCast", "Cannot save object to backend", e);
                        }
                    }
                    break;
                case OBJECT:
                    mObjectPersistor.save(object, p.name());
                    break;
                case FILE:
                    // only for testing to get csv data from device
                    // assume this can only be the MotionDataSetDto -> else Exception
                    if (PersistenceConfiguration.SAVE_LOCAL) {
                        try {
                            mTextFilePersistor.saveMotionSet((MotionDataSetDto) object);
                            ;
                        } catch (Exception e) {
                            Log.e("ClassCast", "Cannot save object to csv", e);
                        }
                    }
                    break;
            }
        }
    }

    public Object load(Class clazz) {
        Object o = null;
        // Check if persistence is enabled
        if (clazz.isAnnotationPresent(Persistence.class)) {
            Persistence p = (Persistence) clazz.getAnnotation(Persistence.class);
            switch (p.type()) {
                case BACKEND:
                    // not implemented right now
                    // returns null
                    break;
                case FILE:
                    // only for testing to load csv data from local device
                    // only for testing to get csv data from device
                    // assume this can only be the MotionDataSetDto -> else Exception
                    try {
                        o = mTextFilePersistor.getMotionDataSetDtos();
                    } catch (Exception e) {
                        Log.e("ClassCast", "Cannot save object to csv", e);
                    }
                    break;
                case OBJECT:
                    o = mObjectPersistor.load(p.name());
                    break;
            }
        }
        return o;
    }
}
