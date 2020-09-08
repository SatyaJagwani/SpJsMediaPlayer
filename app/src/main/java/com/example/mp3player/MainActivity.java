package com.example.mp3player;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    public static final int RUNTIME_PERMISSION_CODE = 7;
    ArrayList<File> songslist;
    ArrayList<String> songName;
    ListView listView;
    ArrayAdapter<String> adapter ;
    Button audio,video;
    Boolean AudioSelected =true;
    Toolbar toolbar;
    int pos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Runtime night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Requesting run time permission for Read External Storage.
        AndroidRuntimePermission();

        // Linking the xml with Java coding
        toolbar = findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listView1);
        audio=findViewById(R.id.Audio);
        video=findViewById(R.id.Video);

        // onClicker listeners
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.setBackgroundColor(Color.GRAY);
                video.setBackgroundColor(Color.BLACK);

                AudioSelected =true;

                songName.clear();
                songslist = readAudios(Environment.getExternalStorageDirectory());

                for (File f : songslist) {
                    songName.add(f.getName());
                }

                adapter = new ArrayAdapter(getApplicationContext(), R.layout.audiolistitems, R.id.items, songName);
                listView.setAdapter(adapter);

            }
        });



        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.setBackgroundColor(Color.BLACK);
                video.setBackgroundColor(Color.GRAY);
                AudioSelected =false;
                songName.clear();
                songslist = readVideos(Environment.getExternalStorageDirectory());

                for (File f : songslist) {
                    songName.add(f.getName());
                }

                adapter = new ArrayAdapter(getApplicationContext(), R.layout.videolistitems, R.id.items, songName);
                listView.setAdapter(adapter);

            }
        });


            songslist = readAudios(Environment.getExternalStorageDirectory());
            songName = new ArrayList<>();

            for (File f : songslist) {
            songName.add(f.getName());
        }

        adapter = new ArrayAdapter(getApplicationContext(), R.layout.audiolistitems, R.id.items, songName);
        listView.setAdapter(adapter);





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos=songName.indexOf(parent.getItemAtPosition(position).toString());
                if (AudioSelected) {
                    startActivity(new Intent(getApplicationContext(), musicplayer.class).putExtra("pos", pos).putExtra("songs", songslist));
                } else {
                    startActivity(new Intent(getApplicationContext(), Videoplayer.class).putExtra("pos", pos).putExtra("songs", songslist));
                }

               listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
               listView.setSelector(android.R.color.darker_gray);
            }
        });
        listView.setSelected(true);


    }

    // Creating Runtime permission function.
    public void AndroidRuntimePermission(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel",null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                }
                else {

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            }else {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        switch(requestCode){

            case RUNTIME_PERMISSION_CODE:{

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
                else {

                }
            }
        }
    }

    static ArrayList<File> readAudios(File root){
        ArrayList<File> arr = new ArrayList<>();
        File files[] = root.listFiles();


       for (File f : files) {
            if (f.isDirectory()) {
                arr.addAll(readAudios(f));
            } else {
                if (f.getName().endsWith(".m4a") || f.getName().endsWith(".mp3") ||f.getName().endsWith(".wav") || f.getName().endsWith(".wma")
                        || f.getName().endsWith(".flac")
                ) {
                    arr.add(f);
                }

            }
        }
        return arr;


    }

    static ArrayList<File> readVideos (File root){
        ArrayList<File> arr = new ArrayList<>();
        File files[] = root.listFiles();


        for (File f : files) {
            if (f.isDirectory()) {
                arr.addAll(readVideos(f));
            } else {
                if (f.getName().endsWith(".mp4") || f.getName().endsWith(".mkv") || f.getName().endsWith(".webm")
                        || f.getName().endsWith(".flv")|| f.getName().endsWith(".avi")
                        || f.getName().endsWith(".m4v") || f.getName().endsWith(".3gp")
                ) {
                    arr.add(f);
                }

            }
        }
        return arr;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Shuffle:
                songName.clear();
                if(AudioSelected ==true) {songslist = readAudios(Environment.getExternalStorageDirectory());}
                else {songslist = readVideos(Environment.getExternalStorageDirectory());}


                for (File f : songslist) {
                    songName.add(f.getName());
                }
                adapter = new ArrayAdapter(getApplicationContext(), R.layout.audiolistitems, R.id.items, songName);
                listView.setAdapter(adapter);
                item.setChecked(true);




                return true;

            case R.id.Asscending:
                songName.clear();
                if(AudioSelected ==true) {songslist = readAudios(Environment.getExternalStorageDirectory());}
                else {songslist = readVideos(Environment.getExternalStorageDirectory());}
                Collections.sort(songslist);

                for (File f : songslist) {
                    songName.add(f.getName());
                }
                adapter = new ArrayAdapter(getApplicationContext(), R.layout.audiolistitems, R.id.items, songName);
                listView.setAdapter(adapter);
                item.setChecked(true);

                return true;

            case R.id.Descending:
                songName.clear();

                if(AudioSelected ==true) {songslist = readAudios(Environment.getExternalStorageDirectory());}
                else {                    songslist = readVideos(Environment.getExternalStorageDirectory());}

                Collections.sort(songslist, Collections.reverseOrder());

                for (File f : songslist) {
                    songName.add(f.getName());
                }

                adapter = new ArrayAdapter(getApplicationContext(), R.layout.audiolistitems, R.id.items, songName);
                listView.setAdapter(adapter);
                item.setChecked(true);

                return true;




            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.appmenu,menu);
        MenuItem mSearch = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}

