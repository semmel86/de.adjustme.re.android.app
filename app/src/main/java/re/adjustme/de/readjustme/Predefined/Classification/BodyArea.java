package re.adjustme.de.readjustme.Predefined.Classification;

import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Defines the different Body Areas and the referring labeled Motions
 * <p>
 * // long duration -> 600000 = 10 min;
 * <p>
 * Created by Semmel on 18.11.2017.
 */

public enum BodyArea {
    SHOULDER("shoulder", ShoulderLabel.values(), 300000L, new Sensor[]{Sensor.SENSOR_LEFT_SHOULDER, Sensor.SENSOR_RIGHT_SHOULDER, Sensor.SENSOR_UPPER_BACK,Sensor.SENSOR_FRONT}),
    SPLINE("bws", BwsLabel.values(), 600000L, new Sensor[]{Sensor.SENSOR_LOWER_BACK,Sensor.SENSOR_FRONT, Sensor.SENSOR_LEFT_SHOULDER, Sensor.SENSOR_RIGHT_SHOULDER, Sensor.SENSOR_UPPER_BACK}),
    HWS("hws", HwsLabel.values(), 600000L, new Sensor[]{Sensor.SENSOR_FRONT, Sensor.SENSOR_LEFT_SHOULDER, Sensor.SENSOR_RIGHT_SHOULDER, Sensor.SENSOR_UPPER_BACK}),
    LWS("lws", LwsLabel.values(), 6000000L, new Sensor[]{Sensor.SENSOR_LOWER_BACK,Sensor.SENSOR_UPPER_BACK,Sensor.SENSOR_FRONT});

    private String areaName;
    private Label[] label;
    private long notificationDuration;
    private Sensor[] sensors;

    BodyArea(String areaName, Label[] label, Long l, Sensor[] sensors) {
        this.areaName = areaName;
        this.label = label;
        this.notificationDuration = l;
        this.sensors = sensors;
    }

    public long getMaxDuration() {
        return this.notificationDuration;
    }

    public boolean containsSensors(Sensor sensor) {
        for(Sensor s:sensors){
            if(s.equals(sensor)){
                return true;
            }
        }
        return false;
    }

    public boolean contains(String label) {
        for (Label v : this.label) {
            if (v.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    public Label getLable(String label) {
        for (Label v : this.label) {
            if (v.getLabel().equals(label)) {
                return v;
            }
        }
        return null;
    }

    public Label getLableByDescription(String label) {
        for (Label v : this.label) {
            if (v.getDescription().equals(label)) {
                return v;
            }
        }
        return null;
    }

    public Label getLable(long round) {
        String classNo = String.valueOf(round);
        for (Label v : this.label) {
            if (v.getSVMClass().equals(classNo)) {
                return v;
            }
        }
        return null;
    }
}
