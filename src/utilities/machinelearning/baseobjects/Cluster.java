package utilities.machinelearning.baseobjects;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;

import java.io.Serializable;
import java.util.*;

/**
 * Created by YaHung on 2015/9/10.
 */
public class Cluster implements Serializable,Iterable<Record>{
    protected Integer clusterId;
    protected Dataset clusterDataset;
    protected Integer clusterHeadId;
    protected boolean setName;
    protected String name;

    public Cluster(Integer clusterId) {
        this.setName = false;
        this.name = "";
        this.clusterId = clusterId;
        clusterDataset = new Dataset();
    }

    public void setClusterId(int id){ clusterId = id;}
    public Integer getClusterHeadId() {
        return clusterHeadId;
    }
    public void setClusterHeadId(Integer clusterHeadId) {
        this.clusterHeadId = clusterHeadId;
    }
    public Integer getClusterId() {
        return clusterId;
    }
    public Record getClusterHead(){
        return clusterDataset.get(clusterHeadId);
    }
    public Record getNode(Integer rId){
        return clusterDataset.get(rId);
    }
    public Dataset getClusterDataset(){
        return clusterDataset;
    }
    public int getNumberRecord(){ return clusterDataset.getRecordNumber();}
    public void setName(String name){
        this.name = name;
        this.setName = true;
    }
    public String getName(){ return this.name;}
    public boolean checkName(){ return setName;}


    public Record getClusterCenter(){
        ArrayList<Double> x = new ArrayList<>();
        int xSize = clusterDataset.get(0).getX().size();
        for (int i = 0; i < xSize; i++) {
            x.add(0.0);
        }
        for(Integer rId: clusterDataset){
            for (int i = 0; i < xSize; i++) {
                x.set(i,x.get(i)+(Double)clusterDataset.get(rId).getX().get(i));
            }
        }
        for (int i = 0; i < xSize; i++) {
            x.set(i,x.get(i)/clusterDataset.getRecordNumber());
        }
        return new Record(x,getClusterId());
    }


    public int size() {
        if(clusterDataset == null) {
            return 0;
        }
        return clusterDataset.getRecordNumber();
    }

    @Override
    public Iterator<Record> iterator() {
        return new Iterator<Record>() {
            int counter = 0;
            private final int n = clusterDataset.getRecordNumber();

            @Override
            public boolean hasNext() {
                return counter<n;
            }

            @Override
            public Record next() {
                return clusterDataset.get(counter++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void clear() {
        clusterDataset.clear();
    }

    public void add(Record r){
        clusterDataset.add(r);
    }

    public void remove(Integer rId){
        clusterDataset.remove(rId);
    }


}