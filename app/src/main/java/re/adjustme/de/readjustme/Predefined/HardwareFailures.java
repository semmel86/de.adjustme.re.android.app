package re.adjustme.de.readjustme.Predefined;

/**
 * Created by Semmel on 18.11.2017.
 */

public enum HardwareFailures {
    GENRAL_FAILURE(200, "Unexpected failure."),
    INITIATION_FAILURE(201, "MPU initialization failed."),
    RUNTIME_ERROR(202, "Runtime Error."),
    INITIALIZATION(500, "INIT New Start.");

    private String message;
    private int code;

    HardwareFailures(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HardwareFailures getFailure(int i) {
        for (HardwareFailures s : HardwareFailures.values()) {
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
