package net.akmobile.youtubeapp.models;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
/** * Created by Rahimli Rahim on 10/10/2016. * ragim95@gmail.com * https: */
public class t extends Model {
private String a;
private String b;
private String c;
String id;
private String d;
public t(String a, String b, String c) {
this.quality = a;
this.format = b;
this.url = c;
}
private static t fromJSON(String rawJSON, String d) {
Gson gson = new Gson();
t video = gson.fromJson(rawJSON, VideoStream.class);
video.setTitle(d);
video.format = video.format.substring(video.format.lastIndexOf(" ")+1).toLowerCase();
return video;
}
public static ArrayList<VideoStream> getVideoStreamsList(String id, JSONArray videoLinks, String d) {
ArrayList<VideoStream> videos = new ArrayList<>();
JSONObject item;
t video;
try {
for (int i = 0;
i < videoLinks.length();
i++) {
item = videoLinks.getJSONObject(i);
video = fromJSON(item.toString(),d);
video.setId(id);
if (video != null) videos.add(video);
}
}
catch (JSONException e) {
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
return a;
}
public String getExtension() {
return b;
}
public String getUrl() {
return c;
}
public String getTitle() {
return d;
}
public void setTitle(String d) {
this.title = d;
}
}
