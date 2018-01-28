package re.adjustme.de.readjustme.Bean.Entity;

import java.util.HashMap;

import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.Classification.BodyArea;
import re.adjustme.de.readjustme.Predefined.PersistenceType;
import re.adjustme.de.readjustme.Prediction.SvmPredictor;

/**
 * Created by semmel on 27.01.2018.
 */
@Persistence(name = "SVMClassificator", type = PersistenceType.OBJECT)
public class SVMClassificationEntity {
    private HashMap<BodyArea, SvmPredictor> svmMotionclassifier;


    public HashMap<BodyArea, SvmPredictor> getSvmMotionclassifier() {
        return svmMotionclassifier;
    }

    public void setSvmMotionclassifier(HashMap<BodyArea, SvmPredictor> svmMotionclassifier) {
        this.svmMotionclassifier = svmMotionclassifier;
    }
}
