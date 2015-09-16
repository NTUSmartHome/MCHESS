package utilities.communicator;
import javax.jms.*;

@URL("tcp://140.112.49.154:61616")
@TOPIC("ssh.RAW_DATA")
public class Producer extends BaseConnector {
	String msg;
	@Override
	/** Send the message to ssh.RAW_DATA <br>*/ 
	public void sendOut(String s) {
		msg = s;
        TextMessage message;
		try {
			/** Test the producer is started or not*/
			if(!isStarted()) {
				start();
				getSendor();
			}
			message = session.createTextMessage(s);
			/** Here we are sending the message */
			sendor.send(message);
	        //System.out.println("Sent message: " + message.getText() + "(count " + count + ")");	
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}

	/** Send the message to specific topic <br> 
	 * Please use this method carefully, and not frequently !
	 * @param s message
	 * @param topic listening topic*/
	public void sendOut(String s, String topic) {
		msg = s;
        TextMessage message;
        Destination dest;
		try {
			if(!isStarted()) {
				start();
				getSendor();
			}
			dest = session.createTopic(topic);
			message = session.createTextMessage(s);

			/** Here we are sending the message */
			session.createProducer(dest).send(message);
	        //System.out.println("Sent message: " + message.getText() + "(count " + count + ")");	
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void processMsg(String msg) {
		
	}
	/** Get message*/
	public String getMsg(){
		return msg;
	}
	
}
