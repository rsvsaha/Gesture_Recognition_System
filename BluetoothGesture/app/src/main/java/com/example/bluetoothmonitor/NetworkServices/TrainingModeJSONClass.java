package com.example.bluetoothmonitor.NetworkServices;

import org.json.JSONObject;

public class TrainingModeJSONClass extends PostJSONRequestClass {


    public void createRequestObject(String GestureName, int itrnumber, String Data) {
        requestObject=new JSONObject();
        try{
            String filename=GestureName+"_"+itrnumber;
            requestObject.put("FileName",filename);
            requestObject.put("Data",Data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
