package utilities.dataobjects.sensorobjects;

import com.sun.beans.util.Cache;
import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;
import utilities.dataobjects.homeobjects.Appliances;
import utilities.dataobjects.homeobjects.Rooms;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by YaHung on 2015/9/15.
 */

public class Sensors implements Serializable, Iterable<Sensor>  {
    Timestamp firstTimestamp;
    Timestamp lastTimestamp;
    String filePath;
    protected ArrayList<Sensor> sensors = new ArrayList<>();
    protected Dataset sensorDataset = new Dataset();

    public Sensors(){
        checkDirectory();
    }

    private void checkDirectory(){
        filePath = "util";
        File file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/dataobjects";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/sensorobjects";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}
    }

    public void saveDataset(){
        sensorDataset.save(filePath+"/sensorsDataset");
    }


    public void add(int sId, double value){
        lastTimestamp = new Timestamp(System.currentTimeMillis());

        if(firstTimestamp==null){
            firstTimestamp = lastTimestamp;
        }
        if( sId >= sensors.size()){
            while( sId >= sensors.size()){
                //Sensor sensor = new Sensor(sensors.size());
                Sensor sensor = createSensor(sensors.size());
                sensors.add(sensor);
            }
        }
        sensors.get(sId).add(value);
    }

    public void add(int sId, double value, Timestamp timestamp){
        lastTimestamp = timestamp;
        if(firstTimestamp==null){
            firstTimestamp = timestamp;
        }
        if( sId >= sensors.size()){
            while( sId >= sensors.size()){
                //Sensor sensor = new Sensor(sensors.size());
                Sensor sensor = createSensor(sensors.size());
                sensors.add(sensor);
            }
        }
        sensors.get(sId).add(value,timestamp);
    }

    private Sensor createSensor(int sId){
        Sensor s;
        Appliances appliances = new Appliances();
        Rooms rooms = new Rooms();
        rooms.load();
        String livingroom = "Living room";
        String hallway = "Hallway";
        String studyroom = "Study room";
        String kitchen = "Kitchen";
        String bedroom = "Bedroom";
        switch (sId){
            case 1:
                s = new Sensor(sId,rooms.get(livingroom),appliances.getlightId());
                break;
            case 2:
                s = new Sensor(sId,rooms.get(hallway),appliances.getlightId());
                break;
            case 3:
                s = new Sensor(sId,rooms.get(bedroom),appliances.getlightId());
                break;
            case 4:
                s = new Sensor(sId,rooms.get(kitchen),appliances.getlightId());
                break;
            case 5:
                s = new Sensor(sId,rooms.get(studyroom),appliances.getlightId());
                break;
            case 6:
                s = new Sensor(sId,rooms.get(livingroom),appliances.getElectronicApplianceId());
                break;
            case 7:
                s = new Sensor(sId,rooms.get(bedroom),appliances.getElectronicApplianceId());
                break;
            case 8:
                s = new Sensor(sId,rooms.get(bedroom),appliances.getElectronicApplianceId());
                break;
            case 9:
                s = new Sensor(sId,rooms.get(livingroom),appliances.getElectronicApplianceId());
                break;
            case 10:
                s = new Sensor(sId,rooms.get(livingroom),appliances.getElectronicApplianceId());
                break;
            case 11:
                s = new Sensor(sId,rooms.get(livingroom),appliances.getElectronicApplianceId());
                break;
            case 12:
                s = new Sensor(sId,rooms.get(kitchen),appliances.getElectronicApplianceId());
                break;
            case 13:
                s = new Sensor(sId,rooms.get(bedroom),appliances.getElectronicApplianceId());
                break;
            case 14:
                s = new Sensor(sId,rooms.get(livingroom),appliances.getElectronicApplianceId());
                break;
            case 15:
                s = new Sensor(sId,rooms.get(studyroom),appliances.getElectronicApplianceId());
                break;
            case 16:
                s = new Sensor(sId,rooms.get(studyroom),appliances.getElectronicApplianceId());
                break;
            case 17:
                s = new Sensor(sId,rooms.get(bedroom),appliances.getElectronicApplianceId());
                break;
            default:
                s = new Sensor(sId);
        }
        return s;
    }

    public void buildSensorsClusters(){
        sensors.forEach(utilities.dataobjects.sensorobjects.Sensor::buildClusters);
    }

    public void buildDataset(){
        Timestamp iterTimestamp = firstTimestamp;
        Timestamp goalTimestamp = getFuturTimestamp(iterTimestamp, 1);

        while(iterTimestamp.before(goalTimestamp) && iterTimestamp.before(lastTimestamp)){
            ArrayList<Integer> x = new ArrayList<>();
            for(Sensor sensor: sensors){
                if(sensor.checkUsed()) {
                    BaseSensor nearestInstance = sensor.getInstances().get(0);
                    for (BaseSensor instances : sensor.getInstances()) {
                        if (instances.getTimestamp().after(iterTimestamp)) {
                            break;
                        }
                        nearestInstance = instances;
                    }
                    int result = sensor.predict(nearestInstance.getValue());

                    x.add(result);
                } else{
                    x.add(0);
                }
            }

            //sensorDataset.add(new Record(x,0));
            sensorDataset.add(new Record(x,0));

            iterTimestamp = goalTimestamp;
            goalTimestamp = getFuturTimestamp(iterTimestamp, 1);
        }

        save();
    }

    private Timestamp getFuturTimestamp(Timestamp currentTimestamp, int second){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTimestamp.getTime());
        cal.add(Calendar.SECOND, second);
        return new Timestamp(cal.getTime().getTime());
    }

    public Integer predict(int sId, double value){
        return sensors.get(sId).predict(value);
    }

    public int getNumberSensors(){
        return sensors.size();
    }

    public Sensor getSensor(int sId){
        return sensors.get(sId);
    }

    public void save(){
        try {
            FileOutputStream fos = new FileOutputStream(filePath+"/AllSensors");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try {
            FileInputStream fis = new FileInputStream(filePath+"/AllSensors");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Sensors s = (Sensors) ois.readObject();
            ois.close();

            this.firstTimestamp = s.firstTimestamp;
            this.lastTimestamp = s.lastTimestamp;
            this.filePath = s.filePath;
            this.sensors = s.sensors;
            this.sensorDataset = s.sensorDataset;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<Sensor> iterator() {
        return new Iterator<Sensor>() {
            private Integer counter = 0;
            private final int n = sensors.size();

            @Override
            public boolean hasNext() {
                return counter<n;
            }

            @Override
            public Sensor next() {
                return sensors.get(counter++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
