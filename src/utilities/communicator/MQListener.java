package utilities.communicator;

import utilities.dataobjects.Message;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by YaHung on 2015/9/23.
 */
public class MQListener extends Thread{

    private Consumer consumer = new Consumer();

    private String urlAddress = "tcp://140.112.49.154:61616";

    Message msg = new Message("{\"subject\":\"start\"}");

    String msg_="";

    public boolean flag = false;
    public boolean flagForTrain = true;

    public MQListener(){
        this.consumer.setURL(urlAddress);
        this.consumer.setTopic("ssh.RAW_DATA");
        this.consumer.connect();
        this.consumer.listen();

    }

    public MQListener(String topic){
        this.consumer.setURL(urlAddress);
        this.consumer.setTopic("ssh."+topic);
        this.consumer.connect();
        this.consumer.listen();
    }


    public void run(){
        try {
            while(true){
                if(this.consumer.isNewMsg()) {

                    FileWriter fw = new FileWriter("MQ_msg_log.txt", true);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    msg_ = this.consumer.getMsg();
                    if (msg_ != null) {
                        flag = true;
                        msg = new Message(msg_);
                        checkEndTrain();
                        fw.write(timestamp.toString() + "\r\n");
                        fw.write(msg_ + "\r\n");
                    }
                    fw.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMsg_(){
        return msg_;
    }

    public Message getMsg(){
        return msg;
    }

    public boolean checkNewMsg(){
        System.out.print("");
        return flag;
    }

    public void deleteMsg(){
        flag = false;
    }

    public void checkEndTrain(){
        if(msg.isSensorModelEnd()){
            flagForTrain = false;
        }
    }

    public boolean getFlagFroTrain(){
        return flagForTrain;
    }
}
