package net.akmobile.youtubeapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rahimli Rahim on 29/09/2016.
 * ragim95@gmail.com
 * https://github.com/rahimlis/
 */

/**
 * This class describes basic model of video, searched on youtube
 * it contains title of video, description under it, urls to thumbnail and mp3 file
 */
public class SearchedItem extends Model {
    private boolean isDownloading =false;
    private String videoID;
    private String title;
    private String description;
    private String channel;
    private String thumbURL;
    private String mp3Url;

    public void setVideos(ArrayList<VideoStream> videos) {
        this.videos = videos;
    }

    private ArrayList<VideoStream> videos;

    public SearchedItem(String videoID, String title, String description, String channel, String thumbURL,ArrayList<VideoStream> videos) {
        this.videoID = videoID;
        this.title = title;
        this.description = description;
        this.channel = channel;
        this.thumbURL = thumbURL;
        this.videos=videos;
    }


    /**
     * The method returns an instance of SearchedItem from raw JSON data
     * @param rawVideo input parameter which we get from server
     * @return SearchedItem item parsed
     */
    public static SearchedItem fromJSON(JSONObject rawVideo) {
        String title, videoID, description,channel,thumbURL;
        ArrayList<VideoStream> videos = new ArrayList<>();
        try {
            videoID = rawVideo.getString("videoId");
            title = rawVideo.getString("title");
            description = rawVideo.getString("description");
            channel = rawVideo.getString("channelTitle");
            thumbURL = rawVideo.getString("thumbnail");
            return new SearchedItem(videoID,title,description,channel,thumbURL,videos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getVideoID() {
        return videoID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getChannel() {
        return channel;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public ArrayList<VideoStream> getVideoStreams() {
        return videos;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public SearchedItem setDownloading(boolean downloading) {
        isDownloading = downloading;
        return this;
    }

    public void normalizeTitle() {
        title = title.replaceAll("[^\\p{L}\\d\\s\\-\\&]","");
    }
}
