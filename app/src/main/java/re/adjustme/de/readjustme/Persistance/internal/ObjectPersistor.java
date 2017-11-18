package re.adjustme.de.readjustme.Persistance.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import re.adjustme.de.readjustme.Configuration.PersitenceConfiguration;

/**
 * @author selmkes
 */
public class ObjectPersistor {

    private final File persistanceDir = PersitenceConfiguration.getPersitanceDirectory();

    /**
     * Constructor
     */
    public ObjectPersistor() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Save Object to a named File, returns true on success, otherwise false
     * Important -> Object:File = 1:1
     *
     * @param object
     * @param fileName
     * @return Boolean
     */
    public boolean save(final Object object, final String fileName) {
        try {
            final FileOutputStream fos = new FileOutputStream(new File(persistanceDir + fileName));
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load object from named file, returns the Object or NULL
     *
     * @param fileName
     * @return Object o
     */
    public Object load(final String fileName) {
        if (new File(persistanceDir + fileName).exists()) {
            try {
                final FileInputStream fis = new FileInputStream(new File(persistanceDir + fileName));
                final ObjectInputStream ois = new ObjectInputStream(fis);
                final Object object = ois.readObject();
                ois.close();
                return object;
            } catch (final IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            System.err.println("Persistance File does not exist.");
            return null;
        }
    }
}
