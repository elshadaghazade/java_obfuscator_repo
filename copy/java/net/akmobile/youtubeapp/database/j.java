i adapter = new i(a);
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import net.akmobile.youtubeapp.models.DownloadedFile;
import net.akmobile.youtubeapp.utils.FileManager;
import java.util.ArrayList;
/** * Created by Rahimli Rahim on 19/10/2016. * ragim95@gmail.com * https: */
public class j extends AsyncTask<Void,Void,Void> {
private Context a;
public j(Context a){
this.context = a;
}
@Override
protected Void doInBackground(Void... inputParams) {
ArrayList<DownloadedFile> dbFiles= adapter.getDownloadedFiles();
for (DownloadedFile downloadedFile : dbFiles) {
if(!FileManager.isFileExists(downloadedFile.getTitleWithExtension(),FileManager.MUSIC_FOLDER) && !FileManager.isFileExists(downloadedFile.getTitleWithExtension(),FileManager.VIDEOS_FOLDER)) adapter.deleteFile(downloadedFile);
}
return null;
}
}
