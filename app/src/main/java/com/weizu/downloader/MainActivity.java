package com.weizu.downloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.weizu.filedownloader.controller.DownloadController;
import com.weizu.filedownloader.enums.FileSuffix;
import com.weizu.filedownloader.listener.IDownloadListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DownloadController downloadController;
    private Button start, pause;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setMax(100);


downloadController = new DownloadController.Builder(this)
        .url("http://vjs.zencdn.net/v/oceans.mp4")
        .suffix(FileSuffix.MP4)
        .name("oceans")
        .cacheDirName("MP4")
        .build();

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            // 清除暂停状态
            downloadController.clearPause();
            // 下载
            downloadController.download(new IDownloadListener() {

                @Override
                public void onSuccess(String filePath) {
                }

                @Override
                public void onError(String msg) {
                    Log.e("TAG", "onError: " + msg);
                }

                @Override
                public void onProgress(long currentPos, long totalLength) {
                    int val = (int) (currentPos * 1.0 / totalLength * 100);
                    progressbar.setProgress(val);
                }
            });
        }else if (v.getId() == R.id.pause) {
            downloadController.pauseDownload();
        }
    }
}