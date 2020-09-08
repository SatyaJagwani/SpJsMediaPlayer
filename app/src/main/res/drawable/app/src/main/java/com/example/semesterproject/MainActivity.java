package com.example.semesterproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    GridView view;
    ArrayList<File> songslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.gridView);
        TextView textView = findViewById(R.id.text);


        songslist = readSongs(Environment.getExternalStorageDirectory());
        final ArrayList<String> songName = new ArrayList<>();

        for (File f : songslist) {
            songName.add(f.getName());
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.gallery, R.id.text, songName);
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (songName.get(position).endsWith(".mp4")) {
                    startActivity(new Intent(getApplicationContext(), Musicplayer.class).putExtra("pos", position).putExtra("songs", songName));
                } else {
                    startActivity(new Intent(getApplicationContext(), Videoplayer.class).putExtra("pos", position).putExtra("songs", songName));
                }

            }
        });

    }


        static ArrayList<File> readSongs (File root){
            ArrayList<File> arr = new ArrayList<>();
            File files[] = root.listFiles();


            for (File f : files) {
                if (f.isDirectory()) {
                    arr.addAll(readSongs(f));
                } else {
                    if (f.getName().endsWith(".mp4") || f.getName().endsWith(".mp3")) {
                        arr.add(f);
                    }
                }
            }
            return arr;
        }


    }



