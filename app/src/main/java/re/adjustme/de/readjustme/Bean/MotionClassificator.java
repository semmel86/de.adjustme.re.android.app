package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;
import re.adjustme.de.readjustme.Predefined.Sensor;

import static re.adjustme.de.readjustme.Configuration.ClassificationConfiguration.CALCULATE_ROTATION;

/**
 * Created by semmel on 16.12.2017.
 */
public class MotionClassificator implements Serializable {

    String name;
    HashMap<Integer, ClassificationData> classificationDataMap;

    public MotionClassificator(String name) {
        classificationDataMap = new HashMap<>();
        this.name = name;
    }

    public ClassificationData getClassificationData(Integer i) {
        return classificationDataMap.get(i);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void putClassificationData(Integer i, ClassificationData c) {
        this.classificationDataMap.put(i, c);
    }

    public boolean containsSensor(Integer i) {
        return classificationDataMap.containsKey(i);
    }

    // returns the probability that the user is currently in this motion
    public double getProbability(MotionDataSetDto motionDataSetDto) {

        MotionData[] motionDataSet = motionDataSetDto.getMotionDataSet();
        double probability = 0;// for each sensor calculate
        // the probablility in form
        Set keySet = classificationDataMap.keySet();
        for (Sensor sensor : Sensor.values()) {
            int[] rotation = null;
            // only if we use this sensor for calculation, e.g. lower back must
            // not be used for shoulder motions
            if (classificationDataMap.containsKey(sensor.getSensorNumber())) {
                MotionData md = motionDataSet[sensor.getSensorNumber() - 1];
                ClassificationData classificationData = classificationDataMap.get(sensor.getSensorNumber());
                rotation = this.getRotation(motionDataSet);

                if (sensor.isExclude_x()) {
                    // check if the sensor point is within the possible range
                    // for this Motion
                    if (isInBetween(md.getY() + rotation[1], classificationData.getMinY(), classificationData.getMaxY())
                            && isInBetween(md.getZ() + rotation[2], classificationData.getMinZ(),
                            classificationData.getMaxZ())) {
                        // calculate probability
                        probability += calcProbability(md.getY() + rotation[1], md.getZ() + rotation[2],
                                classificationData.getMeanY(), classificationData.getMeanZ(),
                                classificationData.getMaxDistance());
                    }

                } else if (sensor.isExclude_y()) {
                    // check if the sensor point is within the possible range
                    // for this Motion
                    if (isInBetween(md.getX() + rotation[0], classificationData.getMinX(), classificationData.getMaxX())
                            && isInBetween(md.getZ() + rotation[2], classificationData.getMinZ(),
                            classificationData.getMaxZ())) {
                        // calculate probability
                        probability += calcProbability(md.getX() + rotation[0], md.getZ() + rotation[2],
                                classificationData.getMeanX(), classificationData.getMeanZ(),
                                classificationData.getMaxDistance());
                    }
                } else if (sensor.isExclude_z()) {
                    // check if the sensor point is within the possible range
                    // for this Motion
                    if (isInBetween(md.getY() + rotation[1], classificationData.getMinY(), classificationData.getMaxY())
                            && isInBetween(md.getX() + rotation[0], classificationData.getMinX(),
                            classificationData.getMaxX())) {
                        // calculate probability
                        probability += calcProbability(md.getY() + rotation[1], md.getX() + rotation[0],
                                classificationData.getMeanY(), classificationData.getMeanX(),
                                classificationData.getMaxDistance());
                    }
                } else {
                    // check if the sensor point is within the possible range
                    // for this Motion
                    if (isInBetween(md.getX(), classificationData.getMinX(), classificationData.getMaxX())
                            && isInBetween(md.getY(), classificationData.getMinY(), classificationData.getMaxY())
                            && isInBetween(md.getZ(), classificationData.getMinZ(), classificationData.getMaxZ())) {
                        // calculate probability
                        probability += calcProbability(md.getX() + rotation[0], md.getY() + rotation[1],
                                md.getZ() + rotation[2], classificationData.getMeanX(), classificationData.getMeanY(),
                                classificationData.getMeanZ(), classificationData.getMaxDistance());

                    }
                }
            }
        }

        return (probability / Sensor.values().length);
    }

    private boolean isInBetween(int x, int min, int max) {
        if (x < min || x > max) {
            return false;
        }
        return true;
    }

    // Calc the difference from current to classification data, expects that a
    // rotation is possible
    // and the returned vector represents the difference (x,y,z)
    private int[] getRotation(MotionData[] motionDataSet) {
        int[] vector = new int[3];
        boolean allowedVarianceRestriction=true;
        if(ClassificationConfiguration.CALCULATE_ROTATION) {
//		vector[0] = ((motionDataSet[0].getX() + motionDataSet[1].getX() + motionDataSet[2].getX()
//				+ motionDataSet[3].getX() + motionDataSet[4].getX())
//				- (Math.round(this.classificationDataMap.get(1).getMeanX())
//						+ Math.round(this.classificationDataMap.get(2).getMeanX())
//						+ Math.round(this.classificationDataMap.get(3).getMeanX())
//						+ Math.round(this.classificationDataMap.get(4).getMeanX())
//						+ Math.round(this.classificationDataMap.get(5).getMeanX())))
//				/ 5;
//		vector[1] = ((motionDataSet[0].getY() + motionDataSet[1].getY() + motionDataSet[2].getY()
//				+ motionDataSet[3].getY() + motionDataSet[4].getY())
//				- (Math.round(this.classificationDataMap.get(1).getMeanY())
//						+ Math.round(this.classificationDataMap.get(2).getMeanY())
//						+ Math.round(this.classificationDataMap.get(3).getMeanY())
//						+ Math.round(this.classificationDataMap.get(4).getMeanY())
//						+ Math.round(this.classificationDataMap.get(5).getMeanY())))
//				/ 5;
//
//		vector[2] = ((motionDataSet[0].getZ() + motionDataSet[1].getZ() + motionDataSet[2].getZ()
//				+ motionDataSet[3].getZ() + motionDataSet[4].getZ())
//				- (Math.round(this.classificationDataMap.get(1).getMeanZ())
//						+ Math.round(this.classificationDataMap.get(2).getMeanZ())
//						+ Math.round(this.classificationDataMap.get(3).getMeanZ())
//						+ Math.round(this.classificationDataMap.get(4).getMeanZ())
//						+ Math.round(this.classificationDataMap.get(5).getMeanZ())))
//				/ 5;
//

         /*   for(Sensor s:Sensor.values()) {
                vector[0] += (motionDataSet[s.getSensorNumber()-1].getX() - (Math.round(this.classificationDataMap.get(s.getSensorNumber()).getMeanX())));
                vector[1] += (motionDataSet[s.getSensorNumber()-1].getX() - (Math.round(this.classificationDataMap.get(s.getSensorNumber()).getMeanX())));
                vector[2] += (motionDataSet[s.getSensorNumber()-1].getX() - (Math.round(this.classificationDataMap.get(s.getSensorNumber()).getMeanX())));

            if((motionDataSet[s.getSensorNumber()-1].getX() - (Math.round(this.classificationDataMap.get(s.getSensorNumber()).getMeanX())))- vector[0]/s.getSensorNumber()
           }*/
        }else {
            // default vector setting, no rotation
            vector[0] = 0;
            vector[1] = 0;
            vector[2] = 0;
        }
        return vector;

    }

    private double calcProbability(int x, int y, int z, long meanX, long meanY, long meanZ, double maxDist) {

        // calculate probability
        // 1 - distance
        double distance = Math.sqrt(Math.pow(meanX - x, 2) + Math.pow(meanY - y, 2) + Math.pow(meanZ - z, 2));

        // 2 - get Probability from distance/maxDistance*100
        return (1 - (distance / maxDist)) * 100;
    }

    private double calcProbability(int x, int y, long meanX, long meanY, double maxDist) {

        // calculate probability
        // 1 - distance
        double distance = Math.sqrt(Math.pow(meanX - x, 2) + Math.pow(meanY - y, 2));

        // 2 - get Probability from distance/maxDistance*100
        return (1 - (distance / maxDist)) * 100;
    }
}