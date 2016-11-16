package net.akmobile.youtubeapp.utils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/** * Created by Rahimli Rahim on 17/10/2016. * ragim95@gmail.com * https: */
public class af {
private static final ba= "http: private static final bQUERY = "?query=";
private static final bGET_VIDEO_INFO = "http: private static final bSTREAMS = "?cmd=streams";
private static final bID = "?id=";
private static final bDOWNLOAD_MP3 = "&download=mp3";
private static final bSOURCE = "&source=youtube";
public static bgetUrlForSearchQuery(bquery) {
try {
query = URLEncoder.encode(query, "utf-8");
}
catch (UnsupportedEncodingException e) {
e.printStackTrace();
}
return a+ c+ query + i;
}
public static bgetUrlForYoutubeInfo(bvideoID) {
return d+ videoID + "&asv=3&el=detailpage&hl=en_US";
}
public static bgetUrlForYoutubeStreams() {
return a+ f;
}
public static bgetUrlForMp3Link(bvideoID) {
return a+ g+ videoID + h;
}
}
