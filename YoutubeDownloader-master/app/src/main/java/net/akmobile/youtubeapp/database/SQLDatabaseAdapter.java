package net.akmobile.youtubeapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.akmobile.youtubeapp.models.DownloadedFile;
import net.akmobile.youtubeapp.models.SearchedItem;
import net.akmobile.youtubeapp.models.VideoStream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ragim on 15/03/2016.
 */
public class SQLDatabaseAdapter{
    SQLHelper helper;

    public SQLDatabaseAdapter (Context context){
        helper = SQLHelper.getInstance(context);
    }

    public long insertFile(SearchedItem downloadedItem, VideoStream downloadedStream)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.FILE_TITLE,downloadedItem.getTitle());
        contentValues.put(SQLHelper.FILE_THUMBNAIL_PATH,downloadedItem.getThumbURL());
        contentValues.put(SQLHelper.FILE_EXTENSION,downloadedStream.getExtension());
        contentValues.put(SQLHelper.FILE_YOUTUBE_ID, downloadedItem.getVideoID());
        Log.d("SQLDATABASEADAPTER",contentValues.toString());
        long result =  db.insert (SQLHelper.FILES_TABLE_NAME,null,contentValues);
        db.close();
        return result;
    }

    public boolean isDownloaded (String youtubeID, String extension){
        SQLiteDatabase db = helper.getWritableDatabase();
        String Query = "Select * from " + SQLHelper.FILES_TABLE_NAME + " where " + SQLHelper.FILE_YOUTUBE_ID + " = '" + youtubeID+"' AND "+SQLHelper.FILE_EXTENSION+" = '"+extension+"'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    public ArrayList<DownloadedFile> getDownloadedFiles(){
        ArrayList<DownloadedFile> files = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {SQLHelper.FILE_ID,SQLHelper.FILE_TITLE, SQLHelper.FILE_EXTENSION, SQLHelper.FILE_THUMBNAIL_PATH, SQLHelper.FILE_YOUTUBE_ID};
        Cursor cursor = db.query(SQLHelper.FILES_TABLE_NAME, columns, null, null,null,null,null);
        while (cursor.moveToNext())
        {
            String title = cursor.getString(cursor.getColumnIndex(SQLHelper.FILE_TITLE));
            String extension = cursor.getString(cursor.getColumnIndex(SQLHelper.FILE_EXTENSION));
            String youtubeID= cursor.getString(cursor.getColumnIndex(SQLHelper.FILE_YOUTUBE_ID));
            String thumb= cursor.getString(cursor.getColumnIndex(SQLHelper.FILE_THUMBNAIL_PATH));
            int id = cursor.getInt(cursor.getColumnIndex(SQLHelper.FILE_ID));
            DownloadedFile file = new DownloadedFile(id,title,extension,thumb,youtubeID);
            files.add(file);
        }
        cursor.close();
        db.close();
        return files;
    }

   public long deleteFile(DownloadedFile downloadedFile) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String [] params = { downloadedFile.getId() + ""};
        long result = db.delete(SQLHelper.FILES_TABLE_NAME,SQLHelper.FILE_ID+" = ?",params);
        db.close();
        return result;
    }

    static class SQLHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "UserDownloads";
        private  static final String FILES_TABLE_NAME = "files";
        private static final int VERSION = 2;

        //music table fields
        private static final String FILE_ID= "id";
        private static final String FILE_TITLE= "title";
        private static final String FILE_EXTENSION = "extension";
        private static final String FILE_THUMBNAIL_PATH= "thumb_path";
        private static final String FILE_YOUTUBE_ID= "video_id";
        private static final String FILE_CREATED_AT= "created_at";


        private static SQLHelper mInstance = null;

        private static final String CREATE_FILES_TABlE = "CREATE TABLE IF NOT EXISTS "+FILES_TABLE_NAME+" ("+
                FILE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FILE_TITLE+" VARCHAR(200), "+
                FILE_THUMBNAIL_PATH+" VARCHAR(100), "+
                FILE_EXTENSION+" VARCHAR(10), "+
                FILE_CREATED_AT+" DATETIME DEFAULT CURRENT_TIMESTAMP, "+
                FILE_YOUTUBE_ID+" VARCHAR(15), "+
                "UNIQUE(" +FILE_YOUTUBE_ID+", "+FILE_EXTENSION + ") ON CONFLICT REPLACE);";

        Context helper_context;
        public SQLHelper(Context context) {
            super(context, DATABASE_NAME,null, VERSION);
            helper_context = context;
        }
        public static SQLHelper getInstance(Context context)
        {
            if(mInstance==null)
                mInstance = new SQLHelper(context.getApplicationContext());
            return mInstance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(CREATE_FILES_TABlE);
            }
            catch (SQLException e)
            {
                Log.d("SQLEXCEPTION",""+e);
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
