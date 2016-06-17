import com.sun.org.apache.bcel.internal.classfile.Synthetic;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import utilities.JsonBuilder;
import utilities.communicator.MQListener;
import utilities.communicator.MQTTSubscriber;
import utilities.communicator.Producer;
import utilities.dataobjects.Message;

import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.io.IOException;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import static java.lang.String.valueOf;

import com.company.*;

/**
 * Created by YaHung on 2015/9/8.
 */
public class MCHESS extends Thread {
    SHSystem sh = new SHSystem();
    //people
    int livingP = 0;
    int bedP = 0;
    int studyP = 0;
    int kitchenP = 0;
    int totalP = 0;
    //ap
    boolean nightlamp = false;
    boolean tv = false;
    boolean xbox = false;
    boolean pc = false;
    boolean livinglamp = false;
    boolean fan = false;
    boolean hue = false;
    boolean hue1 = false;
    boolean hue2 = false;
    boolean outlet1 = false;
    boolean outlet2 = false;
    //activities
    boolean sleep = false;
    boolean watchTV = false;
    boolean playXbox = false;
    boolean goOut = false;
    boolean comeBack = false;
    boolean usingPC = false;
    boolean usingPCnWatchTV = false;
    boolean reading = false;
    boolean readingnWatchTV = false;
    //light
    int lightC = 0;
    int lightR = 0;
    int lightB = 0;
    int lightK = 0;
    int lightS = 0;
    int lightH = 0;

    double[] message = new double[9];


    boolean firstAct = false;
    boolean checkGoOut = false;
    JSONObject newMsg = new JSONObject();

    FileWriter pw;

    MQListener mq;
    MQTTSubscriber sad;

    public MCHESS(String option){
        System.out.println("Start MCHESS");
        //Build MQ Listener
        mq = new MQListener();
        sad = new MQTTSubscriber();
        //sad.start(new mchess_callback());
        System.out.println("MQ is opened");
        Thread mqt = new Thread(new Runnable() {
            @Override
            public void run() {
                mq.start();
                sad.start(new mchess_callback());
            }
        });
        mqt.start();
    }



