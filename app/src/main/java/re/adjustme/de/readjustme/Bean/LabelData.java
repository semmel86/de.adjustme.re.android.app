package re.adjustme.de.readjustme.Bean;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by semmel on 06.01.2018.
 */

public class LabelData {

    private String area;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    private String label;
    private Timestamp begin;
    private Long duration;

    public LabelData(String label,String area){
        this.label=label;
        this.begin=new Timestamp(new Date().getTime());
        this.duration=0L;
        this.area=area;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
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
