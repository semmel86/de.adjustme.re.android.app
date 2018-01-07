package re.adjustme.de.readjustme;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import re.adjustme.de.readjustme.Configuration.PersistenceConfiguration;
import re.adjustme.de.readjustme.Predefined.PersistenceType;
import re.adjustme.de.readjustme.Predefined.Sensor;
import re.adjustme.de.readjustme.Entity.MotionData;
import re.adjustme.de.readjustme.Persistence.MotionDataPersistor;
import re.adjustme.de.readjustme.Persistence.PersistorFactory;

/**
 * Created by Semmel on 18.11.2017.
 */
public class PersistenceTest {
    static private final Timestamp t = new Timestamp(System.currentTimeMillis());
    static Sensor r = Sensor.SENSOR_FRONT;
    static long d1 = 1000;
    static long d2 = 2500;

    static MotionData data1 = new MotionData();
    static MotionData data2 = new MotionData();
    static MotionData data3 = new MotionData();
    static MotionData data4 = new MotionData();
    static MotionData data5 = new MotionData();

    @BeforeClass
    public static void init() {
        data1.setSensor(Sensor.SENSOR_FRONT);
        data2.setSensor((Sensor.SENSOR_LEFT_SHOULDER));
        data3.setSensor((Sensor.SENSOR_FRONT));
        data4.setSensor(Sensor.SENSOR_LEFT_SHOULDER);
        data5.setSensor(Sensor.SENSOR_LEFT_SHOULDER);

        data1.setBegin(t);
        data2.setBegin(t);
        data3.setBegin(t);
        data4.setBegin(t);
        data5.setBegin(t);

        data1.setDuration(d1);
        data2.setDuration(d2);
        data3.setDuration(d1);
        data4.setDuration(d2);
        data5.setDuration(d1);

        data1.setX(100);
        data2.setX(200);
        data3.setX(15);
        data4.setX(56);
        data5.setX(123);

        data1.setY(100);
        data2.setY(200);
        data3.setY(15);
        data4.setY(56);
        data5.setY(123);

        data1.setZ(100);
        data2.setZ(200);
        data3.setZ(15);
        data4.setZ(56);
        data5.setZ(123);

        System.out.println(PersistenceConfiguration.getPersistenceDirectory());
        System.out.println(data1);
        System.out.println(data2);
        System.out.println(data3);
        System.out.println(data4);
        System.out.println(data5);
    }

    @Test
    public void testFilePersistor() {
        MotionDataPersistor myPersistor = PersistorFactory.getMotionDataPersistor(PersistenceType.FILE);
        myPersistor.saveMotion(data1);
        MotionData load1 = myPersistor.getLastMotionData(Sensor.SENSOR_FRONT);
        System.out.println(load1);
        TestCase.assertEquals(load1.toString(), data1.toString());

        myPersistor.saveMotion(data2);
        MotionData load2 = myPersistor.getLastMotionData(Sensor.SENSOR_FRONT);
        System.out.println(load1);
        TestCase.assertEquals(load2.toString(), data1.toString());

        myPersistor.saveMotion(data3);
        myPersistor.saveMotion(data4);
        myPersistor.saveMotion(data5);

        List<MotionData> loadedList = myPersistor.getMotionDataForSensor(Sensor.SENSOR_LEFT_SHOULDER);
        System.out.println(loadedList.size());
    }

    @Test
    public void testObjectPersistor() {
        MotionDataPersistor myPersistor = PersistorFactory.getMotionDataPersistor(PersistenceType.OBJECT);
        myPersistor.saveMotion(data1);
        MotionData load1 = myPersistor.getLastMotionData(Sensor.SENSOR_FRONT);
        System.out.println(load1);

        myPersistor.saveMotion(data2);
        MotionData load2 = myPersistor.getLastMotionData(Sensor.SENSOR_FRONT);
        System.out.println(load1);

        myPersistor.saveMotion(data3);
        myPersistor.saveMotion(data4);
        myPersistor.saveMotion(data5);

        List<MotionData> loadedList = myPersistor.getMotionDataForSensor(Sensor.SENSOR_LEFT_SHOULDER);
        System.out.println(loadedList.size());
    }
}