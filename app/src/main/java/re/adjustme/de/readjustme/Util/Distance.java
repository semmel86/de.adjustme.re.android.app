package re.adjustme.de.readjustme.Util;

/**
 * Class providing static access to distance calculation methods.
 * (=> Euclidean Distance)
 *
 * Created by semmel on 20.01.2018.
 */

public class Distance {

    // get the Distance of a Point (x,y,z) to a second point(x2,y2,z2)
    public static double getEuclideanDistance(int x1, int x2, int y1, int y2, int z1, int z2){
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
        return distance;
    }

    // get the Distance of a Point (x,y,z) to a second point(x2,y2,z2)
    public static double getEuclideanDistance(long x1, int x2, long y1, int y2, long z1, int z2){
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
        return distance;
    }

    // get the Distance of a Point (x,y,z) to a second point(x2,y2,z2)
    public static double getEuclideanDistance(int x1, long x2, int y1, long y2, int z1, long z2){
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
        return distance;
    }

    // get the Distance of a Point (x,y,z) to a second point(x2,y2,z2)
    public static double getEuclideanDistance(long x1, long x2, long y1, long y2, long z1, long z2){
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
        return distance;
    }
    // get the Distance of a Point (x,y) to a second point(x2,y2)
    public static double getEuclideanDistance(int x1, int x2, int y1, int y2){
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return distance;
    }

    // get the Distance of a Point (x,y) to a second point(x2,y2)
    public static double getEuclideanDistance(long x1, int x2, long y1, int y2){
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return distance;
    }

    // get the Distance of a Point (x,y) to a second point(x2,y2)
    public static double getEuclideanDistance(int x1, long x2, int y1, long y2) {
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return distance;
    }
    // get the Distance of a Point (x,y) to a second point(x2,y2)
    public static double getEuclideanDistance(long x1, long x2, long y1, long y2){
        // calculate distance
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return distance;
    }
}
