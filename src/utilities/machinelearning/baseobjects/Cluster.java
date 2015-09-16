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
    protected ArrayList<Integer> recordIdSet;
    protected Dataset clusterDataset;
    protected Object labelY;
    protected Integer clusterHeadId;

    public Cluster(Integer clusterId) {
        this.clusterId = clusterId;
        recordIdSet = new ArrayList<>();
        clusterDataset = new Dataset();
    }

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
    public Object getLabelY() {
        return labelY;
    }
    protected void setLabelY(Object labelY) {
        this.labelY = labelY;
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