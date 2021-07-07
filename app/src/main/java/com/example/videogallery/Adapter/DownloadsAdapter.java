package com.example.videogallery.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.videogallery.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder>{

    private ArrayList<String> mPaths;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public DownloadsAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mPaths = data;
        Log.i("TAG", "DownloadsAdapter: " + this.mPaths);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_downloads, parent, false);
        Log.i("TAG", "onCreateViewHolder: ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(getItem(position));
        Log.i("TAG", "onBindViewHolder: "+position);
//        holder.myImageView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    public String getItem(int id) {
        return mPaths.get(id);
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.downloadsImage);
            Log.i("TAG", "ViewHolder: ");
            itemView.setOnClickListener((View.OnClickListener) this);
        }

        void bindTo(String path) {
            Uri myURI = null;
            //                myURI = new URI(path);
            myURI = Uri.fromFile(new File(path));
            Log.i("TAG", "bindTo: " + myURI);

            Glide.with(itemView)
                    .load(myURI)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(myImageView);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(DownloadsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
