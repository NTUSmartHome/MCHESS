package utilities.dataobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
/**
 * @version 1.0
 * @author Yahung 2015/9/8
 * We use Record to store all entries of our dataset
 * Record has entities of x, y, predicted class and probabilities of predictions
 *
 * The data point may have different types
 * e.g. Real number, boolean, category variable(nominal), ordinal, etc
 *
 */
public class Record implements Serializable {

    ArrayList<?> x = new ArrayList<>();
    private Object y;
    private Object yPredicted;
    private ArrayList<?> yPredictedProbabilities = new ArrayList<>();

    public Record(Object[] x, Object y){
        ArrayList<Object> f = new ArrayList<>();
        for(Object value: x){
            f.add(value);
        }
        this.x = f;
        this.y = y;
    }

    public Record(ArrayList<?> x, Object y) {
        this.x = x;
        this.y = y;
    }

    public Record(ArrayList<?> x, Object y, Object yPredicted, ArrayList<?> yPredictedProbabilities) {
        this.x = (ArrayList<?>) x.clone();
        this.y = y;
        this.yPredicted = yPredicted;
        if (yPredictedProbabilities != null) {
            this.yPredictedProbabilities = (ArrayList<?>) yPredictedProbabilities.clone();
        }
        else {
            this.yPredictedProbabilities = null;
        }
    }

    public int getNumberOfX(){
        return x.size();
    }

    public Object getX(int xId){
        return x.get(xId);
    }

    public ArrayList<?> getX() {
        if(x == null) {
            return null;
        }
        return x;
    }

    public void setY(Object y){this.y = y;}
    public Object getY() {
        return y;
    }


    public void setyPredicted(Object y){
        this.yPredicted = y;
    }
    public Object getYPredicted() {
        return yPredicted;
    }

    public ArrayList<?> getYPredictedProbabilities() {
        if(yPredictedProbabilities == null) {
            return null;
        }
        return yPredictedProbabilities;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        else if (getClass() != obj.getClass()) {
            return false;
        }

        final Record other = (Record) obj;

        if (!Objects.equals(this.y, other.y)) {
            return false;
        }
        else if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        return true;
    }

}
