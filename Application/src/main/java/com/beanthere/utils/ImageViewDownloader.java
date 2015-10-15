package com.beanthere.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.beanthere.objects.AppObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by staccie
 */
public class ImageViewDownloader extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;

    public ImageViewDownloader(ImageView iv) {
        this.imageView = iv;
    }

    protected Bitmap doInBackground(String... params) {

        Bitmap bm = null;

        try {
            URL url = new URL(AppObject.url_dev + params[0]);
            Log.e("ImageViewDownloader", AppObject.url_dev + params[0]);
            bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        }
        return bm;
    }

    protected void onPostExecute(Bitmap bm) {
        if (bm != null) {
            this.imageView.setImageBitmap(bm);
        }
    }
}
