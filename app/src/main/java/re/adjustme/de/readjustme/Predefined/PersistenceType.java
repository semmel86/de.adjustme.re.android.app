package re.adjustme.de.readjustme.Predefined;

/**
 * Predefined types for Persistence.
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum PersistenceType {

    FILE("File"),
    OBJECT("Object"),
    BACKEND("Backend");

    private String persistenceType;

    PersistenceType(String persistenceType) {
        this.persistenceType = persistenceType;
    }

    public String getType() {
        return this.persistenceType;
    }
}
