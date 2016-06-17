package utilities.communicator;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import utilities.dataobjects.Message;

/**
 * Created by g2525_000 on 2016/4/20.
 */
public class MQTTSubscriber {
    private String HOST = "tcp://localhost:1883";
    private String TOPIC = "smartthings-pei";
    private String clientID = "MCHESS-test";
    private MqttClient client;
    private MqttConnectOptions options;
    private MqttCallback mqttCallback;
    private int[] Qos;
    public boolean flag = false;
    Message msg = new Message("{\"subject\":\"start\"}");
    JSONObject jsonmsg = new JSONObject();


    public MQTTSubscriber() {
        //HOST 主機位置，ID 連接MQTT之客戶端ID， MemoryPersistence設置clientid的保存形式，默認是內存
        try {
            client = new MqttClient(HOST, clientID, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //MQTT的連接設置
        options = new MqttConnectOptions();
        options.setCleanSession(true);
        //設置超時時間，單位為秒
        options.setConnectionTimeout(10);
        //設置確認時間
        options.setKeepAliveInterval(20);
    }

    public MQTTSubscriber(String host) {
        //HOST 主機位置，ID 連接MQTT之客戶端ID， MemoryPersistence設置clientid的保存形式，默認是內存
        this.HOST = host;
        try {
            client = new MqttClient(HOST, clientID, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //MQTT的連接設置
        options = new MqttConnectOptions();
        options.setCleanSession(true);
        //設置超時時間，單位為秒
        options.setConnectionTimeout(10);
        //設置確認時間
        options.setKeepAliveInterval(20);

    }

    public void start(MqttCallback mqttCallback) {
        try {

            this.mqttCallback = mqttCallback;
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
            Qos = new int[]{1};
            String[] topics = {TOPIC};
            client.subscribe(topics, Qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (options == null) {
            System.out.println("Please set options");
            return;
        }

        if (mqttCallback == null) {
            System.out.println("Please set mqttCallback");
            return;
        }
        if (client == null) {
            System.out.println("Please set MqttClient");
            return;
        }

        try {
            client.connect(options);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void subscribe(String topic) {
        try {

            int[] qos ={1};
            client.subscribe(new String[]{topic});
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setTOPIC(String TOPIC) {
        this.TOPIC = TOPIC;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public int[] getQos() {
        return Qos;
    }

    public void setQos(int[] qos) {
        Qos = qos;
    }

    public String getHOST() {
        return HOST;
    }

    public String getTOPIC() {
        return TOPIC;
    }

    public String getClientID() {
        return clientID;
    }

    public MqttConnectOptions getOptions() {
        return options;
    }

    public void setOptions(MqttConnectOptions options) {
        this.options = options;
    }

    public MqttClient getClient() {
        return client;
    }

    public void setClient(MqttClient client) {
        this.client = client;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public MqttCallback getMqttCallback() {
        return mqttCallback;
    }

    public void setMqttCallback(MqttCallback mqttCallback) {
        //設置callback
        client.setCallback(mqttCallback);
        this.mqttCallback = mqttCallback;
    }

    public static void main(String[] args) {
        MQTTSubscriber subscriber = new MQTTSubscriber();
        subscriber.start(new MQTTCallBack());

    }

    public void newmsg(){
        flag = true;
    }

    public void setmsg(){
        flag = false;
    }

    public boolean checknewmsg(){
        return flag;
    }

    public void setjsonmsg(JSONObject jmsg){
        jsonmsg = jmsg;
    }

    public JSONObject getjsonmsg(){
        return jsonmsg;
    }
}
