package com.example.videogallery.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.videogallery.MainActivity;
import com.example.videogallery.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final ArrayList<String> mUrls;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public MainAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mUrls = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
//         holder.myImageView.setImageResource(R.drawable.ic_launcher_foreground);
        holder.bindTo(getItem(position));
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener((View.OnClickListener) this);
        }

        void bindTo(String url) {
            Glide.with(itemView)
                    .load(url)
//                    .transform(new CenterCrop(),new RoundedCorners(25))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(myImageView);
//            try {
//                Bitmap imgBitmap = retriveVideoFrameFromVideo(url);
//                myImageView.setImageBitmap(imgBitmap);
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mUrls.get(id);
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
