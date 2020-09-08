package com.example.semesterproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class Videoplayer extends AppCompatActivity {
    Button start,previous,next;

    SeekBar seekBar;
    int pausePosition;
    private TextView mPass;
    private TextView mDue;
    ListView list;
    private Handler mHandler;
    private Runnable mRunnable;
    VideoView player;
    ArrayList<File> songslist;
    boolean isPlaying = true;
    int pos=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        start = findViewById(R.id.button2);
        next = findViewById(R.id.next2);
        previous=findViewById(R.id.previous2);
        seekBar = findViewById(R.id.seekBar2);
        mPass = findViewById(R.id.tv_pass2);
        mDue = findViewById(R.id.tv_due2);
        player =(VideoView)findViewById(R.id.videoView1);

        mHandler = new Handler();

        ArrayList<String> songsName=new ArrayList<>();
        songslist=MainActivity.readSongs(Environment.getExternalStorageDirectory());

        Intent i = getIntent();
        Bundle b = i.getExtras();
        songsName = (ArrayList) b.getParcelableArrayList("songs");

        int position = b.getInt("pos",0);

        if (player.isPlaying()) {
            stopPlaying();
        }
        startPlaying(position);
        pos=position;

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (pos > 0) {
                        pos--;
                        startPlaying(pos);
                    } else {
                        pos=songslist.size()-1;
                        startPlaying(pos);
                    }
                }
                catch(Exception e){}
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    if (pos < (songslist.size()-1)) {
                        pos++;
                        startPlaying(pos);
                    } else {
                        pos=0;
                        startPlaying(pos);
                    }


                }catch(Exception e){}
            }
        });




        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying) {
                    player.pause();
                    pausePosition = player.getCurrentPosition();
                    start.setText("Play");
                }
                else{
                    if (player == null) {
                        startPlaying(pos);

                    } else if (!player.isPlaying()) {
                        player.seekTo(pausePosition);
                        player.start();
                        changeSeekBar();
                    }
                    start.setText("Pause");

                }
                isPlaying = !isPlaying;

            }
        });


    }

    public void startPlaying(int position) {
        try {
            player.setVideoURI(Uri.parse(songslist.get(position).toString()));
            seekBar.setMax(player.getDuration());
            player.start();
            changeSeekBar();
        }
        catch (Exception e){
            Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
        }


    }

    public void stopPlaying(){
        player.stopPlayback();
        player=null;
        mHandler.removeCallbacks(mRunnable);
        seekBar.setProgress(0);
        mPass.setText( "0:0");

        mDue.setText("0:0");

    }

    protected void getAudioStats() {
        int duration = player.getDuration() / 1000; // In milliseconds
        int due = (player.getDuration() - player.getCurrentPosition()) / 1000;
        int pass = duration - due;

        int sec = pass % 60;
        int min = (pass / 60)%60;

        mPass.setText(min + ":" + sec);

        sec = due % 60;
        min = (due / 60)%60;

        mDue.setText(min + ":" + sec);
    }

    private void changeSeekBar(){
        seekBar.setProgress(player.getCurrentPosition());

            mRunnable = new Runnable() {
                @Override
                public void run() {
                    getAudioStats();
                    changeSeekBar();
                }
            };
            mHandler.postDelayed(mRunnable, 1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    player.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo((int) seekBar.getProgress());
            }
        });
    }
}