package re.adjustme.de.readjustme.Predefined;

/**
 * Created by Semmel on 18.11.2017.
 */

public enum PersistenceType {

    FILE("File"),
    OBJECT("Object"),
    BACKEND("Backend");

    private String persitanceType;

    PersistenceType(String persistanceType) {
        this.persitanceType = persitanceType;
    }
}
