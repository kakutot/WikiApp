package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import android.media.Image;

public class MyImageInfoResponse {
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;


    public MyImageInfoResponse() {
    }

    @Override
    public String toString() {
        return "MyImageInfoResponse{" +
                "imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
