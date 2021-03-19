package com.example.batteryleveldetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewInfo;
    private TextView mTextViewPercentage;
    private ProgressBar mProgressBar;
    private String[] mStringArray = null;
    private ArrayAdapter<String> arrayAdapter;
    private ListView lvList;
    int mProgressStatus, level;
    int currentHourIn24Format, currentMinute, mSec;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            mTextViewInfo.setText("Battery Scale : " + scale);

            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            mTextViewInfo.setText(mTextViewInfo.getText() + "\nBattery Level : " + level);

            float percentage = level/ (float) scale;
            mProgressStatus = (int) ((percentage) * 100);

            mTextViewPercentage.setText("" + mProgressStatus + "%");
            mTextViewInfo.setText(mTextViewInfo.getText() + "\nPercentage : "+ mProgressStatus + "%");
            mProgressBar.setProgress(mProgressStatus);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        Calendar currentTime = Calendar.getInstance();
        currentHourIn24Format = currentTime.get(Calendar.HOUR_OF_DAY);
        currentMinute = currentTime.get(Calendar.MINUTE);
        mSec = currentTime.get(Calendar.MILLISECOND);

        Context mContext = getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver,iFilter);
        mTextViewInfo = findViewById(R.id.tv_info);
        mTextViewPercentage = findViewById(R.id.tv_percentage);
        mProgressBar = findViewById(R.id.pb);
        lvList = findViewById(R.id.lv_list);

        SharedPreferences aklatSharedPref = getSharedPreferences("bibiliyeah", Context.MODE_PRIVATE);
        String aklatData = aklatSharedPref.getString("MGA_AKLAT", null);
        Gson gson = new Gson();
        List<String> data = new ArrayList<>();
        if(aklatData != null){
            data = gson.fromJson(aklatData, new TypeToken<List<String>>(){}.getType());
        }

        mStringArray = new String[data.size()];
        mStringArray = (String[]) data.toArray(mStringArray);
        arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, mStringArray);
        lvList.setAdapter(arrayAdapter);
        lvList.setBackgroundColor(Color.BLACK);
        saveToSharedPref(data);

    }
    @Override
    protected void onResume(){
        super.onResume();

        String onResume = "onResume";
        int batPer = level;
        int timeH = currentHourIn24Format;
        int timeM = currentMinute;
        int timeMS = mSec;
        String resume = onResume + "\n" + timeH + ":" + timeM + ":" + timeMS + "\n" + batPer +"%";
        saveToSharedPref(resume);
    }
    @Override
    protected void onPause() {
        super.onPause();

        String onPause = "onPause";
        int batPer = level;
        int timeH = currentHourIn24Format;
        int timeM = currentMinute;
        int timeMS = mSec;
        String pause = onPause + "\n" + timeH + ":" + timeM + ":" + timeMS + "\n" + batPer + "%";
        saveToSharedPref(pause);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        String onDestroy = "onDestroy";
        String destroy = onDestroy;
        saveToSharedPref(destroy);
    }

    private void saveToSharedPref(List<String> wholeData){
        Gson gson = new Gson();
        String wholeDataString = gson.toJson(wholeData);
        SharedPreferences aklatSharedPref = getSharedPreferences("bibiliyeah", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = aklatSharedPref.edit();
        editor.putString("MGA_AKLAT", wholeDataString);
        editor.apply();
    }
    private void saveToSharedPref(String book){
        Gson gson = new Gson();

        SharedPreferences aklatSharedPref = getSharedPreferences("bibiliyeah", Context.MODE_PRIVATE);
        String aklatData = aklatSharedPref.getString("MGA_AKLAT", null);

        ArrayList<String> data = new ArrayList<>();
        if(aklatData != null){
            data = gson.fromJson(aklatData, new TypeToken<List<String>>(){}.getType());
        }
        data.add(book);

        String dataString = gson.toJson(data);
        SharedPreferences.Editor editor = aklatSharedPref.edit();
        editor.putString("MGA_AKLAT", dataString);
        editor.commit();
    }


}