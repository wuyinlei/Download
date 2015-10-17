package com.example.download_internet.services;

import android.content.Context;
import android.content.Intent;

import com.example.download_internet.db.ThreadDAO;
import com.example.download_internet.db.ThreadDAOImpl;
import com.example.download_internet.entities.FileInfo;
import com.example.download_internet.entities.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 下载任务类
 * Created by ruolan on 2015/10/17.
 */
public class DownloadTask {
    private FileInfo mFileInfo;
    private Context mContext;
    private ThreadDAO mDAO;
    private int mFinished = 0;
    public boolean isPause = false;

    public DownloadTask(Context context, FileInfo fileInfo) {
        mFileInfo = fileInfo;
        mContext = context;
        mDAO = new ThreadDAOImpl(mContext);

    }

    public void download(){
        //读取数据库的线程信息
        List<ThreadInfo> threadInfos = mDAO.getThreads(mFileInfo.getUrl());
        ThreadInfo threadInfo = null;
        if (threadInfos .size() == 0){ //第一次下载
            //初始化线程信息对象
            threadInfo = new ThreadInfo(0,mFileInfo.getUrl(),0,mFileInfo.getLength(),0);
        } else {
            threadInfo = threadInfos.get(0);   //单线程现在是
        }

        //创建子线程进行下载
        new DownloadThread(threadInfo).start();
    }

    /**
     * 下载线程
     */
    class DownloadThread extends Thread {
        private ThreadInfo mThreadInfo;

        public DownloadThread(ThreadInfo threadInfo) {
            mThreadInfo = threadInfo;
        }

        RandomAccessFile raf;
        HttpURLConnection conn;
        InputStream is;

        @Override
        public void run() {
            //向数据库中插入线程信息
            if (!mDAO.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
                mDAO.insertThread(mThreadInfo);
            }

            try {
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //设置下载位置
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes = " + start + "-" + mThreadInfo.getEnd());

                //设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFilename());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);  //seek()方法，在读写的时候跳过设置好的字节数，从下一个字节数开始读写
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                mFinished += mThreadInfo.getFinished();
                //开始下载
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //读取数据
                    is = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = is.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer, 0, len);
                        //下载进度发送广播给activity
                        mFinished += len;
                        if (System.currentTimeMillis() - time > 500) {
                            time = System.currentTimeMillis();
                            intent.putExtra("finished", mFinished * 100 / mFileInfo.getLength());
                            mContext.sendBroadcast(intent);
                        }
                        //下载暂停时，保存下载进度
                        if (isPause) {
                            mDAO.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);
                            return;
                        }
                    }
                    //删除线程信息
                    mDAO.deletsThread(mThreadInfo.getUrl(), mThreadInfo.getId());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                try {
                    raf.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
