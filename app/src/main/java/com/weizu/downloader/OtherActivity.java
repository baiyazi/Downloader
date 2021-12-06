package com.weizu.downloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.excellence.annotations.Download;
import com.excellence.downloader.Downloader;
import com.excellence.downloader.FileDownloader;
import com.excellence.downloader.exception.DownloadError;
import com.excellence.downloader.utils.IListener;
import com.excellence.downloader.utils.Listener;

import java.io.File;

public class OtherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DOWNLOAD_DIR = "ZVDownloader";
    private static final String MNT_DIR = "/mnt/";
    protected static final String DOWNLOAD_PATH;

    private Button start, pause;
    private ProgressBar progressbar;
    private File file;
    private String url;
    private FileDownloader.DownloadTask downloadTask;

    static
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), DOWNLOAD_DIR);
        if (file.exists() || file.mkdirs())
            DOWNLOAD_PATH = file.getPath();
        else
        {
            file = new File(MNT_DIR, DOWNLOAD_DIR);
            if (file.exists() || file.mkdirs())
                DOWNLOAD_PATH = file.getPath();
            else
                DOWNLOAD_PATH = MNT_DIR;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        start = findViewById(R.id.start1);
        pause = findViewById(R.id.pause1);
        progressbar = findViewById(R.id.progressbar1);
        progressbar.setMax(100);

        Downloader.init(this);
        file = new File(DOWNLOAD_PATH, "Test.mp4");
        url = "https://vjs.zencdn.net/v/oceans.mp4";

        // 解绑 Downloader.unregister(this);
        // 暂停任务 DownloadTask.pause();
        // 恢复下载任务 DownloadTask.resume();
        // 删除下载任务 DownloadTask.discard();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start1){
            if(downloadTask != null){
                // 添加下载任务
                Downloader.addTask(file, url, new Listener() {
                    // 监听回调没有反应
                    @Override
                    public void onProgressChange(long fileSize, long downloadedSize, long speed) {
                        super.onProgressChange(fileSize, downloadedSize);
                        start.setText(String.valueOf(speed));
                        double val = fileSize * 1.0 / downloadedSize;
                        progressbar.setProgress((int) val);
                    }
                    @Override
                    public void onSuccess() {
                        Log.e("TAG", "onSuccess: " );
                    }
                    @Override
                    public void onError(DownloadError error) {
                        Log.e("TAG", "onError: ");
                    }
                });
            }else{
                // 恢复下载任务
                downloadTask = Downloader.get(file, url);
                downloadTask.resume();
            }
        }else if(v.getId() == R.id.pause1){
            downloadTask.pause();
        }
    }
}