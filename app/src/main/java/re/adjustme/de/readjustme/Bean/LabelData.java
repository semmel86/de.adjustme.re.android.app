package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import re.adjustme.de.readjustme.Configuration.BodyAreas;
import re.adjustme.de.readjustme.Configuration.Label;

/**
 * Created by semmel on 06.01.2018.
 */

public class LabelData implements Serializable {

    private BodyAreas area;
    private Label label;
    private Timestamp begin;
    private Long duration;
    public LabelData(Label label, BodyAreas area) {
        this.label = label;
        this.begin = new Timestamp(new Date().getTime());
        this.duration = 0L;
        this.area = area;
    }

    public BodyAreas getArea() {
        return area;
    }

    public void setArea(BodyAreas area) {
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


}
