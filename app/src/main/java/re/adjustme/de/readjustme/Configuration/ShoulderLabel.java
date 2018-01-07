package re.adjustme.de.readjustme.Configuration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum ShoulderLabel implements Label {
    A("A", "Null Position"),
    AK("AK", ""),
    AL("AL", ""),
    AM("AM", ""),
    AN("AN", ""),
    AO("AO", ""),
    AP("AP", ""),
    AQ("AQ", ""),
    AR("AR", ""),
    AS("AS", ""),
    AT("AT", ""),
    AU("AU", ""),
    AV("AV", ""),
    AW("AW", ""),
    AX("AX", ""),
    AY("AY", ""),
    AZ("AZ", ""),
    AAC("AAC", ""),
    AAD("AAD", ""),
    AAE("AAE", ""),
    AAF("AAF", ""),
    AAG("AAG", ""),
    AAH("AAH", ""),
    AAI("AAI", ""),
    AAJ("AAJ", ""),
    AAK("AAK", ""),
    AAL("AAL", ""),
    AAM("AAM", ""),
    UNLABELED("Unlabeled", "");


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

}
