package com.moonjr.simpleimagedownloadview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.moonjr.simpleimagedownloadview.listener.OnDownloadListener;
import com.moonjr.simpleimagedownloadview.thread.ImageDownloadThread;

import java.net.URL;

/**
 * Created by MoonJR on 2015. 11. 7..
 */
public class SimpleImageDownloadView extends ImageView {

    private Context mContext;
    private boolean isCache = true;
    private OnDownloadListener mOnDownloadListener;


    public SimpleImageDownloadView(Context context) {
        super(context);
        this.mContext = context;
    }

    public SimpleImageDownloadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public SimpleImageDownloadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimpleImageDownloadView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
    }

    public void setmContext(boolean isCache) {
        this.isCache = isCache;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        mOnDownloadListener = listener;
    }


    public void setImageURL(@Nullable URL url) {
        ImageDownloadThread thread = new ImageDownloadThread(mContext, this, url);
        thread.setCache(isCache);
        thread.setOnDownloadListener(mOnDownloadListener);
        thread.start();
    }


}
