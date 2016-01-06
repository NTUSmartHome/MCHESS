package utilities.machinelearning.baseobjects;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;

import java.io.*;
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

    public Classes getClasses(){
        return classes;
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
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream( parameters.get(insts.getModelName())+"_dataset");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(trainingData);
            oos.flush();
            oos.close();

            fos = new FileOutputStream( parameters.get(insts.getModelName())+"_classes");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(classes);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {

            FileInputStream fis = new FileInputStream( parameters.get(insts.getModelName())+"_dataset");
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.trainingData = (Dataset) ois.readObject();
            ois.close();

            fis = new FileInputStream( parameters.get(insts.getModelName())+"_classes");
            ois = new ObjectInputStream(fis);
            this.classes = (Classes) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateClasses() {
        try {
            FileOutputStream fos = new FileOutputStream( parameters.get(insts.getModelName())+"_classes");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(classes);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
