package net.akmobile.youtubeapp.services;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.activities.MainActivity;
import net.akmobile.youtubeapp.models.DownloadedFile;
import net.akmobile.youtubeapp.utils.AlbumUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
/** * Created by Rahimli Rahim on 06/10/2016. * ragim95@gmail.com * https: */
public class z extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
private MediaPlayer a;
private ArrayList<DownloadedFile> b;
private int c;
private final IBinder musicBinder = new MusicBinder();
private static final int d= 1;
private RemoteViews e, f;
public static final String g= "update";
private boolean h= false;
private AudioManager i;
private boolean j= false;
private static final String k= "action.stop";
private static final String l= "action.prev";
private static final String m= "action.next";
private static final String n= "action.pause";
@Override
public void onCreate() {
super.onCreate();
c= 0;
i= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
a= new MediaPlayer();
initMusicPlayer();
}
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
if (intent != null) {
String action = intent.getAction();
if (n.equals(action)) {
if (isPlaying()) {
pausePlayer();
}
else {
go();
}
sendBroadcast(new Intent(g));
}
else if (m.equals(action)) {
playNext();
}
else if (l.equals(action)) {
playPrevious();
}
else if (k.equals(action)) {
if (isPlaying()) pausePlayer();
i.abandonAudioFocus(this);
stopForeground(true);
updateActivityUI();
}
}
return START_STICKY;
}
public void initMusicPlayer() {
a.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
a.setAudioStreamType(AudioManager.STREAM_MUSIC);
a.setOnPreparedListener(this);
a.setOnCompletionListener(this);
a.setOnErrorListener(this);
}
public void setList(ArrayList<DownloadedFile> b) {
this.songs = b;
}
private void updateActivityUI() {
Intent intent = new Intent(g);
sendBroadcast(intent);
}
public void playSong() {
a.reset();
if (c<= b.size() - 1) {
File song = b.get(c).getFile();
try {
a.setDataSource(song.getAbsolutePath());
a.prepare();
}
catch (IOException e) {
e.printStackTrace();
}
}
}
public File getPlayingSong() {
return b.get(c).getFile();
}
public boolean isPlaybackNotEmpty() {
return b.size() > 0;
}
@Override
public void onAudioFocusChange(int focusChange) {
pausePlayer();
if(focusChange==AudioManager.AUDIOFOCUS_GAIN) go();
}
public class MusicBinder extends Binder {
public z getService() {
return MusicService.this;
}
}
@Override
public IBinder onBind(Intent intent) {
return musicBinder;
}
@Override
public boolean onUnbind(Intent intent) {
a.stop();
a.release();
return false;
}
@Override
public void onCompletion(MediaPlayer mp) {
if (a.getCurrentPosition() > 0) {
mp.reset();
playNext();
}
}
@Override
public boolean onError(MediaPlayer mp, int what, int extra) {
mp.reset();
return false;
}
@Override
public void onPrepared(MediaPlayer mp) {
int result = i.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
mp.start();
updateActivityUI();
showNotification();
}
}
public void setSongPosition(int c) {
this.songPosition = c;
}
@Override
public void onDestroy() {
i.abandonAudioFocus(this);
stopForeground(true);
}
private void showNotification() {
Intent mainIntent = new Intent(this, MainActivity.class);
mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
Notification notification = new NotificationCompat.Builder(this) .setSmallIcon(R.drawable.ic_notification_app_logo) .setWhen(System.currentTimeMillis()) .setContentIntent(contentIntent) .setCustomContentView(getSmallContentView()) .setCustomBigContentView(getBigContentView()) .setPriority(NotificationCompat.PRIORITY_MAX) .setOngoing(true) .build();
startForeground(d, notification);
e= null;
f= null;
}
private RemoteViews getSmallContentView() {
if (f== null) {
f= new RemoteViews(getPackageName(), R.layout.remote_view_music_player_small);
setUpRemoteView(f);
}
updateRemoteViews(f);
return f;
}
private RemoteViews getBigContentView() {
if (e== null) {
e= new RemoteViews(getPackageName(), R.layout.remote_view_music_player);
setUpRemoteView(e);
}
updateRemoteViews(e);
return e;
}
private void setUpRemoteView(RemoteViews remoteView) {
remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close);
remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last);
remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next);
remoteView.setOnClickPendingIntent(R.id.button_close, getPendingIntent(k));
remoteView.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(l));
remoteView.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(m));
remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(n));
}
private void updateRemoteViews(RemoteViews remoteView) {
if(b.size()<1) return;
DownloadedFile currentSong = b.get(c);
if (b.get(c) != null) {
remoteView.setTextViewText(R.id.text_view_name, currentSong.getTitle());
remoteView.setTextViewText(R.id.text_view_artist, currentSong.getTitle());
}
remoteView.setImageViewResource(R.id.image_view_play_toggle, isPlaying() ? R.drawable.ic_remote_view_pause : R.drawable.ic_remote_view_play);
AlbumUtils utils = new AlbumUtils();
Bitmap album = utils.parseAlbum(getPlayingSong());
if (album == null) {
remoteView.setImageViewResource(R.id.image_view_album, R.mipmap.ic_launcher);
}
else {
remoteView.setImageViewBitmap(R.id.image_view_album, album);
}
}
private PendingIntent getPendingIntent(String action) {
Intent intent = new Intent(this, MusicService.class);
intent.setAction(action);
return PendingIntent.getService(this, 0, intent, 0);
}
public void playPrevious() {
if (c== 0) c= b.size() - 1;
else c--;
playSong();
}
public void playNext() {
if (h) {
playSong();
return;
}
if (j) {
c= getRandomNumber(c);
playSong();
return;
}
else c++;
if (c== b.size()) c= 0;
playSong();
}
private int getRandomNumber(int previous) {
Random rand = new Random();
int next = rand.nextInt(b.size());
if (next == previous) return getRandomNumber(previous);
else return next;
}
public void pausePlayer() {
a.pause();
showNotification();
}
public void seek(int posn) {
a.seekTo(posn);
}
public void go() {
a.start();
showNotification();
}
public void toggleShuffle() {
j= !isShuffled;
h= false;
}
public void toggleRepeating() {
h= !isRepeating;
j= false;
}
public int getPosition() {
return a.getCurrentPosition();
}
public int getDuration() {
return a.getDuration();
}
public void stop(){
a.stop();
stopForeground(true);
}
public boolean isPlaying() {
return a.isPlaying();
}
public boolean j() {
return j;
}
public boolean h() {
return h;
}
}
