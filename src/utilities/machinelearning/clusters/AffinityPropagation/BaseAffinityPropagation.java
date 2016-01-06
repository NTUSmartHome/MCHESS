package utilities.machinelearning.clusters.AffinityPropagation;

import fr.lri.tao.apro.ap.Apro;
import fr.lri.tao.apro.ap.AproBuilder;
import fr.lri.tao.apro.data.DataProvider;
import fr.lri.tao.apro.data.MatrixProvider;
import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;
import utilities.machinelearning.baseobjects.BaseMLCluster;
import utilities.machinelearning.baseobjects.Cluster;
import utilities.machinelearning.baseobjects.Clusters;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by YaHung on 2015/9/9.
 */
public class BaseAffinityPropagation extends BaseMLCluster {
    //Apro apro;
    double[][] similarityMatrix;

    public BaseAffinityPropagation(){
        super();
        checkDirector();
        this.parameters.add(insts.getIterator(), 100);
        this.parameters.add(insts.getModelName(),filePath+"/tmpAPModel");
    }

    public BaseAffinityPropagation(String parameters) {
        super(parameters);
        checkDirector();
        if(!this.parameters.exist(insts.getIterator())){
            this.parameters.add(insts.getIterator(),100);
        }
        if(!this.parameters.exist(insts.getModelName())) {
            this.parameters.add(insts.getModelName(), filePath + "/tmpAPModel");
        }
    }

    private void checkDirector(){
        filePath += "/AffinityPropagation";
        File file = new File(filePath);
        if(!file.exists()) { file.mkdir();}
    }


    public Dataset run(){

        normalizeTrainingdata();

        if(this.trainingData.getRecordNumber()<=3){
            clusters = new Clusters();
            int[] rI = new int[this.trainingData.getRecordNumber()];

            //sort clusters
            if(this.trainingData.getRecordNumber() > 1) {
                double[] rV = new double[this.trainingData.getRecordNumber()];
                for (int i = 0; i < this.trainingData.getRecordNumber(); i++) {
                    for (int j = 0; j < this.trainingData.get(i).getX().size(); j++) {
                        rV[i] += (Double)this.trainingData.get(i).getX().get(j);
                    }
                }
                rI = sort(rV);
            }

            for (int i = 0; i < this.trainingData.getRecordNumber(); i++) {
                Cluster cluster = new Cluster(i);
                cluster.add(this.trainingData.get(rI[i]));
                clusters.add(i, cluster);
                this.trainingData.get(rI[i]).setY(i);
            }
        }
        else {
            buildSimilarityMatrix(this.trainingData);
            AproBuilder builder = new AproBuilder();
            builder.setFullAuto();
            DataProvider provider = new MatrixProvider(similarityMatrix);
            Apro apro = builder.build(provider);
            apro.run((Integer) parameters.get(insts.getIterator()));

            setClusters(apro);
        }
        return trainingData;
    }

    public Dataset run(int iters){
        parameters.set(insts.getIterator(), iters);
        return run();
    }

    /** Actually, it predict by 1NN*/
    public Record predict(Record r){
        double distance = 100000;
        int mostSimilarClusterId = 0;
        for(Cluster cluster: clusters){
            for(Record compareR: cluster) {
                double currentDistance = distance(r, compareR);
                if (currentDistance < distance) {
                    distance = currentDistance;
                    mostSimilarClusterId = cluster.getClusterId();
                }
            }
        }
        return new Record(r.getX(),mostSimilarClusterId);
    }

    public Object predict(Object[] x, Object y){
        Record r = new Record(x,y);
        return predict(r).getY();
    }

    public Dataset predict(Dataset testingData){
        Dataset resultData = new Dataset();
        for(Integer rId: testingData){
            resultData.add(predict(testingData.get(rId)));
        }
        return resultData;
    }

