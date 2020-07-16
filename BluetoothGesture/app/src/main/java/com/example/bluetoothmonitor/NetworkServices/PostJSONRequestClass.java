package com.example.bluetoothmonitor.NetworkServices;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

public class PostJSONRequestClass extends AsyncTask<Void,Void,Void> {
    JSONObject requestObject;
    String url;
    IServerResponseWorker iServerResponseWorker;
    int timeout=20000;
    public void setTimeout(int timeout){
        this.timeout=timeout;
    }
    public void setiServerResponseWorker(IServerResponseWorker iServerResponseWorker) {
        this.iServerResponseWorker = iServerResponseWorker;
    }

    public void createRequestObject(String Data){
        requestObject=new JSONObject();
        try{
            requestObject.put("Data",Data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private void setPOSTRequestContent(HttpURLConnection httpURLConnection,JSONObject jsonObject) throws IOException{
        OutputStream outputStream=httpURLConnection.getOutputStream();
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
        writer.write(jsonObject.toString());
        writer.flush();
        writer.close();
        outputStream.close();

    }

    private String HttpPOST() throws IOException, SocketTimeoutException {
        URL url=new URL(this.url);
        HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        urlConnection.setConnectTimeout(timeout);
        setPOSTRequestContent(urlConnection,requestObject);
        urlConnection.connect();



        if(urlConnection.getResponseMessage().toString().contains("OK")){
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb=new StringBuilder();
            String tempString;
            while((tempString=bufferedReader.readLine())!=null){
                    sb.append(tempString);
            }
            return sb.toString();
        }
        return "ERROR";
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {

            String result=HttpPOST();
            iServerResponseWorker.ResponseWork(result);
        }
        catch (SocketTimeoutException e){
            iServerResponseWorker.ResponseWork("CONNECTION ERROR");
        }
        catch (Exception e){
            e.printStackTrace();
        }
 return null;
    }
}
