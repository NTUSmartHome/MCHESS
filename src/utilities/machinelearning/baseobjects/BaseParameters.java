package utilities.machinelearning.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by YaHung on 2015/9/11.
 */
public class BaseParameters implements Serializable {
    ArrayList<ArrayList<Object>> parameters;
    public BaseParameters(){
        parameters = new ArrayList<>();
    }

    public void add(String instList){
        String[] instStr = instList.split(" ");
        for(int index=0; index<instStr.length; index++) {
            if (instStr[index].contains("-")) {
                ArrayList<Object> parameter = new ArrayList<>();
                if ((index + 1) < instStr.length) {
                    if (instStr[index + 1].contains("-")) {
                        parameter.add(instStr[index]);
                    } else {
                        parameter.add(instStr[index]);
                        if(isInteger(instStr[index + 1])){
                            parameter.add(Integer.valueOf(instStr[index + 1]));
                        }
                        else {
                            parameter.add(instStr[index + 1]);
                        }
                        index++;
                    }
                } else {
                    parameter.add(instStr[index]);
                }
                parameters.add(parameter);
            }
        }
    }

   public void add(String inst, Object param){
       ArrayList<Object> parameter = new ArrayList<>();
       parameter.add(inst);
       parameter.add(param);
       parameters.add(parameter);
   }

    public void set(String inst, Object param){
        if(exist(inst)){
            for(int i=0; i<parameters.size(); i++){
                if(parameters.get(i).get(0).equals(inst)){
                    parameters.get(i).set(1,param);
                }
            }
        }
        else {
            add(inst,param);
        }
    }


    public Object get(String inst){
        Object param_=0;
        for(ArrayList<Object> param: parameters){
            if(param.get(0).equals(inst)){
                param_ = param.get(1);
                break;
            }
        }
        return param_;
    }

    public boolean exist(String inst){
        boolean exist = false;
        for(ArrayList<Object> param: parameters){
            if(param.get(0).equals(inst)){
                exist = true;
                break;
            }
        }
        return exist;
    }

    public static boolean isInteger(String value) {
        Pattern pattern = Pattern.compile("^[-+]?\\d+$");
        return pattern.matcher(value).matches();
    }

}
