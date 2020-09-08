package com.example.semesterproject;

import androidx.appcompat.R;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class Musicplayer extends AppCompatActivity {
    Button start,previous,next;

    SeekBar seekBar;
    int pausePosition;
    private TextView mPass;
    private TextView mDue;
    ListView list;
    private Handler mHandler;
    private Runnable mRunnable;
    MediaPlayer player;


    ArrayList<File> songslist;
    boolean isPlaying = true;
    int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);
        start = findViewById(R.id.button);
        next = findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);
        mPass = findViewById(R.id.tv_pass);
        mDue = findViewById(R.id.tv_due);

        mHandler = new Handler();



        ArrayList<String> songsName=new ArrayList<>();
        songslist=MainActivity.readSongs(Environment.getExternalStorageDirectory());

        Intent i = getIntent();
        Bundle b = i.getExtras();
        songsName = (ArrayList) b.getParcelableArrayList("songs");

        int position = b.getInt("pos",0);

        if (player!=null) {
            player=null;
        }

            startPlaying(position);
            pos=position;

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (pos > 0) {
                        pos--;
                        stopPlaying();
                        startPlaying(pos);
                    } else {
                        pos=songslist.size()-1;
                        stopPlaying();
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
                        stopPlaying();
                        startPlaying(pos);
                    } else {
                        pos=0;
                        stopPlaying();
                        startPlaying(pos);
                    }


                }catch(Exception e){}


            }
        });

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

            }


        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying) {
                    player.pause();
                    pausePosition = player.getCurrentPosition();

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
            player = MediaPlayer.create(getApplicationContext(), Uri.parse(songslist.get(position).toString()));
            seekBar.setMax(player.getDuration());
            player.setLooping(true);
            player.start();
            changeSeekBar();
        }catch (Exception e){
            Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
        }


    }

    public void stopPlaying(){
        player.stop();
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
        if(player.isPlaying()){
            mRunnable=new Runnable() {
                @Override
                public void run() {
                    getAudioStats();
                    changeSeekBar();
                }
            };
            mHandler.postDelayed(mRunnable,1000);
        }
    }
}