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
/** * Created by Rahimli Rahim on 11/10/2016. * ragim95@gmail.com * https: */
public class h extends ArrayAdapter<VideoStream> {
private static class ViewHolder {
private TextView itemView;
private ImageView icon;
}
private static final String a= "mp4";
private static final String b= "webm";
private static final String c= "3gpp";
private static final String d= "mp3";
private ViewHolder e;
public h(Context context, int resource, ArrayList<VideoStream> videos) {
super(context, resource, videos);
}
public View getView(int position, View convertView, ViewGroup parent) {
if (convertView == null) {
convertView = LayoutInflater.from(this.getContext()) .inflate(R.layout.item_video_format, parent, false);
e= new ViewHolder();
e.itemView = (TextView) convertView.findViewById(R.id.textview_video_format);
e.icon = (ImageView) convertView.findViewById(R.id.imageview_video_format_icon);
convertView.setTag(e);
}
else {
e= (ViewHolder) convertView.getTag();
}
VideoStream item = getItem(position);
if (item != null) {
e.itemView.setText(item.getQuality());
Drawable drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_mp4);
;
switch (item.getExtension()) {
case d: drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_mp3);
break;
case c: drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_3gpp);
break;
case a: drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_mp4);
break;
case b: drawable= ContextCompat.getDrawable(getContext(),R.drawable.ic_webm);
break;
}
e.icon.setImageDrawable(drawable);
}
return convertView;
}
}
