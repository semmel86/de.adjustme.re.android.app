package re.adjustme.de.readjustme.Predefined.Classification;

/**
 * Defines the different Body Areas and the referring labeled Motions
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum BodyArea {
    SHOULDER("Shoulder", ShoulderLabel.values()),
    SPLINE("Spline", SplineLabel.values());

    private String areaType;
    private Label[] label;

    BodyArea(String areaType, Label[] label) {
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
