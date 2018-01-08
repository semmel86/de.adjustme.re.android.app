package re.adjustme.de.readjustme.Predefined.Classification;

/**
 * Defines the different Body Areas and the referring labeled Motions
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum BodyArea {
    SHOULDER("Shoulder", ShoulderLabel.values(),300000L), // 300000 = 5min;
    SPLINE("Spline", SplineLabel.values(),600000L); // 600000 = 10 min;

    private String areaType;
    private Label[] label;
    private long notificationDuration;

    BodyArea(String areaType, Label[] label,Long l) {
        this.areaType = areaType;
        this.label = label;
        this.notificationDuration=l;
    }

    public long getMaxDuration(){
        return this.notificationDuration;
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
