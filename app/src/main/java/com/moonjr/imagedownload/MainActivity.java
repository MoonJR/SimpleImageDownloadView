package com.moonjr.imagedownload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.moonjr.simpleimagedownloadview.SimpleImageDownloadView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleImageDownloadView view = (SimpleImageDownloadView) findViewById(R.id.image);
        try {
            view.setImageURL(new URL("http://dimg.donga.com/wps/NEWS/IMAGE/2014/02/06/60632132.1.jpg"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
