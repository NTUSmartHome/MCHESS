package utilities.dataobjects.sensorobjects;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;
import utilities.machinelearning.clusters.AffinityPropagation.BaseAffinityPropagation;

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

    public void printSensorDataset(){
        for(Integer rId: sensorDataset){
            System.out.println("Feature: "+sensorDataset.get(rId).getX()+", Result: "+sensorDataset.get(rId).getY());
        }
    }

    public void printSensorsClusters(){
        for(Sensor sensor: sensors){
            sensor.getClusterList().printClusters(filePath+"/"+sensor.getId()+".txt");
        }
    }

    public void add(int sId, double value){
        lastTimestamp = new Timestamp(System.currentTimeMillis());

        if(firstTimestamp==null){
            firstTimestamp = lastTimestamp;
        }
        if( sId >= sensors.size()){
            while( sId >= sensors.size()){
                Sensor sensor = new Sensor(sensors.size());
                sensors.add(sensor);
            }
        }
        sensors.get(sId).add(value);
    }

    public void buildSensorsClusters(){
        for(Sensor sensor: sensors){
            sensor.buildClusters();
        }
    }

    public void buildDataset(){
        Timestamp iterTimestamp = firstTimestamp;
        while(iterTimestamp.before(lastTimestamp)){
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
                    x.add(sensor.predict(nearestInstance.getValue()));
                }
                else{
                    x.add(0);
                }

            }
            sensorDataset.add(new Record(x,0));

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(iterTimestamp.getTime());
            cal.add(Calendar.SECOND, 1);
            iterTimestamp = new Timestamp(cal.getTime().getTime());
        }

        save();
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
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath+"/AllSensors");
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
        Sensors s;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath+"/AllSensors");
            ObjectInputStream ois = new ObjectInputStream(fis);
            s = (Sensors) ois.readObject();
            ois.close();
            //this.id = s.getId();
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
