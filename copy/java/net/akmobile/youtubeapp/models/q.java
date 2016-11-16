package net.akmobile.youtubeapp.models;
import java.io.File;
public class q {
 private File a;
 private String b;
 private String c;
 private String d;
 private String e;
 private int f;
 private int g;
 private static final String h="mp3";
 private static final String i="mp4";
 public DownloadedFile(File a) {
 this.a= a;
 this.b= a.getName();
 int i = b.lastIndexOf('.');
 if (i > 0) {
 c= b.substring(i+1);
 }
 }
 public DownloadedFile(int f, String aname, String c, String d, String e){
 this.f= f;
 this.b= aname;
 this.c= c;
 this.d= d;
 this.e= e;
 }
 public boolean ish(){
 return this.b.toLowerCase().endsWith(h);
 }
 public boolean isi(){
 return this.b.toLowerCase().endsWith(i);
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
 return b+"."+c;
 }
 public vofsetExtension(String c) {
 this.c= c;
 }
 public String getThumbPath() {
 return d;
 }
 public vofsetThumbPath(String d) {
 this.d= d;
 }
 public String getYoutubee() {
 return e;
 }
 public vofsetYoutubee(String e) {
 this.e= e;
 }
 public int getId() {
 return f;
 }
 public vofsetPosition(int g) {
 this.g= g;
 }
 public int getPosition() {
 return g;
 }
}
