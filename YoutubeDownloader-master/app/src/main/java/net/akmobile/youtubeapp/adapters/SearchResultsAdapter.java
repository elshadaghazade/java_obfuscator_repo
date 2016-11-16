package net.akmobile.youtubeapp.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.activities.MainActivity;
import net.akmobile.youtubeapp.interfaces.OnListItemsClickListener;
import net.akmobile.youtubeapp.models.SearchedItem;
import net.akmobile.youtubeapp.utils.DonutProgress;
import net.akmobile.youtubeapp.utils.FileManager;

import java.util.ArrayList;

/**
 * Created by Rahimli Rahim on 29/09/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private ArrayList<SearchedItem> videos;
    private OnListItemsClickListener listener;

    public SearchResultsAdapter(ArrayList<SearchedItem> videos) {
        this.videos = videos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new SearchResultsAdapter.ViewHolder(view);
    }

    public void setDownloading(int position,boolean isDownloading){
        videos.set(position,videos.get(position).setDownloading(isDownloading));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SearchedItem video = videos.get(position);
        holder.title.setText(video.getTitle());
        if(video.isDownloading())
            holder.progress.setVisibility(View.VISIBLE);
        else
            holder.progress.setVisibility(View.GONE);

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listener.onDownloadClick(video,holder.progress,position);
            }
        });

        Picasso.with(holder.context).load(video.getThumbURL()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setListener(OnListItemsClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail, download;
        private TextView title;
        private Context context;
        private DonutProgress progress;
        ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            thumbnail = (ImageView) itemView.findViewById(R.id.imageview_video_thumb);
            title = (TextView) itemView.findViewById(R.id.textview_video_title);
            download = (ImageView) itemView.findViewById(R.id.imageview_download_music_icon);
            progress = (DonutProgress) itemView.findViewById(R.id.donut_download_progress);
        }
    }
}
