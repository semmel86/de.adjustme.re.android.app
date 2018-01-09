package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;

/**
 * Represents the Result of an Classification,
 * A motion Label for the current Posture and the Body area of that posture.
 * In addition the Timestamp is set on detection
 * and the duration is tracked.
 *
 * Created by semmel on 06.01.2018.
 */

public class LabelData implements Serializable {

    private BodyArea area;
    private Label label;
    private Timestamp begin;
    private Long duration;
    public LabelData(Label label, BodyArea area) {
        this.label = label;
        this.begin = new Timestamp(new Date().getTime());
        this.duration = 0L;
        this.area = area;
    }

    public BodyArea getArea() {
        return area;
    }

    public void setArea(BodyArea area) {
        this.area = area;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Timestamp getBegin() {
        return begin;
    }

    public void setBegin(Timestamp begin) {
        this.begin = begin;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Timestamp getEnd() {
        return new Timestamp(this.getBegin().getTime() + this.getDuration());
    }
}
