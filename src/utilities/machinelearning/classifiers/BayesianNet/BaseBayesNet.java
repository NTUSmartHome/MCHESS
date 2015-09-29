package utilities.machinelearning.classifiers.BayesianNet;

import utilities.dataobjects.Record;
import utilities.machinelearning.baseobjects.BaseMLClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;
import java.io.*;

public class BaseBayesNet extends BaseMLClassifier{
    protected Classifier bn;
    Instances instances;
    FastVector atts = new FastVector();
    public BaseBayesNet(){
        super();
        checkDirector();
        parameters.set(insts.getModel(), "weka.classifiers.bayes.net.search.global.TAN");
        this.parameters.add(insts.getModelName(), filePath + "/tmpAPModel");
    }
    public BaseBayesNet(String parameters) {
        super(parameters);
        checkDirector();
        this.parameters.set(insts.getModel(), "weka.classifiers.bayes.net.search.global.TAN");
        if(!this.parameters.exist(insts.getModelName())) {
            this.parameters.set(insts.getModelName(), filePath + "/tmpAPModel");
        }
    }

    private void checkDirector(){
        filePath += "/bayesNet";
        File file = new File(filePath);
        if(!file.exists()) { file.mkdir();}
    }

    public Classifier run(){
        // weka instance
        // set class
        FastVector classVals = new FastVector(classes.getYList().size());
        for(Object classVal:classes.getYList()){
            classVals.addElement("c"+classVal);
        }
        Attribute class_ = new Attribute("Class", classVals);
        atts.addElement(class_);

        for(int dim=0; dim<trainingData.getNumDimension(); dim++) {
            atts.addElement(new Attribute("a" + dim));
        }

        this.instances = new Instances("Dataset", atts, trainingData.getRecordNumber()+1);

        // set features
        for(int i=0; i<trainingData.getRecordNumber(); i++){
            Instance instance = new Instance(atts.size());
            for(int dim=1; dim<=trainingData.getNumDimension(); dim++) {
                if(trainingData.get(i).getX().get(dim-1) instanceof  Integer){
                    instance.setValue((Attribute) atts.elementAt(dim), (double) (Integer) trainingData.get(i).getX().get(dim-1));
                }
                else {
                    instance.setValue((Attribute) atts.elementAt(dim), (Double) trainingData.get(i).getX().get(dim-1));
                }
                instance.setValue((Attribute) atts.elementAt(0), "c"+ trainingData.get(i).getY());
            }
            this.instances.add(instance);
        }
        this.instances.setClassIndex(0);

        // build Naive BN
        try {
            bn = new NaiveBayes();
            bn.buildClassifier(this.instances);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Output
        save();

        return bn;
    }

    public Record predict(Record instance){
        try {
            Instance instance_ = new Instance(atts.size());
            for(int dim=1; dim<=instance.getX().size(); dim++) {
                instance_.setValue((Attribute) atts.elementAt(dim), (double)(Integer) instance.getX().get(dim - 1));
            }

            String defaultClass = "c"+this.classes.getY(0);

            instance_.setValue((Attribute) atts.elementAt(0), defaultClass);
            instance_.setDataset(this.instances);
            instance.setyPredicted(bn.classifyInstance(instance_));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void printEvaluation(){
        // evaluate
        double right = 0.0;
        for(int i=0; i<this.instances.numInstances(); i++){
            try {
                if(bn.classifyInstance(this.instances.instance(i))==this.instances.instance(i).classValue()){
                    right++;
                }

                System.out.println("Result is "+bn.classifyInstance(this.instances.instance(i)));
                System.out.println("Ground truth is "+this.instances.instance(i).classValue());
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("naiveBN classification precision: "+(right/(double)this.instances.numInstances()));
    }

    public void save(){
        try {
            FileOutputStream fos = new FileOutputStream((String) parameters.get(insts.getModelName()));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bn);
            oos.flush();
            oos.close();

            fos = new FileOutputStream( parameters.get(insts.getModelName())+"_instances");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(instances);
            oos.flush();
            oos.close();

            fos = new FileOutputStream( parameters.get(insts.getModelName())+"_attributes");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(atts);
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
            bn = new NaiveBayes();
            FileInputStream fis = new FileInputStream((String) parameters.get(insts.getModelName()));
            ObjectInputStream ois = new ObjectInputStream(fis);
            bn = (Classifier) ois.readObject();
            ois.close();

            fis = new FileInputStream( parameters.get(insts.getModelName())+"_instances");
            ois = new ObjectInputStream(fis);
            this.instances = (Instances) ois.readObject();
            ois.close();

            fis = new FileInputStream( parameters.get(insts.getModelName())+"_attributes");
            ois = new ObjectInputStream(fis);
            this.atts = (FastVector) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
