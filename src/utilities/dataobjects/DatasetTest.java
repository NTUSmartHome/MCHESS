package utilities.dataobjects;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by YaHung on 2015/9/8.
 */
public class DatasetTest {
    DatasetTest(){

        //Dataset db = new Dataset("testingDB1");

        Dataset db = new Dataset();

        for(int i=0; i<10; i++) {
            if(i%2==0)
                db.add(new Double[] {i+0.1,i+0.2,i+0.3},"T");
            else
                db.add(new Double[] {i+0.1,i+0.2,i+0.3},5);
        }
        for(Integer rId: db){
            Record r = db.get(rId);

            System.out.println(Arrays.toString(r.getX().toArray()));
            System.out.println(r.getY());
        }



        db.save("testingDB1");

        db.clear();

        db.load("testingDB1");

        System.out.println();System.out.println();System.out.println();
        for(Integer rId: db){
            Record r = db.get(rId);
            ArrayList<Double> p = (ArrayList<Double>) r.getX();
            for(double value: p){
                System.out.print(value+", ");
            }
            System.out.println();
        }


    }




    public static void main(String[] args) {

        new DatasetTest();
    }


}
