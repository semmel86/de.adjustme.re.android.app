package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum ShoulderLabel implements Label {
    A("A","Neutral Position", "1"),
    AK("AK", "Anteversion links", "2"), // je eine Schulterseite ausserhalb d. neutr. pos
    AL("AL", "Anteversion rechts", "3"),
    AM("AM", "Anteversion links & rechts", "4"),
    AN("AN", "Retroversion links", "5"),
    AO("AO", "Retroversion rechts", "6"),
    AP("AP", "Retroversion links & rechts", "7"),
    AQ("AQ", "Elevation links", "8"),
    AR("AR", "Elevation rechts", "9"),
    AS("AS", "Elevation links & rechts", "10"),
    AT("AT", "Depression links", "11"),
    AU("AU", "Depression rechts", "12"),
    AV("AV", "Depression links & rechts", "13"),
    //try simple model
    AW("AW", "Anteversion links & Elevation links", "14"),
    AX("AX", "Anteversion rechts & Elevation rechts", "15"),
    AY("AY", "Retroversion links & Elevation links", "16"),
    AZ("AZ", "Retroversion rechts & Elevation rechts", "17"),
    AAC("AAC", "Anteversion links & Elevation links", "18"),// Doppelt AAC - AAE == AW-AY
    AAD("AAD", "Anteversion rechts & Elevation rechts", "19"),
    AAE("AAE", "Retroversion links & Elevation links", "20"),
    AAF("AAF", "Anteversion links & Depression links", "21"),
    AAG("AAG", "Anteversion rechts & Depression rechts", "22"),
    AAH("AAH", "Retroversion links & Depression links", "23"),
    AAI("AAI", "Retroversion rechts & Depression rechts", "24"),

    AAJ("AAJ", "Anteversion links & Retroversion rechts", "25"), // ab hier Diagonalen über beide Schultern
    AAK("AAK", "Anteversion rechts & Retroversion links", "26"),
    //
    AAL("AAL", "Elevation links & Depression rechts", "27"),
    AAM("AAM", "Elevation rechts & Depression links", "28"),
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "Unbekannte Position", "0");


    private String label;
    private String description;
    private String SVMClass;

    ShoulderLabel(String label, String description, String svm) {
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
