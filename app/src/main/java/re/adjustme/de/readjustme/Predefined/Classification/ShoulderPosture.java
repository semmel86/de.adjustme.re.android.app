package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum ShoulderPosture implements Label {
    A("A", "neutrale Haltung", "1"),
    AK("AK", "links vorgezogen", "2"), // je eine Schulterseite ausserhalb d. neutr. pos
    AL("AL", "rechts vorgezogen", "3"),
    AM("AM", "links & rechts vorgezogen", "4"),
    AN("AN", "links zurückgezogen", "5"),
    AO("AO", "rechts zurückgezogen", "6"),
    AP("AP", "links & rechts zurückgezogen", "7"),
    AQ("AQ", "links hochgezogen", "8"),
    AR("AR", "rechts hochgezogen", "9"),
    AS("AS", "links & rechts hochgezogen", "10"),
    AT("AT", "links runtergezogen", "11"),
    AU("AU", "rechts runtergezogen", "12"),
    AV("AV", "links & rechts runtergezogen", "13"),

//    AW("AW", "Anteversion links & Elevation links", "14"),
//    AX("AX", "Anteversion rechts & Elevation rechts", "15"),
//    AY("AY", "Retroversion links & Elevation links", "16"),
//    AZ("AZ", "Retroversion rechts & Elevation rechts", "17"),
//    AAC("AAC", "Anteversion links & Elevation links", "18"),// Doppelt AAC - AAE == AW-AY
//    AAD("AAD", "Anteversion rechts & Elevation rechts", "19"),
//    AAE("AAE", "Retroversion links & Elevation links", "20"),
//    AAF("AAF", "Anteversion links & Depression links", "21"),
//    AAG("AAG", "Anteversion rechts & Depression rechts", "22"),
//    AAH("AAH", "Retroversion links & Depression links", "23"),
//    AAI("AAI", "Retroversion rechts & Depression rechts", "24"),

    AAJ("AAJ", "links vorgezogen & rechts zurückgezogen", "25"), // ab hier Diagonalen über beide Schultern
    AAK("AAK", "rechts vorgezogen & links zurückgezogen", "26"),
    //
    AAL("AAL", "links hochgezogen & rechts runtergezogen", "27"),
    AAM("AAM", "rechts hochgezogen & links runtergezogen", "28"),
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "unbekannte Haltung", "0");


    private String label;
    private String description;
    private String SVMClass;

    ShoulderPosture(String label, String description, String svm) {
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
