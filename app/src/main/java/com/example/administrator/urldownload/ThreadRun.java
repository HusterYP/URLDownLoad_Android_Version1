package com.example.administrator.urldownload;

//每条线程属性

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadRun extends Thread {
    //成员变量
    private int start_Pos;  //每条线程开始下载位置
    private int end_Pos;   //结束下载位置
    private int perCurCount;//当前下载量
    private int ID;
    private preDownLoad downLoad;  //公共属性

    //构造函数
    public ThreadRun(int start, int end, preDownLoad preDown, int id) {
        start_Pos = start;
        end_Pos = end;
        downLoad = preDown;
        perCurCount = 0;
        ID = id;
    }

    //run方法
    @Override
    public void run() {

        try {
            //从文件读取每个线程的当前下载量
            File curFile = new File(downLoad.getCachePath() + ID + ".txt");
            if (curFile.exists()) {   //如果当前存储下载量的文件存在，主要用于断点续传
                FileInputStream in = new FileInputStream(curFile);
                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                start_Pos += Integer.valueOf(bf.readLine());
                perCurCount = start_Pos;
            }

            //线程连接的准备工作
            URL url = new URL(downLoad.getURLAddress());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(8000);    //读取超时
            connection.setConnectTimeout(8000); //设置链接超时
            connection.setRequestMethod("GET"); //连接方法
            connection.setRequestProperty("Range", "bytes=" + start_Pos + "-" + end_Pos);//设置请求数据的范围
            byte bytes[] = new byte[1024];      //每次读取1kb
            int b;

            //创建一个随机存储文件
            File file = new File(downLoad.getFilePath());
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");//rwd模式可以同步存储到临时文件
            raf.seek(start_Pos);//文件指针定位到每条线程开始读取的位置
            Message msg = downLoad.getHandler().obtainMessage();

            //创建当前文档的存储文件
            FileOutputStream of = new FileOutputStream(file);
            BufferedWriter wt = new BufferedWriter(new OutputStreamWriter(of));

            if (connection.getResponseCode() == 206) {  //连接成功，请求局部数据时，成功的返回码是206
                InputStream in = url.openStream();
                while ((b = in.read(bytes)) != -1) {
                    raf.write(bytes,0,b);
                    perCurCount += b;
                    wt.write(perCurCount + "");
                    Log.d("@HusterYP", String.valueOf(perCurCount+";"+ID));
                    msg.arg1 = perCurCount;
                    downLoad.getHandler().sendMessage(msg);
                }
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
