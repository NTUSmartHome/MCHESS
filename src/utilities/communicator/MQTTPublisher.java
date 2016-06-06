package utilities.communicator;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by g2525_000 on 2016/4/20.
 */
public class MQTTPublisher {
    private String HOST = "tcp://localhost:1883";
    private String TOPIC = "solarpower";
    private String clientID = "MCHESSServer";
    private MqttClient client;

    public MQTTPublisher() throws MqttException {
        //HOST 主機位置，ID 連接MQTT之客戶端ID， MemoryPersistence設置clientid的保存形式，默認是內存
        client = new MqttClient(HOST, clientID, new MemoryPersistence());
        connect();
    }

    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        //設置超時時間，單位為秒
        options.setConnectionTimeout(10);
        //設置確認時間
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new MQTTCallBack());
            client.connect(options);
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message, int Qos) throws MqttPersistenceException, MqttException{
        MqttTopic mqttTopic = client.getTopic(topic);
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(Qos);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
        System.out.println("Message is published");
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public String getTOPIC() {
        return TOPIC;
    }

    public void setTOPIC(String TOPIC) {
        this.TOPIC = TOPIC;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public MqttClient getClient() {
        return client;
    }

    public void setClient(MqttClient client) {
        this.client = client;
    }

    public static void main(String[] args) throws MqttException {
        MQTTPublisher publisher = new MQTTPublisher();
        publisher.publish(publisher.TOPIC, "test", 2);

        System.out.println("Over");
    }

}
