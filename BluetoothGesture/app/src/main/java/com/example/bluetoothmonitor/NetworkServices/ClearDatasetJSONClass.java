package com.example.bluetoothmonitor.NetworkServices;

import org.json.JSONObject;

public class ClearDatasetJSONClass extends PostJSONRequestClass {
    @Override
    public void createRequestObject(String Choice) {
        requestObject=new JSONObject();
        try{
            requestObject.put("Clear",Choice);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
