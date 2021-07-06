package com.example.videogallery;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageAccessLevel;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import com.amplifyframework.storage.options.StorageListOptions;
import com.example.videogallery.Adapter.MainAdapter;
import com.example.videogallery.S3.Data;
import com.example.videogallery.S3.DataCallback;
import com.example.videogallery.S3.DataPreferences;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity implements MainAdapter.ItemClickListener {
    private MainAdapter adapter;
//    private ArrayList<String> Data.keys;
//    private ArrayList<String> Data.urls;
    private Boolean cached;
    private DataPreferences preferences;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        preferences = new DataPreferences(this);

        Data.keys = new ArrayList<String>();
        Data.urls = new ArrayList<String>();

        // TODO:
        // While using this Data.urls returning before adding to array
        // Asynchronous behavior even in call back
        cached = preferences.arePreferencesCached(preferences.ARE_URLS_CACHED) &&
                preferences.arePreferencesCached(preferences.ARE_KEYS_CACHED);
        if (cached) {
            ArrayList<String> list1 = preferences.getSavedPreferences(preferences.KEY_SAVE_URLS);
            Data.urls.clear();
            Data.urls.addAll(list1);

            ArrayList<String> list2 = preferences.getSavedPreferences(preferences.KEY_SAVE_Keys);
            Data.keys.clear();
            Data.keys.addAll(list2);

            Data.initialize(Data.urls.size());
            Log.i("TAG", "onCreate: " + Data.urls.size());
            Log.i("Loading Cached :", "Data.urls & Keys");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MainAdapter(this, Data.urls);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        if(!cached) {
            loadKeys();
        }
    }

    private void loadKeys() {
        Data.INSTANCE.getKeys(new DataCallback() {
            @Override
            public void onSuccess(ArrayList<String> list) {
                runOnUiThread(() -> {
                    preferences.savePreferences(Data.keys, preferences.KEY_SAVE_Keys);
                    ArrayList<String> newList = new ArrayList<String>(Arrays.asList(
                            new String[Data.keys.size()]));
                    Collections.fill(newList, "");
                    Data.urls.clear();
                    Data.urls.addAll(newList);
                    Log.i("Load keys Data.keys", String.valueOf(Data.keys));
                });
                Log.i("Starting :", "loading Data.urls");
                loadUrls();
            }
            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Log.i("Error", "loadKeys()");
                });
            }
        });
    }

    private void loadUrls() {
        Data.INSTANCE.getUrls(new DataCallback() {
            @Override
            public void onSuccess(ArrayList<String> list) {
                runOnUiThread(() -> {
//                    Data.urls.clear();
//                    Data.urls.addAll(list);

                    // We notify adapter to use Data.urls to get thumbnails
                    Log.i("loadUrls()", String.valueOf(Data.urls));
                    adapter.notifyDataSetChanged();
                    preferences.savePreferences(Data.urls, preferences.KEY_SAVE_URLS);
                });
            }
            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Log.i("Error", "loadUrls()");
                });
            }
        });
    }

    private ArrayList<String> getDownloadKeys() {
        // Run a progress bar
        // listFiles();
        // Stop progress bar
        Log.i("Keys", String.valueOf(Data.keys.size()));
        return Data.keys;
    }

    private void downloadVideo(int position) {
        Data.downloadFile(this, position);
    }

    private boolean isAlreadyDownloaded(int position) {
        return Data.isVideoDownloaded(position);
    }

    private void downloadAlert(int position) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to download this video?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    downloadVideo(position);
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onItemClick(View view, int position) {
//         Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
        check(position);
    }

    private void check(int position) {
        if(isAlreadyDownloaded(position)){
            Toast t = Toast.makeText(getApplicationContext(),
                    "You have already downloaded this video",
                    Toast.LENGTH_LONG);
            t.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            t.show();
        }
        else{
            downloadAlert(position);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}

