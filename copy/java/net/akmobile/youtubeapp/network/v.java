w handler = new w(d);
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
/** * Created by Rahimli Rahim on 17/10/2016. * ragim95@gmail.com * https: */
public class v {
private DownloadManager a;
private boolean b, c= false;
private MainActivity d;
private SearchedItem e;
private DonutProgress f;
private DownloadStatusListener g;
private String h;
private VideoStream i;
private Resources j;
public v(MainActivity d, SearchedItem e, DownloadManager a, DonutProgress f, DownloadStatusListener g) {
this.activity = d;
this.itemToDownload = e;
this.manager = a;
this.progressBar = f;
this.listener = g;
this.resources = d.getResources();
}
public void initiateVideoDownload() {
AsyncHttpClient client = new AsyncHttpClient();
client.get(UrlBuilder.getUrlForYoutubeInfo(e.getVideoID()), new AsyncHttpResponseHandler() {
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
showVideoStreamDownloadProgress(false);
g.downloadFailed(j.getString(R.string.error_connecting_to_youtube));
}
}
);
}
private void showVideoStreamDownloadProgress(boolean show) {
ProgressBar f= (ProgressBar) d.findViewById(R.id.progressbar_bottom_sheet);
if (show) f.setVisibility(View.VISIBLE);
else f.setVisibility(View.GONE);
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
e= handler.handleVideoStreams(e, new String(responseBody));
if (!itemToDownload.getVideoStreams().isEmpty()) onVideoStreamsDownloaded();
else d.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
}
@Override
public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
g.downloadFailed(j.getString(R.string.error_connecting_to_server));
}
}
);
}
private void onVideoStreamsDownloaded() {
ArrayList<VideoStream> videoStreamList = e.getVideoStreams();
ListView fileFormatsListView = (ListView) d.findViewById(R.id.listview_select_filetype);
VideoStream mp3Stream = new VideoStream(j.getString(R.string.music_mp3), "mp3", "");
mp3Stream.setTitle(e.getTitle());
videoStreamList.add(0, mp3Stream);
final VideoStreamsAdapter adapter = new VideoStreamsAdapter(d, R.layout.item_video_format, videoStreamList);
fileFormatsListView.setAdapter(adapter);
fileFormatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
d.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
i= adapter.getItem(position);
h= i.getExtension().equals("mp3") ? FileManager.MUSIC_FOLDER : FileManager.VIDEOS_FOLDER;
SQLDatabaseAdapter dbAdapter = new SQLDatabaseAdapter(d);
if (dbAdapter.isDownloaded(e.getVideoID(), i.getExtension())) {
final AlertDialog.Builder dialog = new AlertDialog.Builder(d);
dialog.setMessage(j.getString(R.string.already_downloaded) + " " + i.getExtension() + " . " + j.getString(R.string.overwrite));
dialog.setCancelable(false);
dialog.setPositiveButton(j.getString(R.string.yes), new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface paramDialogInterface, int paramInt) {
c= true;
if (position == 0) getMusicStream();
}
}
);
dialog.setNegativeButton(j.getString(R.string.no), new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
c= false;
}
}
);
dialog.show();
}
else {
if (position == 0) getMusicStream();
else downloadFile();
}
}
}
);
}
private void deleteIfExists() {
if (FileManager.isFileExists(e.getTitle() + "." + i.getExtension(), h)) FileManager.deleteFile(e.getTitle() + "." + i.getExtension(), h);
}
private void downloadFile() {
g.onDownloadStart();
f.setVisibility(View.VISIBLE);
DownloadManager.Request request = new DownloadManager.Request(Uri.parse(i.getUrl()));
String description = j.getString(R.string.downloading);
String title = j.getString(R.string.download_notification_title);
request.setDescription(description + " " + e.getTitle());
request.setTitle(title);
request.allowScanningByMediaScanner();
request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
if (c) deleteIfExists();
request.setDestinationInExternalPublicDir("", h+ e.getTitle() + "." + i.getExtension());
final long downloadId = a.enqueue(request);
new Thread() {
@Override
public void run() {
do {
b= true;
final Cursor c;
DownloadManager.Query query = new DownloadManager.Query();
query.setFilterById(downloadId);
c = a.query(query);
if (c.moveToFirst()) {
final int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
final long bytes_downloaded = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
final long bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
final long download_percentage = (bytes_downloaded * 100l) / bytes_total;
new Handler(Looper.getMainLooper()).post(new Runnable() {
@Override
public void run() {
String message;
if (status == DownloadManager.STATUS_RUNNING) {
b= true;
f.setProgress((int) download_percentage);
}
else if (status == DownloadManager.STATUS_FAILED) {
b= false;
f.setVisibility(View.GONE);
message = j.getString(R.string.error_blocked);
g.downloadFailed(message);
}
else if (status == DownloadManager.STATUS_SUCCESSFUL) {
b= false;
f.setVisibility(View.GONE);
g.downloadSuccessful(e, i);
}
}
}
);
}
else {
new Handler(Looper.getMainLooper()).post(new Runnable() {
@Override
public void run() {
b= false;
f.setVisibility(View.GONE);
g.downloadFailed(j.getString(R.string.cursor_error));
}
}
);
}
c.close();
}
while (b);
}
}
.start();
}
private void getMusicStream() {
AsyncHttpClient client = new AsyncHttpClient();
client.setTimeout(600000);
final ProgressDialog dialog = new ProgressDialog(d);
client.get(UrlBuilder.getUrlForMp3Link(e.getVideoID()), new AsyncHttpResponseHandler() {
@Override
public void onStart() {
super.onStart();
dialog.setTitle(j.getString(R.string.please_wait));
dialog.setMessage(j.getString(R.string.converting_the_mp3));
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
Toast.makeText(d, j.getString(R.string.starting_download), Toast.LENGTH_SHORT).show();
i= new VideoStream("mp3 music", "mp3", rawJson.getString("mp3"));
i.setTitle(e.getTitle());
downloadFile();
}
}
catch (JSONException e) {
e.printStackTrace();
g.downloadFailed(j.getString(R.string.parsing_error));
}
}
@Override
public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
dialog.dismiss();
g.downloadFailed(j.getString(R.string.too_big_file_error));
}
}
);
}
public interface DownloadStatusListener {
void downloadFailed(String reason);
void onDownloadStart();
void downloadSuccessful(SearchedItem downloadedItem, VideoStream downloadedStream);
}
}
