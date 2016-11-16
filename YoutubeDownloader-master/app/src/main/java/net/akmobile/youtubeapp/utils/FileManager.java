package net.akmobile.youtubeapp.utils;

import android.os.Environment;
import android.util.Log;

import net.akmobile.youtubeapp.models.DownloadedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Rahimli Rahim on 30/09/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class FileManager {
    public static final String VIDEOS_FOLDER = "YoutubeApp/Videos/";
    public static final String MUSIC_FOLDER = "YoutubeApp/Music/";

    /**
     * This method creates an instance of outputStream out of directory and filename
     * So basically it makes everything ready for writing data to storage
     * it also makes the directories if they are not exist
     *
     * @param filename the file name which will be in the directory
     * @return output Stream
     */
    public OutputStream getOutputStream(String filename, String extension) {
        File dir;
        OutputStream output;
        if (extension.equals("mp3"))
            dir = Environment.getExternalStoragePublicDirectory(MUSIC_FOLDER);
        else
            dir = Environment.getExternalStoragePublicDirectory(VIDEOS_FOLDER);

            dir.mkdirs();
        File file = new File(dir, filename + "." + extension);
        try {
            output = new FileOutputStream(file);
            return output;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method returns all the files which are in the {@param parentDir } folder
     *
     * @param parentDir root folder
     * @return all files in the parentDir
     */
    public ArrayList<DownloadedFile> getDownloadedFiles(File parentDir) {
        ArrayList<DownloadedFile> downloadedFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        if (files == null)
            return downloadedFiles;
        for (File file : files) {
            if (!file.isDirectory() && file.getName().contains("."))
                downloadedFiles.add(new DownloadedFile(file));
        }
        return downloadedFiles;
    }

    public static boolean isFileExists(String filename,String folder){
        File folder1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folder +"/"+ filename);
        Log.d("FileManager",folder1.getAbsolutePath()+" "+folder1.exists());
        return folder1.exists();
    }
    public static boolean deleteFile( String filename,String folder){
        File folder1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folder+"/" + filename);
        return folder1.delete();
    }
}
