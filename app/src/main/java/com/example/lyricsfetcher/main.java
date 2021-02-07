package com.example.lyricsfetcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.os.Build;
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
    int id = 31;
    GetLyrics lyricsFetcher = new GetLyrics();
    NotificationCompat.Builder builder = new NotificationCompat.Builder(main.this,"notif");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        //Notification code
        //If statement for API's greater than oreo(26)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("notif","Channel 1", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent resultIntent = new Intent(this,main.class);
        PendingIntent resultPendingIntent= PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);



        //Intent filter for the broadcast receiver
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        //iF.addAction("com.sec.android.app.music.metachanged");
        //iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.spotify.music.metadatachanged");
        iF.addAction("com.spotify.music.playbackstatechanged");
        registerReceiver(mReceiver, iF);
        //Widget declarations
        Button button = findViewById(R.id.getLyrics);
        TextView artistTitle = findViewById(R.id.artistTitle);
        TextView songTitle = findViewById(R.id.songTitle);
        TextView lyrics = findViewById(R.id.lyrics);



        lyrics.setMovementMethod(ScrollingMovementMethod.getInstance());

        //Setting the title texts
        //artistTitle.setText(artistName);
        //songTitle.setText(trackName);


        View.OnClickListener listener = v -> {
            lyrics.setText(lyricsText);
            artistTitle.setText(artistName);
            songTitle.setText(trackName);
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
            Log.i("track_info", "This is the track information:");
            Log.v("track_info", artist + ":" + track);
            Thread thread3 = new Thread(() -> {
                artistName = artist;
                trackName = track;
            });
            thread3.start();
            try {
                thread3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(action.equals("com.spotify.music.metadatachanged") |
                    action.equals("com.android.music.metachanged")){
                Log.d("METADATACHANGED","metadata is changed currently");
                Thread thread1 = new Thread(() -> {

                    Log.d("threading","Broadcast thread is working right now,current playing track: "
                            + artistName + "-" + trackName);
                    lyricsText = lyricsFetcher.GetLyrics(trackName,artistName);
                });
                thread1.start();
            }
            else if(action.equals("com.spotify.music.playbackstatechanged") |
                    action.equals("com.android.music.playstatechanged")){
                Thread thread2 = new Thread(() -> {
                    set_notification(id,playing);
                });
                thread2.start();
                Log.d("playing","State of playing var is: " + playing);
            }
        }

    };


    public void set_notification(int id,boolean playing){
        if(playing){
            builder.setContentTitle(artistName);
            builder.setContentText(trackName);
            builder.setSmallIcon(R.drawable.ic_message);
            builder.setOngoing(true);
            builder.setTimeoutAfter(1000*60*5);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(main.this);
            managerCompat.notify(id,builder.build());
        }
        else{
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(main.this);
            managerCompat.cancel(id);
        }
    }

}