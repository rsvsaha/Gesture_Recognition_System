package com.example.bluetoothmonitor.BluetoothServices;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothOperationsClass {

    private String bTUnavailable_Warning="SORRY NO BLUETOOTH IS AVAILABLE FOR THIS DEVICE";
    private int btEnableRequestCode=1234;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice connectedDevice;
    BluetoothSocket bluetoothSocket;
    InputStream bluetoothInputStream;
    OutputStream bluetoothOutputStream;
    boolean isReading;
    IPostBluetoothData IpostBluetoothData;


    public void setIpostBluetoothData(IPostBluetoothData postBluetoothData){
        this.IpostBluetoothData=postBluetoothData;
    }
    private static BluetoothOperationsClass singletonBluetoothOperations;

    private BluetoothOperationsClass(){
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    public static synchronized BluetoothOperationsClass getInstanceofBluetoothOperations(){
        if(singletonBluetoothOperations==null){
            singletonBluetoothOperations=new BluetoothOperationsClass();
        }

        return singletonBluetoothOperations;
    }


    public boolean isBTEnabled(){
        if(!(bluetoothAdapter==null)) {
            if (bluetoothAdapter.isEnabled()) {
            return true;
            }
        }
        return false;
    }


    public Set<BluetoothDevice> getPairedDevicesList(){
        return  bluetoothAdapter.getBondedDevices();
    }

    public void connectToDevice(String connectToDeviceName) throws IOException {
        Set<BluetoothDevice> pairedDevices=getPairedDevicesList();
        if(pairedDevices.size()>0){
            for(BluetoothDevice device:pairedDevices){
                if(device.getName().equals(connectToDeviceName)){
                    connectedDevice=device;
                    break;

                }
            }
        }
        if(connectToDeviceName!=null){
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            bluetoothSocket=connectedDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            bluetoothInputStream=bluetoothSocket.getInputStream();
            bluetoothOutputStream=bluetoothSocket.getOutputStream();
        }
    }

    public void listenForData(){

        if(bluetoothInputStream!=null){
            isReading=true;
            InputStreamReader inputStreamReader=new InputStreamReader(bluetoothInputStream);
            final BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            Thread readerThread=new Thread(new Runnable() {

                @Override
                public void run() {
                    boolean isRecording=false;
                    ArrayList<String> message=new ArrayList<>();
                    while(!Thread.currentThread().isInterrupted() && isReading){
                        try{
                            String received_message=bufferedReader.readLine().trim();
                            received_message=received_message.replace("\r\n","");
                            if(received_message!=null){}
                            if(!isRecording){
                                if(received_message.contains("BATCH STARTED")){
                                    isRecording=true;
                                    //Log.v("TEST","STARTED");
                                }
                            }
                            else if(isRecording){

                                if(received_message.contains("BATCH ENDED")){
                                    IpostBluetoothData.postBTData(message);
                                    isRecording=false;
                                    message=new ArrayList<>();
                                    //Log.v("TEST","ENDED");
                                }
                                else {

                                    message.add(received_message);
                                    //Log.v("RECEIVED",received_message);
                                    final String Data=received_message;


                                }
                            }





                        }
                        catch (Exception e){
                            e.printStackTrace();
                            isReading=false;
                        }

                    }


                }
            }) ;


            readerThread.start();

        }
    }

    public void disconnectFromDevice() throws IOException
    {
        isReading=false;
        bluetoothInputStream.close();
        bluetoothOutputStream.close();
        bluetoothSocket.close();

    }

}
