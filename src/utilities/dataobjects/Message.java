package utilities.dataobjects;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    String subject;
    int id;
    double value;
    String activityName;
    String note;

    public Message(String msg){
        // initialization
        subject = "no";
        id = 0;
        value = 0.0;
        activityName = "non";
        note = "non";

        For313Process(msg);
    }

    public void For313Process(String msg){
        try {
            JSONObject jsonObject = new JSONObject(msg);
            subject = jsonObject.getString("subject");
            if(subject.equals("comfort_sensor")){
                id = jsonObject.getInt("id");
                value = jsonObject.getDouble("lux");
                switch (id){
                    case 94:  id = 1; break;
                    case 110: id = 2; break;
                    case 47:  id = 3; break;
                    case 80:  id = 4; break;
                    case 40:  id = 5; break;
                    default:  id =0;  break;
                }
            }
            else if (subject.equals("socketmeter")){
                id = jsonObject.getInt("id");
                value = jsonObject.getDouble("ampere");
                switch (id){
                    case 2: id = 6; break;
                    case 3:  id = 7; break;
                    case 5: id = 8; break;
                    case 7: id = 9; break;
                    case 8:  id = 10;break;
                    case 9: id = 11;break;
                    case 11: id = 12;break;
                    case 20: id = 13;break;
                    case 21: id = 14;break;
                    case 10:  id = 15;break;
                    case 14: id = 16;break;
                    case 16: id = 17;break;
                    default: id =0;  break;
                }
            }
            else if (subject.equals("socketmeter_energy")) {
                id = jsonObject.getInt("id");
                String status = jsonObject.getString("status");
                if (status.equals("off")){
                    value = 0;
                }
                else if(status.equals("on")){
                    value = 2;
                }
                else {
                    value = 1;
                }
                switch (id){
                    case 90: id = 18; break;
                    case 6:  id = 19; break;
                    case 25: id = 20; break;
                    case 88: id = 21; break;
                    case 5:  id = 22;break;
                    case 89: id = 23;break;
                    case 58: id = 24;break;
                    case 20: id = 25;break;
                    case 21: id = 26;break;
                    case 8:  id = 27;break;
                    case 16: id = 28;break;
                    case 15: id = 29;break;
                    default: id =0;  break;

                }
            }
            else if (subject.contains("end")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void standardProcess(String msg){
        try {
            JSONObject jsonObject = new JSONObject(msg);
            subject = jsonObject.getString("subject");
            if(subject.equals("sensor")){
                id = jsonObject.getInt("id");
                value = jsonObject.getDouble("value");
                note = jsonObject.getString("note");
            }
            else if(subject.equals("activity"))
            {
                activityName = jsonObject.getString("activityName");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSubject(){return subject;}
    public int getId(){return id;}
    public double getValue(){return value;}
    public String getActivityName(){return activityName;}

    public boolean isSensorData(){
        if(subject.equals("comfort_sensor")||subject.equals("socketmeter")){
            return true;
        }
        return false;
    }

    public boolean isSensorModelEnd(){
        if(subject.equals("training_end")){
            return true;
        }
        return false;
    }

    public boolean isActivity(){
        if(subject.equals("activity")){
            return true;
        }
        return false;
    }
}
