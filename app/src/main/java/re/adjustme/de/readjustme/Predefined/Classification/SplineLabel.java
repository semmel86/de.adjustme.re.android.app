package re.adjustme.de.readjustme.Predefined.Classification;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum SplineLabel implements Label {
    A("A", "Null Position"),
    H("H", ""),
    J("J", ""),
    K("K", ""),
    L("L", ""),
    M("M", ""),
    N("N", ""),
    O("O", ""),
    P("P", ""),
    Q("Q", ""),
    R("R", ""),
    S("S", ""),
    T("T", ""),
    U("U", ""),
    V("V", ""),
    W("W", ""),
    X("X", ""),
    Y("Y", ""),
    Z("Z", ""),
    AA("AA", ""),
    AB("AB", ""),
    AC("AC", ""),
    AD("AD", ""),
    AE("AE", ""),
    AF("AF", ""),
    AG("AG", ""),
    AH("AH", ""),
    UNLABELED("Unlabeled", "");


    private String label;
    private String description;

    SplineLabel(String label, String description) {
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
