package com.example.bluetoothmonitor.NetworkServices;

public class BackendURLs {

    public static String IP="https://pacific-bayou-35154.herokuapp.com";
    public static String getTrainURL(){
        String trainURL=IP+"/train";
        return trainURL;
    }
    public static String getPredictURL(){
        String predictURL=IP+"/predict";

        return predictURL;
    }

    public static String getTrainModelURL(){
        String trainModelURL=IP+"/trainModel";

        return trainModelURL;
    }
    public static String getClearDatasetURL(){
        String clearDatasetURL=IP+"/clearDataset";
        return  clearDatasetURL;
    }

}
