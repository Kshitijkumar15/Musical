package com.example.music;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    private static int sTime;
    private TextView currentTime, totalTime;


    private Handler hand = new Handler();
    TextView textView;
    ImageView play, previous, next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTimer);
        totalTime = findViewById(R.id.totalTimer);
        int totalDuration;
        //to hide the action bar
        getSupportActionBar().hide();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        totalDuration = mediaPlayer.getDuration();
        sTime = mediaPlayer.getCurrentPosition();
        seekBar.setMax(totalDuration);
        totalTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) totalDuration), TimeUnit.MILLISECONDS.toSeconds((long) totalDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) totalDuration))));
        currentTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(sTime), TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
        seekBar.setProgress(sTime);
        hand.postDelayed(UpdateSongTime, 100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

//        updateSeek = new Thread() {
//            @Override
//            public void run() {
//                int currentPosition = 0;
//                try {
//                    while (currentPosition < mediaPlayer.getDuration()) {
//                        currentPosition = mediaPlayer.getCurrentPosition();
//                        seekBar.setProgress(currentPosition);
//                        seekBar.setMax(mediaPlayer.getDuration());
//                        sleep(800);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });


        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tDuration;
//                mediaPlayer.stop();
//                mediaPlayer.release();
                if (position != 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                tDuration = mediaPlayer.getDuration();
                sTime = mediaPlayer.getCurrentPosition();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(tDuration);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                totalTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) tDuration), TimeUnit.MILLISECONDS.toSeconds((long) tDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) tDuration))));

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tDuration;
//                mediaPlayer.release();
                if (position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                tDuration = mediaPlayer.getDuration();
                sTime = mediaPlayer.getCurrentPosition();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(tDuration);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                totalTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) tDuration), TimeUnit.MILLISECONDS.toSeconds((long) tDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) tDuration))));


            }
        });

    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            sTime = mediaPlayer.getCurrentPosition();
            currentTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(sTime), TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
            seekBar.setProgress(sTime);
            hand.postDelayed(this, 100);
        }
    };
}