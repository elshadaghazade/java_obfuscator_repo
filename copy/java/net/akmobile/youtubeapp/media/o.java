package net.akmobile.youtubeapp.media;
import android.widget.MediaController;
import net.akmobile.youtubeapp.services.MusicService;
/** * Created by Rahimli Rahim on 06/10/2016. * ragim95@gmail.com * https: */
public class o implements MediaController.MediaPlayerControl {
private MusicService a;
private boolean b;
public o(MusicService service, boolean b){
this.musicService= service;
this.musicBound = b;
}
@Override
public void start() {
a.go();
}
@Override
public void pause() {
a.pausePlayer();
}
@Override
public int getDuration() {
if(a!=null && b&& a.isPlaying()) return a.getDuration();
return 0;
}
@Override
public int getCurrentPosition() {
if(a!=null && b&& a.isPlaying()) return a.getPosition();
return 0;
}
@Override
public void seekTo(int pos) {
a.seek(pos);
}
@Override
public boolean isPlaying() {
return a!= null && b&& a.isPlaying();
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
