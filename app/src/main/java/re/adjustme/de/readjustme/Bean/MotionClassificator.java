package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import re.adjustme.de.readjustme.Configuration.Sensor;

/**
 * Created by semmel on 16.12.2017.
 */

public class MotionClassificator implements Serializable{

    String name;
    HashMap<Integer, ClassificationData> classificationDataMap;

    public MotionClassificator(String name){
        classificationDataMap=new HashMap<>();
        this.name=name;
    }

    public ClassificationData getClassificationData(Integer i){
        return classificationDataMap.get(i);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void putClassificationData(Integer i, ClassificationData c){
        this.classificationDataMap.put(i,c);
    }

    public boolean containsSensor(Integer i){
        return classificationDataMap.containsKey(i);
    }

    // returns the probability that the user is currently in this motion
    public double getProbability(MotionDataSetDto motionDataSetDto) {

        MotionData[] motionDataSet = motionDataSetDto.getMotionDataSet();
        double probability = 0;// for each sensor calculate
        // the probablility in form
        Set keySet = classificationDataMap.keySet();
        for (Sensor sensor : Sensor.values()) {

            // only if we use this sensor for calculation, e.g. lower back must not be used for shoulder motions
            if (classificationDataMap.containsKey(sensor.getSensorNumber())) {
                MotionData md = motionDataSet[sensor.getSensorNumber() - 1];
                ClassificationData classificationData = classificationDataMap.get(sensor.getSensorNumber());

                if (!classificationData.useX()) {
                    // check if the sensor point is within the possible range for this Motion
                    if (isInBetween(md.getY(), classificationData.getMinY(), classificationData.getMaxY()) &&
                            isInBetween(md.getZ(), classificationData.getMinZ(), classificationData.getMaxZ())) {
                        // calculate probability
                        probability += calcProbability(md.getY(), md.getZ(), classificationData.getMeanY(),
                                classificationData.getMeanZ(), classificationData.getMaxDistance());
                    }

                } else if (!classificationData.useY()) {
                    // check if the sensor point is within the possible range for this Motion
                    if (isInBetween(md.getX(), classificationData.getMinX(), classificationData.getMaxX()) &&
                            isInBetween(md.getZ(), classificationData.getMinZ(), classificationData.getMaxZ())) {
                        // calculate probability
                        probability += calcProbability(md.getX(), md.getZ(), classificationData.getMeanX(),
                                classificationData.getMeanZ(), classificationData.getMaxDistance());
                    }
                } else if (!classificationData.useZ()) {
                    // check if the sensor point is within the possible range for this Motion
                    if (isInBetween(md.getY(), classificationData.getMinY(), classificationData.getMaxY()) &&
                            isInBetween(md.getX(), classificationData.getMinX(), classificationData.getMaxX())) {
                        // calculate probability
                        probability += calcProbability(md.getY(), md.getX(), classificationData.getMeanY(),
                                classificationData.getMeanX(), classificationData.getMaxDistance());
                    }
                } else {
                    // check if the sensor point is within the possible range for this Motion
//                    if (isInBetween(md.getX(), classificationData.getMinX(), classificationData.getMaxX()) &&
//                            isInBetween(md.getY(), classificationData.getMinY(), classificationData.getMaxY()) &&
//                            isInBetween(md.getZ(), classificationData.getMinZ(), classificationData.getMaxZ())) {
                        // calculate probability
                        probability += calcProbability(md.getX(), md.getY(), md.getZ(),
                                classificationData.getMeanX(), classificationData.getMeanY(),
                                classificationData.getMeanZ(), classificationData.getMaxDistance());

                    }
//                }
            }
        }
        //
        return (probability / Sensor.values().length);
    }

    private boolean isInBetween(int x, int min, int max) {
        if (x < min || x > max) {
            return false;
        }
        return true;
    }

    private double calcProbability(int x, int y, int z, long meanX, long meanY, long meanZ, double maxDist) {

        // calculate probability
        // 1 - distance
        double distance = Math.sqrt(Math.pow(meanX - x, 2)
                + Math.pow(meanY - y, 2)
                + Math.pow(meanZ - z, 2));

        // 2 - get Probability from distance/maxDistance*100
        return (1-(distance / maxDist)) * 100;
    }

    private double calcProbability(int x, int y, long meanX, long meanY, double maxDist) {

        // calculate probability
        // 1 - distance
        double distance = Math.sqrt(Math.pow(meanX - x, 2)
                + Math.pow(meanY - y, 2));

        // 2 - get Probability from distance/maxDistance*100
        return (distance / maxDist) * 100;
    }

}
