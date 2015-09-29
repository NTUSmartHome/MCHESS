package utilities.machinelearning.baseobjects;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;

import java.io.File;
import java.util.*;

/**
 * Created by YaHung on 2015/9/9.
 */
public class BaseMLClassifier implements BaseMLModel {
    protected String filePath;
    protected BaseParameters parameters;
    protected BaseInstractors insts;
    protected Dataset trainingData;
    protected Classes classes;
    public BaseMLClassifier(){
        parameters = new BaseParameters();
        insts = new BaseInstractors();
        classes = new Classes();
        checkDirectory();
    }

    public BaseMLClassifier(String parameters){
        this.parameters = new BaseParameters();
        this.parameters.add(parameters);
        insts = new BaseInstractors();
        classes = new Classes();
        checkDirectory();
    }

    private void checkDirectory(){
        filePath = "util";
        File file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/machineleraning";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/classifier";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

    }

    public Object getY(Integer rId){
        return classes.getY(rId);
    }

    public Object getYList(){
        return classes.getYList();
    }

    public int getNumberOfClass(){
        return classes.getNumClasses();
    }

    @Override
    public void setDataset(Dataset trainingData){
        this.trainingData = trainingData;
        this.classes.setClasses(trainingData);
    }

    @Override
    public Record predict(Record testingData) {
        return null;
    }

    @Override
    public Dataset predict(Dataset testingData) {
        return null;
    }

    @Override
    public Dataset validate(Dataset testingData) {
        return null;
    }

    @Override
    public void save(String modelName) {

    }

    @Override
    public void load(String modelName) {

    }
}
