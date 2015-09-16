package utilities.machinelearning.clusters.AffinityPropagation;

import utilities.dataobjects.Dataset;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by YaHung on 2015/9/10.
 */
public class DensityAffinityPropagation extends BaseAffinityPropagation {
    ArrayList<Integer> densityParameter;

    public DensityAffinityPropagation() {
        super();
        checkDirector();
        // -k 0:median, 1:minimum
        this.parameters.add(insts.getKernal(), 0);
        this.parameters.set(insts.getModelName(),filePath+"/tmpAPModel");
    }

    public DensityAffinityPropagation(String parameters) {
        super(parameters);
        checkDirector();
        if(!this.parameters.exist(insts.getKernal())){
            // -k 0:median, 1:minimum
            this.parameters.add(insts.getKernal(), 0);
        }
        this.parameters.set(insts.getModelName(),filePath+"/tmpAPModel");
    }

    private void checkDirector(){

        filePath += "/DensityAffinityPropagation";
        File file = new File(filePath);
        if(!file.exists()) { file.mkdir();}
    }

    public void setDataset(Dataset trainingData){
        this.trainingData = trainingData;
        aggregateNodes();
    }

    private void aggregateNodes(){
        densityParameter = new ArrayList<>();
        Dataset aggregateTrainingData = new Dataset();
       // ArrayList<Record> nodes = new ArrayList<>();
        // Find all nodes
        for(Integer rId: trainingData){
            boolean existNode = false;
            for(int nId=0; nId<aggregateTrainingData.getRecordNumber(); nId++){
                if(aggregateTrainingData.get(nId).equals(trainingData.get(rId))){
                    densityParameter.set(nId, densityParameter.get(nId)+1);
                    existNode = true;
                    break;
                }
            }
            if(!existNode) {
                aggregateTrainingData.add(trainingData.get(rId));
                densityParameter.add(1);
            }
        }
        trainingData.clear();
        trainingData = aggregateTrainingData;
    }

    private void setSelfSuitability(){
        int maxDensity = 0;
        for(int density: densityParameter){
            if(density>maxDensity){
                maxDensity = density;
            }
        }
        for(int i=0; i<densityParameter.size(); i++){
            double sii = (maxDensity-densityParameter.get(i)+1)/(maxDensity+1);
            if ((Integer) parameters.get(insts.getKernal()) == 0) {
                sii *= getMedian(similarityMatrix, i);
            } else if ((Integer) parameters.get(insts.getKernal()) == 1) {
                sii *= getMinimum(similarityMatrix, i);
            }
            similarityMatrix[i][i] = sii;
        }
    }

    protected void buildSimilarityMatrix(Dataset trainingData){
        int numberOfNodes = trainingData.getRecordNumber();
        similarityMatrix = new double[numberOfNodes][numberOfNodes];
        // set s(i,k)
        for(int i=0; i<numberOfNodes; i++){
            for(int k=i+1; k<numberOfNodes; k++){
                double distance = distance(trainingData.get(i),trainingData.get(k));
                similarityMatrix[i][k] = distance;
                similarityMatrix[k][i] = distance;
            }
        }
        // set self suitability
        setSelfSuitability();
    }


}
