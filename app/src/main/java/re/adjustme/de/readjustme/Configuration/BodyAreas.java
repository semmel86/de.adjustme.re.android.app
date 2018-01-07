package re.adjustme.de.readjustme.Configuration;

/**
 * Defines the different Body Areas and the referring labeld Motions
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum BodyAreas {
    SHOULDER("Shoulder", ShoulderLabel.values()),
    SPLINE("Spline", SplineLabel.values());

    private String areaType;
    private Label[] label;

    BodyAreas(String areaType, Label[] label) {
        this.areaType = areaType;
        this.label = label;
    }


    public boolean contains(String label) {
        for (Label v : this.label) {
            if (v.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    public Label getLable(String label) {
        for (Label v : this.label) {
            if (v.getLabel().equals(label)) {
                return v;
            }
        }
        return null;
    }
}
