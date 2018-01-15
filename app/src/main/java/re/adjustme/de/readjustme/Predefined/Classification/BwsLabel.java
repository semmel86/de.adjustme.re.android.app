package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum BwsLabel implements Label {
    A("A", "Neutral Position", "1"),
    H("H", "Rotation rechts", "29"),
    J("J", "Rotation links", "30"),
    K("K", "Lateralflexion rechts", "31"),
    L("L", "Lateralflexion links", "32"),
    M("M", "Extension", "33"),
    N("N", "Flexion", "34"), //
    O("O", "Rotation rechts & Lateralflexion rechts", "35"),
    P("P", "Rotation links & Lateralflexion rechts", "36"),
    Q("Q", "Rotation rechts & Lateralflexion links", "37"),
    R("R", "Rotation links & Lateralflexion links", "38"),
    S("S", "Extension & Rotation rechts", "39"),
    T("T", "Extension & Rotation links", "40"),
    U("U", "Flexion & Rotation rechts", "41"),
    V("V", "Flexion & Rotation links", "42"),
    W("W", "Extension & Lateralflexion rechts", "43"),
    X("X", "Extension & Lateralflexion links", "44"),
    Y("Y", "Flexion & Lateralflexion rechts", "45"),
    Z("Z", "Flexion & Laterlflexion links", "46"),
    AA("AA", "Extension & Lateralflexion + Rotation rechts", "47"),
    AB("AB", "Extension & Lateralflexion rechts & Rotation links", "48"),
    AC("AC", "Extension & Lateralflexion links & Rotation rechts", "49"),
    AD("AD", "Extension & Lateralflexion + Rotation links", "50"),
    AE("AE", "Flexion & Lateralflexion + Rotation rechts", "51"),
    AF("AF", "Flexion & Lateralflexion rechts & Rotation links", "52"),
    AG("AG", "Flexion & Lateralflexion links & Rotation rechts", "53"),
    AH("AH", "Flexion & Lateralflexion + Rotation links", "54"),
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "Unbekannte Position", "0");


    private String label;
    private String description;
    private String SVMClass;


    BwsLabel(String label, String description, String svm) {
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
