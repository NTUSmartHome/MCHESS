package agent;

import utilities.dataobjects.Message;
import utilities.dataobjects.sensorobjects.Sensor;
import utilities.dataobjects.sensorobjects.Sensors;

import java.sql.Timestamp;
import java.util.Scanner;

/**
 * Created by YaHung on 2015/9/23.
 */
public class DiscoverEnv {
    Sensors sensors = new Sensors();
    public DiscoverEnv(){
    }

    public void add(Message msg){
        sensors.add(msg.getId(), msg.getValue());
    }

    public void loadSensorDataset(){
        sensors.load();
    }

    public void buildSensorModel(){
        sensors.buildSensorsClusters();
        sensors.buildDataset();

        sensors.printSensorDataset();

        sensors.saveDataset();
    }
}
