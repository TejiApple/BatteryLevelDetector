package com.example.batteryleveldetector;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewInfo;
    private TextView mTextViewPercentage;
    private ProgressBar mProgressBar;
    private String[] mStringArray = null;
    private ArrayAdapter<String> arrayAdapter;
    private ListView lvList;
    int mProgressStatus;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            mTextViewInfo.setText("Battery Scale : " + scale);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
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

    private void saveToSharedPref(List<String> wholeData){
        Gson gson = new Gson();
        String wholeDataString = gson.toJson(wholeData);
        SharedPreferences aklatSharedPref = getSharedPreferences("bibiliyeah", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = aklatSharedPref.edit();
        editor.putString("MGA_AKLAT", wholeDataString);
        editor.apply();
    }

    public void onBatteryOk(View view){

        String ok = "SAVED!!!";
        saveToSharedPref(ok);
    }
    public void onBatteryLow(View view){

        String diOk = "HINDI NA SAVED!!!";
        saveToSharedPref(diOk);
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