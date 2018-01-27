package re.adjustme.de.readjustme.Prediction;

import java.io.Serializable;
import java.util.List;

import re.adjustme.de.readjustme.Persistence.Entity.MotionDataSetDto;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Prediction.internal.svm;
import re.adjustme.de.readjustme.Prediction.internal.svm_model;
import re.adjustme.de.readjustme.Prediction.internal.svm_node;
import re.adjustme.de.readjustme.Prediction.internal.svm_train;


/**
 * Wrapper for ease access to libsvm library.
 *
 * @author selmkes
 */
public class SvmPredictor implements Serializable {
    private svm_model model;

    public double predict(MotionDataSetDto md, BodyArea area) {
        //final int svm_type = svm.svm_get_svm_type(model);
        final int nr_class = svm.svm_get_nr_class(model);

        // Data for prediction
        double[] prob_estimates = new double[nr_class];
        final svm_node[] x = md.getsvmNodes(area);
        double v = svm.svm_predict_probability(model, x, prob_estimates); // internal prediction
        System.out.println(prob_estimates);
        return v;
    }


    public void trainModel(List<MotionDataSetDto> motionDataSetDtos, BodyArea area) {
        final svm_train train = new svm_train();
        model = train.train(motionDataSetDtos, area);
    }
}
