import agent.DiscoverEnv;
import ercie.ERCIE;
import utilities.communicator.MQListener;
import utilities.dataobjects.Message;
import utilities.dataobjects.homeobjects.Rooms;

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
        System.out.println("Start MCHESS");

        //Build MQ Listener
        mq = new MQListener();
        mq.start();
        System.out.println("MQ is opened");

        // Initialization
        if(option.equals("-run")){
            train = false;

            ercie = new ERCIE();
            ercie.loadARModel();
            ercie.start();
        }
        else if(option.equals("-train")){
            train = true;
        }
        else if(option.equals("-train_offline")){
            train_offline = true;

            loadMQOffline("MQ_msg_log_20150925_1025_allActs.txt");
            buildARModel();
        }

    }

    public void run(){
        while(true){
            if(mq.checkNewMsg()){
                Message msg = mq.getMsg();
                if (msg.isSensorData()) {
                    if(train) {
                        if(mq.getFlagFroTrain()){
                            discoverEnv.add(msg);
                        }
                        else {
                            buildARModel();
                        }
                    }
                    else {
                        //System.out.println("\r\n"+msg.getSubject()+" "+msg.getId()+": "+msg.getValue());
                        ercie.newMsg(msg);
                    }
                }
                else if(msg.isActivity()){

                }
                else if(msg.isLabelAct()){
                    ercie.setActName(msg.getId(),msg.getActivityName());
                }
                mq.deleteMsg();
            }
        }
    }


    private void loadMQOffline(String filename){
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine())!= null){
                Timestamp timestamp = Timestamp.valueOf(line);
                line = br.readLine();
                Message msg = new Message(line);
                discoverEnv.add(msg.getId(), msg.getValue(),timestamp);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildARModel(){
        System.out.println("Build sensor clusters...");
        discoverEnv.buildSensorModel();
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
        Rooms rooms = new Rooms();
        rooms.add("Living room");
        rooms.add("Hallway");
        rooms.add("Study room");
        rooms.add("Kitchen");
        rooms.add("Bedroom");
        rooms.save();

        //Thread mchess = new MCHESS("-train");
        Thread mchess = new MCHESS("-train_offline");
        //Thread mchess = new MCHESS("-run");
        mchess.start();
    }
}
