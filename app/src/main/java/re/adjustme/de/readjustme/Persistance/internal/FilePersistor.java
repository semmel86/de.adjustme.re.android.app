package re.adjustme.de.readjustme.Persistance.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import re.adjustme.de.readjustme.Configuration.PersitenceConfiguration;

/**
 * Created by Semmel on 18.11.2017.
 */

public class FilePersistor {
    private final File persistanceDir = PersitenceConfiguration.getPersitanceDirectory();

    /**
     * Save Object information ( referring to toString()) to a named File, returns true on success, otherwise false
     * Important -> Object:File = 1:1
     *
     * @param object
     * @param fileName
     * @return Boolean
     */
    public boolean save(final Object object, final String fileName){
        try{
            final FileOutputStream fos = new FileOutputStream(new File(persistanceDir + fileName));
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            return true;
        }
        catch(final IOException e){
            e.printStackTrace();
            return false;
        }
    }
}
