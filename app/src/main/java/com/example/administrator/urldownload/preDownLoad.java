package com.example.administrator.urldownload;

//所有线程的共有属性:待下载文件地址，保存路径，文件长度

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

public class preDownLoad {
    //成员变量
    private String URLAddress;
    private File filePath;   //Cache目录
    private Handler handler;

    //构造函数
    public preDownLoad(String URLAddress, File filePath, Handler handler) {
        this.URLAddress = URLAddress;
        this.filePath = filePath;
        this.handler = handler;
    }

    //获取地址
    public String getURLAddress() {
        return URLAddress;
    }

    //获取保存文件的地址
    public String getFilePath() {
        String fileKind = "";
        try {
            int index = URLAddress.lastIndexOf("/");
            fileKind = URLAddress.substring(index + 1);
        } catch (Exception e) {
        }
        return filePath.getAbsolutePath() + fileKind;
    }

    //获取文件长度,同时创建一个同样长度的虚拟文件
    public int getTotalLength() throws Exception {
        int length;
        URL url = new URL(URLAddress);
        URLConnection connection = url.openConnection();
        length = connection.getContentLength();
        Log.d("@HusterYP", String.valueOf(length + "total"));
        return length;
    }

    //获取Handler
    public Handler getHandler() {
        return handler;
    }

    //获取cache根目录
    public String getCachePath() {
        return filePath.getAbsolutePath();
    }

}
