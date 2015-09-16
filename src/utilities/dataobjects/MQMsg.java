package utilities.dataobjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YaHung on 2015/9/15.
 */
public class MQMsg {
    String subject;
    int id;
    double value;
    String activityName;

    public MQMsg(String msg){
        try {
            JSONObject jsonObject = new JSONObject(msg);
            subject = jsonObject.getString("subject");
            if(subject.equals("sensor")){
                id = jsonObject.getInt("id");
                value = jsonObject.getDouble("value");
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


}
