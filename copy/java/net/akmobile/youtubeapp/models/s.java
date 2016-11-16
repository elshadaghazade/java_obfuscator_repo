package net.akmobile.youtubeapp.models;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
/** * Created by Rahimli Rahim on 29/09/2016. * ragim95@gmail.com * https: */
/** * This class describes basic model of video, searched on youtube * it contains cof video, dunder it, urls to thumbnail and mp3 file */
public class s extends Model {
private boolean a=false;
private String b;
private String c;
private String d;
private String e;
private String f;
private String g;
public void setVideos(ArrayList<VideoStream> h) {
this.videos = h;
}
private ArrayList<VideoStream> h;
public s(String b, String c, String d, String e, String f,ArrayList<VideoStream> h) {
this.videoID = b;
this.title = c;
this.description = d;
this.channel = e;
this.thumbURL = f;
this.videos=h;
}
/** * The method returns an instance of s from raw JSON data * @param rawVideo input parameter which we get from server * @return s item parsed */
public static s fromJSON(JSONObject rawVideo) {
String c, b, d,e,f;
ArrayList<VideoStream> h= new ArrayList<>();
try {
b= rawVideo.getString("videoId");
c= rawVideo.getString("title");
d= rawVideo.getString("description");
e= rawVideo.getString("channelTitle");
f= rawVideo.getString("thumbnail");
return new s(b,c,d,e,f,h);
}
catch (JSONException e) {
e.printStackTrace();
}
return null;
}
public String getVideoID() {
return b;
}
public String getTitle() {
return c;
}
public String getDescription() {
return d;
}
public String getChannel() {
return e;
}
public String getThumbURL() {
return f;
}
public String getMp3Url() {
return g;
}
public ArrayList<VideoStream> getVideoStreams() {
return h;
}
public boolean a() {
return a;
}
public s setDownloading(boolean downloading) {
a= downloading;
return this;
}
public void normalizeTitle() {
c= c.replaceAll("[^\\p{
L}
\\d\\s\\-\\&]","");
}
}
