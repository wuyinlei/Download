package com.example.download_internet.entities;

import java.io.Serializable;

/**
 * 文件信息
 * Created by ruolan on 2015/10/17.
 */
public class FileInfo implements Serializable{

    private int mId;
    private String mUrl;
    private String mFilename;
    private int length;
    private int finished;

    public FileInfo() {
    }

    public FileInfo(int id, String url, String filename, int length, int finished) {
        mId = id;
        mUrl = url;
        mFilename = filename;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String filename) {
        mFilename = filename;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "mId=" + mId +
                ", mUrl='" + mUrl + '\'' +
                ", mFilename='" + mFilename + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }
}
