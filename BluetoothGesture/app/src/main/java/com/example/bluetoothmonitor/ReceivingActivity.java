package com.example.bluetoothmonitor;

import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothmonitor.BluetoothServices.BluetoothOperationsClass;
import com.example.bluetoothmonitor.BluetoothServices.IPostBluetoothData;
import com.example.bluetoothmonitor.Fragments.IFragmentBTConnectionListener;
import com.example.bluetoothmonitor.Fragments.PredictionFragment;
import com.example.bluetoothmonitor.Fragments.TrainingFragment;
import com.example.bluetoothmonitor.TTSServices.ITTSSpeaker;
import com.example.bluetoothmonitor.TTSServices.TTSMuteUnmuteClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReceivingActivity extends AppCompatActivity implements IPostBluetoothData,IFragmentBTConnectionListener,TextToSpeech.OnInitListener, ITTSSpeaker {



    private static int BT_DEVICE_SELECTION_REQUEST_CODE =12341;
    private static int BT_DEVICE_SELECTION_RESULT_CODE =12342;
    private static int SETTING_REQUEST_CODE=1771;
    TextToSpeech ttsEngine;


    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    BluetoothOperationsClass bluetoothOperationsClass;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new PredictionFragment(), "PREDICTION");
        adapter.addFragment(new TrainingFragment(), "TRAINING");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        bluetoothOperationsClass=BluetoothOperationsClass.getInstanceofBluetoothOperations();
        bluetoothOperationsClass.setIpostBluetoothData(this);
        bluetoothOperationsClass.listenForData();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(myToolbar);
        ttsEngine = new TextToSpeech(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothOperationsClass.listenForData();

    }

    Fragment findActiveFragment(){
        Fragment mfragment=adapter.getItem(viewPager.getCurrentItem());
        return mfragment;
    }



    @Override
    public void postBTData(ArrayList<String> Data) {
        Fragment mfragment=findActiveFragment();
        if(mfragment instanceof IPostBluetoothData){
            ((IPostBluetoothData) mfragment).postBTData(Data);
        }
    }

    @Override
    public void connect() {
        Intent intent=new Intent(this,BTDeviceSelectionActivity.class);
        startActivityForResult(intent, BT_DEVICE_SELECTION_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.mute_unmute:
                if(TTSMuteUnmuteClass.Mute){

                    item.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_volume_unmute_white_24dp));
                    TTSMuteUnmuteClass.Mute=!TTSMuteUnmuteClass.Mute;
                }
                else {
                    item.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_volume_mute_white_24dp));
                    TTSMuteUnmuteClass.Mute=!TTSMuteUnmuteClass.Mute;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_settings_menu, menu);
        return true;
    }

    private void openSettings() {
       // Toast.makeText(this,"OPENING SETTINGS",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,SettingActivity.class);
        startActivityForResult(intent,SETTING_REQUEST_CODE);
    }

    @Override
    public void disconnect() {
        try {
            BluetoothOperationsClass.getInstanceofBluetoothOperations().disconnectFromDevice();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = ttsEngine.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
              //  Log.v("TTS", "This Language is not supported");
            }
        } else {
            //Log.v("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void speak(String data) {
        if(!TTSMuteUnmuteClass.Mute)
            {if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsEngine.speak(data,TextToSpeech.QUEUE_FLUSH,null,null);
            } else {
                ttsEngine.speak(data, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsEngine.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ttsEngine.stop();
    }
}

class TabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}


