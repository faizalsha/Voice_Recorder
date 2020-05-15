package com.faizal.shadab.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    private Chronometer mChronometer;
    private Button mPlayButton, mStopButton;
    private long lastPause = 0;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private ImageView recordingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChronometer = findViewById(R.id.ch_timer);
        mPlayButton = findViewById(R.id.btn_start_recording);
        mStopButton = findViewById(R.id.btn_stop);
        mStopButton.setEnabled(false);
        recordingList = findViewById(R.id.btn_list);
        recordingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListRecordingActivity.class);
                startActivity(intent);
            }
        });
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
    }

    private void startRecording(){
        if(checkPermission()){
            mChronometer.setBase(SystemClock.elapsedRealtime());

            String recordPath = getExternalFilesDir("/").getAbsolutePath();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
            Date now = new Date();
            recordFile = "recording_" + formatter.format(now) + ".3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();

            mChronometer.start();
            mPlayButton.setEnabled(false);
            mStopButton.setEnabled(true);
            recordingList.setEnabled(false);
        }
    }
    private void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(false);
        recordingList.setEnabled(true);
        Toast.makeText(this, "Recording Saved", Toast.LENGTH_SHORT).show();

    }
    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==  PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 12);
            return false;
        }
    }

    public void myClickListener(int position){
        Toast.makeText(this, "clicked" + position, Toast.LENGTH_SHORT).show();
    }
    public interface myInterface{
        public void myMethod();
    }
}
