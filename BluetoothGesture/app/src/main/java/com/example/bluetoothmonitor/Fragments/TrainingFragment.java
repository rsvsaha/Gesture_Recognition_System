package com.example.bluetoothmonitor.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothmonitor.BluetoothServices.IPostBluetoothData;
import com.example.bluetoothmonitor.NetworkServices.BackendURLs;
import com.example.bluetoothmonitor.NetworkServices.IServerResponseWorker;
import com.example.bluetoothmonitor.NetworkServices.TrainModelJSONClass;
import com.example.bluetoothmonitor.NetworkServices.TrainingModeJSONClass;
import com.example.bluetoothmonitor.R;
import com.example.bluetoothmonitor.TTSServices.ITTSSpeaker;

import java.util.ArrayList;

public class TrainingFragment extends Fragment implements IPostBluetoothData, IServerResponseWorker {

    /*  TODO IMPLEMENT FOR GRAPHING
    private Switch dataGraphToggleSwitch;
    */
    private static boolean isTraining=false;

    private ITTSSpeaker ittsSpeaker;
    private TextView dataField;
    Button btConnect;
    Button btDisconnect;
    Button trainModel;
    EditText sampleWord;
    TextView serverResponse;
    TextView noOfSamplesRecorded;
    SeekBar noOfSamplesSeekbar;
    int requiredNoofSamples=20;
    int sampleCounter=0;
    TextView sampleSeekBarLabel;



    Button setWordPopup;
    Button cancelWordPopup;
    EditText editTextGestureWordPopup;
    TableLayout popUpView;
    AlertDialog alertDialog;
    //TODO FOR GRAPH FrameLayout graphSpace;


    private IFragmentBTConnectionListener iFragmentBTConnectionListener;

    private static String RESPONSE_FROM_SERVER="SERVER RESPONSE: ";
    private static String NO_OF_SAMPLES_RECORDED="NO. OF SAMPLES RECORDED: ";
    private static String NUMBER_OF_SAMPLES="NUMBER OF SAMPLES";




