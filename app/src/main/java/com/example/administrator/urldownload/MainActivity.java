package com.example.administrator.urldownload;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //成员变量
    private boolean StartStopFlag = false; //开始和结束标志,false表示暂停，true表示开始
    private String urlAddress;
    private int threadCount;               //线程数
    private preDownLoad download;
    private File file;
    private long curSumDown = 0;           //当前总的下载量
    private Button startStop;              //开始暂停按钮
    private ProgressBar progressBar;

    //事件响应，刷新UI
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                curSumDown += msg.arg1;
//                progressBar.setProgress((int) (curSumDown / download.getTotalLength()) * 200);
                Toast.makeText(MainActivity.this, curSumDown + "", Toast.LENGTH_SHORT).show();
//                Log.d("@HusterYP", String.valueOf(curSumDown));
//                Log.d("@HusterYP", String.valueOf(download.getTotalLength()));
//                Log.d("@HusterYP", String.valueOf(12));
            } catch (Exception e) {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        startStop = (Button) findViewById(R.id.startStop);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    //开始暂停按钮的响应函数
    public void onDownLoad(View view) {
        StartStopFlag = !StartStopFlag; //状态交换

        if (StartStopFlag) {             //为true表示开始
            startStop.setText("暂停下载");
            urlAddress = "http://t2.27270.com/uploads/tu/201707/9999/44017f1fbb.jpg";
            threadCount = 3;
            download = new preDownLoad(urlAddress, getCacheDir(), handler);
            file = new File(download.getFilePath());  //通过路径得到文件

            //开启一个子线程，创建一个临时文件，改进：创建临时文件可否搬到preDownLoad中去做？
            final Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        //如果文件不存在，创建一个临时文件
                        if (!file.exists()) {
                            int length;
                            URL url = new URL(urlAddress);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET"); //实验了一下，这些可以不需要，只是为了防止连接的时候出现意外
                            conn.setConnectTimeout(5000);
                            conn.setReadTimeout(5000);
                            if (conn.getResponseCode() == 200) {
                                length = conn.getContentLength();
                                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                                raf.setLength(length);
                                raf.close();
                            }
                        }

                        //根据线程数量，创建对应数量的线程
                        int Average = download.getTotalLength() / threadCount;//每条线程应该下载的平局值
                        for (int i = 1; i <= threadCount; i++) {
                            if (i == threadCount)       //最后一条线程
                                new ThreadRun((i - 1) * Average, download.getTotalLength() - Average * (i - 1), download, i).start();
                            //开始位置：(i-1)*Average; 结束位置：i*Average-1; 线程ID: i
                            new ThreadRun((i - 1) * Average, i * Average - 1, download, i).start();
                        }
                    } catch (Exception e) {
                    }
                }
            };
            thread.start();
        } else {
            startStop.setText("开始下载");
        }

    }
}
