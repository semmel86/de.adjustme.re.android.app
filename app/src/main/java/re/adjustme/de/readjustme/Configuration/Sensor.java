package re.adjustme.de.readjustme.Configuration;

/**
 * Created by Semmel on 18.11.2017.
 */

public enum Sensor {

    SENSOR_FRONT(1),
    SENSOR_LOWER_BACK(2),
    SENSOR_UPPER_BACK(3),
    SENSOR_RIGHT_SHOULDER(4),
    SENSOR_LEFT_SHOULDER(5);

    private int sensorNumber;

    Sensor(int Sensor) {
        this.sensorNumber = Sensor;
    }

    public static Sensor getSensor(int i) {
        for (Sensor s : Sensor.values()) {
            if (s.getSensorNumber() == i) {
                return s;
            }
        }
        return null;
    }

    public int getSensorNumber() {
        return sensorNumber;
    }
}
