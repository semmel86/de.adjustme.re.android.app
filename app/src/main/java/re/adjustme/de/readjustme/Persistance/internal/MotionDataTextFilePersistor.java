package re.adjustme.de.readjustme.Persistance.internal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.PersitenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;
import re.adjustme.de.readjustme.Persistance.MotionDataPersistenceInterface;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionDataTextFilePersistor extends TextFilePersistor implements MotionDataPersistenceInterface {

    @Override
    public boolean saveMotion(MotionData data) {
        this.save(data, data.getSensor().name());
        return false;
    }

    @Override
    public List<MotionData> getMotionDataForSensor(Sensor sensor) {
        List<String> motions = this.loadLines(sensor.name());
        List<MotionData> result = new ArrayList<>();

        for (String s : motions) {
            MotionData md =this.getMotionFromString(s);
        }
        return result;
    }

    @Override
    public MotionData getLastMotionData(Sensor sensor) {
        String last=this.loadLine(sensor.name());
        MotionData md =this.getMotionFromString(last);
        return md;
    }

    private MotionData getMotionFromString(String s){
        MotionData md = new MotionData();
        // 1 ; 20171118123325634213 ; 25632 ; 10 ; 90 ; 155
        // 1 - set Sensor
        int i = Integer.getInteger(s.substring(0, s.indexOf(PersitenceConfiguration.CSV_SEPARATOR)));
        md.setSensor(i);
        s = s.substring(s.indexOf(PersitenceConfiguration.CSV_SEPARATOR));

        // 2 - set begin
        long l = Long.getLong(s.substring(0, s.indexOf(PersitenceConfiguration.CSV_SEPARATOR)));
        md.setBegin(new Timestamp(l));
        s = s.substring(s.indexOf(PersitenceConfiguration.CSV_SEPARATOR));

        // 3 - set duration
        l = Long.getLong(s.substring(0, s.indexOf(PersitenceConfiguration.CSV_SEPARATOR)));
        md.setDuration(l);
        s = s.substring(s.indexOf(PersitenceConfiguration.CSV_SEPARATOR));

        // 4 - set x
        i = Integer.getInteger(s.substring(0, s.indexOf(PersitenceConfiguration.CSV_SEPARATOR)));
        md.setX(i);
        s = s.substring(s.indexOf(PersitenceConfiguration.CSV_SEPARATOR));

        // 5 - set y
        i = Integer.getInteger(s.substring(0, s.indexOf(PersitenceConfiguration.CSV_SEPARATOR)));
        md.setX(i);
        s = s.substring(s.indexOf(PersitenceConfiguration.CSV_SEPARATOR));

        // 6- set z
        i = Integer.getInteger(s.substring(0, s.length() - 1));
        md.setX(i);
        return md;
    }
}
