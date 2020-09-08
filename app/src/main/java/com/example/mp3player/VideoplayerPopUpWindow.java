package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;

public class VideoplayerPopUpWindow extends AppCompatActivity {
    ArrayList<String> songName;
    ArrayList<File> songslist;
    ListView popuplist;
    private ArrayAdapter adapter;
    private Toolbar toolbar;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer_pop_up_window);
        toolbar = findViewById(R.id.toolbar);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        popuplist=findViewById(R.id.popuplist);


        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width=metrics.widthPixels;
        int height=metrics.heightPixels;

        getWindow().setLayout((int)(width*.7),(int)(height*.7));
        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=20;
        getWindow().setAttributes(params);



        songslist=(ArrayList) b.getParcelableArrayList("songs");
        pos = b.getInt("pos",0);

        songName = new ArrayList<>();

        for (File f : songslist) {
            songName.add(f.getName());
        }

        adapter = new ArrayAdapter(getApplicationContext(), R.layout.videolistitems, R.id.items, songName);
        popuplist.setAdapter(adapter);


        popuplist.setSelection(pos);
        popuplist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        popuplist.setSelector(android.R.color.darker_gray);
        popuplist.setSelected(true);

        popuplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos=songName.indexOf(parent.getItemAtPosition(position).toString());
                startActivity(new Intent(getApplicationContext(), Videoplayer.class).putExtra("pos", pos).putExtra("songs", songslist));


            }
        });


    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.popup_menu,menu);
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


    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Videoplayer.class).putExtra("pos", pos).putExtra("songs", songslist));
    }
}