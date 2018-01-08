package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;

/**
 * Defines the different Labels
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum SplineLabel implements Label {
    A("A", "Neutral Position"),
    H("H", "Rotation rechts"),
    J("J", "Rotation links"),
    K("K", "Lateralflexion rechts"),
    L("L", "Lateralflexion links"),
    M("M", "Extension"),
    N("N", "Flexion"),
    O("O", "Rotation rechts & Lateralflexion rechts"),
    P("P", "Rotation links & Lateralflexion rechts"),
    Q("Q", "Rotation rechts & Lateralflexion links"),
    R("R", "Rotation links & Lateralflexion links"),
    S("S", "Extension & Rotation rechts"),
    T("T", "Extension & Rotation links"),
    U("U", "Flexion & Rotation rechts"),
    V("V", "Flexion & Rotation links"),
    W("W", "Extension & Lateralflexion rechts"),
    X("X", "Extension & Lateralflexion links"),
    Y("Y", "Flexion & Lateralflexion rechts"),
    Z("Z", "Flexion & Laterlflexion links"),
    AA("AA", "Extension & Lateralflexion + Rotation rechts"),
    AB("AB", "Extension & Lateralflexion rechts & Rotation links"),
    AC("AC", "Extension & Lateralflexion links & Rotation rechts"),
    AD("AD", "Extension & Lateralflexion + Rotation links"),
    AE("AE", "Flexion & Lateralflexion + Rotation rechts"),
    AF("AF", "Flexion & Lateralflexion rechts & Rotation links"),
    AG("AG", "Flexion & Lateralflexion links & Rotation rechts"),
    AH("AH", "Flexion & Lateralflexion + Rotation links"),
    UNLABELED(ClassificationConfiguration.UNKNOWN_POSITION, "Unbekannte Position");


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

    public String getDescription(){
        return this.description;
    }
}
