package ercie;

import sun.misc.MessageUtils;
import utilities.JsonBuilder;
import utilities.communicator.Producer;
import utilities.dataobjects.Dataset;
import utilities.dataobjects.Message;
import utilities.dataobjects.Record;
import utilities.dataobjects.sensorobjects.Sensors;
import utilities.machinelearning.baseobjects.Classes;
import utilities.machinelearning.baseobjects.Cluster;
import utilities.machinelearning.baseobjects.Clusters;
import utilities.machinelearning.classifiers.BayesianNet.BaseBayesNet;
import utilities.machinelearning.clusters.AffinityPropagation.DensityAffinityPropagation;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by YaHung on 2015/9/16.
 */
public class ERCIE extends Thread{

    Dataset trainingData ;
    Dataset aggregateTrainingData ;
    BaseBayesNet bn;
    Sensors sensors;
    Message msg;
    boolean flag = false;
    boolean requestFlag = false;
    public ERCIE(){
        // Initialization
        this.trainingData = new Dataset();
        this.bn = new BaseBayesNet();
        this.sensors = new Sensors();
        this.sensors.load();

        for (int i = 0; i < sensors.getNumberSensors(); i++) {
            System.out.println("Sensor " + i + " in Room " + sensors.getSensor(i).getRoomId() + " and it is " + sensors.getSensor(i).getApplianceId());
        }
    }

    public void loadARModel(){
        this.loadActivityBayesNet();
    }

    public void buildARModel(){
        System.out.println("Load training data (sensor data)");
        this.loadTrainingData();
        System.out.println("Discover Activity by sensor context (affinity propagation)");
        this.buildActivityClustersBaseline(0.05);
        //this.buildActivityClusters();
        System.out.println("Build Activity Recognition Model (BN)");
        this.buildActivityBayesNet();
    }

    public void run(){
        ArrayList<Integer> sensorValues = new ArrayList<>();
        for (int i = 0; i < sensors.getNumberSensors(); i++) {
            sensorValues.add(0);
        }
        Record instance = new Record(sensorValues,0.0);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, 1);
        Timestamp goalTimestamp = new Timestamp(cal.getTime().getTime());


        while(true){
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            // collect all msg in one second
            while(goalTimestamp.after(currentTimestamp)){
                // listen message from mq
                if(flag){
                    Message newMsg = msg;
                    Integer sensorResult = sensors.predict(newMsg.getId(), newMsg.getValue());
                    sensorValues.set(newMsg.getId(), sensorResult);
                    flag = false;
                }
                currentTimestamp = new Timestamp(System.currentTimeMillis());
            }

            // predict instance
            instance = predict(instance);

            int result = (int)(double)(Double)instance.getYPredicted();
            boolean clusterSetName = checkClusterName(result);

            if(!clusterSetName && !requestFlag){

                System.out.println("Predict cluster does not give name....");
                System.out.println("cluster id is " + result);


                requestLabelAct(result);
            }

            System.out.println("Instance value is "+instance.getX());
            System.out.println("Result is "+instance.getYPredicted()+"\r\n");

            // set future 1 second timestamp
            cal = Calendar.getInstance();
            cal.setTimeInMillis(goalTimestamp.getTime());
            cal.add(Calendar.SECOND, 1);
            goalTimestamp = new Timestamp(cal.getTime().getTime());
        }
    }

    public void setActName(int cId, String name){
        this.bn.getClasses().setName(cId,name);
        this.bn.updateClasses();
        getRequest();
    }

    protected void getRequest(){
        requestFlag = false;
    }

    protected void requestLabelAct(int cId){
        requestFlag = true;

        Producer producer = new Producer();
        producer.setURL("tcp://140.112.49.154:61616");
        while(!producer.connect());
        producer.getSendor();

        JsonBuilder json = new JsonBuilder();
        json.reset();
        producer.sendOut(json.add("subject", "label_unkown").add("id", Integer.toString(cId)).toJson(),"ssh.RAW_DATA");
    }

    protected boolean checkClusterName(int y){
        return this.bn.getClasses().checkName(y);
    }

    public void newMsg(Message msg){
        this.msg = msg;
        flag = true;
    }

    public void loadTrainingData(){
        try {
            FileInputStream fis = new FileInputStream("util/dataobjects/sensorobjects/sensorsDataset");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Dataset db = (Dataset) ois.readObject();
            ois.close();
            this.trainingData = db;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void buildActivityClusters(){
        DensityAffinityPropagation dap = new DensityAffinityPropagation();
        dap.setDataset(trainingData);
        trainingData = dap.run();

        // build Activity Table
        Clusters ActClusters = dap.getClusters();

        System.out.println();
        for(Cluster cluster: ActClusters){
            System.out.println("Cluster id "+cluster.getClusterId());
            System.out.println("\t"+Arrays.toString(cluster.getNode(0).getX().toArray()));
        }
    }


    public void buildActivityClustersBaseline(double ratio){
        //Aggregate Nodes
        ArrayList<Double> nodeRatio = aggragateTrainingData();

        Clusters ActClusters = new Clusters();
        int cId = 0;
        for(Integer rId: aggregateTrainingData){
            if( nodeRatio.get(rId) > ratio){
                Cluster cluster = new Cluster(cId);
                cluster.setClusterHeadId(0);
                cluster.add(aggregateTrainingData.get(rId));
                ActClusters.add(cId,cluster);
                cId ++;
            }
        }

        // set clustering result for the dataset
        for (Integer rId: trainingData){
            trainingData.get(rId).setY(findNearestNeighbor(trainingData.get(rId),ActClusters));
        }

        System.out.println();
        for(Cluster cluster: ActClusters){
            System.out.println("Cluster id "+cluster.getClusterId());
            System.out.println("\t"+Arrays.toString(cluster.getNode(0).getX().toArray()));
        }

    }

    private int findNearestNeighbor(Record r, Clusters clusters){
        int minId = 0;
        double minDistance = 10000000;
        for(Cluster cluster: clusters){
            double distance_ = 0;
            for (int i = 0; i < cluster.getClusterHead().getX().size(); i++) {
                distance_ += Math.abs((Integer)r.getX().get(i) - (Integer)cluster.getClusterHead().getX().get(i));
            }
            if(distance_<minDistance){
                minId = cluster.getClusterId();
                minDistance = distance_;
            }
        }
        return minId;
    }

    public void buildActivityBayesNet(){
        this.bn.setDataset(trainingData);
        this.bn.run();
    }

    public void loadActivityBayesNet(){
        this.bn.load();
    }

    public Record predict(Record instance){
        return this.bn.predict(instance);
    }

    private ArrayList<Double> aggragateTrainingData(){
        ArrayList<Double> nodeRatio = new ArrayList<>();
        aggregateTrainingData = new Dataset();
        // Find all nodes
        for(Integer rId: trainingData){
            boolean existNode = false;
            for(int nId=0; nId<aggregateTrainingData.getRecordNumber(); nId++){
                if(aggregateTrainingData.get(nId).equals(trainingData.get(rId))){
                    nodeRatio.set(nId, nodeRatio.get(nId)+1.0);
                    existNode = true;
                    break;
                }
            }
            if(!existNode) {
                aggregateTrainingData.add(trainingData.get(rId));
                nodeRatio.add(1.0);
            }
        }
        for (int i = 0; i < nodeRatio.size(); i++) {
            nodeRatio.set(i, nodeRatio.get(i) / (double) trainingData.getRecordNumber());
        }
        return nodeRatio;
    }



}
