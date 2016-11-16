package net.akmobile.youtubeapp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Rahimli Rahim on 17/10/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class UrlBuilder {
    private static final String SERVER_URL = "http://67.205.150.62/youtube.php";
    private static final String QUERY = "?query=";
    private static final String GET_VIDEO_INFO = "http://www.youtube.com/get_video_info?&video_id=";
    private static final String STREAMS = "?cmd=streams";
    private static final String ID = "?id=";
    private static final String DOWNLOAD_MP3 = "&download=mp3";
    private static final String SOURCE = "&source=youtube";

    public static String getUrlForSearchQuery(String query) {
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return SERVER_URL + QUERY + query + SOURCE;
    }

    public static String getUrlForYoutubeInfo(String videoID) {
        return GET_VIDEO_INFO + videoID + "&asv=3&el=detailpage&hl=en_US";
    }

    public static String getUrlForYoutubeStreams() {
        return SERVER_URL + STREAMS;
    }

    public static String getUrlForMp3Link(String videoID) {
        return SERVER_URL + ID + videoID + DOWNLOAD_MP3;
    }
}
