package net.akmobile.youtubeapp.utils;
import android.os.Environment;
import android.util.Log;
import net.akmobile.youtubeapp.models.DownloadedFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
/** * Created by Rahimli Rahim on 30/09/2016. * ragim95@gmail.com * https: */
public class ae {
public static final String a= "YoutubeApp/Videos/";
public static final String b= "YoutubeApp/Music/";
/** * This method creates an instance of outputStream out of directory and filename * So basically it makes everything ready for writing data to storage * it also makes the directories if they are not exist * * @param filename the file name which will be in the directory * @return output Stream */
public OutputStream getOutputStream(String filename, String extension) {
File dir;
OutputStream output;
if (extension.equals("mp3")) dir = Environment.getExternalStoragePublicDirectory(b);
else dir = Environment.getExternalStoragePublicDirectory(a);
dir.mkdirs();
File file = new File(dir, filename + "." + extension);
try {
output = new FileOutputStream(file);
return output;
}
catch (FileNotFoundException e) {
e.printStackTrace();
}
return null;
}
/** * This method returns all the files which are in the {
@param parentDir }
folder * * @param parentDir root folder * @return all files in the parentDir */
public ArrayList<DownloadedFile> a(File parentDir) {
ArrayList<DownloadedFile> c= new ArrayList<>();
File[] files = parentDir.listFiles();
if (files == null) return c;
for (File file : files) {
if (!file.isDirectory() && file.getName().contains(".")) c.add(new DownloadedFile(file));
}
return c;
}
public static boolean b(String filename,String folder){
File folder1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folder +"/"+ filename);
Log.d("FileManager",folder1.getAbsolutePath()+" "+folder1.exists());
return folder1.exists();
}
public static boolean c( String filename,String folder){
File folder1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folder+"/" + filename);
return folder1.delete();
}
}
