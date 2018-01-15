package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum HwsLabel implements Label {
    A("A", "Neutral Position","1"),
    H("B", "Rotation rechts","55"),
    J("C", "Rotation links","56"),
    K("D", "Lateralflexion rechts","57"), // raus
    L("E", "Lateralflexion links","58"), // raus
    M("F", "Extension","59"), // turtle neck
    N("G", "Flexion","60"), // tech-neck
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "Unbekannte Position","0");


    private String label;
    private String description;
    private String SVMClass;



    HwsLabel(String label, String description, String svm) {
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

    public String getDescription(){
        return this.description;
    }
    public String getSVMClass(){
        return this.SVMClass;
    }
}
