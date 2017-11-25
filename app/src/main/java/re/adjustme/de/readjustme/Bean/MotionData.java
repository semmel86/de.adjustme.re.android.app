package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionData implements Serializable {

    final long serialVersionUID = 41234500967890L;

    private Integer id;


    private Sensor sensor = Sensor.SENSOR_FRONT;

    private int x;

    private int y;

    private int z;

    private Timestamp begin = new Timestamp(new Date().getTime());

    private long duration = 0;


    public MotionData() {
        super();
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public void setSensor(int i) {
        this.sensor = Sensor.getSensor(i);
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Timestamp getBegin() {
        return begin;
    }

    public void setBegin(Timestamp begin) {
        this.begin = begin;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    //    public MotionData(Sensor sensor, Timestamp begin, Long duration, int x, int y, int z){
//        this.sensor=sensor;
//        this.begin=begin;
//        this.duration=duration;
//        this.x=x;
//        this.y=y;
//        this.z=z;
//    }
    // 1 ; 20171118123325634213 ; 25632 ; 10 ; 90 ; 155
    @Override
    public String toString() {
        return sensor.getSensorNumber() + PersistenceConfiguration.CSV_SEPARATOR +
                begin + PersistenceConfiguration.CSV_SEPARATOR +
                duration + PersistenceConfiguration.CSV_SEPARATOR +
                x + PersistenceConfiguration.CSV_SEPARATOR +
                y + PersistenceConfiguration.CSV_SEPARATOR
                + z;
    }

}
