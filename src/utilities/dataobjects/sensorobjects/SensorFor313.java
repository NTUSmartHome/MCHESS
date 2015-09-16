package utilities.dataobjects.sensorobjects;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YaHung on 2015/9/15.
 */
public class SensorFor313 {
    ArrayList<Integer> sensorIdList = new ArrayList<>();

    public SensorFor313(){

    }

    public void loadMappingTable(){
        try {
            FileReader fr = new FileReader("util/sensorMappingTable.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line=br.readLine())!=null){
                String[] tmp = line.split(",");
                sensorIdList.add(Integer.valueOf(tmp[1]));
            }

            br.close();
            fr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printMappingTable(){
        try {
            FileWriter fw = new FileWriter("util/sensorMappingTable.txt");
            for (int i = 0; i < sensorIdList.size(); i++) {
                fw.write(i+","+sensorIdList.get(i)+"\r\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTransferId(String subject, int id){
        int tSId = -1;
        if(subject.contains("comfort_sensor")){
            boolean exist = false;
            for(int i=0; i<sensorIdList.size(); i++){
                if(sensorIdList.get(i) == id){
                    exist = true;
                    tSId = i;
                    break;
                }
            }
            if(!exist){
                tSId = sensorIdList.size();
                sensorIdList.add(id);
                sensorIdList.add(id);
                sensorIdList.add(id);
            }
        }
        else if(subject.contains("socketmeter")){
            boolean exist = false;
            for(int i=0; i<sensorIdList.size(); i++){
                if(sensorIdList.get(i) == id){
                    exist = true;
                    tSId = i;
                    break;
                }
            }
            if(!exist){
                tSId = sensorIdList.size();
                sensorIdList.add(id);
            }
        }
        return tSId;
    }

}
