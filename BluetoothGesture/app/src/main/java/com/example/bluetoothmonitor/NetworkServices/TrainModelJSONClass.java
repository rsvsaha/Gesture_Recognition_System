package com.example.bluetoothmonitor.NetworkServices;

import org.json.JSONObject;

public class TrainModelJSONClass extends PostJSONRequestClass {


    public void createRequestObject(String Choice) {
        requestObject=new JSONObject();
        try{
            requestObject.put("TrainModel",Choice);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
