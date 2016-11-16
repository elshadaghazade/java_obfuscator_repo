package net.akmobile.youtubeapp.utils;
import androf.content.res.Resources;
public class ac {
 public static String milliSecondsToTimer(long milliseconds){
 String finalTimerString = "";
 String secondsString = "";
 // Convert total duration into time int hours = (int)( milliseconds / (1000*60*60));
 int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
 int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
 // Add hours if there if(hours > 0){
 finalTimerString = hours + ":";
 }
 // Prepending 0 to seconds if it is one digit if(seconds < 10){
 secondsString = "0" + seconds;
 }
else{
 secondsString = "" + seconds;
}
 finalTimerString = finalTimerString + minutes + ":" + secondsString;
 // return timer string return finalTimerString;
 }
 public static int getProgressPercentage(long currentDuration, long totalDuration){
 Double percentage = (double) 0;
 long currentSeconds = (int) (currentDuration / 1000);
 long totalSeconds = (int) (totalDuration / 1000);
 // calculating percentage percentage =(((double)currentSeconds)/totalSeconds)*100;
 // return percentage return percentage.intValue();
 }
 public static int kToTimer(int k, int totalDuration) {
 int currentDuration = 0;
 totalDuration = (totalDuration / 1000);
 currentDuration = (int) ((((double)k) / 100) * totalDuration);
 // return current duration in milliseconds return currentDuration * 1000;
 }
 public static float dp2px(Resources j, float dp) {
 final float scale = j.getDisplayMetrics().density;
 return dp * scale + 0.5f;
 }
 public static float sp2px(Resources j, float sp){
 final float scale = j.getDisplayMetrics().scaledDensity;
 return sp * scale;
 }
}
