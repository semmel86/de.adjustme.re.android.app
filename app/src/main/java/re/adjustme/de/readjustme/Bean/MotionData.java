package re.adjustme.de.readjustme.Bean;

import java.sql.Timestamp;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionData {


    private int x;
    private int y;
    private int z;
    private Timestamp begin;
    private long duration;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Timestamp getBegin() {
        return begin;
    }

    public void setBegin(Timestamp begin) {
        this.begin = begin;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }



}
