package utilities.dataobjects.sensorobjects;

import org.json.JSONException;
import org.json.JSONObject;
import utilities.dataobjects.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Created by YaHung on 2015/9/15.
 */
public class SensorTest {
    final String subject = "subject";
    final String comfort_sensor = "comfort_sensor";
    final String socketmeter = "socketmeter";
    final String sensor = "sensor";
    final String ampere = "ampere";
    final String value = "value";
    final String temperature = "temperature";
    final String humidity = "humidity";
    final String lux = "lux";
    Timestamp goalTimestamp;

    SensorFor313 sensorFor313 = new SensorFor313();
    ArrayList<String> listenMsg = new ArrayList<>();


    public SensorTest(){
        buildSensorModel();

        //loadSensorModel();

    }

    private void loadSensorModel(){
        Sensors sensors = new Sensors();
        sensors.load();
        Scanner scanner = new Scanner(System.in);

        sensors.printSensorDataset();
    }



    private void buildSensorModel(){
        goalTimestamp = new Timestamp(System.currentTimeMillis());

        Sensors sensors = new Sensors();
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        while (!line.contains("end")) {
            transferMsg(line);
            line = scanner.nextLine();

            System.out.println(line);

            for(String mmsg: listenMsg) {
                Message msg = new Message(mmsg);
                sensors.add(msg.getId(), msg.getValue());
            }

            listenMsg.clear();
        }

        sensorFor313.printMappingTable();

        sensors.buildSensorsClusters();
        sensors.buildDataset();

        sensors.printSensorDataset();

        sensors.saveDataset();
    }

    private void transferMsg(String msg){
        try {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            while(currentTimestamp.before(goalTimestamp)) {
                currentTimestamp = new Timestamp(System.currentTimeMillis());
            }
                JSONObject jsonObject = new JSONObject(msg);
                int sID = -1;
                if (jsonObject.getString(subject).equals(comfort_sensor)) {
                    sID = sensorFor313.getTransferId(jsonObject.getString(subject), jsonObject.getInt("id"));
                    listenMsg.add("{\"subject\":\"sensor\",\"id\":\"" + sID + "\",\"value\":\"" + jsonObject.getDouble(temperature) + "\"}");
                    listenMsg.add("{\"subject\":\"sensor\",\"id\":\"" + (sID + 1) + "\",\"value\":\"" + jsonObject.getDouble(humidity) + "\"}");
                    listenMsg.add("{\"subject\":\"sensor\",\"id\":\"" + (sID + 2) + "\",\"value\":\"" + jsonObject.getDouble(lux) + "\"}");
                } else if (jsonObject.getString(subject).equals(socketmeter)) {
                    sID = sensorFor313.getTransferId(jsonObject.getString(subject), jsonObject.getInt("id"));
                    listenMsg.add("{\"subject\":\"sensor\",\"id\":\"" + sID + "\",\"value\":\"" + jsonObject.getDouble(ampere) + "\"}");
                }

                // wait QQ
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(currentTimestamp.getTime());
                cal.add(Calendar.MILLISECOND, 8);
                goalTimestamp = new Timestamp(cal.getTime().getTime());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new SensorTest();
    }

}
