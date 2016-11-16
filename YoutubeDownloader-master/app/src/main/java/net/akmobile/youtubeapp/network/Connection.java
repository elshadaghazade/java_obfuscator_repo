package net.akmobile.youtubeapp.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import net.akmobile.youtubeapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * This class is designed for establishing connection to web server
 * After initializing the connectForJSON() method should be used in order to establish connection
 * SendJSON and getStringFromServer are supplementory functions for communication with server
 */
public class Connection {
    private String SERVER_ADDRESS;
    public HttpURLConnection getConnection() {
        return connection;
    }

    private HttpURLConnection connection;
    private URL url = null;

    public Connection(String address)
    {
        this.SERVER_ADDRESS=address;
    }

    public boolean connectForJSON()
    {
        try {
            url = new URL(SERVER_ADDRESS);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestMethod("GET");
            //connection.setDoOutput(true);
            connection.connect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Function which gets string response from web server.
     * Returns String
     */
    public String getStringFromServer()  {
        InputStream input;
        StringBuilder result;
        String line,JSONRESPOND="";
        BufferedReader reader;

        try {
            input = this.connection.getInputStream();
            reader= new BufferedReader(new InputStreamReader(input));
            result= new StringBuilder();
            while ((line=reader.readLine())!=null)
            {
                result.append(line);
            }
            JSONRESPOND = ""+result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return JSONRESPOND;
    }

    public String getErrors(){
        InputStream errors;
        StringBuilder result;
        String line,JSONRESPOND="";
        BufferedReader reader;

        try {
            errors = this.connection.getErrorStream();
            if(errors==null)
                return null;
            reader= new BufferedReader(new InputStreamReader(errors));
            result= new StringBuilder();
            while ((line=reader.readLine())!=null)
            {
                result.append(line);
            }
            JSONRESPOND +=result.toString()+" Response code:"+connection.getResponseCode()+" responseMessage:"+connection.getResponseMessage();
        } catch (IOException e) {
            return null;
        }

        return JSONRESPOND;
    }

    public void disconnect() {this.connection.disconnect();}

    public void downloadMP3(String pathForSavingFile){
        int count;
        byte data[] = new byte[1024];
        long total = 0;
        try {
            url = new URL(SERVER_ADDRESS);
            connection= (HttpURLConnection) url.openConnection();
            connection.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile =connection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(pathForSavingFile);
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // publishProgress((int)(total*100/lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Drawable downloadImage(String _url)
    {
        //Prepare to download image
        URL url;
        BufferedOutputStream out;
        InputStream in;
        BufferedInputStream buf;

        //BufferedInputStream buf;
        try {
            url = new URL(_url);
            in = url.openStream();

            /*
             * THIS IS NOT NEEDED
             *
             * YOU TRY TO CREATE AN ACTUAL IMAGE HERE, BY WRITING
             * TO A NEW FILE
             * YOU ONLY NEED TO READ THE INPUTSTREAM
             * AND CONVERT THAT TO A BITMAP
            out = new BufferedOutputStream(new FileOutputStream("testImage.jpg"));
            int i;

             while ((i = in.read()) != -1) {
                 out.write(i);
             }
             out.close();
             in.close();
             */

            // Read the inputstream
            buf = new BufferedInputStream(in);

            // Convert the BufferedInputStream to a Bitmap
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            if (in != null) {
                in.close();
            }
            if (buf != null) {
                buf.close();
            }
            return new BitmapDrawable(bMap);

        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }

        return null;
    }

    /**
     *  Function for sending json to a server,
     *  Accepts JSONObject as a parameter and sends to web server
     */
    public static boolean checkInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static void finishActivity(Context context){
        ((Activity)context).finish();
    }

    public static void goToEnableInternet(final Context cnt, final boolean finish)
    {
        final Context context = cnt;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setMessage(context.getResources().getString(R.string.network_not_enabled));
        dialog.setCancelable(false);
        dialog.setPositiveButton(context.getResources().getString(R.string.open_network_settings),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(myIntent);
                        if(finish)
                            finishActivity(cnt);
                    }
                });

        dialog.setNeutralButton(context.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            finishActivity(cnt);
                    }
                });
        
        dialog.setNegativeButton("WI-FI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    context.startActivity(intent);
                    if(finish)
                        finishActivity(cnt);
                }
        });
        dialog.show();
    }

    public void sendJSON (JSONObject jsonParam)
    {
        try {
            OutputStreamWriter out = new OutputStreamWriter(this.connection.getOutputStream());
            out.write(jsonParam.toString());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
