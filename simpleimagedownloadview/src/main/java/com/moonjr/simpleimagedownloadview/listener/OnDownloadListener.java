package com.moonjr.simpleimagedownloadview.listener;

import java.net.URL;

/**
 * Created by MoonJR on 2015. 11. 7..
 */
public interface OnDownloadListener {
    public void onStartImageDownload(URL downloadURL);

    public void onFinishedImageDownload(boolean isSuccess, boolean isCached);

    public void onFailedDownloadImage(Exception e);
}
