package utilities.machinelearning.baseobjects;

import utilities.dataobjects.Dataset;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by YaHung on 2015/9/11.
 */
public class Classes implements Serializable {
    protected ArrayList<Object> yList;
    protected ArrayList<String> yNameList;
    protected ArrayList<Boolean> setName;
    public Classes(){
        this.yList = new ArrayList<>();
        this.setName = new ArrayList<>();
        this.yNameList = new ArrayList<>();
    }

    public int getNumClasses(){ return yList.size();}

    public void setClasses(Dataset trainingData){
        for(Integer rId: trainingData){
            Object rY = trainingData.get(rId).getY();
            boolean exist = false;
            for(Object y: this.yList) {
                if (y.equals(rY)) {
                    exist = true;
                    break;
                }
            }
            if(!exist) {
                this.yList.add(rY);
            }
        }
        for (int i = 0; i < this.yList.size(); i++) {
            setName.add(false);
            yNameList.add("");
        }
    }

    public void setName(int cId, String name){
        System.out.println("Set class "+cId+" name "+name);
        this.yNameList.set(cId, name);
        this.setName.set(cId,true);
    }
    public String getName(int cId){ return this.yNameList.get(cId);}
    public boolean checkName(int cId){ return this.setName.get(cId);}

    public ArrayList<Object> getYList(){ return yList;}

    public Object getY(int yId){ return yList.get(yId);}

    public void setY(int yId, Object y){
        yList.set(yId,y);
    }

    public void addY(Object y){
        yList.add(y);
    }

}
