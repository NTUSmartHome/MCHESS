package utilities.machinelearning.baseobjects;

import utilities.dataobjects.Dataset;

import java.util.ArrayList;

/**
 * Created by YaHung on 2015/9/11.
 */
public class Classes {
    public ArrayList<Object> yList;
    public Classes(){
        yList = new ArrayList<>();
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
    }

    public ArrayList<Object> getYList(){ return yList;}

    public Object getY(int yId){ return yList.get(yId);}

    public void setY(int yId, Object y){
        yList.set(yId,y);
    }

    public void addY(Object y){
        yList.add(y);
    }

}
