package com.faizal.shadab.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ListRecordingActivity extends AppCompatActivity implements ListRecordingAdapter.OnRecordingClickListener {

    private File[] mAllFiles;
    private MediaPlayer mMediaPlayer = null;
    private File mSelectedAudioFile;
    private ImageView playButton;
    private Timer timer;
    private SeekBar seekBar;
    private MediaPlayer.OnCompletionListener completionListener;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;
    private View.OnClickListener playButtonListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recording);

        playButton = findViewById(R.id.btn_play);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        initializeListener();
        playButton.setOnClickListener(playButtonListener);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        String path = getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        mAllFiles = directory.listFiles();
        ListRecordingAdapter adapter = new ListRecordingAdapter(mAllFiles,this);
        RecyclerView recordingList = findViewById(R.id.recycler_view_list_recording);
        recordingList.setHasFixedSize(true);
        recordingList.setLayoutManager(new LinearLayoutManager(this));
        recordingList.setAdapter(adapter);
    }

    private void initializeListener() {
        playButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer == null) return;
                if(mMediaPlayer.isPlaying()) pauseAudio();
                else playAudio(mSelectedAudioFile);
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
                seekBar.setProgress(mp.getDuration()/1000);
                playButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp));
            }
        };

    }



    @Override
    public void onclick(int position) {
        if (mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying()){
                pauseAudio();
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            mMediaPlayer=null;
        }


        mSelectedAudioFile = mAllFiles[position];
        playAudio(mAllFiles[position]);
        Toast.makeText(this, "playing....", Toast.LENGTH_SHORT).show();
    }

    private void pauseAudio() {
        //mMediaPlayer cannot be null here
        mMediaPlayer.pause();
        playButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp));
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
                },0,1000);
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

}
