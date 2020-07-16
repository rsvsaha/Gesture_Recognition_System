package com.example.bluetoothmonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothmonitor.BluetoothServices.BluetoothOperationsClass;

import java.util.ArrayList;

public class BTDeviceSelectionActivity extends AppCompatActivity {

    RecyclerView bTPairedDevicesListRecyclerView;
    Button btn_SCAN;
    BluetoothOperationsClass mBTOperations;
    private BTPairedDevicesListAdapter btPairedDevicesListAdapter;

    private static int BT_ENABLE_REQUEST_CODE=12345;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBTOperations= BluetoothOperationsClass.getInstanceofBluetoothOperations();
        setContentView(R.layout.activity_btdevice_selection);

        bTPairedDevicesListRecyclerView=(RecyclerView) findViewById(R.id.PairedDevicesList);
        btn_SCAN=(Button)findViewById(R.id.scanning_button);
        btn_SCAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanForBTDevices();
            }
        });


        if(!mBTOperations.isBTEnabled()){
            Intent enableBTIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent,BT_ENABLE_REQUEST_CODE);
        }
        else{ScanForBTDevices();}


    }

    void FillDeviceList(){

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        bTPairedDevicesListRecyclerView.setLayoutManager(layoutManager);
        btPairedDevicesListAdapter=new BTPairedDevicesListAdapter(GetListOfPairedDevices());

        bTPairedDevicesListRecyclerView.setAdapter(btPairedDevicesListAdapter);
        btPairedDevicesListAdapter.setItemClickListener(new BTPairedDevicesListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String DeviceName, String Address) {
                ConnectAndReturnToReceivingActivity(DeviceName,Address);
            }
        });

    }

    private void ConnectAndReturnToReceivingActivity(String deviceName, String address) {
        try{
            mBTOperations.connectToDevice(deviceName); //ConnectToDevice Then Go To The Activity

        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"SOMETHING WENT WRONG",Toast.LENGTH_SHORT).show();;
        }
        finish();
    }


    private void ScanForBTDevices() {
        FillDeviceList();
    }


    private ArrayList<BTDeviceDetails> GetListOfPairedDevices(){

        ArrayList<BTDeviceDetails> list_BTDeviceDetails=new ArrayList<BTDeviceDetails>();

        for(BluetoothDevice bluetoothDevice:mBTOperations.getPairedDevicesList()){
            BTDeviceDetails btDeviceDetails=new BTDeviceDetails(bluetoothDevice.getName(),bluetoothDevice.getAddress());
          //  Log.e("TESTING",btDeviceDetails.getDeviceName());
            //Log.e("TESTING",btDeviceDetails.getDeviceAddress());
            list_BTDeviceDetails.add(btDeviceDetails);
        }
        return list_BTDeviceDetails;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==BT_ENABLE_REQUEST_CODE && resultCode==Activity.RESULT_OK){
            ScanForBTDevices();
        }
        else {
            Toast.makeText(getApplicationContext(),"YOU NEED TO ENABLE BLUETOOTH",Toast.LENGTH_SHORT).show();;
            finish();
        }
    }
}


class BTDeviceDetails{
    private String deviceName,deviceAddress;

    BTDeviceDetails(String deviceName,String deviceAddress){
        this.deviceName=deviceName;
        this.deviceAddress=deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

}

class BTPairedDevicesListAdapter extends RecyclerView.Adapter<BTPairedDevicesListAdapter.ViewHolder>{

    public interface  ItemClickListener{
        void onItemClick(String DeviceName,String Address);
    }
    private ArrayList<BTDeviceDetails> list_BTDeviceDetails;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView device_NameField;
        private TextView device_AddressField;
        private LinearLayout device_ItemContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            device_ItemContainer=(LinearLayout)itemView.findViewById(R.id.device_name_layout_itemContainer);
            device_NameField=(TextView) itemView.findViewById(R.id.device_name_layout_deviceName);
            device_AddressField=(TextView)itemView.findViewById(R.id.device_name_layout_Address);
        }
    }

    public BTPairedDevicesListAdapter(ArrayList<BTDeviceDetails> BTDeviceDetails)
    {
        this.list_BTDeviceDetails=BTDeviceDetails;
     //   Log.e("TESTING",this.list_BTDeviceDetails.toString());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_name_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        BTDeviceDetails each_BTDeviceDetails=list_BTDeviceDetails.get(i);
        viewHolder.device_NameField.setText(each_BTDeviceDetails.getDeviceName());
        viewHolder.device_AddressField.setText(each_BTDeviceDetails.getDeviceAddress());
        viewHolder.device_ItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(viewHolder.device_NameField.getText().toString(),viewHolder.device_AddressField.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_BTDeviceDetails.size();
    }


}