package com.example.download_internet.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.download_internet.entities.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {

    //路径标识
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
    //开始标识
    public static final String ACTION_START = "ACTION_START";
    //结束标识
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";

    //定义下载任务
    private DownloadTask mTask = null;

    //handler传递标识
    public static final int MSG_INIT = 0;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获得activity传来的参数
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.d("wuyinlei", "开始 " + fileInfo.toString());
            //启动初始化线程
            new InitThread(fileInfo).start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            if (mTask !=null){
                mTask.isPause = true;
            }
            Log.d("wuyinlei", "停止" + fileInfo.toString());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //定义初始化的子线程
    class InitThread extends Thread {
        private FileInfo mFileInfo;

        public InitThread(FileInfo fileInfo) {
            this.mFileInfo = fileInfo;
        }

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_INIT:
                        FileInfo fileInfo = (FileInfo) msg.obj;
                        Log.d("wuyinlei", "Init" + fileInfo);
                        //启动下载任务
                        mTask = new DownloadTask(DownloadService.this,fileInfo);
                        mTask.download();
                        break;
                }
            }
        };

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                //连接网络文件
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //获得文件长度
                int length = -1;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                //在本地创建
                File file = new File(dir, mFileInfo.getFilename());
                raf = new RandomAccessFile(file, "rwd");//在文件的任何位置进行写入，这也是为了后面的断点续传
                // 设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);   //把文件的长度赋值给mFilename对象
                mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();  //发给handler
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.disconnect();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
