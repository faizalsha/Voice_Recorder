package com.faizal.shadab.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ListRecordingActivity extends AppCompatActivity implements ListRecordingAdapter.OnRecordingClickListener {

    private ArrayList<File> mAllFiles;
    private MediaPlayer mMediaPlayer = null;
    private File mSelectedAudioFile;
    private ImageView playButton;
    private Timer timer;
    private SeekBar seekBar;
    private MediaPlayer.OnCompletionListener completionListener;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;
    private View.OnClickListener buttonClickListener;
    private Button deleteButton, cancelButton;
    private RecyclerView recyclerViewRecordings;
    private LinearLayout mediaPlayerLayout;
    private TextView confirmDeleteTextView;
    private ListRecordingAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recording);

        confirmDeleteTextView = findViewById(R.id.txt_confirmation);
        mediaPlayerLayout = findViewById(R.id.media_player);
        deleteButton = findViewById(R.id.btn_delete);
        cancelButton = findViewById(R.id.btn_cancel);
        playButton = findViewById(R.id.btn_play);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        initializeListener();
        playButton.setOnClickListener(buttonClickListener);
        deleteButton.setOnClickListener(buttonClickListener);
        cancelButton.setOnClickListener(buttonClickListener);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        String path = Objects.requireNonNull(getExternalFilesDir("/")).getAbsolutePath();
        File directory = new File(path);
        File[] fileList = directory.listFiles();
        mAllFiles = new ArrayList<>(Arrays.asList(fileList));
        adapter = new ListRecordingAdapter(mAllFiles,this);
        recyclerViewRecordings = findViewById(R.id.recycler_view_list_recording);
        recyclerViewRecordings.setHasFixedSize(false);
        recyclerViewRecordings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecordings.setAdapter(adapter);
    }

    private void initializeListener() {
        buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_play:
                        if(mMediaPlayer == null) return;
                        if(mMediaPlayer.isPlaying()) pauseAudio();
                        else playAudio(mSelectedAudioFile);
                        break;
                    case R.id.btn_delete:
                        recyclerViewRecordings.setVisibility(View.VISIBLE);
                        mediaPlayerLayout.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                        confirmDeleteTextView.setVisibility(View.GONE);
                        final boolean delete = mSelectedAudioFile.delete();
                        mSelectedAudioFile = null;

                        if (delete)
                        Toast.makeText(ListRecordingActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btn_cancel:
                        recyclerViewRecordings.setVisibility(View.VISIBLE);
                        mediaPlayerLayout.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                        confirmDeleteTextView.setVisibility(View.GONE);
                        Toast.makeText(ListRecordingActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                        break;

                }


            }
        };

        seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mMediaPlayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(timer != null){
                    timer.cancel();
                    timer = null;
                }
                //seekBar.setProgress(mp.getDuration()/1000);
                playButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp));
            }
        };

    }



    @Override
    public void onclick(int position) {
        if(recyclerViewRecordings.getVisibility() == View.VISIBLE){
            if (mMediaPlayer!=null){
                if (mMediaPlayer.isPlaying()){
                    pauseAudio();
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                mMediaPlayer=null;
            }


            mSelectedAudioFile = mAllFiles.get(position);
            playAudio(mAllFiles.get(position));
            Toast.makeText(this, "playing....", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLongClick(int position) {
        recyclerViewRecordings.setVisibility(View.GONE);
        mediaPlayerLayout.setVisibility(View.GONE);
        deleteButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        confirmDeleteTextView.setVisibility(View.VISIBLE);
        pauseAudio();
        mSelectedAudioFile = mAllFiles.get(position);
    }

    private void pauseAudio() {
        //mMediaPlayer cannot be null here
        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
        playButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp));
        if(timer != null)
            timer.cancel();
        timer = null;
    }
    private void playAudio(File audioFile) {
        if(audioFile == null){
            Toast.makeText(this, "Select A Audio first", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(audioFile.getAbsolutePath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(completionListener);
                playButton.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp));
                seekBar.setMax(mMediaPlayer.getDuration()/1000);
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        seekBar.setProgress(mMediaPlayer.getCurrentPosition()/1000);
                    }
                },0,900);
                seekBar.setEnabled(true);


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error Playing this file", Toast.LENGTH_SHORT).show();
            }
        } else{
            mMediaPlayer.start();
            playButton.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp));
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    seekBar.setProgress(mMediaPlayer.getCurrentPosition()/1000);
                }
            },0,1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
        if (mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying()){
                pauseAudio();
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            mMediaPlayer=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying()){
                pauseAudio();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(recyclerViewRecordings.getVisibility() == View.GONE){
            recyclerViewRecordings.setVisibility(View.VISIBLE);
            mediaPlayerLayout.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            confirmDeleteTextView.setVisibility(View.GONE);
        } else
            super.onBackPressed();
    }
}
