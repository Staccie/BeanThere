package com.beanthere.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.beanthere.objects.AppObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by staccie
 */
public class ImageViewDownloader extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;
    boolean isLogo;

    public ImageViewDownloader(ImageView iv, boolean isLogo) {
        this.imageView = iv;
        this.isLogo = isLogo;
    }

    protected Bitmap doInBackground(String... params) {

        Bitmap bm = null;

        try {
            URL url = new URL(AppObject.image_folder + params[0]);
            Log.e("ImageViewDownloader", AppObject.url_dev + params[0]);
            InputStream is = url.openConnection().getInputStream();

//            bm = BitmapFactory.decodeStream(is);

            byte[] byteArr = new byte[0];
            byte[] buffer = new byte[1024];
            int len;
            int count = 0;

            while ((len = is.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);

//            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inSampleSize = 2;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            /*int[] pids = { android.os.Process.myPid() };
            MemoryInfo myMemInfo = mAM.getProcessMemoryInfo(pids)[0];
            Log.e(TAG, "dalvikPss (decoding) = " + myMemInfo.dalvikPss);*/

            bm = BitmapFactory.decodeByteArray(byteArr, 0, count, options);


            float width = bm.getWidth();
            float height = bm.getHeight();
            int scaledWidth;
            int scaledHeight;

            if (isLogo) {
                scaledWidth = (AppObject.screenWidth > 0) ? Math.round((float)AppObject.screenWidth / 4) : 80;
            } else {
                scaledWidth = (AppObject.screenWidth > 0) ? AppObject.screenWidth : 800;
            }

            if (width > scaledWidth) {
                scaledHeight = Math.round(800 / width * height);
                bm = Bitmap.createScaledBitmap(bm, scaledWidth, scaledHeight, false);
            }

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
