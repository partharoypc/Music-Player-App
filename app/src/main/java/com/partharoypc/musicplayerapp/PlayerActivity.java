package com.partharoypc.musicplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnNext,btnPlay,btnPrevious,btnFastForward,btnFastBackward;
    ImageView imageView;
    TextView txtSongName,txtSongStart,txtSongEnd;
    SeekBar musicBar;

    String songName;
    static MediaPlayer mediaPlayer;
    public static final String EXTRA_NAME = "song_name";
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnFastForward = findViewById(R.id.btnFastFor);
        btnFastBackward = findViewById(R.id.btnFastBack);

        txtSongName = findViewById(R.id.txtSongName);
        txtSongStart = findViewById(R.id.txtSongStart);
        txtSongEnd = findViewById(R.id.txtSongEnd);
        musicBar = findViewById(R.id.SeekBar);



        if (mediaPlayer!=null){
            mediaPlayer.start();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        String sName = intent.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        txtSongName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songName = mySongs.get(position).getName();
        txtSongName.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        songEndTime();


        updateSeekBar = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        musicBar.setProgress(currentPosition);


                    }
                    catch (InterruptedException| IllegalStateException e){
                        e.printStackTrace();

                    }
                }
            }
        };

        musicBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        musicBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        musicBar.getThumb().setColorFilter(getResources().getColor(R.color.progressColor),PorterDuff.Mode.MULTIPLY);

        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });


        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createDuration(mediaPlayer.getCurrentPosition());
                txtSongStart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.play_song_icon);
                    mediaPlayer.pause();
                }else {
                    btnPlay.setBackgroundResource(R.drawable.pause_song_icon);
                    mediaPlayer.start();

                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnNext.performClick();

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Uri uri1 = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri1);
                songName = mySongs.get(position).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                songEndTime();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                mediaPlayer.release();
                position = ((position-1)%mySongs.size());
                if (position<0)
                    position = mySongs.size()-1;
                Uri uri1 = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri1);
                songName = mySongs.get(position).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                songEndTime();

            }
        });

        btnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        btnFastBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });


    }
    public void songEndTime() {
        String endTime = createDuration(mediaPlayer.getDuration());
        txtSongEnd.setText(endTime);
    }

    public String createDuration(int duration) {

        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time = time + min + ":";

        if (sec < 10) {

            time += "0";

        }
        time += sec;
        return time;

    }
}