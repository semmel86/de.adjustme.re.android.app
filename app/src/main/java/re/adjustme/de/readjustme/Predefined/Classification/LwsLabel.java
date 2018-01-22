package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 13.01.2017.
 */

public enum LwsLabel implements Label {
    A("A", "neutrale Haltung", "1"),
    AJ("AJ", "gestreckt", "61"),
    AI("AI", "vorgebeugt", "62"),
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "Unbekannte Position", "0");

    private String label;
    private String description;
    private String SVMClass;

    LwsLabel(String label, String description, String svm) {
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
