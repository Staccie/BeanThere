package com.beanthere.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by staccie on 10/6/15.
 */
public class ImageViewDownloader extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;

    public ImageViewDownloader(ImageView iv) {
        this.imageView = iv;
    }

    protected Bitmap doInBackground(String... params) {

        Bitmap bm = null;

        try {
            URL url = new URL(params[0]);
            bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    protected void onPostExecute(Bitmap bm) {
        this.imageView.setImageBitmap(bm);
    }
}
