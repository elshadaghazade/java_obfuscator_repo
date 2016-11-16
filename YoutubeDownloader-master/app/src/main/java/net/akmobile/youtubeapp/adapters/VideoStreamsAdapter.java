package net.akmobile.youtubeapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.models.VideoStream;

import java.util.ArrayList;

/**
 * Created by Rahimli Rahim on 11/10/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class VideoStreamsAdapter extends ArrayAdapter<VideoStream> {
    private static class ViewHolder {
        private TextView itemView;
        private ImageView icon;
    }

    private static final String MP4  = "mp4";
    private static final String WEBM = "webm";
    private static final String THREEGPP= "3gpp";
    private static final String MP3= "mp3";
    private ViewHolder viewHolder;

    public VideoStreamsAdapter(Context context, int resource, ArrayList<VideoStream> videos) {
        super(context, resource, videos);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.item_video_format, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.textview_video_format);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.imageview_video_format_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VideoStream item = getItem(position);
        if (item != null) {
            viewHolder.itemView.setText(item.getQuality());
            Drawable drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_mp4);;
            switch (item.getExtension()) {
                case MP3: drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_mp3); break;
                case THREEGPP: drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_3gpp); break;
                case MP4: drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_mp4); break;
                case WEBM: drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_webm); break;
            }
            viewHolder.icon.setImageDrawable(drawable);
        }

        return convertView;
    }
}
