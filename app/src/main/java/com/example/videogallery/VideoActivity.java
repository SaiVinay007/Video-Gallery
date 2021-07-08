package com.example.videogallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    public static final String VIDEO_PATH="video_path";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_video);


        Bundle extras = getIntent().getExtras();
        String path = extras.getString(VIDEO_PATH);

        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoPath(path);
        videoView.start();
    }
}
