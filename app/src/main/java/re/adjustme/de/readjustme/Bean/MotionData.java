package re.adjustme.de.readjustme.Bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionData implements Serializable, Comparable {

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

    @Override
    public String toString() {
        return sensor.getSensorNumber() + PersistenceConfiguration.CSV_SEPARATOR +
                begin + PersistenceConfiguration.CSV_SEPARATOR +
                duration + PersistenceConfiguration.CSV_SEPARATOR +
                x + PersistenceConfiguration.CSV_SEPARATOR +
                y + PersistenceConfiguration.CSV_SEPARATOR
                + z;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        MotionData m = null;
        try {
            m = (MotionData) o;
        } catch (Exception e) {
            // not equal if it isn't a MotionData
            return -1;
        }
        int difX = this.getX() - m.getX();
        int difY = this.getY() - m.getY();
        int difZ = this.getZ() - m.getZ();
        if ((difX > 0 && difX > this.sensor.getEpsilon_x()) || (difX < 0 && difX < this.sensor.getEpsilon_x())) {
            // x-difference is to high
            return 1;
        } else if ((difY > 0 && difY > this.sensor.getEpsilon_y()) || (difY < 0 && difY < this.sensor.getEpsilon_y())) {
            // y-difference is to high
            return 1;
        } else if ((difZ > 0 && difZ > this.sensor.getEpsilon_z()) || (difZ < 0 && difZ < this.sensor.getEpsilon_z())) {
            // z-difference is to high
            return 1;
        }
        // all possible differences are between the 0 and predefined epsilon
        // so we consider this motion data equals Object o
        return 0;
    }
}
