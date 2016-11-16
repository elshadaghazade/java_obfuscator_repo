package net.akmobile.youtubeapp.models;

import java.io.File;

/**
 * Created by Rahimli Rahim on 28/09/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class DownloadedFile {
    private File file;
    private String title;
    private String extension;
    private String thumbPath;
    private String youtubeID;
    private int id;
    private int position;
    private static final String MP3 = "mp3";
    private static final String MP4 = "mp4";

    public DownloadedFile(File file) {
        this.file = file;
        this.title = file.getName();
        int i = title.lastIndexOf('.');
        if (i > 0) {
            extension = title.substring(i+1);
        }
    }

    /**
     * Constructor for database operations
     * @param id
     * @param filename
     * @param extension
     * @param thumbPath
     * @param youtubeID
     */
    public DownloadedFile(int id, String filename, String extension, String thumbPath, String youtubeID){
        this.id= id;
        this.title = filename;
        this.extension = extension;
        this.thumbPath = thumbPath;
        this.youtubeID = youtubeID;
    }

    public boolean isMP3(){
        return this.title.toLowerCase().endsWith(MP3);
    }

    public boolean isMP4(){
        return this.title.toLowerCase().endsWith(MP4);
    }

    public File getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    public String getExtension() {
        return extension;
    }
    public String getTitleWithExtension(){
        return  title+"."+extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }


    public String getYoutubeID() {
        return youtubeID;
    }

    public void setYoutubeID(String youtubeID) {
        this.youtubeID = youtubeID;
    }

    public int getId() {
        return id;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
