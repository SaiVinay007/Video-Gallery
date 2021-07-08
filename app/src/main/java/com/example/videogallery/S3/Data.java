package com.example.videogallery.S3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import com.example.videogallery.MainActivity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class Data {
    public static final Data INSTANCE = new Data();

    private Executor executor;
    public static ArrayList<String> keys;
    public static ArrayList<String> urls;
    public static ArrayList<Boolean> isDownloaded;
    public static ArrayList<String> localFilePaths;
    public static ArrayList<String> downloadedPaths;

    public Map<String, Integer> keysPosition = new HashMap<String, Integer>();

    private DownloadVideo downloadVideo;


    private int counter = 0;

    private Data() {
        executor = Executors.newFixedThreadPool(4);
    }

    public static void initialize(int len) {
        isDownloaded = new ArrayList<Boolean>(Arrays.asList(new Boolean[len]));
        Collections.fill(isDownloaded, Boolean.FALSE);
        localFilePaths = new ArrayList<String>(Arrays.asList(new String[len]));
        Collections.fill(localFilePaths, "");
        downloadedPaths = new ArrayList<String>();
    }

    public void getKeys(DataCallback callback) {
        executor.execute(() -> {
            Amplify.Storage.list(
                    "",
                    result -> {
                        int i=0;
                        for (StorageItem item : result.getItems()) {
                            String s = (String) item.getKey();
                            if(s.endsWith(".mp4")){
                                keys.add(s);
                                keysPosition.put(s, i);
                                i++;
                            }
                        }
                        Log.i("In data class", String.valueOf(keys));
                        callback.onSuccess(keys);
                    },
                    error -> {
                        Log.e("MyAmplifyApp", "List failure", error);
                        callback.onError();
                    }
            );
        });
    }

    public void getUrls(DataCallback callback) {
        executor.execute(() -> {
            for(String key : keys) {
                counter++;
                Amplify.Storage.getUrl(
                        key,
                        result -> {
                            String s = String.valueOf(result.getUrl());
                            Log.i("Url : ", s);
                            urls.set(keysPosition.get(key), s);
//                            urls.add(s);
                            counter--;
                        },
                        error -> {
                            Log.e("MyAmplifyApp", "URL generation failure", error);
                            callback.onError();
                        }
                );
            }
            while(true) {
                try {
                    if(counter==0){
                        Log.i("In data class urls", String.valueOf(urls));
                        callback.onSuccess(urls);
                        break;
                    }
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void downloadFile(Context context, int position) {
        Log.i("TAG", "downloadFile: "+keys.size());
        String key = keys.get(position);
        String[] parts = key.split("/");
        String path = Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                +"/"+ parts[parts.length-1];

        try {
            check_directory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(path);
        if(file.exists()) {
            Log.i("Exists", "downloadFile: " + path);
        } else {
            try {
                file.createNewFile();
                Log.i("Created", "downloadFile: " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Amplify.Storage.downloadFile(
                key,
                new File(path),
                StorageDownloadFileOptions.defaultInstance(),
                progress -> Log.i("MyAmplifyApp", "Fraction completed: " + progress.getFractionCompleted()),
                result -> {
                    Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName());
                    localFilePaths.set(position, String.valueOf(result.getFile()));
                    isDownloaded.set(position, true);
                    downloadedPaths.add(String.valueOf(result.getFile()));
                    MainActivity.downloadFinish(context);
                },
                error -> Log.e("MyAmplifyApp",  "Download Failure", error)
        );

    }

    private static void check_directory() throws IOException {
//        File directory = new File (Environment.
//                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                "/Video_Gallery");
//        if (! directory.exists()){
//            directory.mkdirs();
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.createDirectories(Paths.get(String.valueOf(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));
        }
    }

    public static Boolean isVideoDownloaded(int position) {
        return isDownloaded.get(position);
    }



}


