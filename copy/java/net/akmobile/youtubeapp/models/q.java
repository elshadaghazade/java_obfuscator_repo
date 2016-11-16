package net.akmobile.youtubeapp.models;
import java.io.File;
/** * Created by Rahimli Rahim on 28/09/2016. * ragim95@gmail.com * https: */
public class q {
private File a;
private String b;
private String c;
private String d;
private String e;
private int f;
private int g;
private static final String h= "mp3";
private static final String i= "mp4";
public q(File a) {
this.file = a;
this.title = a.getName();
int i = b.lastIndexOf('.');
if (i > 0) {
c= b.substring(i+1);
}
}
/** * Constructor for database operations * @param f* @param aname * @param c* @param d* @param e*/
public q(int f, String aname, String c, String d, String e){
this.id= f;
this.title = aname;
this.extension = c;
this.thumbPath = d;
this.youtubeID = e;
}
public boolean isMP3(){
return this.title.toLowerCase().endsWith(h);
}
public boolean isMP4(){
return this.title.toLowerCase().endsWith(i);
}
public File getFile() {
return a;
}
public String getTitle() {
return b;
}
public String getExtension() {
return c;
}
public String getTitleWithExtension(){
return b+"."+extension;
}
public void setExtension(String c) {
this.extension = c;
}
public String getThumbPath() {
return d;
}
public void setThumbPath(String d) {
this.thumbPath = d;
}
public String getYoutubeID() {
return e;
}
public void setYoutubeID(String e) {
this.youtubeID = e;
}
public int getId() {
return f;
}
public void setPosition(int g) {
this.position = g;
}
public int getPosition() {
return g;
}
}
