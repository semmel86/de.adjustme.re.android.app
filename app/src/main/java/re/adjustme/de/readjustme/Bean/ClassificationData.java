package re.adjustme.de.readjustme.Bean;

import java.io.Serializable;

/**
 * Bean holding values for the Classification.
 *
 * Created by semmel on 16.12.2017.
 */

public class ClassificationData implements Serializable {

    long dur = 0;
    private int maxX = -180;
    private int minX = 180;
    private long meanX = 0;
    private int maxY = -180;
    private int minY = 180;
    private long meanY = 0;
    private int maxZ = -180;
    private int minZ = 180;
    private long meanZ = 0;

@Override
public String toString(){
    return "    Min X: "+minX+" Max X: "+maxX+" Mean X: "+meanX+"   Min Y: "+minY+" Max Y: "+maxY+" Mean Y: "+meanY;
}

    private double MaxDistance;

    public long getDur() {
        return dur;
    }

    public void setDur(long durX) {
        this.dur = durX;
    }

    public double getMaxDistance() {
        return MaxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        MaxDistance = maxDistance;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public long getMeanZ() {
        return meanZ;
    }

    public void setMeanZ(long meanZ) {
        this.meanZ = meanZ;
    }

    public long getMeanX() {
        return meanX;
    }

    public void setMeanX(long meanX) {
        this.meanX = meanX;
    }

    public long getMeanY() {
        return meanY;
    }

    public void setMeanY(long meanY) {
        this.meanY = meanY;
    }


}
