package utilities.communicator;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.Message;
import org.apache.activemq.broker.region.Destination;
import sun.misc.MessageUtils;
import utilities.JsonBuilder;

import javax.jms.*;

/**
 * Created by YaHung on 2015/9/14.
 */
public class MQTest {
    private Producer producer = new Producer();
    private JsonBuilder json = new JsonBuilder();
    private int reconnect_counter = 0;

    private Consumer consumer = new Consumer();

    private String urlAddress = "tcp://140.112.49.154:61616";

    MQTest(){


    }

    private void producerTest(){
        producer.setURL(urlAddress);
        while(!producer.connect());
        producer.getSendor();
        json.reset();
        producer.sendOut(json.add("subject", "activity").add("value","Sleeping").toJson(), "ssh.CONTEXT");
        while(true){

            for (int i = 0; i <10000 ; i++) {
                for (int j = 0; j < 1000; j++) {

                }
            }
            producer.sendOut(json.add("subject", "activity").add("value","Sleeping").toJson(), "ssh.CONTEXT");


        }
    }

    private void consumerTest(){
        this.consumer.setURL(urlAddress);
        this.consumer.setTopic("ssh.RAW_DATA");
        this.consumer.connect();
        this.consumer.listen();

        while(true){
            String msg = this.consumer.getMsg();
            System.out.println(msg);
        }

    }

    public static void main(String[] args) {

        MQTest mq = new MQTest();
        mq.consumerTest();
    }
}
