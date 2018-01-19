package re.adjustme.de.readjustme.Configuration;

import re.adjustme.de.readjustme.Prediction.internal.svm_parameter;

/**
 * Contains the local calssification configuration params
 * <p>
 * Created by Semmel on 10.11.2017.
 */

public class ClassificationConfiguration {
    /*
    // General settings for classification
    */

    public static final Long EVALUATION_TIME = 500L;
    public static final String UNKNOWN_POSITION = "Unknown";
    // if true the model will be calculated
    // else load from APK
    public static final boolean CALCULATE_MODEL = true;

    /*
    // SVM model settings
    */
    public static final boolean USE_SVM_MODEL = true;
    public static int CROSS_VALIDATION=0;
    public static String motionDataSetFile="FullMotionDataSet.csv";

    // default values for SVM training
    public final static svm_parameter getSVMParams(){
     svm_parameter param = new svm_parameter();

        param.svm_type = svm_parameter.C_SVC; // C_SVC & NU_SVC
        param.kernel_type = svm_parameter.POLY; // Linear, Poly, RBF, Sigmoid
        param.degree = 3;
        param.gamma = 0; // 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        return param;
        }

    /*
    // Simple distance based model settings
    */

    // if true the max distance will be calculated for each model,
    // else =180
    public static final boolean CALCULATE_DISTANCE = true;
    public static final boolean CALCULATE_ROTATION = false;
    public static final double MIN_PROBABILITY = 5;
    // maximum allowed difference for calculation of the rotation vector
    public static final double MAX_ROTATION_VARIANCE_DEGREES = 5;
    // predict with SVM or internal Model builder

}
