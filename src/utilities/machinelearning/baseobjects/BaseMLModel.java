package utilities.machinelearning.baseobjects;

import utilities.dataobjects.Dataset;
import utilities.dataobjects.Record;

import java.io.Serializable;

/**
 * Created by YaHung on 2015/9/9.
 */
interface BaseMLModel extends Serializable {

    void setDataset(Dataset trainingData);

    Record predict(Record testingData);

    Dataset predict(Dataset testingData);

    Dataset validate(Dataset testingData);

    void save();

    void load();
}
