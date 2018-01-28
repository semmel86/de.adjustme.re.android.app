package re.adjustme.de.readjustme.Persistence.internal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Bean.MotionDataBean;
import re.adjustme.de.readjustme.Bean.Entity.MotionDataSetEntity;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionDataTextFilePersistor extends TextFilePersistor {

    public void saveMotionSet(MotionDataSetEntity m) {
        this.save(m, "fullMotionDataSet.csv");
    }

    public List<MotionDataSetEntity> getMotionDataSetDtos() {
        final List<String> motions = this.loadFullLines("fullMotionDataSet.csv");
        final List<MotionDataSetEntity> list = new ArrayList<>();

        for (final String s : motions) {
            list.add(this.getFullMotionDataFromString(s));
        }
        return list;
    }

    private MotionDataSetEntity getFullMotionDataFromString(String s) {
        final MotionDataSetEntity mDto = new MotionDataSetEntity();
        final MotionDataBean[] motionDataBeanSet = new MotionDataBean[5];
        // sensor
//        int z_=0;
        for (int j = 0; j < 5; j++) {
            final MotionDataBean md = new MotionDataBean();
            // 1 ; 20171118123325634213 ; 25632 ; 10 ; 90 ; 155
            // 1 - set Sensor
            final String sub = s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR));
            int i = Integer.parseInt(sub);
            final Sensor sen = Sensor.getSensor(i);
            md.setSensor(sen);
            s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);

            // 2 - set begin
            final String st = s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR));
            // System.out.println(st);
            final Timestamp tsp = Timestamp.valueOf("2017-01-04 12:12:12");
            md.setBegin(tsp);
            s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);

            // 3 - set duration
            final long l = Long.parseLong(s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR)));
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

            // 6- set z,
            // depends on the existence of label information
            if (s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) > 0) {
                i = Integer.parseInt(s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR)));
                s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);
            } else {
                i = Integer.parseInt(s.substring(0, s.length()));
                System.out.println("Parsing error, should never happen.");
            }
//            md.setZ(i);
//            z_ +=i;

            motionDataBeanSet[j] = md;
        }

        // there is some additional label information
        if (s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) >= 0) {
            final String label = s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR));
            s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);
            mDto.setLable(label);
            // there is also an is in labeled Position information
            if (s.length() > 0) {
                final boolean b = Boolean.valueOf(s.substring(0, s.length()));
                mDto.setInLabledPos(b);
            }
        }
//        clean up z
//        z_ = z_/5;
//        for (int j = 0; j < 5; j++) {
//            motionDataBeanSet[j].setZ(motionDataBeanSet[j].getZ()-z_);
//        }
        mDto.addMotionData(motionDataBeanSet);
        return mDto;
        // 5x MotionDataBean + lable+ boolean

    }
}
