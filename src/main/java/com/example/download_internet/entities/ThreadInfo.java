package com.example.download_internet.entities;

/**
 * Created by ruolan on 2015/10/17.
 */
public class ThreadInfo {

    private int mId;
    private String mUrl;
    private int mStart;
    private int mEnd;
    private int finished;

    public ThreadInfo() {
    }

    public ThreadInfo(int id , String url,int start, int end, int finished) {
        mId = id;
        mStart = start;
        mUrl = url;
        mEnd = end;
        this.finished = finished;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public int getStart() {
        return mStart;
    }

    public void setStart(int start) {
        mStart = start;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setEnd(int end) {
        mEnd = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "mId=" + mId +
                ", mUrl='" + mUrl + '\'' +
                ", mStart=" + mStart +
                ", mEnd=" + mEnd +
                ", finished=" + finished +
                '}';
    }
}
