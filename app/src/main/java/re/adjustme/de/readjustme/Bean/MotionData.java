package re.adjustme.de.readjustme.Bean;

import java.sql.Timestamp;

import re.adjustme.de.readjustme.Configuration.PersitenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionData {

    private Sensor sensor;
    private int x;
    private int y;
    private int z;
    private Timestamp begin;
    private long duration;


    public Sensor getSensor() {
        return this.sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public void setSensor(int i) {
        this.sensor = Sensor.getSensor(i);
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

    // 1 ; 20171118123325634213 ; 25632 ; 10 ; 90 ; 155
    public String toString() {
        return sensor.getSensorNumber() + PersitenceConfiguration.CSV_SEPARATOR +
                begin + PersitenceConfiguration.CSV_SEPARATOR +
                duration + PersitenceConfiguration.CSV_SEPARATOR +
                x + PersitenceConfiguration.CSV_SEPARATOR +
                y + PersitenceConfiguration.CSV_SEPARATOR
                + z;
    }

}
