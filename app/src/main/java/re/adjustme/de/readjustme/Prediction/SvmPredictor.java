package re.adjustme.de.readjustme.Prediction;

import java.io.Serializable;
import java.util.List;

import re.adjustme.de.readjustme.Bean.PersistedEntity.MotionDataSetEntity;
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
    private svm_model mSvmModel;

    public double predict(MotionDataSetEntity md, BodyArea area) {
        //final int svm_type = svm.svm_get_svm_type(mSvmModel);
        final int nr_class = svm.svm_get_nr_class(mSvmModel);

        // Data for prediction
        double[] prob_estimates = new double[nr_class];
        final svm_node[] x = md.getsvmNodes(area);
        double v = svm.svm_predict_probability(mSvmModel, x, prob_estimates); // internal prediction
        System.out.println(prob_estimates);
        return v;
    }


    public void trainModel(List<MotionDataSetEntity> motionDataSetEntities, BodyArea area) {
        final svm_train train = new svm_train();
        mSvmModel = train.train(motionDataSetEntities, area);
    }
}
