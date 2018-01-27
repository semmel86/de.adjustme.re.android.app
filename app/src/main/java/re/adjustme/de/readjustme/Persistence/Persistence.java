package re.adjustme.de.readjustme.Persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import re.adjustme.de.readjustme.Predefined.PersistenceType;

/**
 * Annotation for Persistence Entitys.
 * <p>
 * <p>
 * Created by semmel on 27.01.2018.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Persistence {
    PersistenceType type();

    String name();
}