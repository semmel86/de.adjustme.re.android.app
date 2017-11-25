package re.adjustme.de.readjustme.Persistance.internal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Configuration.Sensor;
import re.adjustme.de.readjustme.Persistance.MotionDataPersistor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionDataTextFilePersistor extends TextFilePersistor implements MotionDataPersistor {

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
            MotionData md = new MotionData();
            md = this.getMotionFromString(s);
            result.add(md);
        }
        return result;
    }

    @Override
    public MotionData getLastMotionData(Sensor sensor) {
        String last = this.loadLine(sensor.name());
        MotionData md = null;
        if (last != null) {
            md = this.getMotionFromString(last);
        }
        return md;
    }

    private MotionData getMotionFromString(String s) {
        MotionData md = new MotionData();
        // 1 ; 20171118123325634213 ; 25632 ; 10 ; 90 ; 155
        // 1 - set Sensor
        String sub = s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR));
        int i = Integer.parseInt(sub);
        Sensor sen = Sensor.getSensor(i);
        md.setSensor(sen);
        s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);

        // 2 - set begin
        String st = s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR));
        Timestamp tsp = Timestamp.valueOf(st);
        md.setBegin(tsp);
        s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);

        // 3 - set duration
        long l = Long.parseLong(s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR)));
        md.setDuration(l);
        s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);

        // 4 - set x
        i = Integer.parseInt(s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR)));
        md.setX(i);
        s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);

        // 5 - set y
        i = Integer.parseInt(s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR)));
        md.setY(i);
        s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);

        // 6- set z
        i = Integer.parseInt(s.substring(0, s.length()));
        md.setZ(i);
        return md;
    }
}
