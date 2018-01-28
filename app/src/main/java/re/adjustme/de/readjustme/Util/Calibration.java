package re.adjustme.de.readjustme.Util;

/**
 * Created by semmel on 28.01.2018.
 */

public class Calibration {

    public static int calibrate(int x, int x_){
        int result=0;
        if((x - x_) >=(-180)) {
            if (((x - x_) <= (180))) {
                // 1-normal case
                result=x - x_;
            }else{
                // 2- >180 so we have to calculate the rest above 180
                // and scale new from -180
                int rest=x - x_-180;
                result= -180 + rest;
            }
        }
        else{
            // 3- < -180 so we have to calculate the rest above 180
            // and scale new from 180
            int rest=x - x_+180;
            result=180 + rest;

        }
        return result;
    }


    public static int scale(int x, int x_){
    // adjust z with new z_
    // check if z and z_ are >0 or <0 are reached
        if(x>0 && x_<0 || x<0 && x_>0) {
        return (x+(x_));
    }else {
        return(x - (x_));
    }}
}
