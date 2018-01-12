package re.adjustme.de.readjustme.Configuration;

/**
 * Contains the local calssification configuration params
 * <p>
 * Created by Semmel on 10.11.2017.
 */

public class ClassificationConfiguration {

    public static final Long EVALUATION_TIME = 500L;
    public static final boolean CALCULATE_ROTATION = false;
    public static final double MIN_PROBABILITY = 5;
    public static final String UNKNOWN_POSITION = "Unknown";
    // maximum allowed difference for calculation of the rotation vector
    public static final double MAX_ROTATION_VARIANCE_DEGREES = 5;
}
