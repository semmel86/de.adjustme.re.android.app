package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum HwsPosture implements Label {
    A("A", "neutrale Haltung", "1"),
    H("B", "nach rechts gedreht", "55"),
    J("C", "nach links gedreht", "56"),
    //    K("D", "nach rechts gebeugt", "57"), // raus
//    L("E", "nach links gebeugt", "58"), // raus
    F("F", "gestreckt", "59"), // turtle neck
    G("G", "vorgebeugt", "60"), // tech-neck
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "unbekannte Haltung", "0");


    private String label;
    private String description;
    private String SVMClass;


    HwsPosture(String label, String description, String svm) {
        this.label = label;
        this.description = description;
        this.SVMClass = svm;
    }

    public String getDescription(String label) {
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSVMClass() {
        return this.SVMClass;
    }
}
