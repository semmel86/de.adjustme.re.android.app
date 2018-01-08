package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum ShoulderLabel implements Label {
    A("A", "Neutral Position"),
    AK("AK", "Anteversion links"), // je eine Schulterseite ausserhalb d. neutr. pos
    AL("AL", "Anteversion rechts"),
    AM("AM", "Anteversion links & rechts"),
    AN("AN", "Retroversion links"),
    AO("AO", "Retroversion rechts"),
    AP("AP", "Retroversion links & rechts"),
    AQ("AQ", "Elevation links"),
    AR("AR", "Elevation rechts"),
    AS("AS", "Elevation links & rechts"),
    AT("AT", "Depression links"),
    AU("AU", "Depression rechts"),
    AV("AV", "Depression links & rechts"),
    AW("AW", "Anteversion links & Elevation links"),
    AX("AX", "Anteversion rechts & Elevation rechts"),
    AY("AY", "Retroversion links & Elevation links"),
    AZ("AZ", "Retroversion rechts & Elevation rechts"),
    AAC("AAC", "Anteversion links & Elevation links"),// Doppelt AAC - AAE == AW-AY
    AAD("AAD", "Anteversion rechts & Elevation rechts"),
    AAE("AAE", "Retroversion links & Elevation links"),
    AAF("AAF", "Anteversion links & Depression links"),
    AAG("AAG", "Anteversion rechts & Depression rechts"),
    AAH("AAH", "Retroversion links & Depression links"),
    AAI("AAI", "Retroversion rechts & Depression rechts"),
    AAJ("AAJ", "Anteversion links & Retroversion rechts"), // ab hier Diagonalen über beide Schultern
    AAK("AAK", "Anteversion rechts & Retroversion links"),
    AAL("AAL", "Elevation links & Depression rechts"),
    AAM("AAM", "Elevation rechts & Depression links"),
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "Unbekannte Position");


    private String label;
    private String description;

    ShoulderLabel(String label, String description) {
        this.label = label;
        this.description = description;
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
}
