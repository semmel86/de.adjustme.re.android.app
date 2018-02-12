package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.Classification.Label;

/**
 * Represents the result of an Classification.
 * A label for the current Posture and the Body area of that posture.
 * In addition the Timestamp is set on detection
 * and the duration is tracked.
 * <p>
 * Created on 06.01.2018.
 * @author Sebastian Selmke
 * @version 1.0
 * @since 1.0
 *
 */

public class PostureBean implements Serializable {

    private BodyArea area;
    private Label label;
    private Timestamp begin;
    private Long duration;

    /**
     * Public constructor using Label and BodyArea
     * Start and duration are set by default as Date().getTime() and 0.
     * @param label
     * @param area
     */
    public PostureBean(Label label, BodyArea area) {
        this.label = label;
        this.begin = new Timestamp(new Date().getTime());
        this.duration = 0L;
        this.area = area;
    }

    /**
     * Getter Area.
     * @return bodyarea
     *  @see BodyArea
     */
    public BodyArea getArea() {
        return area;
    }

    /**
     * Getter label.
     * @return
     */
    public Label getLabel() {
        return label;
    }

    /**
     *  Setter label.
     * @param label
     */
    public void setLabel(Label label) {
        this.label = label;
    }

    /**
     * Getter begin.
     * @return begin
     */
    public Timestamp getBegin() {
        return begin;
    }

    /**
     * Getter duratotion.
     * @return duration
     */
    public Long getDuration() {
        return duration != null ? duration : 0L;
    }

    /**
     * Setter duration.
     * @param duration
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * Returns postures end as sum of begin + duration.
     * @return timestamp end
     */
    public Timestamp getEnd() {
        return new Timestamp(this.getBegin().getTime() + this.getDuration());
    }
}
