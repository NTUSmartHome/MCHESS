package utilities.communicator;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by g2525_000 on 2016/4/20.
 */
public class MQTTCallBack implements MqttCallback {
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Disconnected");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Topic :" + topic);
        System.out.println("Qos : " + message.getQos());
        System.out.println("Message : " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
