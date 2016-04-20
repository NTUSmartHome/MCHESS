package utilities.communicator;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by g2525_000 on 2016/4/20.
 */
public class MQTTSubscriber {
    public static final String HOST = "tcp://localhost:1883";
    public static final String TOPIC = "solarpower";
    public static final String clientID = "MCHESS";
    private MqttClient client;
    private MqttConnectOptions options;

    public void start(MqttCallback mqttCallback) {
        try {
            //HOST 主機位置，ID 連接MQTT之客戶端ID， MemoryPersistence設置clientid的保存形式，默認是內存
            client = new MqttClient(HOST, clientID, new MemoryPersistence());
            //MQTT的連接設置
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            //設置超時時間，單位為秒
            options.setConnectionTimeout(10);
            //設置確認時間
            options.setKeepAliveInterval(20);
            //設置callback
            client.setCallback(mqttCallback);
            MqttTopic topic = client.getTopic(TOPIC);

            client.connect(options);
            int[] Qos = {1};
            String[] topics = {TOPIC};
            client.subscribe(topics, Qos);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        MQTTSubscriber subscriber = new MQTTSubscriber();
        subscriber.start(new MQTTCallBack());

    }


}
