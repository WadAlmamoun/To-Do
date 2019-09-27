package com.hamidelmamoun.to_do.model;

public class Task {

    private String mId, mName, mComment, mDate, mPriority;
    private int mFinished;
    private boolean mOverdueTask;

    public Task(String id, String name, String comment, String date, String priority, int finished, boolean overdueTask){
        mId = id;
        mName = name;
        mComment = comment;
        mDate = date;
        mPriority = priority;
        mFinished = finished;
        mOverdueTask = overdueTask;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmId() {
        return mId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmPriority(String mPriority) {
        this.mPriority = mPriority;
    }

    public String getmPriority() {
        return mPriority;
    }

    public void setmFinished(int mFinished) {
        this.mFinished = mFinished;
    }

    public int getmFinished() {
        return mFinished;
    }


    public boolean getmOverdueTask() {
        return mOverdueTask;
    }
}