    public TrainingFragment() {
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
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataField=(TextView)view.findViewById(R.id.data_field);
        dataField.setMovementMethod(new ScrollingMovementMethod());

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


        trainModel=(Button)view.findViewById(R.id.train_model);
        trainModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainMLModel();
            }
        });

        noOfSamplesRecorded=(TextView)view.findViewById(R.id.no_of_samples_recorded);
        serverResponse=(TextView)view.findViewById(R.id.training_server_response);
        sampleSeekBarLabel=(TextView)view.findViewById(R.id.sample_seekbar_label);

        sampleWord=(EditText)view.findViewById(R.id.sample_word);

        sampleWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenEditingPopup();
            }
        });
        //TODO FOR GRAPH graphSpace=(FrameLayout)view.findViewById(R.id.data_graph_holder);





        noOfSamplesSeekbar=(SeekBar)view.findViewById(R.id.sample_count);
        noOfSamplesSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                requiredNoofSamples=progress;
                sampleSeekBarLabel.setText(NUMBER_OF_SAMPLES+"("+requiredNoofSamples+")");
                if(requiredNoofSamples-sampleCounter>=0){
                    requiredNoofSamples=requiredNoofSamples-sampleCounter;
                }
                else{
                    requiredNoofSamples=0;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sampleSeekBarLabel.append("("+ noOfSamplesSeekbar.getProgress() +")");

       /* dataGraphToggleSwitch=(Switch)view.findViewById(R.id.graph_data_toggler);
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


    }

    private void OpenEditingPopup() {
        initPopupView();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // Set title, icon, can not cancel properties.
        alertDialogBuilder.setTitle("GESTURE WORD SETTER ALERT");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(popUpView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        setWordPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleWord.setText(editTextGestureWordPopup.getText().toString());
                sampleCounter=0;
                requiredNoofSamples=20;
                alertDialog.cancel();
                noOfSamplesSeekbar.setProgress(requiredNoofSamples);
            }
        });
        cancelWordPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    private void initPopupView(){

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        popUpView=(TableLayout)layoutInflater.inflate(R.layout.enter_gesture_popup_layout,null);
        editTextGestureWordPopup=(EditText)popUpView.findViewById(R.id.sample_word_popup_edittext);
        setWordPopup=(Button)popUpView.findViewById(R.id.btn_popup_setword);
        cancelWordPopup=(Button)popUpView.findViewById(R.id.btn_popup_cancelword);
    }





    /*TODO IMPLEMENT IT
    private void LoadRawData(){

        dataField.setVisibility(View.VISIBLE);
    }*/
    /*TODO IMPLEMENT IT
    private void LoadGraph() {
        dataField.setVisibility(View.GONE);
        graphSpace.draw(draw(graphSpace.getWidth(),graphSpace.getHeight()));
    }
    */
    private void trainMLModel() {

        TrainModelJSONClass trainModelJSONClass=new TrainModelJSONClass();
        trainModelJSONClass.createRequestObject("YES");
        trainModelJSONClass.setUrl(BackendURLs.getTrainModelURL());
        trainModelJSONClass.setiServerResponseWorker(this);
        trainModelJSONClass.execute();
        trainModel.setEnabled(false);
        this.isTraining=true;
    }

    private void disconnectFromBT() {
        iFragmentBTConnectionListener.disconnect();
    }

    private void connectToBT() {
        iFragmentBTConnectionListener.connect();
    }

    @Override
    public void postBTData(ArrayList<String> Data) {
        StringBuilder SenosrReadings=new StringBuilder();
        for(String reading:Data){
            SenosrReadings.append(reading+"\n");
        }
        showData(SenosrReadings.toString());
        sampleRecordingFunction(SenosrReadings.toString());

        /*TODO IMPLEMENT IT FOR GRAPHING
        DataProcessorClass.getInstanceofDataProcessorClass().InitialiseProcessedDataSet();
        DataProcessorClass.getInstanceofDataProcessorClass().createDataSetForGraph(Data);
        */

    }


    private void sampleRecordingFunction(String data){
        if(!sampleWord.getText().toString().equals("")&& requiredNoofSamples!=0){
            sampleCounter+=1;
            sendToServer(data);
            requiredNoofSamples=requiredNoofSamples-1;
            if(requiredNoofSamples==0){
                sampleCounter=0;
            }

        }

    }


    private void sendToServer(String data) {
        TrainingModeJSONClass trainingModeJSONClass=new TrainingModeJSONClass();
        trainingModeJSONClass.setiServerResponseWorker(this);
        trainingModeJSONClass.createRequestObject(sampleWord.getText().toString(),sampleCounter,data);
        trainingModeJSONClass.setUrl(BackendURLs.getTrainURL());
        trainingModeJSONClass.execute();
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


    @Override
    public boolean ResponseWork(final String data) {
        ittsSpeaker.speak(data);



        boolean responseSuccesful=true;
        if (data.equals("ERROR" )|| !data.equals("CONNECTION_ERROR")) {
            requiredNoofSamples=requiredNoofSamples+1; //INCASE OF AN ERRORED REQUEST REVERSE THE DECREMENT OF THE COUNTER
            responseSuccesful=false;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isTraining){
                    trainModel.setEnabled(true);
                    isTraining=false;
                }
                serverResponse.setText(RESPONSE_FROM_SERVER+data);
                if (data.contains("_") ) {
                    noOfSamplesRecorded.setText(NO_OF_SAMPLES_RECORDED + data.split("_")[1]);
                }
            }
        });
        return responseSuccesful;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IFragmentBTConnectionListener && context instanceof ITTSSpeaker){
            ittsSpeaker=(ITTSSpeaker)context;
            iFragmentBTConnectionListener=(IFragmentBTConnectionListener)context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    /*TODO IMPLEMENT IT
    Canvas draw(int maxwidth,int maxheight){
        float heightDivision=maxheight/65536;
        Canvas drawingCanvas=new Canvas();
        DataProcessorClass dataProcessorClass=DataProcessorClass.getInstanceofDataProcessorClass();
        ArrayList<ArrayList<Integer>> arrayLists=dataProcessorClass.getProcessedDataset();
        ArrayList<Paint> colors=dataProcessorClass.ColorChoser();

        for(int i=0;i<30;i++){
            ArrayList<Integer> dataPoints=arrayLists.get(i);
            float widthDivision=maxwidth/dataPoints.size();

            for(int j=0;j<=dataPoints.size()-1;j++){
                float Xstart=j*widthDivision;
                float Ystart=maxheight-(dataPoints.get(j)*heightDivision);
                float Xstop=(j+1)*widthDivision;
                float Ystop=maxheight-(dataPoints.get(j+1)*heightDivision);

                drawingCanvas.drawLine(Xstart,Ystart,Xstop,Ystop,colors.get(i));

            }


        }



        return drawingCanvas;
    }
        */


}
