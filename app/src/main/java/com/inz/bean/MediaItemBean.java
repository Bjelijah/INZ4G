package com.inz.bean;

/**
 * Created by howell on 2016/11/25.
 */

public class MediaItemBean {

    private String picturePath;

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public MediaItemBean(String picturePath) {
        this.picturePath = picturePath;
    }

    public MediaItemBean() {
    }

    @Override
    public String toString() {
        return "MediaItemBean{" +
                "picturePath='" + picturePath + '\'' +
                '}';
    }
}
