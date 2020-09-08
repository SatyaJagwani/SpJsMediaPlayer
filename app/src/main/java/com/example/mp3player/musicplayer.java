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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

import static com.example.mp3player.R.drawable.*;

public class musicplayer extends AppCompatActivity {
    Button start,previous,next,popup;
    ArrayList<String> songsName;
    SeekBar seekBar;
    int pausePosition;
    private TextView mPass,mDue, songname;
    ListView list;
    private Handler mHandler;
    private Runnable mRunnable;
    MediaPlayer player;
    ArrayList<File> songslist;
    boolean isPlaying = true;
    int pos=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);

        // Linking the xml with Java coding
        popup = findViewById(R.id.popUp);
        start = findViewById(R.id.button);
        next = findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);
        mPass = findViewById(R.id.tv_pass);
        mDue = findViewById(R.id.tv_due);
        songname=findViewById(R.id.songname);
        songname.setSelected(true);
        //handler for thread
        mHandler = new Handler();

        //intializing the arraylists
        songsName=new ArrayList<>();
        songslist=new ArrayList<>();

        //
        Intent i = getIntent();
        Bundle b = i.getExtras();
        //Saving the array list, passed by another activity
        songslist=(ArrayList) b.getParcelableArrayList("songs");

        //Saving the position, passed by another activity
        int position = b.getInt("pos",0);
        startPlaying(position);
        pos=position;

        //video view events
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


        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
                //passing the arraylist to popUp activity and switching activities
                startActivity(new Intent(getApplicationContext(), MusicPlayerPopup.class).putExtra("pos", pos).putExtra("songs", songslist));

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setBackgroundResource(pause);
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
                start.setBackgroundResource(pause);

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
                        start.setBackgroundResource(pause);
                    }


                }
                isPlaying = !isPlaying;

            }
        });

    }

    public void startPlaying(int position) {
        try {



            player = MediaPlayer.create(getApplicationContext(), Uri.parse(songslist.get(position).toString()));
            seekBar.setMax(player.getDuration());
            player.start();
            changeSeekBar();

            String currentSong=songslist.get(position).getName().replace(".mp3","");
            songname.setText(currentSong);
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
        sec = duration % 60;
        min = (duration / 60)%60;

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

    public void onBackPressed() {
       super.onBackPressed();
        if (player.isPlaying()) {
          player.stop();
        }
        startActivity(new Intent( musicplayer.this, MainActivity.class));


    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.background:
                this.moveTaskToBack(true);
                return true;



            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.musicplayer_menu, menu);
        return true;
    }
}