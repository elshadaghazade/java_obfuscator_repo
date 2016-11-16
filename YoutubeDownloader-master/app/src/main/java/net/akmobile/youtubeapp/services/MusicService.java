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

/**
 * Created by Rahimli Rahim on 06/10/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private MediaPlayer mediaPlayer;
    private ArrayList<DownloadedFile> songs;
    private int songPosition;
    private final IBinder musicBinder = new MusicBinder();
    private static final int NOTIFY_ID = 1;
    private RemoteViews mContentViewBig, mContentViewSmall;
    public static final String UPDATE_UI = "update";
    private boolean isRepeating = false;
    private AudioManager audioManager;
    private boolean isShuffled = false;
    private static final String ACTION_STOP_SERVICE = "action.stop";
    private static final String ACTION_PLAY_PREVIOUS = "action.prev";
    private static final String ACTION_PLAY_NEXT = "action.next";
    private static final String ACTION_PLAY_PAUSE = "action.pause";

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
//            sendBroadcast(new Intent(UPDATE_UI));
            if (ACTION_PLAY_PAUSE.equals(action)) {
                if (isPlaying()) {
                    pausePlayer();
                } else {
                    go();
                }
                sendBroadcast(new Intent(UPDATE_UI));
            } else if (ACTION_PLAY_NEXT.equals(action)) {
                playNext();
            } else if (ACTION_PLAY_PREVIOUS.equals(action)) {
                playPrevious();
            } else if (ACTION_STOP_SERVICE.equals(action)) {
                if (isPlaying())
                    pausePlayer();
                audioManager.abandonAudioFocus(this);
                stopForeground(true);
                updateActivityUI();
            }
        }
        return START_STICKY;
    }

    public void initMusicPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<DownloadedFile> songs) {
        this.songs = songs;
    }

    private void updateActivityUI() {
        Intent intent = new Intent(UPDATE_UI);
        sendBroadcast(intent);
    }

    public void playSong() {
        mediaPlayer.reset();
        if (songPosition <= songs.size() - 1) {
            File song = songs.get(songPosition).getFile();
            try {
                mediaPlayer.setDataSource(song.getAbsolutePath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getPlayingSong() {
        return songs.get(songPosition).getFile();
    }

    public boolean isPlaybackNotEmpty() {
        return songs.size() > 0;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
         pausePlayer();
        if(focusChange==AudioManager.AUDIOFOCUS_GAIN)
            go();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer.getCurrentPosition() > 0) {
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
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mp.start();
            updateActivityUI();
            showNotification();
        }
    }

    public void setSongPosition(int songPosition) {
        this.songPosition = songPosition;
    }

    @Override
    public void onDestroy() {
        audioManager.abandonAudioFocus(this);
        stopForeground(true);
    }

    private void showNotification() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_app_logo)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(getSmallContentView())
                .setCustomBigContentView(getBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build();
        startForeground(NOTIFY_ID, notification);
        mContentViewBig = null;
        mContentViewSmall = null;
    }

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.remote_view_music_player_small);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private RemoteViews getBigContentView() {
        if (mContentViewBig == null) {
            mContentViewBig = new RemoteViews(getPackageName(), R.layout.remote_view_music_player);
            setUpRemoteView(mContentViewBig);
        }
        updateRemoteViews(mContentViewBig);
        return mContentViewBig;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close);
        remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last);
        remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next);

        remoteView.setOnClickPendingIntent(R.id.button_close, getPendingIntent(ACTION_STOP_SERVICE));
        remoteView.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(ACTION_PLAY_PREVIOUS));
        remoteView.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(ACTION_PLAY_NEXT));
        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_PLAY_PAUSE));
    }

    private void updateRemoteViews(RemoteViews remoteView) {
        if(songs.size()<1)
            return;
        DownloadedFile currentSong = songs.get(songPosition);
        if (songs.get(songPosition) != null) {
            remoteView.setTextViewText(R.id.text_view_name, currentSong.getTitle());
            remoteView.setTextViewText(R.id.text_view_artist, currentSong.getTitle());
        }
        remoteView.setImageViewResource(R.id.image_view_play_toggle, isPlaying() ? R.drawable.ic_remote_view_pause : R.drawable.ic_remote_view_play);
        AlbumUtils utils = new AlbumUtils();
        Bitmap album = utils.parseAlbum(getPlayingSong());
        if (album == null) {
            remoteView.setImageViewResource(R.id.image_view_album, R.mipmap.ic_launcher);
        } else {
            remoteView.setImageViewBitmap(R.id.image_view_album, album);
        }
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        return PendingIntent.getService(this, 0, intent, 0);
    }

    public void playPrevious() {
        if (songPosition == 0) songPosition = songs.size() - 1;
        else songPosition--;
        playSong();
    }

    public void playNext() {
        if (isRepeating) {
            playSong();
            return;
        }

        if (isShuffled) {
            songPosition = getRandomNumber(songPosition);

            playSong();
            return;
        } else
            songPosition++;

        if (songPosition == songs.size()) songPosition = 0;
        playSong();
    }

    private int getRandomNumber(int previous) {
        Random rand = new Random();
        int next = rand.nextInt(songs.size());
        if (next == previous)
            return getRandomNumber(previous);
        else return next;
    }

    public void pausePlayer() {
        mediaPlayer.pause();
        showNotification();
    }

    public void seek(int posn) {
        mediaPlayer.seekTo(posn);
    }

    public void go() {
        mediaPlayer.start();
        showNotification();
    }


    public void toggleShuffle() {
        isShuffled = !isShuffled;
        isRepeating = false;
    }

    public void toggleRepeating() {
        isRepeating = !isRepeating;
        isShuffled = false;
    }
// TODO: 21/10/2016 abandon audio service on all stops and dont run after release of another app if user paused himself

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void stop(){
        mediaPlayer.stop();
        stopForeground(true);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isShuffled() {
        return isShuffled;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

}
