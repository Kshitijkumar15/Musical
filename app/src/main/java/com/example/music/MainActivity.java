package com.example.music;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnRequest;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        btnRequest = findViewById(R.id.btnPermission);
//        btnRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestPermissions();
//            }
//        });

//        Dexter.withContext(this)
//                .withPermission(permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//
////                     Toast.makeText(MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT).show();
//                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
//                        String [] items = new String[mySongs.size()];
//                        for(int i=0;i<mySongs.size();i++){
//                            items[i] = mySongs.get(i).getName().replace(".mp3", "");
//                        }
//
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
//                        listView.setAdapter(adapter);
//                        listView.setOnItemClickListener((parent, view, position, id) -> {
//                            Intent intent = new Intent(MainActivity.this, PlaySong.class);
//                            String currentSong = listView.getItemAtPosition(position).toString();
//                            intent.putExtra("songList", mySongs);
//                            intent.putExtra("currentSong", currentSong);
//                            intent.putExtra("position", position);
//                            startActivity(intent);
//                        });
//
//
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                        permissionToken.continuePermissionRequest();
//                    }
//                })
//                .check();
    }

    private void requestPermissions() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                btnRequest.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Internal Storage accessed", Toast.LENGTH_SHORT).show();
                ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                String[] items = new String[mySongs.size()];
                for (int i = 0; i < mySongs.size(); i++) {
                    items[i] = mySongs.get(i).getName().replace(".mp3", "");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(MainActivity.this, PlaySong.class);
                    String currentSong = listView.getItemAtPosition(position).toString();
                    intent.putExtra("songList", mySongs);
                    intent.putExtra("currentSong", currentSong);
                    intent.putExtra("position", position);
                    startActivity(intent);
                });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList fetchSongs(File file) {
        ArrayList arrayList = new ArrayList();
        File[] songs = file.listFiles();
        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory())
                    arrayList.addAll(fetchSongs(myFile));
                else {
                    if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
    }
}