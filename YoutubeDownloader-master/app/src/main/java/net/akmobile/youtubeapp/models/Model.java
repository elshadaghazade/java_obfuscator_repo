package net.akmobile.youtubeapp.models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rahimli Rahim on 29/09/2016.
 * ragim95@gmail.com
 * https://github.com/rahimlis
 */

public abstract class Model {

    public JSONObject toJSON(){
        Gson gson = new Gson();
        try {
            return new JSONObject (gson.toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
