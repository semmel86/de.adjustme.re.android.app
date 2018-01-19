package re.adjustme.de.readjustme.Persistence.internal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import re.adjustme.de.readjustme.Bean.MotionData;
import re.adjustme.de.readjustme.Bean.MotionDataSetDto;
import re.adjustme.de.readjustme.Configuration.ClassificationConfiguration;
import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Persistence.MotionDataPersistor;
import re.adjustme.de.readjustme.Predefined.Sensor;

/**
 * Created by Semmel on 18.11.2017.
 */

public class MotionDataTextFilePersistor extends TextFilePersistor implements MotionDataPersistor {

    private String motionDataSetFile= ClassificationConfiguration.motionDataSetFile;
    @Override
    public void saveMotion(MotionData data) {
        this.save(data, data.getSensor().name());
    }

    public void saveMotionSet(MotionDataSetDto m) {
        this.save(m, motionDataSetFile);
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

        // 6- set z,
        // depends on the existence of label information
        if (s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) > 0) {
            i = Integer.parseInt(s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR)));
            s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);
        } else {
            i = Integer.parseInt(s.substring(0, s.length()));
        }
        md.setZ(i);

        // there is some additional label information
        if (s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) >= 0) {
            String label = s.substring(0, s.indexOf(PersistenceConfiguration.CSV_SEPARATOR));
            s = s.substring(s.indexOf(PersistenceConfiguration.CSV_SEPARATOR) + 1);
            md.setLabel(label);
            // there is also an is in labeled Position information
            if (s.length() > 0) {
                boolean b = Boolean.valueOf(s.substring(0, s.length()));
                md.setInLabeledPosition(b);
            }
        }

        return md;
    }

    public List<MotionDataSetDto> getMotionDataSetDtos() {
        final List<String> motions = this.loadFullLines(motionDataSetFile);
        final List<MotionDataSetDto> list = new ArrayList<>();

        for (final String s : motions) {
            list.add(this.getFullMotionDataFromString(s));
        }
        return list;
    }

    private MotionDataSetDto getFullMotionDataFromString(String s) {
        final MotionDataSetDto mDto = new MotionDataSetDto();
        final MotionData[] motionDataSet = new MotionData[5];
        // sensor
        for (int j = 0; j < 5; j++) {
            final MotionData md = new MotionData();
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
            md.setZ(i);

            motionDataSet[j] = md;
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
        mDto.addMotionData(motionDataSet);
        return mDto;
        // 5x MotionData + lable+ boolean

    }
}
