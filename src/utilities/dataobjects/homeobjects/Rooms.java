package utilities.dataobjects.homeobjects;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by YaHung on 2015/10/6.
 */
public class Rooms implements Serializable,Iterable<String> {
    ArrayList<String> rooms = new ArrayList<>();
    protected String filePath;

    public Rooms(){
        checkDirectory();
    }

    private void checkDirectory(){
        filePath = "util";
        File file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/dataobjects";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}

        filePath += "/homeobjects";
        file = new File(filePath);
        if(!file.exists()) { file.mkdir();}
    }

    public int get(String room){
        int rId = -1;
        for (int i = 0; i < rooms.size(); i++) {
            if(room.equals(rooms.get(i))){
                rId = i;
                break;
            }
        }
        return rId;
    }

    public void add(String room){
        rooms.add(room);
    }

    public String get(int rId){
        return rooms.get(rId);
    }

    public int size(){
        return rooms.size();
    }

    private String getFilePath(){
        return filePath;
    }
    private ArrayList<String> getRooms(){
        return rooms;
    }

    public void save(){
        try {
            FileOutputStream fos = new FileOutputStream(filePath+"/rooms");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try {
            FileInputStream fis = new FileInputStream(filePath+"/rooms");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Rooms r = (Rooms) ois.readObject();
            ois.close();
            this.filePath = r.getFilePath();
            this.rooms = r.getRooms();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private Integer counter = 0;
            private final int n = size();

            @Override
            public boolean hasNext() {
                return counter<n;
            }

            @Override
            public String next() {
                return rooms.get(counter++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
