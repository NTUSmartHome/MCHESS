import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import utilities.communicator.MQTTSubscriber;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by g2525_000 on 2016/6/7.
 */
public class IIIBed {

    public static DateFormat file_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
    public static DateFormat data_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    public static FileWriter fw;
    public int file_flag = 0;
    private String body_move;
    private String breath_rate;
    private String heart_rate;

    private class BedCallBack implements MqttCallback {

        @Override
        public void connectionLost(Throwable throwable) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            //System.out.println("Topic :" + topic);
            //System.out.println("Qos : " + message.getQos());
            JSONObject jsonObject = new JSONObject(new String(message.getPayload())).getJSONObject("sensor");
            JSONObject jsonObject1 = new JSONObject(new String(message.getPayload()));
            if (file_flag == 0) {
                fw.write(data_time.format(System.currentTimeMillis()) + ",");
                file_flag += 1;
            }
            if (topic.contains("0250")) {
                System.out.println("0250");
                if (file_flag == 1) {
                    fw.write("0250" + ",");
                    file_flag += 1;
                }
            }
            if (topic.contains("0550")) {
                System.out.println("0550");
                if (file_flag == 1) {
                    fw.write("0550" + ",");
                    file_flag += 1;
                }
            }
            if (jsonObject.get("_id").equals("188")) {
                System.out.println("呼吸 : " + jsonObject1.get("data"));
                //fw.write("呼吸:" + jsonObject1.get("data") + ";");
                breath_rate = jsonObject1.get("data").toString();
                file_flag += 1;
                if (file_flag == 5) {
                    file_flag = 0;
                    fw.write(body_move + "," + breath_rate + "," + heart_rate + "\r\n");
                    fw.flush();
                }
            }
            else if (jsonObject.get("_id").equals("187")) {
                System.out.println("心跳 : " + jsonObject1.get("data"));
                //fw.write("心跳:" + jsonObject1.get("data") + ";");
                heart_rate = jsonObject1.get("data").toString();
                file_flag += 1;
                if (file_flag == 5) {
                    file_flag = 0;
                    fw.write(body_move + "," + breath_rate + "," + heart_rate + "\r\n");
                    fw.flush();
                }
            }
            else if (jsonObject.get("_id").equals("186")) {
                System.out.println("翻動 : " + jsonObject1.get("data"));
                //fw.write("翻動:" + jsonObject1.get("data") + ";");
                body_move = jsonObject1.get("data").toString();
                file_flag += 1;
                if (file_flag == 5) {
                    file_flag = 0;
                    fw.write(body_move + "," + breath_rate + "," + heart_rate + "\r\n");
                    fw.flush();
                }
            }
            //System.out.println("Message : " + new String(message.getPayload()));

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    }

    final String HOST = "tcp://getsmartx.com:1883";
    MQTTSubscriber bedSubscriber;

    public IIIBed() {
        try {
            fw = new FileWriter(file_time.format(System.currentTimeMillis()) +".txt",false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bedSubscriber = new MQTTSubscriber(HOST);
        bedSubscriber.getOptions().setUserName("ntu_cs");
        bedSubscriber.getOptions().setPassword("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI1NzQyODcwNjE4MTMxZTBmYTRiNWEwZDgiLCJpYXQiOjE0NjUyNzE0OTMsImV4cCI6MTQ2NjQ4MTA5MywiaXNzIjoiZ2V0c21hcnR4LmNvbSJ9.VaYLNtBAU1f4qxPJpoqLRYodLfuCxt2Om5e4SeDFFIk".toCharArray());
        bedSubscriber.setMqttCallback(new BedCallBack());
        bedSubscriber.start();
        System.out.println("connect success");
        bedSubscriber.subscribe("5742870618131e0fa4b5a0d8/055000000000000000000000_D");
        bedSubscriber.subscribe("5742870618131e0fa4b5a0d8/025000000000000000000000_D");
    }

    public static void main(String[] args) {
        new IIIBed();
    }
    /*public static void main(String[] args) {

        String topic = "MQTT Examples";
        String content = "Message from MqttPublishSample";
        int qos = 2;
        String broker = "tcp://getsmartx.com:1883";
        String clientId = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("ntu_cs");
            connOpts.setPassword("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI1NzQyODcwNjE4MTMxZTBmYTRiNWEwZDgiLCJpYXQiOjE0NjUyNzE0OTMsImV4cCI6MTQ2NjQ4MTA5MywiaXNzIjoiZ2V0c21hcnR4LmNvbSJ9.VaYLNtBAU1f4qxPJpoqLRYodLfuCxt2Om5e4SeDFFIk".toCharArray());

            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }*/
}
