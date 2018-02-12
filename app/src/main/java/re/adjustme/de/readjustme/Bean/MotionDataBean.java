package re.adjustme.de.readjustme.Bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Class represent the Data for one specific Sensor from the Shirt.
 *
 * Created  on 18.11.2017.
 * @author Sebastian Selmke
 * @version 1.0
 * @since 1.0
 *
 */
public class MotionDataBean implements Serializable, Comparable {

    /**
     *  The sensor from which the data originate.
     * @see Sensor
    **/
    private Sensor sensor = Sensor.SENSOR_FRONT;

    /**
     *  The value of the X-axis.
     **/
    private int x;
    /**
     *  The value of the Y-axis.
     **/
    private int y;
    /**
     *  The value of the Z-axis.
     **/
    private int z;
    /**
     *  The initialization time of this object.
     **/
    private Timestamp begin = new Timestamp(new Date().getTime());

    /**
     *  The duration until a new Motion was measured.
     *  Depends on Epsilon settings of the sensor.
     **/
    private long duration = 0;

    /**
     *  An optional label, used for training.
     **/
    private String label;
    /**
     *  Boolean in position, used for training.
     **/
    private Boolean inLabeledPosition;

    /**
     * Getter for inLabeledPosition.
     * @return inLabeledPosition
     */
    public Boolean getInLabeledPosition() {
        return inLabeledPosition;
    }

    /**
     * Setter for inLabeledPosition.
     * @param inLabeledPosition
     */
    public void setInLabeledPosition(Boolean inLabeledPosition) {
        this.inLabeledPosition = inLabeledPosition;
    }

    /**
     *  Getter for label.
     * @return String label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter for label
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter for sensor.
     * @return Sensor
     */
    public Sensor getSensor() {
        return this.sensor;
    }

    /**
     * Set Sensor by number.
     *
     * @param i
     */
    public void setSensor(int i) {
        this.sensor = Sensor.getSensor(i);
    }

    /**
     * Setter for Sensor.
     * @param sensor
     */
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * Getter for x.
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Setter for x.
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Getter for y.
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Setter for y.
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Getter for z.
     * @return z
     */
    public int getZ() {
        return z;
    }

    /**
     * Setter for z.
     * @param z
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Getter for begin.
     * @return Timestamp begin
     */
    public Timestamp getBegin() {
        return begin;
    }

    /**
     * Setter for begin.
     * @param begin
     */
    public void setBegin(Timestamp begin) {
        this.begin = begin;
    }

    /**
     * Getter for duration
     * @return long duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Setter for duration.
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Customized toString() method.
     * Returns the MotionData for this sensor as CSV.
     * CSV Seperator Configured in PersitenceConfiguration.
     * @see PersistenceConfiguration
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(sensor.getSensorNumber());
        s.append(PersistenceConfiguration.CSV_SEPARATOR);
        s.append(begin);
        s.append(PersistenceConfiguration.CSV_SEPARATOR);
        s.append(duration);
        s.append(PersistenceConfiguration.CSV_SEPARATOR);
        s.append(x);
        s.append(PersistenceConfiguration.CSV_SEPARATOR);
        s.append(y);
        s.append(PersistenceConfiguration.CSV_SEPARATOR);
        s.append(z);
        // write only if set
        if (label != null) {
            s.append(PersistenceConfiguration.CSV_SEPARATOR);
            s.append(label);
            if (inLabeledPosition != null) {
                s.append(PersistenceConfiguration.CSV_SEPARATOR);
                s.append(inLabeledPosition);
            }
        }
        return s.toString();
    }

    /**
     *
     * Customized implementation of compareTo().
     * </br>
     * Compare two MotionDataBean objects by its x,y,z values.
     * Return 1, if the difference is bigger/smaller than the pre-configured epsilon
     * </br>
     * Return -1 ,if the given object is not a MotionDataBean.
     * </br>
     * Returns 0 if equal
     *
     * @param o
     * @return int
     */
    @Override
    public int compareTo(@NonNull Object o) {
        MotionDataBean m = null;
        try {
            m = (MotionDataBean) o;
        } catch (Exception e) {
            // not equal if it isn't a MotionDataBean
            return -1;
        }
        int difX = this.getX() - m.getX();
        int difY = this.getY() - m.getY();
        int difZ = this.getZ() - m.getZ();
        if ((difX > 0 && difX > this.sensor.getEpsilon_x()) || (difX < 0 && difX < this.sensor.getEpsilon_x() * (-1))) {
            // x-difference is to high
            return 1;
        } else if ((difY > 0 && difY > this.sensor.getEpsilon_y()) || (difY < 0 && difY < this.sensor.getEpsilon_y() * (-1))) {
            // y-difference is to high
            return 1;
        } else if ((difZ > 0 && difZ > this.sensor.getEpsilon_z()) || (difZ < 0 && difZ < this.sensor.getEpsilon_z() * (-1))) {
            // z-difference is to high
            return 1;
        }
        // all possible differences are between the 0 and predefined epsilon
        // so we consider this motion data equals Object o
        return 0;
    }

    public MotionDataBean clone() {
        MotionDataBean clone = new MotionDataBean();
        clone.setSensor(sensor);
        clone.setLabel(label);
        clone.setInLabeledPosition(inLabeledPosition);
        clone.setDuration(duration);
        clone.setBegin(begin);
        clone.setX(x);
        clone.setY(y);
        clone.setZ(z);

        return clone;
    }
}
