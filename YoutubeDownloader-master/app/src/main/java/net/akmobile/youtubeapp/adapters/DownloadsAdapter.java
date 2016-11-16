package net.akmobile.youtubeapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.interfaces.OnListItemsClickListener;
import net.akmobile.youtubeapp.models.DownloadedFile;

import java.util.ArrayList;

/**
 * this is the basically RecyclerView adapter which takes arraylist of data and
 * returns corresponding views for recycler view
 */
public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {
    private ArrayList<DownloadedFile> files;
    private OnListItemsClickListener mListener;
    private Context context;
    private static final int VIDEO_ITEM = 1;
    private static final int MUSIC_ITEM = 2;

    public DownloadsAdapter(ArrayList<DownloadedFile> files, Context context) {
        this.files = files;
        this.context = context;
    }

    public void setListener(OnListItemsClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_downloaded, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (files.get(position).isMP3())
            return MUSIC_ITEM;
        else
            return VIDEO_ITEM;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DownloadedFile file = files.get(position);
        String fileName = file.getTitle();
        holder.fileName.setText(fileName.substring(0, fileName.lastIndexOf('.')));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onDownloadedFileClick(file, position);
                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                file.setPosition(position);
                return mListener == null || mListener.onDownloadedLongClick(file,holder.downloadedLayout);
            }
        });

        if (getItemViewType(position) == MUSIC_ITEM)
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_music_file));
        else
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_video_file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView icon;
        final TextView fileName;
        final LinearLayout downloadedLayout;

        ViewHolder(View view) {
            super(view);
            mView = view;

            downloadedLayout = (LinearLayout) view.findViewById(R.id.downloaded_linear_layout);
            icon = (ImageView) view.findViewById(R.id.imageview_downloaded_icon);
            fileName = (TextView) view.findViewById(R.id.textview_filename);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + fileName.getText() + "'";
        }
    }
}
