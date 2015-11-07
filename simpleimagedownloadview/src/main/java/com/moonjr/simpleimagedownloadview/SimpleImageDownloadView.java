package com.moonjr.simpleimagedownloadview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by MoonJR on 2015. 11. 7..
 */
public class SimpleImageDownloadView extends ImageView {

    private Context mContext;


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


    public void setImageURL(@Nullable URL url) {

    }

    private class DownloadThumbsImageThread extends Thread {
        private URL url;
        private ImageView mImageView;
        private Context mContext;

        private int sampleSize;

        public DownloadThumbsImageThread(Context contex, URL url) {
            this.mContext = contex;
            this.url = url;
            this.mImageView = SimpleImageDownloadView.this;
            Thread preThread = (Thread) mImageView.getTag();
            if (preThread != null) {
                preThread.interrupt();
            }
            this.mImageView.setTag(this);
            this.sampleSize = 1;
        }

        public DownloadThumbsImageThread(Context contex, URL url, int sampleSize) {
            this(contex, url);
            this.sampleSize = sampleSize;
        }


        @Override
        public void run() {

            InputStream imageStream = null;

            try {
                final Bitmap image;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;


                File cacheImageFile = new File(mContext.getExternalCacheDir(), url.hashCode() + "");
                if (cacheImageFile.exists()) {
                    imageStream = new FileInputStream(cacheImageFile);
                    image = BitmapFactory.decodeStream(imageStream);
                } else {
                    imageStream = this.url.openConnection().getInputStream();
                    image = BitmapFactory.decodeStream(imageStream, null, options);
                    FileOutputStream outputStream = new FileOutputStream(new File(mContext.getExternalCacheDir(), url.hashCode() + ""));
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                }

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

            } catch (Exception e) {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
    }

}
