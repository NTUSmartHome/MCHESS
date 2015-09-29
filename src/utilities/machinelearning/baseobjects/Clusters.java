package utilities.machinelearning.baseobjects;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Clusters implements Serializable, Iterable<Cluster> {
    public Map<Integer, Cluster> clusterList;

    public Clusters(){
        clusterList = new HashMap<>();
    }

    public void add(Integer clusterId, Cluster cluster){
        clusterList.put(clusterId,cluster);
    }

    public Cluster getCluster(Integer clusterId){
        return clusterList.get(clusterId);
    }

    public int getNumberOfCluster(){
        return clusterList.size();
    }

    public Clusters copy() {
        Clusters c = new Clusters();
        for(Cluster clu : this) {
            c.add(clu.getClusterId(), clu);
        }
        return c;
    }

    private Map<Integer, Cluster> getClusterList(){return clusterList;}

    public void save(String dbName){
        try {
            Clusters c = copy();
            FileOutputStream fos = new FileOutputStream(dbName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(c);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void load(String dbName){
        try {
            FileInputStream fis = new FileInputStream(dbName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Clusters c = (Clusters) ois.readObject();
            ois.close();
            this.clusterList = c.getClusterList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<Cluster> iterator() {
        return new Iterator<Cluster>() {
            private Integer counter = 0;
            private final int n = clusterList.size();

            @Override
            public boolean hasNext() {
                return counter<n;
            }

            @Override
            public Cluster next() {
                return clusterList.get(counter++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
