package utilities.dataobjects.sensorobjects;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by YaHung on 2015/9/15.
 */
public class BaseSensor implements Serializable {
    double value;
    Timestamp timestamp;

    public BaseSensor(double value){
        this.value = value;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public BaseSensor(double value, Timestamp timestamp){
        this.value = value;
        this.timestamp = timestamp;
    }

    public void setValue(double value){
        this.value = value;
    }
    public double getValue(){ return value;}

    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }
    public void setCurrentTimestamp(){
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
    public Timestamp getTimestamp(){ return timestamp;}

}
