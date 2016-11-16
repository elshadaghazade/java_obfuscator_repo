package net.akmobile.youtubeapp.models;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rahimli Rahim on 10/10/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class VideoStream extends Model {
    private String quality;
    private String format;
    private String url;
    String id;
    private String title;

    public VideoStream(String quality, String format, String url) {
        this.quality = quality;
        this.format = format;
        this.url = url;
    }

    private static VideoStream fromJSON(String rawJSON, String title) {
        Gson gson = new Gson();
        VideoStream video = gson.fromJson(rawJSON, VideoStream.class);
        video.setTitle(title);
        video.format = video.format.substring(video.format.lastIndexOf(" ")+1).toLowerCase();
        return video;
    }

   public static ArrayList<VideoStream> getVideoStreamsList(String id, JSONArray videoLinks, String title) {
        ArrayList<VideoStream> videos = new ArrayList<>();
        JSONObject item;
        VideoStream video;
        try {
            for (int i = 0; i < videoLinks.length(); i++) {
                item = videoLinks.getJSONObject(i);
                video = fromJSON(item.toString(),title);
                video.setId(id);
                if (video != null)
                    videos.add(video);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuality() {
        return quality;
    }

    public String getExtension() {
        return format;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
