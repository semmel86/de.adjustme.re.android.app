package re.adjustme.de.readjustme.Bean.PersistedEntity;

import java.io.Serializable;

import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.PersistenceType;

/**
 * Created  on 27.01.2018.
 * @author Sebastian Selmke
 * @version 1.0
 * @since 1.0
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
