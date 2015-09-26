package utilities.dataobjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YaHung on 2015/9/15.
 */
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
        //standardProcess(msg);
    }

    public void For313Process(String msg){
        try {

            JSONObject jsonObject = new JSONObject(msg);
            subject = jsonObject.getString("subject");
            if(subject.contains("comfort_sensor")){
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
            else if (subject.contains("socketmeter")){
                id = jsonObject.getInt("id");
                value = jsonObject.getDouble("ampere");
                switch (id){
                    case 90: id = 6; break;
                    case 6:  id = 7; break;
                    case 25: id = 8; break;
                    case 88: id = 9; break;
                    case 5:  id = 10;break;
                    case 89: id = 11;break;
                    case 58: id = 12;break;
                    case 20: id = 13;break;
                    case 21: id = 14;break;
                    case 8:  id = 15;break;
                    case 16: id = 16;break;
                    case 15: id = 17;break;
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
        /*if(subject.equals("sensor")){
            return true;
        }*/
        return false;
    }

    public boolean isSensorModelEnd(){
        if(subject.equals("training_end")){
            return true;
        }
        /*if(subject.equals("sensor")){
            if(note.equals("end")){
                return true;
            }
        }*/
        return false;
    }

    public boolean isActivity(){
        if(subject.equals("activity")){
            return true;
        }
        return false;
    }
}
