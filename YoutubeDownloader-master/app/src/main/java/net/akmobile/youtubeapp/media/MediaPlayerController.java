package net.akmobile.youtubeapp.media;

import android.widget.MediaController;

import net.akmobile.youtubeapp.services.MusicService;

/**
 * Created by Rahimli Rahim on 06/10/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class MediaPlayerController implements MediaController.MediaPlayerControl {

    private MusicService musicService;
    private boolean musicBound;

    public MediaPlayerController(MusicService service, boolean musicBound){
        this.musicService= service;
        this.musicBound = musicBound;
    }
    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
            musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicService!=null && musicBound && musicService.isPlaying())
            return musicService.getDuration();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService!=null && musicBound && musicService.isPlaying())
            return musicService.getPosition();
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        return musicService != null && musicBound && musicService.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
