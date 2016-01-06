package utilities.machinelearning.baseobjects;

import java.io.Serializable;

/**
 * Created by YaHung on 2015/9/11.
 */
public class BaseInstractors implements Serializable {
    final String modelName = "-modelname";
    final String iterator = "-t";
    final String kernal = "-k";
    final String model = "-Q";

    public String getModelName(){return modelName;}
    public String getIterator(){return iterator;}
    public String getKernal(){return kernal;}
    public String getModel(){return model;}
}
