package re.adjustme.de.readjustme.Util;

/**
 * Created by Stefan on 21.01.2018.
 */

public class Duration {
    public static String millisToDuration(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;


        String time = "";

        if (hour > 0) {
            time = String.format("%02d h %02d min", hour, minute);
        } else {
            if (minute > 0) {
                time = String.format("%02d min %02d sec", minute, second);
            } else{
                time = String.format("%02d sec", second);
            }
        }

        return time;
    }
}
