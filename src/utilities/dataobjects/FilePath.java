package utilities.dataobjects;

import java.io.File;

/**
 * Created by YaHung on 2015/9/23.
 */
public class FilePath {
    final String util = "util";
    final String dataobjects = "dataobjects";
    final String sensorobjects = "sensorobjects";

    public FilePath(){
        File file = new File(util);
        if(!file.exists()) { file.mkdir();}
    }






}
