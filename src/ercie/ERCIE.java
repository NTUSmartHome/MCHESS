package ercie;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Message;
import utilities.dataobjects.Record;
import utilities.dataobjects.sensorobjects.Sensors;
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

    public ERCIE(){
        // Initialization
        this.trainingData = new Dataset();
        this.bn = new BaseBayesNet();
        this.sensors = new Sensors();
        this.sensors.load();
    }

    public void loadARModel(){
        this.loadActivityBayesNet();
    }
    public void saveARModelBaseline(){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("aggregateNode");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(aggregateTrainingData);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadARModelBaseline(){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("aggregateNode");
            ObjectInputStream ois = new ObjectInputStream(fis);
            aggregateTrainingData = (Dataset) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void buildARModel(){
        System.out.println("Load training data (sensor data)");
        this.loadTrainingData();
        System.out.println("Discover Activity by sensor context (affinity propagation)");
        this.buildActivityClustersBaseline();
        //this.buildActivityClusters();
        //System.out.println("Build Activity Recognition Model (BN)");
        //this.buildActivityBayesNet();
        this.saveARModelBaseline();
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
                    System.out.print(newMsg.getSubject()+":"+newMsg.getValue()+", ");
                    Integer sensorResult = sensors.predict(newMsg.getId(), newMsg.getValue());
                    sensorValues.set(newMsg.getId(), sensorResult);
                    flag = false;
                }
                currentTimestamp = new Timestamp(System.currentTimeMillis());
            }

            // predict instance
            //instance = predict(instance);
            instance = predictACBaseline(instance);
            System.out.println("Instance value is "+instance.getX());
            System.out.println("Result is "+instance.getY()+"\r\n");

            // set future 1 second timestamp
            cal = Calendar.getInstance();
            cal.setTimeInMillis(goalTimestamp.getTime());
            cal.add(Calendar.SECOND, 1);
            goalTimestamp = new Timestamp(cal.getTime().getTime());
        }
    }

    public void newMsg(Message msg){
        this.msg = msg;
        flag = true;
    }

    public void loadTrainingData(){
        Dataset db;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("util/dataobjects/sensorobjects/sensorsDataset");
            ObjectInputStream ois = new ObjectInputStream(fis);
            db = (Dataset) ois.readObject();
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
            System.out.println();
        }
    }

    public void buildActivityClustersBaseline(){
        //Aggregate Nodes
        ArrayList<Integer> densityParameter = new ArrayList<>();
         aggregateTrainingData = new Dataset();
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
        Clusters ActClusters = new Clusters();
        for(Integer cId: aggregateTrainingData){
            Cluster cluster = new Cluster(cId);
            cluster.setClusterHeadId(0);
            cluster.add(aggregateTrainingData.get(cId));
            ActClusters.add(cId,cluster);
        }

        ArrayList<Integer> clusterId = new ArrayList<>();
        for (int i = 0; i < aggregateTrainingData.getRecordNumber(); i++) {
            clusterId.add(i);
        }

        for (Integer rId: trainingData){
            Record r = trainingData.get(rId);
            for(Integer cId: aggregateTrainingData){
                Record c = aggregateTrainingData.get(cId);
                boolean same = true;
                for (int i = 0; i < trainingData.getNumDimension(); i++) {
                    if(!r.getX().get(i).equals(c.getX().get(i))){
                        same = false;
                        break;
                    }
                }
                if(same){
                    trainingData.get(rId).setY(cId);
                    break;
                }
            }
        }



        System.out.println();
        for(Cluster cluster: ActClusters){
            System.out.println("Cluster id "+cluster.getClusterId());
            System.out.println("\t"+Arrays.toString(cluster.getNode(0).getX().toArray()));
            System.out.println();
        }

    }


    public void buildActivityBayesNet(){
        this.bn.setDataset(trainingData);
        this.bn.run();
    }

    public void loadActivityBayesNet(){
        this.bn.load();
    }

    public void activityTable(){

    }

    public Record predict(Record instance){
        Record r = this.bn.predict(instance);
        System.out.println("Result is "+r.getY());
        return r;
    }

    public Record predictACBaseline(Record instance){
        for(Integer cId: aggregateTrainingData){
            Record c = aggregateTrainingData.get(cId);
            boolean same = true;
            for (int i = 0; i < aggregateTrainingData.getNumDimension(); i++) {
                if(!instance.getX().get(i).equals(c.getX().get(i))){
                    same = false;
                    break;
                }
            }
            if(same){
                System.out.println("Result is "+cId);
                instance.setY(cId);
                break;
            }
        }
        return instance;
    }

}
