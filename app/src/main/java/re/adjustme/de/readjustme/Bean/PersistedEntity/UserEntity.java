package re.adjustme.de.readjustme.Bean.PersistedEntity;

import java.io.Serializable;

import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.PersistenceType;

/**
 * Created by semmel on 27.01.2018.
 */
@Persistence(name = "user", type = PersistenceType.OBJECT)
public class UserEntity implements Serializable {

    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
