package com.example.videogallery.S3;

import android.os.AsyncTask;

import java.util.ArrayList;

public class DownloadVideo extends AsyncTask<ArrayList<String>, Void, Void > {

    public DownloadVideo() {

    }

    @Override
    protected Void doInBackground(ArrayList<String>... arrayLists) {
        return null;
    }


    protected void onProgressUpdate(Void params) {
        // setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Void params) {
        // showDialog("Downloaded " + result + " bytes");
    }
}
