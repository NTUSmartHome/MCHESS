package utilities.machinelearning.baseobjects;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;

import java.io.File;
import java.util.*;

/**
 * Created by YaHung on 2015/9/9.
 */
public class BaseMLCluster implements BaseMLModel{
    protected String filePath;
    protected BaseParameters parameters;
    protected BaseInstractors insts;
    protected Dataset trainingData;
    protected Clusters clusters;

    public BaseMLCluster(){
        parameters = new BaseParameters();
        insts = new BaseInstractors();
        clusters = new Clusters();
        checkDirectory();
    }

    public BaseMLCluster(String parameters){
        this.parameters = new BaseParameters();
        this.parameters.add(parameters);
        clusters = new Clusters();
        insts = new BaseInstractors();
        checkDirectory();
    }

    private void checkDirectory(){
        filePath = "util";
        File file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/machineleraning";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/cluster";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

    }

    public Clusters getClusters(){
        return clusters;
    }

    public void setClusters(Clusters clusters){
        this.clusters = clusters;
    }


    @Override
    public void setDataset(Dataset trainingData){
        this.trainingData = new Dataset();
        this.trainingData = trainingData;
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

    }

    @Override
    public void load() {

    }
}
