package com.example.music;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Thread th = new Thread(){
            public void run(){
                try{
                    Thread.sleep(1000);
                }
                catch (Exception ignored){
                }
                finally {
                    Intent intent = new Intent(splashScreen.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        th.start();
    }
}