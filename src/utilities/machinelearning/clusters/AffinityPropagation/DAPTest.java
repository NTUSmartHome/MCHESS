package utilities.machinelearning.clusters.AffinityPropagation;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;
import utilities.machinelearning.baseobjects.BaseInstractors;
import utilities.machinelearning.baseobjects.Cluster;

import java.util.Random;

/**
 * Created by YaHung on 2015/9/11.
 */
public class DAPTest {
    DAPTest(){
        Dataset db = new Dataset();
        Random random = new Random();
        /*
        for(int i=0; i<250; i++){
            Integer[] node = new Integer[1];
            node[0] = random.nextInt(2)+100;
            //node[1] = random.nextInt(2)-100;
            db.add(node,0);
        }
        for(int i=0; i<250; i++){
            Integer[] node = new Integer[1];
            node[0] = random.nextInt(2)+50;
           // node[1] = random.nextInt(2)+50;
            db.add(node,0);
        }
        for(int i=0; i<250; i++){
            Integer[] node = new Integer[1];
            node[0] = random.nextInt(2)-150;
           // node[1] = random.nextInt(2)-50;
            db.add(node,0);
        }
        */
        for(int i=0; i<20; i++){
            Integer[] node = new Integer[1];
            node[0] = 100;
            //node[1] = random.nextInt(2)-100;
            db.add(node,0);
        }
        for(int i=0; i<10; i++){
            Integer[] node = new Integer[1];
            node[0] = 50;
            // node[1] = random.nextInt(2)+50;
            db.add(node,0);
        }
        for(int i=0; i<10; i++){
            Integer[] node = new Integer[1];
            node[0] = -150;
            // node[1] = random.nextInt(2)+50;
            db.add(node,0);
        }
        for(int i=0; i<10; i++){
            Integer[] node = new Integer[1];
            node[0] = -250;
            // node[1] = random.nextInt(2)+50;
            db.add(node,0);
        }

        DensityAffinityPropagation dap = new DensityAffinityPropagation("-k 0 -t 50");

        //dap.load();
        dap.setDataset(db);
        dap.run(100);

        System.out.println();

        for(Cluster cluster: dap.getClusters()) {
            for (Record r : cluster) {
                System.out.println("Node "+r.getX() + ", cluster "+r.getY());
            }
            System.out.println();
        }

        /*System.out.println("[50][50] belongs to "+dap.predict(new Integer[]{50, 50}, 0));
        System.out.println("[100][-100] belongs to " + dap.predict(new Integer[]{100, -100}, 0));
        System.out.println("[-150][-50] belongs to "+dap.predict(new Integer[]{-150, -50}, 0));
        System.out.println("[53][50] belongs to "+dap.predict(new Integer[]{53, 50}, 0));
        System.out.println("[49][48] belongs to "+dap.predict(new Integer[]{49, 48}, 0));
        */
        System.out.println("[50][50] belongs to "+dap.predict(new Integer[]{50}, 0));
        System.out.println("[100][-100] belongs to " + dap.predict(new Integer[]{100}, 0));
        System.out.println("[-150][-50] belongs to "+dap.predict(new Integer[]{-150}, 0));
        System.out.println("[53][50] belongs to "+dap.predict(new Integer[]{53}, 0));
        System.out.println("[49][48] belongs to "+dap.predict(new Integer[]{49}, 0));


        dap.save();
    }

    public static void main(String[] args) {

        new DAPTest();
    }

}
