package utilities.communicator;

@URL("tcp://140.112.49.154:61616")
@TOPIC("ssh.COMMAND")
public class Consumer extends BaseConnector{

    String msg;
    Listener listener;

	boolean flag;

	Consumer(){
		super();
		flag = false;
	}

	@Override
	public void sendOut(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processMsg(String m) {
		//System.out.println("recv a message from MQ.");
		msg = m;
		flag = true;

		//trigger();
	}
	
	public String getMsg(){
		//System.out.println("QAQ");
		flag = false;
		return msg;
	}

	public boolean isNewMsg(){
		System.out.print("");
		return  flag;
	}

	public void addListener(Listener l) {
		listener = l;
	}
	
	public void trigger() {
		listener.onEvent(msg);
	}
	
}