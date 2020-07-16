package com.example.bluetoothmonitor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bluetoothmonitor.BTDeviceSelectionActivity;
import com.example.bluetoothmonitor.BluetoothServices.BluetoothOperationsClass;
import com.example.bluetoothmonitor.BluetoothServices.IPostBluetoothData;
import com.example.bluetoothmonitor.NetworkServices.BackendURLs;
import com.example.bluetoothmonitor.NetworkServices.IServerResponseWorker;
import com.example.bluetoothmonitor.NetworkServices.PostJSONRequestClass;
import com.example.bluetoothmonitor.NetworkServices.TrainingModeJSONClass;
import com.example.bluetoothmonitor.R;
import com.example.bluetoothmonitor.TTSServices.ITTSSpeaker;

import java.util.ArrayList;



public class PredictionFragment extends Fragment implements IPostBluetoothData, IServerResponseWorker {
    /*  TODO IMPLEMENT FOR GRAPHING
    private Switch dataGraphToggleSwitch;

     */
    private TextView dataField;
    //TODO FOR GRAPH private FrameLayout graphSpace;
    private Button btConnect;
    private Button btDisconnect;
    ProgressBar progressBar;
    TextView predictedWord;
    private IFragmentBTConnectionListener iFragmentBTConnectionListener;
    private ITTSSpeaker iTTSSpeaker;

    public PredictionFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prediction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataField=view.findViewById(R.id.data_field);
        dataField.setMovementMethod(new ScrollingMovementMethod());

        predictedWord=(TextView)view.findViewById(R.id.predicted_word);
        //TODO FOR GRAPH    graphSpace=(FrameLayout)view.findViewById(R.id.data_graph_holder);

        /*TODO IMPLEMENT FOR GRAPH
        dataGraphToggleSwitch=(Switch)view.findViewById(R.id.graph_data_toggler);
        dataGraphToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LoadGraph();

                }
                else{
                    LoadRawData();
                }
            }
        });
        */

        progressBar=(ProgressBar)view.findViewById(R.id.prediction_progress);

        btConnect=(Button)view.findViewById(R.id.btn_connect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToBT();
            }
        });


        btDisconnect=(Button)view.findViewById(R.id.btn_disconnect);
        btDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectFromBT();
            }
        });
    }
    /*TODO IMPLEMENT FOR GRAPH
    private void LoadRawData(){
        dataField.setVisibility(View.VISIBLE);
    }

    private void LoadGraph() {
        dataField.setVisibility(View.GONE);
    }
    */


    private void disconnectFromBT() {
        iFragmentBTConnectionListener.disconnect();
    }

    private void connectToBT() {
        iFragmentBTConnectionListener.connect();
    }



    @Override
    public void onDetach() {
        super.onDetach();

    }



    @Override
    public void postBTData(ArrayList<String> Data) {
        StringBuilder SenosrReadings=new StringBuilder();
        for(String reading:Data){
            SenosrReadings.append(reading+"\n");
        }
        showData(SenosrReadings.toString());


        /*TODO IMPLEMENT IT FOR GRAPHING
        DataProcessorClass.getInstanceofDataProcessorClass().InitialiseProcessedDataSet();
        DataProcessorClass.getInstanceofDataProcessorClass().createDataSetForGraph(Data);
        */
        sendToServer(SenosrReadings.toString());

    }
    void showData(final String data){
        if(dataField!=null){

           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   dataField.setText(data);
               }
           });

        }
    }

    private void sendToServer(String data) {
        PostJSONRequestClass postJSONRequestClass=new PostJSONRequestClass();
        postJSONRequestClass.setUrl(BackendURLs.getPredictURL());
        postJSONRequestClass.createRequestObject(data);
        postJSONRequestClass.setiServerResponseWorker(this);
        postJSONRequestClass.execute();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });




    }


    @Override
    public boolean ResponseWork(final String data) {
        boolean isRequestSuccesful=true;
        if(data.equals("ERROR")){
            isRequestSuccesful=false;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                predictedWord.setText(data);
            }
        });
        iTTSSpeaker.speak(data);
        return isRequestSuccesful;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IFragmentBTConnectionListener && context instanceof ITTSSpeaker){
            iFragmentBTConnectionListener=(IFragmentBTConnectionListener)context;
            iTTSSpeaker=(ITTSSpeaker)context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
