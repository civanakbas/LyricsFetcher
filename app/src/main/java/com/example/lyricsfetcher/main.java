package com.example.lyricsfetcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class main extends AppCompatActivity {
    String artistName = "null";
    String trackName = "null";
    String lyricsText = "null";
    GetLyrics lyricsFetcher = new GetLyrics();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);



        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.sec.android.app.music.metachanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.spotify.music.metadatachanged");

        registerReceiver(mReceiver, iF);

        //Widget declarations
        Button button = (Button) findViewById(R.id.getLyrics);
        TextView artistTitle = (TextView) findViewById(R.id.artistTitle);
        TextView songTitle = (TextView) findViewById(R.id.songTitle);
        TextView lyrics = (TextView) findViewById(R.id.lyrics);



        lyrics.setMovementMethod(ScrollingMovementMethod.getInstance());

        //Setting the title texts
        artistTitle.setText(artistName);
        songTitle.setText(trackName);




        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyrics.setText(lyricsText);
                artistTitle.setText(artistName);
                songTitle.setText(trackName);
            }
        };
        button.setOnClickListener(listener);
    }


    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean playing = intent.getBooleanExtra("playing", false);
            String cmd = intent.getStringExtra("command");
            Log.v("anan ", action + " / " + cmd);
            String artist = intent.getStringExtra("artist");
            String track = intent.getStringExtra("track");
            Log.i("tag", "This is the track information:");
            Log.v("tag", artist + ":" + track);
            artistName = artist;
            trackName = track;
            if(action.equals("com.spotify.music.metadatachanged") |
                    action.equals("com.android.music.metachanged")){

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        lyricsText = lyricsFetcher.GetLyrics(trackName,artistName);
                        Log.d("threading","Broadcast thread is working right now,current playing track: "
                                + artistName + "-" + trackName);
                    }
                });
                try {
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };

}