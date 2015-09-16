package utilities.machinelearning.classifiers.BayesianNet;

import utilities.dataobjects.Dataset;

import java.util.Random;

/**
 * Created by YaHung on 2015/9/11.
 */
public class BNTest {
    BNTest(){

        /*
        Dataset db = new Dataset();
        Random random = new Random();
        for(int i=0; i<250; i++){
            Integer[] node = new Integer[2];
            node[0] = random.nextInt(2)+100;
            node[1] = random.nextInt(2)-100;
            db.add(node,0);
        }
        for(int i=0; i<250; i++){
            Integer[] node = new Integer[2];
            node[0] = random.nextInt(2)+50;
            node[1] = random.nextInt(2)+50;
            db.add(node,1);
        }

        for(int i=0; i<250; i++){
            Integer[] node = new Integer[2];
            node[0] = random.nextInt(2)-150;
            node[1] = random.nextInt(2)-50;
            db.add(node,2);
        }
        */

        BaseBayesNet bn = new BaseBayesNet();
        //bn.setDataset(db);
        //bn.run();

        bn.load();
        bn.printEvaluation();

    }


    public static void main(String[] args) {

        new BNTest();
    }

}