    private void setClusters(Apro apro){
        Clusters clusters = new Clusters();
        Set<Integer> clusterHeadSet = apro.getExemplarSet();
        int[] exemplars = apro.getExemplars();
        int clusterId = 0;
        for(int exemplar: clusterHeadSet){
            Cluster cluster = new Cluster(clusterId);
            for(int i=0; i<exemplars.length; i++) {
                if (exemplars[i] == exemplar) {
                    trainingData.get(i).setY(clusterId);
                    cluster.add(trainingData.get(i));
                }
            }
            clusters.add(clusterId, cluster);
            clusterId++;
        }

        // sort clusters
        double[] allDimensionSum = new double[clusters.getNumberOfCluster()];
        for (int i = 0; i < allDimensionSum.length; i++) {
            for (int j = 0; j < clusters.getCluster(i).getClusterCenter().getX().size(); j++) {
                allDimensionSum[i] += (Double)clusters.getCluster(i).getClusterCenter().getX().get(j);
            }
            allDimensionSum[i] /= clusters.getCluster(i).getClusterCenter().getX().size();
        }

        int[] sortCluster = sort(allDimensionSum);

        this.clusters = new Clusters();
        for (int i = 0; i < sortCluster.length; i++) {
            clusters.getCluster(sortCluster[i]).setClusterId(i);
            this.clusters.add(i,clusters.getCluster(sortCluster[i]));
        }

        for(Integer rId: trainingData){
            for (int i = 0; i < sortCluster.length; i++) {
                if(trainingData.get(rId).getY().equals(sortCluster[i])){
                    trainingData.get(rId).setY(i);
                    break;
                }
            }
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
        for (int i=0; i<numberOfNodes; i++){
            similarityMatrix[i][i] = getMedian(similarityMatrix,i);
        }
    }
    /**@param option 0 is median, 1 is minimum*/
    protected void buildSimilarityMatrix(int option, Dataset trainingData){
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
        for (int i=0; i<numberOfNodes; i++){
            if(option == 0)
                similarityMatrix[i][i] = getMedian(similarityMatrix,i);
            else if(option == 1)
                similarityMatrix[i][i] = getMinimum(similarityMatrix,i);
        }
    }

    public void save(){
        clusters.save((String) parameters.get(insts.getModelName()));
    }

    public void save(String apName){
        parameters.set(insts.getModelName(),filePath+"/"+apName);
        clusters.save((String) parameters.get(insts.getModelName()));
    }

    public void load(){
        System.out.println("Load model " + (String) parameters.get(insts.getModelName()));
        clusters.load((String) parameters.get(insts.getModelName()));
    }

    public void load(String apName){
        parameters.set(insts.getModelName(), filePath + "/" + apName);
        clusters.load((String) parameters.get(insts.getModelName()));
    }

    protected double distance(Record a, Record b){
        int featureSize = a.getNumberOfX();
        double distance = 0;
        for(int i=0; i<featureSize; i++){
            double axValue;
            double bxValue;
            if(a.getX(i) instanceof Integer) {
                axValue = (double)(Integer) a.getX(i);
                bxValue = (double)(Integer) b.getX(i);
            }
            else{
                axValue = (Double) a.getX(i);
                bxValue = (Double) b.getX(i);
            }
            distance += Math.pow(axValue - bxValue, 2);
        }
        return Math.sqrt(distance);
    }

    protected double getMedian(double[][] similarityMatrix, int iId){
        int numberOfRelatedNodes = similarityMatrix.length -1;
        double[] iRelatedSimilarity = new double[numberOfRelatedNodes];
        for(int i=0, j=0; i<similarityMatrix.length; i++){
            if(i != iId){
                iRelatedSimilarity[j] = similarityMatrix[iId][i];
                j++;
            }
        }
        Arrays.sort(iRelatedSimilarity);
        double median;
        if((iRelatedSimilarity.length%2) == 0){
            int medianId = (iRelatedSimilarity.length/2);
            median = (iRelatedSimilarity[medianId]+iRelatedSimilarity[medianId-1])/2;
        }
        else{
            int medianId = ((iRelatedSimilarity.length-1)/2);
            median = iRelatedSimilarity[medianId];
        }
        return  median;
    }

    protected double getMinimum(double[][] similarityMatrix, int iId){
        int numberOfRelatedNodes = similarityMatrix.length -1;
        double[] iRelatedSimilarity = new double[numberOfRelatedNodes];
        for(int i=0, j=0; i<similarityMatrix.length; i++){
            if(i != iId){
                iRelatedSimilarity[j] = similarityMatrix[iId][i];
                j++;
            }
        }
        Arrays.sort(iRelatedSimilarity);
        return  iRelatedSimilarity[0];
    }

    private double[] swap(double a, double b){
        return new double[] {b,a};
    }

    private int[] swap(int a, int b){
        return new int[] {b,a};
    }

    private int[] sort(double[] value){
        int[] id = new int[value.length];
        for (int i = 0; i < id.length; i++) {
            id[i] = i;
        }
        for (int i = 0; i < id.length; i++) {
            for (int j = i + 1; j < id.length; j++) {
                if (value[i] > value[j]) {
                    int[] tmpI = swap(id[i],id[j]);
                    id[i] = tmpI[0]; id[j] = tmpI[1];
                    double[] tmpV = swap(value[i],value[j]);
                    value[i] = tmpV[0]; value[j] = tmpV[1];
                }
            }
        }
        return id;
    }

    public void normalizeTrainingdata(){
        for (int i = 0; i < trainingData.getNumDimension(); i++) {
            double max = 0;
            for (int j = 0; j < trainingData.getRecordNumber(); j++)
                if( (Double)trainingData.get(j).getX(i) > max)
                    max = (Double)trainingData.get(j).getX(i);
            for (int j = 0; j < trainingData.getRecordNumber(); j++) {
                if(max!=0)
                    trainingData.get(j).setX(i, ((Double) trainingData.get(j).getX(i) / max) * 100);
            }
        }
    }
}