    public void run(){
        //Open File
        Timestamp timestamps =new Timestamp(System.currentTimeMillis());
        String fileName = timestamps.toString();
        try {
            pw = new FileWriter(fileName, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<9;i++) message[i] = 0.0;

        while(true){
            Thread.yield();
            if(sh.isCollect()) {
                sh.setMessage(viewData());
            }
            if(sad.checknewmsg()||mq.checkNewMsg()){
                JSONObject msg2 = newMsg;
                Message msg = mq.getMsg();
                if(sh.isRecognize())
                    sh.recognize(passData());
                if (msg.isSensorData()) {
                    if(mq.checkNewMsg()) {
                        if (msg.getSubject().equals("people")) {
                            if (msg.getId() == 1) {
                                //System.out.println("living room people is "+msg.getId());
                                //System.out.println("L:[" + livingP + ']');
                                livingP = (int) msg.getValue();
                                message[0] = livingP;
                                //System.out.println("L:"+livingP);
                            } else if (msg.getId() == 5) {
                                //System.out.println("B:["+bedP+']');
                                bedP = (int) msg.getValue();
                                message[1] = bedP;
                                //System.out.println("B:"+bedP);
                            } else if (msg.getId() == 4) {

                                kitchenP = (int) msg.getValue();
                                message[3] = kitchenP;
                            } else if (msg.getId() == 2) {
                                //System.out.println("S:["+studyP+']');
                                studyP = (int) msg.getValue();
                                message[2] = studyP;
                                //System.out.println("S:"+studyP);
                            }
                        }
                    }
                }
                try {
                    /*if(msg2.getString("name").equals("switch")) {
                        if (msg2.getString("displayName").equals("Hue Lamp")) {
                            hue = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Hue Lamp 1")) {
                            hue1 = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Hue Lamp 2")) {
                            hue2 = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet1")) {
                            outlet1 = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet2")) {
                            outlet2 = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet_Bedroom_PC")) {
                            pc = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet_LivingRoom_Fan")) {
                            fan = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet_LivingRoom_TV")) {
                            tv = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet_NightLamp")) {
                            nightlamp = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet_GroundLamp")) {
                            livinglamp = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        if (msg2.getString("displayName").equals("Outlet_Xbox")) {
                            xbox = ((msg2.getString("value").equals("on")) ? true : false);
                        }
                        sad.setmsg();
                    }*/
                    if(sad.checknewmsg()) {
                        if (msg2.getString("name").equals("power") ) {
                            if (msg2.getString("displayName").equals("Hue Lamp")) {
                                hue = ((msg2.getString("value").equals("on")) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Hue Lamp 1")) {
                                hue1 = ((msg2.getString("value").equals("on")) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Hue Lamp 2")) {
                                hue2 = ((msg2.getString("value").equals("on")) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet1")) {
                                outlet1 = ((msg2.getString("value").equals("on")) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet2")) {
                                outlet2 = ((msg2.getString("value").equals("on")) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet_Bedroom_PC")) {
                                pc = ((Double.parseDouble(msg2.getString("value")) > 3) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet_LivingRoom_Fan")) {
                                fan = ((Double.parseDouble(msg2.getString("value")) > 3) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet_LivingRoom_TV")) {
                                tv = ((Double.parseDouble(msg2.getString("value")) > 3) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet_NightLamp")) {
                                nightlamp = ((Double.parseDouble(msg2.getString("value")) > 2) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet_GroundLamp")) {
                                livinglamp = ((Double.parseDouble(msg2.getString("value")) > 1) ? true : false);
                            }
                            if (msg2.getString("displayName").equals("Outlet_Xbox")) {
                                xbox = ((Double.parseDouble(msg2.getString("value")) > 3) ? true : false);
                            }
                            sad.setmsg();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(firstAct){
                    if(sh.isCollect()) {
                        sh.setMessage(viewData());
                    }
                    timestamps = new Timestamp(System.currentTimeMillis());
                    int nightlampInt = boolToInt(nightlamp);
                    int tvInt = boolToInt(tv);
                    int xboxInt = boolToInt(xbox);
                    int pcInt = boolToInt(pc);
                    int livinglampInt = boolToInt(livinglamp);
                    int fanInt = boolToInt(fan);
                    int hueInt = boolToInt(hue);
                    int hue1Int = boolToInt(hue1);
                    int hue2Int = boolToInt(hue2);
                    int outlet1Int = boolToInt(outlet1);
                    int outlet2Int = boolToInt(outlet2);
                    //livingP+bedP+StudyP+KitchenP+nightlamp+tv+xbox+pc+livinglamp
                    String data = valueOf(livingP) + "\t" + valueOf(bedP) + "\t" + valueOf(studyP) + "\t" + valueOf(kitchenP) + "\t" + valueOf(nightlampInt) + "\t"+ valueOf(tvInt) + "\t" + valueOf(xboxInt) + "\t"
                            + valueOf(pcInt) + "\t"+ valueOf(livinglampInt) + "\t"+ valueOf(fanInt) + "\t"+ valueOf(hueInt) + "\t"+ valueOf(hue1Int) + "\t"+ valueOf(hue2Int) + "\t"+ valueOf(outlet1Int) + "\t"+ valueOf(outlet2Int) + "\n";
                    String output = timestamps.toString() + "\t" + data;
                    try {
                        pw.append(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        pw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(checkAct2()){
                    firstAct = true;
                    try {
                        // System.out.println(System.currentTimeMillis());
                        sleep(500);
                        //System.out.println(System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkControl();
                }
                //System.out.println("All device :"+hue+hue1+hue2+outlet1+outlet2+pc+fan+tv+nightlamp+livinglamp+xbox);
            }
        }
    }

    public void run2(){
        //Open File
        Timestamp timestamps =new Timestamp(System.currentTimeMillis());
        String fileName = timestamps.toString();
        try {
            pw = new FileWriter(fileName, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<9;i++) message[i] = 0.0;

        while(true){
            if(sh.isCollect()) {
                sh.setMessage(viewData());
            }
            if(mq.checkNewMsg()){
                Message msg = mq.getMsg();
                if(sh.isRecognize())
                    sh.recognize(passData());
                if (msg.isSensorData()) {
                    if (msg.getSubject().equals("people")) {
                        if (msg.getId() == 1 ) {
                            //System.out.println("living room people is "+msg.getId());
                            System.out.println("L:["+livingP+']');
                            livingP = (int)msg.getValue();
                            message[0] = livingP;
                            //System.out.println("L:"+livingP);
                        }
                        else if (msg.getId() == 5 ) {
                            //System.out.println("B:["+bedP+']');
                            bedP = (int)msg.getValue();
                            message[1] = bedP;
                            //System.out.println("B:"+bedP);
                        }
                        else if (msg.getId() == 4 ) {

                            kitchenP = (int)msg.getValue();
                            message[3] = kitchenP;
                        }
                        else if (msg.getId() == 2 ) {
                            //System.out.println("S:["+studyP+']');
                            studyP = (int)msg.getValue();
                            message[2] = studyP;
                            //System.out.println("S:"+studyP);
                        }
                    }
                    if (msg.getSubject().equals("socketmeter")) {
                        if (msg.getId() == 8 ) {
                            message[4] = msg.getValue();
                            //System.out.println("Night Lamp ampere is "+msg.getValue());
                            if(msg.getValue() > 0.05){
                                nightlamp = true;
                            }
                            else{
                                nightlamp = false;
                            }
                        }
                        else if (msg.getId() == 9 ) {
                            message[6] = msg.getValue();
                            //System.out.println("XBOX ampere is "+msg.getValue());
                            if(msg.getValue() > 0.7){

                                xbox = true;
                            }
                            else{
                                xbox = false;
                            }
                        }
                        else if (msg.getId() == 7 ) {
                            message[5] = msg.getValue();
                            //System.out.println("TV ampere is "+msg.getValue());
                            if(msg.getValue() > 0.3){
                                tv = true;
                            }
                            else{
                                tv = false;
                            }
                        }
                        else if(msg.getId() == 10){
                            message[7] = msg.getValue();
                            // pc
                            if(msg.getValue() > 0.5){
                                pc = true;
                            }
                            else {
                                pc = false;
                            }
                        }
                        else if(msg.getId() == 11){
                            //Living Lamp
                            message[8] = msg.getValue();
                            if(msg.getValue() > 0.1){
                                livinglamp = true;
                            }else{
                                livinglamp = false;
                            }
                        }
                    }
                    if (msg.getSubject().equals("light")) {
                        if (msg.getId() == 1 ) {
                            //System.out.println("living room central light is "+msg.getValue());
                            lightC = (int)msg.getValue();
                        }
                        else if (msg.getId() == 2 ) {
                            //System.out.println("living room ring light is "+msg.getValue());
                            lightR = (int)msg.getValue();
                        }
                        else if (msg.getId() == 4 ) {
                            lightB = (int)msg.getValue();
                        }
                        else if (msg.getId() == 5 ) {
                            lightK = (int)msg.getValue();
                        }
                        else if (msg.getId() == 6 ) {
                            lightS = (int)msg.getValue();
                        }
                    }
                }
                if(firstAct){
                    if(sh.isCollect()) {
                        sh.setMessage(viewData());
                    }
                    timestamps = new Timestamp(System.currentTimeMillis());
                    int nightlampInt = boolToInt(nightlamp);
                    int tvInt = boolToInt(tv);
                    int xboxInt = boolToInt(xbox);
                    int pcInt = boolToInt(pc);
                    int livinglampInt = boolToInt(livinglamp);
                    //livingP+bedP+StudyP+KitchenP+nightlamp+tv+xbox+pc+livinglamp
                    String data = valueOf(livingP) + "\t" + valueOf(bedP) + "\t" + valueOf(studyP) + "\t" + valueOf(kitchenP) + "\t" + valueOf(nightlampInt) + "\t"+ valueOf(tvInt) + "\t" + valueOf(xboxInt) + "\t"
                            + valueOf(pcInt) + "\t"+ valueOf(livinglampInt) + "\n";
                    String output = timestamps.toString() + "\t" + data;
                    try {
                        pw.append(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        pw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(checkAct2()){
                    firstAct = true;
                    try {
                        // System.out.println(System.currentTimeMillis());
                        sleep(500);
                        //System.out.println(System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkControl();
                }
                mq.deleteMsg();
            }
        }
    }

    public void checkControl(){
        passData();
        if(sleep){
            forSleep();
        }
        else if(watchTV){
            forWatchTV();
        }
        else if(playXbox){
            forPlayXbox();
        }
        else if(goOut){
            forGoout();
        }
        else if(comeBack){
            forComeBack();
        }
        else if(usingPC){
            forUsingPC();
        }
        else if(usingPCnWatchTV) {
            forUsingPCnWatchTV();
        }
        else if(reading){
            forreading();
        }
        else if(readingnWatchTV){
            forreadingnWatchTV();
        }
    }

    private void closeLightR(){
        if(lightR>0){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "livingroom-ring-light_0").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }
    private void closeLightC(){
        if(lightC>0){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "livingroom-central-light_0").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }

    private void closeLightS(){
        if(lightS>0){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "study-light_0").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }

    private void closeLightK(){
        if(lightK>0){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "kitchen-light_0").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }

    private void closeLightB(){
        if(lightB>0){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "bedroom-light_0").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }
    private void closeAllLight(){
        closeLightB();

        try {
           // System.out.println(System.currentTimeMillis());
            sleep(500);
            //System.out.println(System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeLightC();
        for (int i = 0; i < 50000; i++) { }
        closeLightK();
        for (int i = 0; i < 50000; i++) { }
        closeLightS();
        for (int i = 0; i < 50000; i++) { }
        closeLightR();
    }
    private void closeLightWithoutCR(){
        closeLightB();
        closeLightK();
        closeLightS();
    }

    private void openLightC(){
        if(lightC<20){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "livingroom-central-light_90").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }

    private void openLightC_Low(){
        if(lightC<20){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "livingroom-central-light_20").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }

    private void openLightR(){
        if(lightR<20){
            //System.out.println("ring light is close");
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "livingroom-ring-light_90").add("change", "Darken").toJson(),"ssh.COMMAND");
        }
    }

    private void closeLightH(){

        Producer producer = new Producer();
        producer.setURL("tcp://140.112.49.154:61616");
        while(!producer.connect());
        producer.getSendor();

        JsonBuilder json = new JsonBuilder();
        json.reset();
        producer.sendOut(json.add("value", "DOOR-LIGHT_OFF").toJson(),"ssh.COMMAND");

    }

    private void openLightH(){

            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "DOOR-LIGHT_ON").toJson(),"ssh.COMMAND");

    }

    private void closeTV(){
        if(tv){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "TV_OFF").toJson(),"ssh.COMMAND");
        }
    }

    private void closeXBOX(){
        if(xbox){
            Producer producer = new Producer();
            producer.setURL("tcp://140.112.49.154:61616");
            while(!producer.connect());
            producer.getSendor();

            JsonBuilder json = new JsonBuilder();
            json.reset();
            producer.sendOut(json.add("value", "XBOX_STOP").toJson(),"ssh.COMMAND");
        }
    }

    private void forSleep(){
        closeAllLight();
        closeTV();
        closeXBOX();
        sendAct("Sleeping");
        System.out.println("Sleep");
    }

    private void forWatchTV(){
        //closeLightWithoutCR();
        closeLightS();
        closeLightB();
        openLightC();
        openLightR();
        sendAct("WatchingTV");
        System.out.println("watch TV");
    }

    private void forPlayXbox(){
        closeLightWithoutCR();
        closeLightR();
        openLightC_Low();
        sendAct("PlayingKinect");
        System.out.println("Play kinect");
    }

    private void forUsingPC(){
        closeLightS();
        closeLightC();
        closeLightR();
        sendAct("UsingPC");
        System.out.println("Using PC");
    }

    private void forUsingPCnWatchTV(){
        closeLightS();
        openLightR();
        openLightC();
        sendAct("UsingPC#WatchingTV");
        System.out.println("UsingPC n WatchingTV");
    }

    private void forreading(){
        closeLightWithoutCR();
        openLightC();
        closeLightR();
        sendAct("Reading");
        System.out.println("Reading");
    }

    private void forreadingnWatchTV(){
        closeLightWithoutCR();
        openLightC();
        closeLightR();
        sendAct("Reading#WatchingTV");
        System.out.println("Raeding n WatchingTV");
    }

    Timestamp goOutTime ;
    long checkGoOutTime;
    boolean goOutDuration = false;
    private void forGoout(){
        closeAllLight();
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeTV();
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeXBOX();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        openLightH();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, 15);
        goOutTime = new Timestamp(cal.getTime().getTime());
        goOutDuration = true;

        sendAct("GoOut");
        System.out.println("Go out");
    }

    private void forComeBack(){
        closeAllLight();
        openLightH();
        sendAct("ComeBack");
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeLightH();
        openLightC();
       // sendAct("ComeBack");
        System.out.println("Come back");
    }

    private void sendAct(String activity){
        Producer producer = new Producer();
        producer.setURL("tcp://140.112.49.154:61616");
        while(!producer.connect());
        producer.getSendor();

        JsonBuilder json = new JsonBuilder();
        json.reset();
        producer.sendOut(json.add("subject","activity").add("name", activity).add("GA","g1-1").toJson(),"ssh.CONTEXT");
    }

    private void setDefault(){
        sleep = false;
        watchTV = false;
        playXbox = false;
        goOut = false;
        comeBack = false;
        usingPC = false;
        checkGoOut = false;
        reading = false;
        usingPCnWatchTV = false;
        readingnWatchTV = false;
    }

    public boolean checkAct2(){
        boolean changeAct = false;
        if(!goOutDuration){
            totalP = livingP + bedP + studyP;      //Total
            if(bedP > 0 && livingP > 0 && pc && tv){
                //usingPC n Watching TV
                if (!usingPCnWatchTV) {
                    setDefault();
                    usingPCnWatchTV = true;
                    changeAct = true;
                }
            }else if(livinglamp && tv && livingP > 1){
                //Reading n Watching TV
                if(!readingnWatchTV){
                    setDefault();
                    readingnWatchTV = true;
                    changeAct = true;
                }
            }else if(tv && xbox && livingP > 0){
                //Playing Xbox
                if (!playXbox) {
                    setDefault();
                    playXbox = true;
                    changeAct = true;
                }
            }else if(nightlamp && bedP > 0){
                //Sleep
                if (!sleep) {
                    setDefault();
                    sleep = true;
                    changeAct = true;
                }
            }else if(tv && livingP > 0){
                //WatchingTV
                if (!watchTV) {
                    setDefault();
                    watchTV = true;
                    changeAct = true;
                }
            }else if (pc && bedP > 0){
                //UsingPC
                if (!usingPC) {
                    setDefault();
                    usingPC = true;
                    changeAct = true;
                }
            }else if(livinglamp && livingP > 0){
                //Reading
                if(!reading){
                    setDefault();
                    reading = true;
                    changeAct = true;
                }
            }else if(livingP == 0 && bedP == 0 && studyP == 0 && kitchenP == 0){
                //Go out
                if (!checkGoOut) {
                    //  if(System.currentTimeMillis() > checkGoOutTime){
                    int count = 0;
                    if (System.currentTimeMillis() > checkGoOutTime)
                        checkGoOutTime = System.currentTimeMillis() + 2000;

                    checkGoOut = true;

                } else {
                    checkGoOut = false;
                    if ((System.currentTimeMillis() > checkGoOutTime) && livingP == 0 && bedP == 0 && studyP == 0 && kitchenP == 0) {
                        if (!goOut) {
                            setDefault();
                            goOut = true;
                            changeAct = true;

                        }
                    }
                }
            }
            if (goOut && (livingP > 0 || bedP > 0 || studyP > 0 || kitchenP > 0)) {
                setDefault();
                comeBack = true;
                changeAct = true;
            }

        }else{
            Timestamp current = new Timestamp(System.currentTimeMillis());
            if(goOutTime.before(current)){
                goOutDuration = false;
                closeLightH();
            }
        }
        return changeAct;
    }

    public boolean checkAct(){
        boolean changeAct = false;
        if(!goOutDuration) {
            totalP = livingP + bedP + studyP;
            //sleep
            if (bedP > 0 && nightlamp && totalP == 1) {
                if (!sleep) {
                    setDefault();
                    sleep = true;
                    changeAct = true;
                }
            }
            //playXbox
            else if (livingP > 0 && tv && xbox && totalP == 1) {
                if (!playXbox) {
                    setDefault();
                    playXbox = true;
                    changeAct = true;
                }
            }
            //watchTV
            else if (livingP > 0 && tv && totalP == 1) {
                if (!watchTV) {
                    setDefault();
                    watchTV = true;
                    changeAct = true;
                }
            }
            //go out
            else if (livingP == 0 && bedP == 0 && studyP == 0 && kitchenP == 0) {
                if (!checkGoOut) {
                    //  if(System.currentTimeMillis() > checkGoOutTime){
                    int count = 0;
                    if (System.currentTimeMillis() > checkGoOutTime)
                        checkGoOutTime = System.currentTimeMillis() + 8000;

                    checkGoOut = true;

                } else {
                    checkGoOut = false;
                    if ((System.currentTimeMillis() > checkGoOutTime) && livingP == 0 && bedP == 0 && studyP == 0 && kitchenP == 0) {
                        if (!goOut) {
                            setDefault();
                            goOut = true;
                            changeAct = true;

                        }
                    }
                }
            }
            // using pc
            else if (bedP > 0 && pc && totalP == 1) {
                if (!usingPC) {
                    setDefault();
                    usingPC = true;
                    changeAct = true;
                }
            }
            else if (livingP > 0 && livinglamp && totalP == 1){
                if(!reading){
                    setDefault();
                    reading = true;
                    changeAct = true;
                }
            }
            else if (totalP > 1 && bedP > 0 && livingP > 0 && pc && tv) {
                if (!usingPCnWatchTV) {
                    setDefault();
                    usingPCnWatchTV = true;
                    changeAct = true;
                }
            }
            else if (totalP > 1 && livingP > 0 && tv && livinglamp) {
                if(!readingnWatchTV){
                    setDefault();
                    readingnWatchTV = true;
                    changeAct = true;
                }
            }

            if (goOut && (livingP > 0 || bedP > 0 || studyP > 0 || kitchenP > 0)) {
                setDefault();
                comeBack = true;
                changeAct = true;
            }
        /*
        else if(changeAct){
            closeLightH();
        }
        */
        }
        else{
            Timestamp current = new Timestamp(System.currentTimeMillis());

            if(goOutTime.before(current)){
                goOutDuration = false;
                closeLightH();
            }


        }
        return changeAct;
    }

    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public String passData(){
        //System.out.println("In");
        return "1 0 0 1 0 1 1 0 0";
       /*
        if(sh.isRecognize()){
            int nightlampInt = boolToInt(nightlamp);
            int tvInt = boolToInt(tv);
            int xboxInt = boolToInt(xbox);
            int pcInt = boolToInt(pc);
            int livinglampInt = boolToInt(livinglamp);

            //livingP+bedP+StudyP+KitchenP+totalP+nightlamp+tv+xbox+pc+livinglamp+lightC+lightR+LightB+LightK+LightS
            return valueOf(livingP) + " " + valueOf(bedP) + " " + valueOf(studyP) + " " + valueOf(kitchenP) + " " + valueOf(nightlampInt) + " "+ valueOf(tvInt) + " " + valueOf(xboxInt) + " "
                    + valueOf(pcInt) + " "+ valueOf(livinglampInt) + "\n";
        }
        else
            return null;
           */
    }

    public String viewData(){
        Timestamp ts =new Timestamp(System.currentTimeMillis());
       // return "Data:" + Arrays.toString(message) + "\n";
       // return "Data: 0 1 0 1 0 0 0 1 0\n";
        double x = new Random().nextDouble()*3+103;
        double z = new Random().nextDouble()*4+30;
        double y = new Random().nextDouble()*4+80;
        return "2 0 0 0 0 "+ (int)(Math.round(x*100.0))/100.0 +" "+(int)(Math.round(y*100.0))/100.0 +" " +(int)(Math.round(z*100.0))/100.0 + " 0 ";
        //return "Data: 1 0 0 0 0 0 "+ (int)(Math.round(z*100.0))/100.0 + " 0 0";
    }

    public static void main(String[] args) {

        //Thread mchess = new MCHESS("-train");
        Thread mchess = new MCHESS("-run");
        //Thread mchess = new MCHESS("-run");
        mchess.start();
    }
    private class mchess_callback implements MqttCallback{
        @Override
        public void connectionLost(Throwable throwable) {
            System.out.println("Disconnected");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String MSG = new String(message.getPayload());
            sad.newmsg();
            newMsg = new JSONObject(MSG);
            sad.setjsonmsg(newMsg);
            //System.out.println("Topic :" + topic );
            //System.out.println("Qos : " + message.getQos());
            //System.out.println("Message : " + MSG);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    }


}

