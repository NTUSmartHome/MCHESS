import agent.DiscoverEnv;
import ercie.ERCIE;
import utilities.communicator.MQListener;
import utilities.dataobjects.Message;
import utilities.dataobjects.sensorobjects.Sensors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by YaHung on 2015/9/8.
 */
public class MCHESS extends Thread{

    MQListener mq;
    Boolean train = false;
    boolean train_offline = false;
    DiscoverEnv discoverEnv = new DiscoverEnv();
    ERCIE ercie;
    public MCHESS(String option){
        //Build MQ Listener
        mq = new MQListener();
        mq.start();

        System.out.println("Start MCHESS");

        // Initialization
        if(option.equals("-run")){
            train = false;
        }
        else if(option.equals("-train")){
            train = true;
        }
        else if(option.equals("-train_offline")){
            train_offline = true;

        }

        System.out.println("Start MCHESS");

        //ERCIE
        // run without training
        if(train_offline) {

            loadMQOffline("MQ_msg_log_20150925_1025_allActs.txt");

            buildARModel();
        }
        else if(!train){
            ercie = new ERCIE();
            //ercie.loadARModel();
            ercie.loadARModelBaseline();
            ercie.start();
        }


    }

    public void run(){

        while(true){
            if(mq.checkNewMsg()){
                Message msg = mq.getMsg();
                //System.out.println(msg.getSubject());
                if (msg.isSensorData()) {
                    if(train) {
                        //if(!msg.isSensorModelEnd()){
                        if(mq.getFlagFroTrain()){
                            discoverEnv.add(msg);
                        }
                        else {
                            discoverEnv.buildSensorModel();
                            buildARModel();
                        }
                    }
                    else {
                        System.out.println(msg.getSubject()+":"+msg.getValue());
                        ercie.newMsg(msg);
                    }
                }
                else if(msg.isActivity()){

                }
                mq.deleteMsg();
            }
        }
    }


    private void loadMQOffline(String filename){
        Sensors sensors = new Sensors();
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line = "";

            while((line = br.readLine())!= null){
                Timestamp timestamp = Timestamp.valueOf(line);
                line = br.readLine();
                Message msg = new Message(line);
                sensors.add(msg.getId(), msg.getValue(),timestamp);
            }

            sensors.buildSensorsClusters();
            sensors.buildDataset();
            //sensors.printSensorDataset();
            sensors.saveDataset();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildARModel(){
        System.out.println("Call ERCIE...");
        ercie = new ERCIE();
        System.out.println("Going to build AR Model...");
        ercie.buildARModel();
        System.out.println("Built AR Model...");
        train = false;
        ercie.start();
        System.out.println("Started ERCIE...");
    }


    public static void main(String[] args) {

        //Thread mchess = new MCHESS("-train");
        //Thread mchess = new MCHESS("-train_offline");
        Thread mchess = new MCHESS("-run");
        mchess.start();
    }
}
