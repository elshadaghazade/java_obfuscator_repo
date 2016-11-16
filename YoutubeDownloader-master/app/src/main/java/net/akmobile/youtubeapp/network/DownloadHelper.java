package net.akmobile.youtubeapp.network;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.activities.MainActivity;
import net.akmobile.youtubeapp.adapters.VideoStreamsAdapter;
import net.akmobile.youtubeapp.database.SQLDatabaseAdapter;
import net.akmobile.youtubeapp.models.SearchedItem;
import net.akmobile.youtubeapp.models.VideoStream;
import net.akmobile.youtubeapp.utils.DonutProgress;
import net.akmobile.youtubeapp.utils.FileManager;
import net.akmobile.youtubeapp.utils.UrlBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Rahimli Rahim on 17/10/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class DownloadHelper {
    private DownloadManager manager;
    private boolean downloading, overwrite = false;
    private MainActivity activity;
    private SearchedItem itemToDownload;
    private DonutProgress progressBar;
    private DownloadStatusListener listener;
    private String destinationFolder;
    private VideoStream streamToDownload;
    private Resources resources;

    public DownloadHelper(MainActivity activity, SearchedItem itemToDownload, DownloadManager manager, DonutProgress progressBar, DownloadStatusListener listener) {
        this.activity = activity;
        this.itemToDownload = itemToDownload;
        this.manager = manager;
        this.progressBar = progressBar;
        this.listener = listener;
        this.resources = activity.getResources();
    }

    public void initiateVideoDownload() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(UrlBuilder.getUrlForYoutubeInfo(itemToDownload.getVideoID()), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showVideoStreamDownloadProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getYoutubeStreams(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
              //  Toast.makeText(activity, "Error connecting to youtube", Toast.LENGTH_SHORT).show();
                showVideoStreamDownloadProgress(false);
                listener.downloadFailed(resources.getString(R.string.error_connecting_to_youtube));
            }
        });
    }

    private void showVideoStreamDownloadProgress(boolean show) {
        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressbar_bottom_sheet);
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    private void getYoutubeStreams(final String responseBody) {
        RequestParams params = new RequestParams();
        params.put("data", responseBody);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlBuilder.getUrlForYoutubeStreams(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                showVideoStreamDownloadProgress(false);
                ResponseHandler handler = new ResponseHandler(activity);
                itemToDownload = handler.handleVideoStreams(itemToDownload, new String(responseBody));
                if (!itemToDownload.getVideoStreams().isEmpty())
                    onVideoStreamsDownloaded();
                else
                    activity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(activity, resources.getString(R.string.error_connecting_to_server), Toast.LENGTH_SHORT).show();
                listener.downloadFailed(resources.getString(R.string.error_connecting_to_server));
            }
        });
    }

    private void onVideoStreamsDownloaded() {
        ArrayList<VideoStream> videoStreamList = itemToDownload.getVideoStreams();
        ListView fileFormatsListView = (ListView) activity.findViewById(R.id.listview_select_filetype);

        //add mp3 option to download menu
        VideoStream mp3Stream = new VideoStream(resources.getString(R.string.music_mp3), "mp3", "");
        mp3Stream.setTitle(itemToDownload.getTitle());

        videoStreamList.add(0, mp3Stream);
        final VideoStreamsAdapter adapter = new VideoStreamsAdapter(activity, R.layout.item_video_format, videoStreamList);
        fileFormatsListView.setAdapter(adapter);
        fileFormatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //Hide bottom sheet and keypad
                activity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                streamToDownload = adapter.getItem(position);
                destinationFolder = streamToDownload.getExtension().equals("mp3") ? FileManager.MUSIC_FOLDER : FileManager.VIDEOS_FOLDER;
                SQLDatabaseAdapter dbAdapter = new SQLDatabaseAdapter(activity);

                if (dbAdapter.isDownloaded(itemToDownload.getVideoID(), streamToDownload.getExtension())) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setMessage(resources.getString(R.string.already_downloaded) + " " + streamToDownload.getExtension() + " . " +
                            resources.getString(R.string.overwrite));
                    dialog.setCancelable(false);
                    dialog.setPositiveButton(resources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            overwrite = true;
                            if (position == 0)
                                getMusicStream();
                        }
                    });
                    dialog.setNegativeButton(resources.getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            overwrite = false;
                        }
                    });
                    dialog.show();
                } else {
                    if (position == 0)
                        getMusicStream();
                    else
                        downloadFile();
                }
            }
        });
    }

    private void deleteIfExists() {
        if (FileManager.isFileExists(itemToDownload.getTitle() + "." + streamToDownload.getExtension(), destinationFolder))
            FileManager.deleteFile(itemToDownload.getTitle() + "." + streamToDownload.getExtension(), destinationFolder);
    }

    private void downloadFile() {
        listener.onDownloadStart();
        progressBar.setVisibility(View.VISIBLE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(streamToDownload.getUrl()));
        String description = resources.getString(R.string.downloading);
        String title = resources.getString(R.string.download_notification_title);

        request.setDescription(description + " " + itemToDownload.getTitle());
        request.setTitle(title);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        if (overwrite)
            deleteIfExists();
        request.setDestinationInExternalPublicDir("", destinationFolder + itemToDownload.getTitle() + "." + streamToDownload.getExtension());
        final long downloadId = manager.enqueue(request);
        new Thread() {
            @Override
            public void run() {
                do {

                    downloading = true;
                    final Cursor c;
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    c = manager.query(query);
                    if (c.moveToFirst()) {
                        final int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));//Get download status
                        final long bytes_downloaded = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        final long bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        final long download_percentage = (bytes_downloaded * 100l) / bytes_total;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                String message;
                                if (status == DownloadManager.STATUS_RUNNING) {
                                    downloading = true;
                                    progressBar.setProgress((int) download_percentage);
                                } else if (status == DownloadManager.STATUS_FAILED) {
                                    downloading = false;
                                    progressBar.setVisibility(View.GONE);
                                    message = resources.getString(R.string.error_blocked);
                                    listener.downloadFailed(message);
                                } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    downloading = false;
                                    progressBar.setVisibility(View.GONE);
                                    listener.downloadSuccessful(itemToDownload, streamToDownload);
                                }
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                downloading = false;
                                progressBar.setVisibility(View.GONE);
                                listener.downloadFailed(resources.getString(R.string.cursor_error));
                            }
                        });
                    }
                    c.close();
                } while (downloading);
            }
        }.start();
    }

    private void getMusicStream() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(600000);
        final ProgressDialog dialog = new ProgressDialog(activity);
        client.get(UrlBuilder.getUrlForMp3Link(itemToDownload.getVideoID()), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                dialog.setTitle(resources.getString(R.string.please_wait));
                dialog.setMessage(resources.getString(R.string.converting_the_mp3));
                dialog.setCancelable(false);
                dialog.setIndeterminate(true);
                dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                String result = new String(responseBody);
                try {
                    JSONObject rawJson = new JSONObject(result);
                    if (rawJson.getString("status").equals("ok")) {
                        Toast.makeText(activity, resources.getString(R.string.starting_download), Toast.LENGTH_SHORT).show();
                        streamToDownload = new VideoStream("mp3 music", "mp3", rawJson.getString("mp3"));
                        streamToDownload.setTitle(itemToDownload.getTitle());
                        downloadFile();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.downloadFailed(resources.getString(R.string.parsing_error));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                listener.downloadFailed(resources.getString(R.string.too_big_file_error));
            }
        });
    }

    public interface DownloadStatusListener {
        void downloadFailed(String reason);

        void onDownloadStart();

        void downloadSuccessful(SearchedItem downloadedItem, VideoStream downloadedStream);
    }
}
