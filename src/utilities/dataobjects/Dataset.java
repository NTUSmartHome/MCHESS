package utilities.dataobjects;

import java.io.*;
import java.util.*;

public class Dataset implements Serializable, Iterable<Integer> {
    private Map<Integer, Record> recordList;

    public Dataset(){
        recordList = new HashMap<>();
    }

    public Dataset(String dbName){
        recordList = new HashMap<>();
        this.load(dbName);
    }

    public Integer add(Object[] feature, Object label){
        ArrayList<Object> f = new ArrayList<>();
        for(Object value: feature){
            f.add(value);
        }
        Integer newId = this.add(new Record(f, label));
        return newId;
    }

    public Integer add(Record r) {
        Integer newId=(Integer) recordList.size();
        recordList.put(newId, r);
        return newId;
    }

    public Integer set(Integer rId, Record r) {
        if(recordList.containsKey(rId)==false) {
            throw new IndexOutOfBoundsException(); //ensure that the record has already be set with add()
        }
        recordList.put(rId, r);
        return rId;
    }

    public int getRecordNumber() {
        return recordList.size();
    }

    public int getNumDimension(){ return recordList.get(0).getX().size(); }

    public boolean isEmpty() {
        return recordList.isEmpty();
    }

    public Dataset copy() {
        Dataset d = new Dataset();

        for(Integer rId : this) {
            d.add(recordList.get(rId));
        }
        return d;
    }

    public void replaceRecordList(Map<Integer, Record> recordList){
        this.recordList.clear();
        this.recordList = recordList;
    }

    public Map<Integer, Record> getRecordList(){
        return recordList;
    }

    public void clear(){
        recordList.clear();
    }

    public void remove(Integer id){
        recordList.remove(id);
    }

    public Record get(Integer id) {
        return recordList.get(id);
    }

    public void save(String dbName){
        Dataset db = copy();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dbName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(db);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void load(String dbName){
        Dataset db;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dbName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            db = (Dataset) ois.readObject();
            ois.close();

            this.replaceRecordList(db.getRecordList());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private Integer counter = 0;
            private final int n = recordList.size();

            @Override
            public boolean hasNext() {
                return counter<n;
            }

            @Override
            public Integer next() {
                return counter++;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
