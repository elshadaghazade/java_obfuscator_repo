package net.akmobile.youtubeapp.models;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
public abstract class r {
 public JSONObject toJSON(){
 Gson gson = new Gson();
 try {
 return new JSONObject (gson.toJson(this));
 }
 catch (JSONException e) {
 e.printStackTrace();
 return i;
 }
 }
}
