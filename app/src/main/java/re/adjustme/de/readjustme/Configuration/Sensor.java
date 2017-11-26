package re.adjustme.de.readjustme.Configuration;

/**
 * Predefined Sensor numbers referring to Bluetooth interface description.
 * Configured epsilon as possible difference for each gyroscope value.
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum Sensor {

    SENSOR_FRONT(1, 5, 5, 5),
    SENSOR_LOWER_BACK(2, 5, 5, 5),
    SENSOR_UPPER_BACK(3, 5, 5, 5),
    SENSOR_RIGHT_SHOULDER(4, 5, 5, 5),
    SENSOR_LEFT_SHOULDER(5, 5, 5, 5);

    private int sensorNumber;
    private int epsilon_x;
    private int epsilon_y;
    private int epsilon_z;

    Sensor(int Sensor, int x, int y, int z) {
        this.sensorNumber = Sensor;
        this.epsilon_x = x;
        this.epsilon_y = y;
        this.epsilon_z = z;
    }

    public static Sensor getSensor(int i) {
        for (Sensor s : Sensor.values()) {
            if (s.getSensorNumber() == i) {
                return s;
            }
        }
        return null;
    }

    public int getEpsilon_x() {
        return epsilon_x;
    }

    public int getEpsilon_y() {
        return epsilon_y;
    }

    public int getEpsilon_z() {
        return epsilon_z;
    }

    public int getSensorNumber() {
        return sensorNumber;
    }
}
