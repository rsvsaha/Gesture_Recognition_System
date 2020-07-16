package com.example.bluetoothmonitor.DataProcessing;

import android.graphics.Color;
import android.graphics.Paint;

import com.example.bluetoothmonitor.BluetoothServices.BluetoothOperationsClass;
import com.example.bluetoothmonitor.BluetoothServices.IPostBluetoothData;

import java.util.ArrayList;
import java.util.HashMap;

public class DataProcessorClass {

    StringBuilder dataBuilderForPosting;
    String dataSetRaw;
    private static DataProcessorClass singletonDataProcessorClass;
    private static String GYROSCOPE="GYRO_";
    private static String ACCLEROMETER="ACC_";
    private static String AXIS_X="X_";
    private static String AXIS_Y="Y_";
    private static String AXIS_Z="Z_";
    ArrayList<ArrayList<Integer>> ProcessedDataset;
    ArrayList<String> AxisLabelarrayList;
    DataProcessorClass(){
            InitialiseLabels();
    }
    public static synchronized DataProcessorClass getInstanceofDataProcessorClass(){
        if(singletonDataProcessorClass==null){
            singletonDataProcessorClass=new DataProcessorClass();
        }

        return singletonDataProcessorClass;
    }

    public ArrayList<ArrayList<Integer>> getProcessedDataset() {
        return ProcessedDataset;
    }
    public void InitialiseLabels(){
        AxisLabelarrayList=new ArrayList<>();
        for(int i=0;i<6;i++){
            AxisLabelarrayList.add(ACCLEROMETER+AXIS_X+i);
            AxisLabelarrayList.add(ACCLEROMETER+AXIS_Y+i);
            AxisLabelarrayList.add(ACCLEROMETER+AXIS_Z+i);
            AxisLabelarrayList.add(GYROSCOPE+AXIS_X+i);
            AxisLabelarrayList.add(GYROSCOPE+AXIS_Y+i);
            AxisLabelarrayList.add(GYROSCOPE+AXIS_Z+i);
        }
    }
    public void InitialiseProcessedDataSet(){

        ProcessedDataset=new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<30;i++){
            ArrayList<Integer> dataPointsArrayList=new ArrayList<Integer>();
            ProcessedDataset.add(dataPointsArrayList);
        }
    }



    public void createDataSetForGraph(ArrayList<String> Data){
        for (String data:Data){
            data=data.replace("\\x00","");
            String dataPoints[]=data.split(",");     //DataPoints are in String Format now
            for(int i=1;i<=30;i++){
                ArrayList<Integer> dataValues=ProcessedDataset.get(i-1); //DataValues are stored as integers
                Integer value=Integer.parseInt(dataPoints[i]);
                dataValues.add(value);
            }
        }
    }

    public ArrayList<Paint> ColorChoser(){
        ArrayList<Paint> colors=new ArrayList<Paint>();
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);

        for(int i=0;i<30;i++){
            colors.add(mPaint);
        }

        return  colors;
    }
}
