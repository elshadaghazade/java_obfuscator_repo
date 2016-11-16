package net.akmobile.youtubeapp.utils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
public class af {
 private static final String a="http://67.205.150.62/youtube.php";
 private static final String b="?query;
 private static final String c="http://www.youtube.com/get_vfeo_info?&vfeo_f;
 private static final String d="?cmd;
 private static final String e="?f;
 private static final String f="&download;
 private static final String g="&source;
 public static String getUrlForSearchQuery(String query) {
 try {
 query = URLEncoder.encode(query, "utf-8");
 }
 catch (UnsupportedEncodingException e) {
 e.printStackTrace();
 }
 return a+ b+ query + g;
 }
 public static String getUrlForYoutubeInfo(String vfeoe) {
 return c+ vfeoe+ "&asv=3&el=detailpage&hl=en_US";
 }
 public static String getUrlForYoutubeStreams() {
 return a+ d;
 }
 public static String getUrlForMp3Link(String vfeoe) {
 return a+ e+ vfeoe+ f;
 }
}
