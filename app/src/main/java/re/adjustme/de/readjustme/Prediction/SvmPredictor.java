package re.adjustme.de.readjustme.Prediction;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Predefined.Sensor;
import re.adjustme.de.readjustme.Prediction.internal.svm;
import re.adjustme.de.readjustme.Prediction.internal.svm_model;
import re.adjustme.de.readjustme.Prediction.internal.svm_node;
import re.adjustme.de.readjustme.Prediction.internal.svm_train;


/**
 * Wrapper for ease access to libsvm library.
 *
 * @author selmkes
 */
public class SvmPredictor implements Serializable{
    private svm_model model;

    public SvmPredictor() {
        super();
        this.init();

    }

    private void init() {
        if (model == null) {
            this.loadModel();
        }
    }

    private void loadModel() {
        // TODO Auto-generated method stub
    }

    private void saveModel(svm_model model) {
        // TODO Auto-generated method stub
    }

    private void trainModel(File trainingsData) {

        final svm_train train = new svm_train();
        if (trainingsData == null) {
            model = train.train(System.getProperty("user.dir") + "/FullMotionDataSet2.svm");
        } else {
            model = train.train(System.getProperty(trainingsData.getAbsolutePath()));
        }
    }

    public double predict(MotionDataSetDto md) {
        //final int svm_type = svm.svm_get_svm_type(model);
        final int nr_class = svm.svm_get_nr_class(model);

        // Data for prediction
        double[] prob_estimates = new double[nr_class];

// calc amount of features -> one node for each feature
        int c = 0;
        for (Sensor s : Sensor.values()) {
            if (!s.isExclude_x()) {
                c++;
            }
            if (!s.isExclude_y()) {
                c++;
            }
            if (!s.isExclude_z()) {
                c++;
            }
        }
        final svm_node[] x = new svm_node[c];
        c = 0;

        for (Sensor s : Sensor.values()) {
            // x
            if (!s.isExclude_x()) {
                x[c] = new svm_node();
                x[c].index = c;
                x[c].value = md.getMotion(s).getX();
                c++;
            }
            // y
            if (!s.isExclude_y()) {
                x[c] = new svm_node();
                x[c].index = c;
                x[c].value = md.getMotion(s).getY();
                c++;
            }
            // z
            if (!s.isExclude_z()) {
                x[c] = new svm_node();
                x[c].index = c ;
                x[c].value = md.getMotion(s).getZ();
                c++;
            }
        }
        double v = svm.svm_predict_probability(model, x, prob_estimates); // internal prediction
        return v;
    }

    public void trainModel(List<MotionDataSetDto> motionDataSetDtos) {
        final svm_train train = new svm_train();
        model = train.train(motionDataSetDtos);
    }
}
