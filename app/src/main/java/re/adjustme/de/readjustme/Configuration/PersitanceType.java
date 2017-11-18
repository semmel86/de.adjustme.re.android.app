package re.adjustme.de.readjustme.Configuration;

/**
 * Created by Semmel on 18.11.2017.
 */

public enum PersitanceType {

    FILE("File"),
    OBJECT("Object"),
    DB("Database"),
    BACKEND("Backend");

    private String persitanceType;

    PersitanceType(String persistanceType){
        this.persitanceType=persitanceType;
    }
}
