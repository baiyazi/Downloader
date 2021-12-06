package com.weizu.downloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.weizu.filedownloader2.DownloadRequest;
import com.weizu.filedownloader2.DownloadTask;
import com.weizu.filedownloader2.enums.FileSuffix;
import com.weizu.filedownloader2.listener.IDownloadListener;
import com.weizu.filedownloader2.utils.LogUtil;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
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

        // 请求读写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadTask task;
                DownloadRequest requests;
                task = new DownloadTask();
                requests = new DownloadRequest.Builder(MainActivity.this)
                        .url("https://media.w3.org/2010/05/sintel/trailer.mp4")
                        .suffix(FileSuffix.MP4)
                        .downloadDir("WTest2")
                        .build();

                task.addTask(requests, new IDownloadListener() {
                    @Override
                    public void onSuccess(String filePath) {
                        LogUtil.d(TAG, filePath);
                    }

                    @Override
                    public void onError(String msg) {
                        LogUtil.e(TAG, msg);
                    }

                    @Override
                    public void onProgress(long currentPos, long totalLength) {
                        progressbar.setProgress((int) (currentPos * 1.0 / totalLength * 100));
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onRestart() {

                    }
                });

                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            task.startTaskByRequest(requests);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            task.pauseTaskByRequest(requests);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}