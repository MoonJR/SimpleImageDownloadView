package com.moonjr.simpleimagedownloadview.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.moonjr.simpleimagedownloadview.SimpleImageDownloadView;
import com.moonjr.simpleimagedownloadview.listener.OnDownloadListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by MoonJR on 2015. 11. 8..
 */

public class ImageDownloadThread extends Thread implements OnDownloadListener {

    private final String TAG = ImageDownloadThread.class.getSimpleName();

    private static boolean isDebugMode = false;

    private OnDownloadListener mOnDownloadListenerDefault;
    private OnDownloadListener mOnDownloadListenerUser;

    private URL url;
    private ImageView mImageView;
    private Context mContext;

    private boolean isCache;

    private int sampleSize;

    public ImageDownloadThread(Context context, ImageView mImageView, URL url) {
        this.mContext = context;
        this.url = url;
        this.mImageView = mImageView;
        Thread preThread = (Thread) mImageView.getTag();
        if (preThread != null) {
            preThread.interrupt();
        }
        this.mImageView.setTag(this);
        this.sampleSize = 1;
        this.mOnDownloadListenerDefault = this;
        this.isCache = true;
    }

    public ImageDownloadThread(Context context, ImageView mImageView, URL url, int sampleSize) {
        this(context, mImageView, url);
        this.sampleSize = sampleSize;
    }

    public void setOnDownloadListener(@Nullable OnDownloadListener onDownloadListener) {
        this.mOnDownloadListenerUser = onDownloadListener;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }

    public void setCache(boolean isCache) {
        this.isCache = isCache;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    public URL getURL() {
        return url;
    }

    private void throwException() {
        if (mContext == null) {
            throw new IllegalStateException("must not context null!!!");
        } else if (url == null) {
            throw new IllegalStateException("must not url null!!!");
        } else if (mImageView == null) {
            throw new IllegalStateException("must not imageView null!!!");
        }
    }

    @Override
    public void run() {
        boolean isCachedImage = false;

        throwException();

        try {
            mOnDownloadListenerDefault.onStartImageDownload(url);

            final Bitmap image;

            if (isCache) {
                File cacheImageFile = new File(mContext.getExternalCacheDir(), url.hashCode() + "");
                if (cacheImageFile.exists()) {
                    InputStream imageStream = new FileInputStream(cacheImageFile);
                    image = BitmapFactory.decodeStream(imageStream);
                    imageStream.close();
                    isCachedImage = true;
                } else {
                    image = getImageURL(url);
                }
            } else {
                image = getImageURL(url);
            }

            setImageView(image, mImageView);

            mOnDownloadListenerDefault.onFinishedImageDownload(true, isCachedImage);

        } catch (Exception e) {
            mOnDownloadListenerDefault.onFailedDownloadImage(e);
            mOnDownloadListenerDefault.onFinishedImageDownload(false, isCachedImage);
        }

    }

    private Bitmap getImageURL(URL url) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        InputStream imageStream = url.openConnection().getInputStream();
        Bitmap image = BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();
        if (isCache) {
            FileOutputStream outputStream = new FileOutputStream(new File(mContext.getExternalCacheDir(), url.hashCode() + ""));
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        }

        return image;

    }

    private void setImageView(final Bitmap image, final ImageView mImageView) {
        if (image != null) {
            mImageView.post(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(image);
                    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    new WeakReference<>(image);
                }
            });

        }
    }

    public static void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
    }

    public static boolean isDebugMode() {
        return isDebugMode;
    }


    @Override
    public void onStartImageDownload(URL downloadURL) {
        if (isDebugMode) {
            Log.d(TAG, "Start Download Image from " + downloadURL);
        }

        if (mOnDownloadListenerUser != null) {
            mOnDownloadListenerUser.onStartImageDownload(downloadURL);
        }
    }

    @Override
    public void onFinishedImageDownload(boolean isSuccess, boolean isCached) {

        if (isDebugMode) {
            if (isSuccess) {
                Log.d(TAG, "Success download!!!");
                if (isCached) {
                    Log.d(TAG, "image is cached image");
                }
            } else {
                Log.d(TAG, "Fail download!!!");
            }
        }

        if (mOnDownloadListenerUser != null) {
            mOnDownloadListenerUser.onFinishedImageDownload(isSuccess, isCached);
        }

    }

    @Override
    public void onFailedDownloadImage(Exception e) {
        if (isDebugMode) {
            Log.d(TAG, "Download Error!!!" + e.getMessage(), e);
        }
        if (mOnDownloadListenerUser != null) {
            mOnDownloadListenerUser.onFailedDownloadImage(e);
        }
    }

}