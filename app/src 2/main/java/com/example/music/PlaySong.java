package com.example.music;

import android.annotation.SuppressLint;
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
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PlaySong extends AppCompatActivity {

    private static int sTime;
    private TextView currentTime, totalTime;

    private final Handler hand = new Handler();
    TextView textView;
    ImageView play, previous, next, shuffleButton, loopButton;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position, tDuration;
    SeekBar seekBar;
    Thread updateSeek;
    boolean isLooping = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        shuffleButton = findViewById(R.id.shuffle);
        loopButton = findViewById(R.id.loop);
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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousSong();

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                tDuration = mediaPlayer.getDuration();
                sTime = mediaPlayer.getCurrentPosition();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(tDuration);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
                totalTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) tDuration), TimeUnit.MILLISECONDS.toSeconds((long) tDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) tDuration))));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            int tDuration;

            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                nextSong();
            }
        });

        // Initialize shuffle button
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleSongs();
            }
        });

        // Initialize loop button
        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLoop();
            }
        });
    }

    void toggleLoop() {
        isLooping = !isLooping;

        if (isLooping) {
            mediaPlayer.setLooping(true);
            loopButton.setImageResource(R.drawable.disable);
        } else {
            mediaPlayer.setLooping(false);
            loopButton.setImageResource(R.drawable.enable);
        }
    }

    void shuffleSongs() {
        Collections.shuffle(songs);
        position = 0;
        playSelectedSong();
    }

    void nextSong() {
        if (position != songs.size() - 1) {
            position++;
        } else {
            position = 0;
        }
        playSelectedSong();
    }

    void previousSong() {
        if (position != 0) {
            position--;
        } else {
            position = songs.size() - 1;
        }
        playSelectedSong();
    }

    void playSelectedSong() {
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        tDuration = mediaPlayer.getDuration();
        sTime = mediaPlayer.getCurrentPosition();
        play.setImageResource(R.drawable.pause);
        seekBar.setMax(tDuration);
        textContent = songs.get(position).getName();
        textView.setText(textContent);
        totalTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) tDuration), TimeUnit.MILLISECONDS.toSeconds((long) tDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) tDuration))));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });
    }

    private final Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            sTime = mediaPlayer.getCurrentPosition();
            currentTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(sTime), TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
            seekBar.setProgress(sTime);
            hand.postDelayed(this, 100);
        }
    };

    void playSong() {
        if (mediaPlayer.isPlaying()) {
            play.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        } else {
            play.setImageResource(R.drawable.pause);
            mediaPlayer.start();
        }
    }
}
