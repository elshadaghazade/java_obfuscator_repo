package net.akmobile.youtubeapp.network;

import android.content.Context;
import android.widget.Toast;

import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.activities.MainActivity;
import net.akmobile.youtubeapp.adapters.SearchResultsAdapter;
import net.akmobile.youtubeapp.database.SQLDatabaseAdapter;
import net.akmobile.youtubeapp.models.SearchedItem;
import net.akmobile.youtubeapp.models.VideoStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rahimli Rahim on 29/09/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

/**
 * This class is designed for parsing the acquired data
 * it checks if the server sent proper response and then parses it
 */
public class ResponseHandler {
    MainActivity context;

    public ResponseHandler(MainActivity context) {
        this.context = context;
    }

    /**
     * This method is created for handling the raw response from web server (in string format)
     * it makes json out if it and then parses this data.
     * @param rawResponse is the string which we got from server
     * @return SearchResultAdapter which handles the recycler view in SearchFragment
     */
    public ArrayList<SearchedItem> handleVideos(String rawResponse) {
        ArrayList<SearchedItem> videos = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(rawResponse);
            String status = response.getString("status");
            if (!status.equals("ok"))
                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
            SQLDatabaseAdapter adapter = new SQLDatabaseAdapter(context);
            JSONArray videosArray = response.getJSONArray("items");
            JSONObject videoJSON;
            SearchedItem video;
            for (int i = 0; i < videosArray.length(); i++) {
                videoJSON = videosArray.getJSONObject(i);
                video = SearchedItem.fromJSON(videoJSON);
                videos.add(video);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Something wrong with data", Toast.LENGTH_SHORT).show();
            return null;
        }
        return videos;
    }

    public SearchedItem handleVideoStreams(SearchedItem item,  String rawResponse){
        try {
            JSONObject response = new JSONObject(rawResponse);
            String status = response.getString("status");
            if (!status.equals("ok"))
                Toast.makeText(context, context.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();

            JSONArray videoStreamsArray = response.getJSONArray("streams");
            item.setVideos(VideoStream.getVideoStreamsList(item.getVideoID(),videoStreamsArray,item.getTitle()));
            return item;

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Something wrong with data", Toast.LENGTH_SHORT).show();
            return item;
        }
    }
}
