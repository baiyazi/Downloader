# V1.0.0
[v1.0.0](https://github.com/baiyazi/Downloader/)     文档地址：[Downloader文档](https://baiyazi.github.io/docs/Downloader/)

学习`MulDownload`对多线程文件下载的重构。对应博客地址：[Android文件下载——多文件多线程断点下载](https://blog.csdn.net/qq_26460841/article/details/121381466)

## 使用案例：

首先导入依赖：
```clike
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

和：

```clike
dependencies {
	implementation 'com.github.baiyazi:Downloader:v1.0.0'
}
```

然后写对应的`xml`和`Java`代码逻辑：

```java
public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start, pause;
    private Button start2, pause2;
    private ProgressBar progressbar;
    private ProgressBar progressbar2;
    private DownloadController downloadController;
    private DownloadController downloadController2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        start = findViewById(R.id.start);
        start2 = findViewById(R.id.start2);
        pause = findViewById(R.id.pause);
        pause2 = findViewById(R.id.pause2);
        progressbar = findViewById(R.id.progressbar);
        progressbar2 = findViewById(R.id.progressbar2);
        progressbar.setMax(100);
        progressbar2.setMax(100);

        downloadController = new DownloadController.Builder(this)
                .url("http://vjs.zencdn.net/v/oceans.mp4")
                .suffix(FileSuffix.MP4)
                .name("oceans")
                .cacheDirName("MP4")
                .build();

        downloadController2 = new DownloadController.Builder(this)
                .url("https://nodejs.org/download/release/v12.14.0/node-v12.14.0-win-x64.zip")
                .suffix(FileSuffix.ZIP)
                .name("node-v12.14.0-win-x64")
                .cacheDirName("MP4")
                .build();

        start.setOnClickListener(this);
        start2.setOnClickListener(this);
        pause.setOnClickListener(this);
        pause2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            downloadController.clearPause();
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
        } else if (v.getId() == R.id.start2) {
            downloadController2.clearPause();
            downloadController2.download(new IDownloadListener() {

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
                    progressbar2.setProgress(val);
                }
            });
        } else if (v.getId() == R.id.pause) {
            downloadController.pauseDownload();
        } else if (v.getId() == R.id.pause2) {
            downloadController2.pauseDownload();
        }
    }
}

```
`xml`文件：
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="10dp"
    >

    <ProgressBar
        android:id="@+id/progressbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="@android:style/Widget.ProgressBar.Horizontal"
        android:progress="0"
        />

    <Button
        android:id="@+id/start"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="下载"
        />

    <Button
        android:id="@+id/pause"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="暂停"
        />

    <ProgressBar
        android:id="@+id/progressbar2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="@android:style/Widget.ProgressBar.Horizontal"
        android:progress="0"
        />

    <Button
        android:id="@+id/start2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="下载"
        />

    <Button
        android:id="@+id/pause2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="暂停"
        />

</LinearLayout>
```

注意添加权限：

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.INTERNET"/>
```

因为访问外部存储需要动态权限申请，在测试中直接是在设备设置中手动开启的。没有在代码开启权限。

