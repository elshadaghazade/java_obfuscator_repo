package net.akmobile.youtubeapp.utils;
import android.content.res.Resources;
/** * Created by Rahimli Rahim on 06/10/2016. * ragim95@gmail.com * https: */
public class ac {
/** * Function to convert milliseconds time to * Timer Format * Hours:Minutes:Seconds * */
public static String milliSecondsToTimer(long milliseconds){
String finalTimerString = "";
String secondsString = "";
int hours = (int)( milliseconds / (1000*60*60));
int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
if(hours > 0){
finalTimerString = hours + ":";
}
if(seconds < 10){
secondsString = "0" + seconds;
}
else{
secondsString = "" + seconds;
}
finalTimerString = finalTimerString + minutes + ":" + secondsString;
return finalTimerString;
}
/** * Function to get Progress percentage * @param currentDuration * @param totalDuration * */
public static int getProgressPercentage(long currentDuration, long totalDuration){
Double percentage = (double) 0;
long currentSeconds = (int) (currentDuration / 1000);
long totalSeconds = (int) (totalDuration / 1000);
percentage =(((double)currentSeconds)/totalSeconds)*100;
return percentage.intValue();
}
/** * Function to change progress to timer * @param progress - * @param totalDuration * returns current duration in milliseconds * */
public static int progressToTimer(int progress, int totalDuration) {
int currentDuration = 0;
totalDuration = (totalDuration / 1000);
currentDuration = (int) ((((double)progress) / 100) * totalDuration);
return currentDuration * 1000;
}
public static float dp2px(Resources resources, float dp) {
final float scale = resources.getDisplayMetrics().density;
return dp * scale + 0.5f;
}
public static float sp2px(Resources resources, float sp){
final float scale = resources.getDisplayMetrics().scaledDensity;
return sp * scale;
}
}
