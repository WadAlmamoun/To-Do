package com.hamidelmamoun.to_do.model;

import android.graphics.drawable.Drawable;

public class Priority {

    private String mId, mName;
    private Drawable mDrawable;

    public Priority(String id, String name, Drawable drawable){
        mId = id;
        mName = name;
        mDrawable = drawable;
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

    public void setmDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }

    public Drawable getmDrawable() {
        return mDrawable;
    }


}
