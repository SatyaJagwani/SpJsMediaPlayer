package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import static com.example.mp3player.R.drawable.pause;
import static com.example.mp3player.R.drawable.play;



public class Videoplayer extends AppCompatActivity {
    LinearLayout buttonView, seekbarView;
    Button start,previous,next,popup;



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
    TextView songname;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        start = findViewById(R.id.button);
        next = findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);
        mPass = findViewById(R.id.tv_pass);
        mDue = findViewById(R.id.tv_due);
        popup = findViewById(R.id.popUp);
        player =(VideoView)findViewById(R.id.videoView1);

        songname=findViewById(R.id.songname);
        songname.setSelected(true);

        buttonView=findViewById(R.id.buttonview);
        seekbarView=findViewById(R.id.seekbarList);

        mHandler = new Handler();


        Intent i = getIntent();
        Bundle b = i.getExtras();
        songslist = (ArrayList) b.getParcelableArrayList("songs");

        int position = b.getInt("pos",0);

        if (player.isPlaying()) {
            stopPlaying();
        }
        startPlaying(position);
        pos=position;


        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),VideoplayerPopUpWindow.class).putExtra("pos", pos).putExtra("songs", songslist));

            }
        });

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonView.isShown())
                {
                    buttonView.setVisibility(View.GONE);
                    seekbarView.setVisibility(View.GONE);
                }
                else
                {
                    buttonView.setVisibility(View.VISIBLE);
                    seekbarView.setVisibility(View.VISIBLE);
                }
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                start.setBackgroundResource(pause);
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

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setProgress(0);
                seekBar.setMax(player.getDuration());
                mHandler.postDelayed(mRunnable, 100);
            }
        });



        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setBackgroundResource(pause);
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
                start.setBackgroundResource(pause);

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
                start.setBackgroundResource(pause);

                if (isPlaying) {
                    player.pause();
                    pausePosition = player.getCurrentPosition();
                    start.setBackgroundResource(play);
                }
                else{
                    if (player == null) {
                        startPlaying(pos);

                    } else if (!player.isPlaying()) {
                        player.seekTo(pausePosition);
                        player.start();
                        changeSeekBar();
                    }
                    start.setBackgroundResource(pause);

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

            String currentSong=songslist.get(position).getName().replace(".mp3","");
            songname.setText(currentSong);
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

        sec = duration % 60;
        min = (duration / 60)%60;

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

    public void onBackPressed() {
        super.onBackPressed();
        if (player.isPlaying()) {
            player.stopPlayback();
        }
        startActivity(new Intent(Videoplayer.this, MainActivity.class));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.VideotoAudio:
                startActivity(new Intent(getApplicationContext(), musicplayer.class).putExtra("pos", pos).putExtra("songs", songslist));
                return true;

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.videoplayer_menu, menu);
        return true;
    }





}