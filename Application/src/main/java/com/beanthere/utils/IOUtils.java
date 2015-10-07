package com.beanthere.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class IOUtils {

	private static final int IMAGE_MAX_HEIGHT = 320;
	private static final int IMAGE_MAX_WIDTH = 480;

	public static void writeFile(String tag, Context context, String content, String fileName) {
		try {
			//			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			String path = context.getExternalFilesDir(null) + "/";
			String name = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())
					+ " "
					+ new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date())
					+ fileName;

			FileOutputStream fos = new FileOutputStream(path + name);
			fos.write(content.getBytes());
			fos.close();

			//			Debugger.v("written file", path + name);

		} catch (FileNotFoundException e) {
			Log.e("writeFile@" + tag, e.getMessage());
		} catch (IOException e) {
			Log.e("writeFile@" + tag, e.getMessage());
		}

	}

	// FOR DEVELOPMENT PURPOSE ONLY
	public static String readJsonFile(Context context) {
		String json = "";

		InputStream is = null;
		ByteArrayOutputStream baos = null;

		try {
			is = new FileInputStream(context.getExternalFilesDir(null).getPath() + "/jobs-1.json");
			baos = new ByteArrayOutputStream();

			int read;
			while( (read = is.read()) != -1) {
				baos.write(read);
			}

			is.close();
			json = baos.toString();
			baos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//		writeFile("", context, json, "_read");
		return json;
	}

	public static Bitmap scaleBitmap(String path) {

		// First decode with inJustDecodeBounds=true to check dimensions
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		final int height = options.outHeight;
		final int width = options.outWidth;

		options.inSampleSize = calculateInSampleSize(options, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		// scaling in decodeFile doesn't really scale down to desired dimension
		return Bitmap.createScaledBitmap(bitmap, width/options.inSampleSize, height/options.inSampleSize, false);

	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static void copyInputStreamToFile( Context context, boolean isCache, InputStream in, String fileName ) {

			File file;
		if ( fileName.isEmpty() ) {
			fileName = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
		}

		if (isCache) {
			file = new File(context.getExternalFilesDir(null), fileName);
		} else {
			file = new File(context.getCacheDir(), fileName);
		}

		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;

			while((len=in.read(buf))>0) {
				out.write(buf,0,len);
			}

			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
