package re.adjustme.de.readjustme.Predefined;

/**
 * Created by Semmel on 18.11.2017.
 */

public enum HardwareCode {
    GENERAL_FAILURE(200, "Unexpected failure."),
    INITIATION_FAILURE(201, "MPU initialization failed."),
    RUNTIME_ERROR(202, "Runtime Error."),
    INITIALIZATION(500, "INIT New Start."),
    SENSOR_STATUS_OK(100,"OK");

    private String message;
    private int code;

    HardwareCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HardwareCode getFailure(int i) {
        for (HardwareCode s : HardwareCode.values()) {
            if (s.getCode() == i) {
                return s;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
