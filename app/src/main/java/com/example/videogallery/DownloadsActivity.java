package com.example.videogallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videogallery.Adapter.DownloadsAdapter;
import com.example.videogallery.Adapter.MainAdapter;
import com.example.videogallery.S3.Data;

import java.io.Serializable;
import java.util.ArrayList;

public class DownloadsActivity extends AppCompatActivity implements DownloadsAdapter.ItemClickListener {

    private DownloadsAdapter adapter;
    private ArrayList<String> videoPaths;
    public static final String PATHS="paths-array";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        videoPaths = (ArrayList<String>) args.getSerializable("ARRAYLIST");

        RecyclerView recyclerView = this.findViewById(R.id.downloadsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new DownloadsAdapter(this, videoPaths);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, int position) {
        playAlert(position);
    }

    private void playVideo(int position) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(VideoActivity.VIDEO_PATH, videoPaths.get(position));
        this.startActivity(intent);
    }

    private void playAlert(int position) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to play this video?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    playVideo(position);
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
