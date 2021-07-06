package com.example.videogallery.S3;

import java.util.ArrayList;
import java.util.List;

public interface DataCallback {
    void onSuccess(ArrayList<String> keyList);
    void onError();
}
