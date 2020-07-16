package com.example.bluetoothmonitor;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothmonitor.NetworkServices.BackendURLs;
import com.example.bluetoothmonitor.NetworkServices.ClearDatasetJSONClass;
import com.example.bluetoothmonitor.NetworkServices.IServerResponseWorker;
import java.util.regex.*;

public class SettingActivity extends AppCompatActivity implements IServerResponseWorker {





    Button saveChange;
    EditText serverIP;
    Button clearData;
    Button delete;
    Button cancel;
    RelativeLayout popUpView;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        serverIP=(EditText)findViewById(R.id.ip_field);
        serverIP.setText(BackendURLs.IP);

        saveChange=(Button)findViewById(R.id.save_change);
        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIP();
            }
        });
        clearData=(Button)findViewById(R.id.clear_dataset);
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
    }

    private void showAlert() {
        initPopupView();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Set title, icon, can not cancel properties.
        alertDialogBuilder.setTitle("GESTURE DATASET CLEAR ALERT");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(popUpView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              clearDataset();
              alertDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    private void initPopupView(){

        LayoutInflater layoutInflater=LayoutInflater.from(this);
        popUpView=(RelativeLayout)layoutInflater.inflate(R.layout.clear_dataset_popup_layout,null);
        delete=(Button)popUpView.findViewById(R.id.btn_popup_delete);
        cancel=(Button)popUpView.findViewById(R.id.btn_popup_cancel);
    }

    private void clearDataset() {
        ClearDatasetJSONClass clearDatasetJSONClass=new ClearDatasetJSONClass();

        clearDatasetJSONClass.setUrl(BackendURLs.getClearDatasetURL());
        clearDatasetJSONClass.setiServerResponseWorker(this);
        clearDatasetJSONClass.createRequestObject("YES");
        clearDatasetJSONClass.execute();
        //Log.v("IP",BackendURLs.IP);
    }


    private void setIP() {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String inputURL=serverIP.getText().toString();
        if(!Pattern.matches(regex,inputURL)){
            Toast.makeText(this,"INVALID URL",Toast.LENGTH_SHORT).show();
        }
        else {
            BackendURLs.IP=inputURL;
            finish();
        }
    }

    @Override
    public boolean ResponseWork(final String data) {
        if(data.contains("ERROR")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"COULD NOT CLEAR DATA",Toast.LENGTH_SHORT).show();
                    }
            });

        }
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"DATA CLEARED:"+data,Toast.LENGTH_SHORT).show();
                }
            });
            }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
