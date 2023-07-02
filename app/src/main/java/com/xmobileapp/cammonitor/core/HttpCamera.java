package com.xmobileapp.cammonitor.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * A CameraSource implementation that obtains its bitmaps via an HTTP request
 * to a URL.
 * 
 * @author Tom Gibara
 *
 */

public class HttpCamera implements CameraSource {

	
	private static final int CONNECT_TIMEOUT = 1000;
	private static final int SOCKET_TIMEOUT = 1000;
	
	private final String url;
	private final Rect bounds;
	private final boolean preserveAspectRatio;
	private final Paint paint = new Paint();

	public HttpCamera(String url, int width, int height, boolean preserveAspectRatio) {
		this.url = url;
		bounds = new Rect(0, 0, width, height);
		this.preserveAspectRatio = preserveAspectRatio;
		
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
	}
	
	public int getWidth() {
		return bounds.right;
	}
	
	public int getHeight() {
		return bounds.bottom;
	}
	
	public boolean open() {
		/* nothing to do */
		return true;
	}

	public boolean capture(Canvas canvas) {
		if (canvas == null) throw new IllegalArgumentException("null canvas");
		try {
			Bitmap bitmap = null;
			InputStream in = null;
			int response = -1;
			try {
				//we use URLConnection because it's anticipated that it is lighter-weight than HttpClient
				//NOTE: At this time, neither properly support connect timeouts
				//as a consequence, this implementation will hang on a failure to connect
				URL url = new URL(this.url);
				URLConnection conn = url.openConnection();
				if (!(conn instanceof HttpURLConnection)) throw new IOException("Not an HTTP connection.");
				HttpURLConnection httpConn = (HttpURLConnection) conn;
				httpConn.setAllowUserInteraction(false);
				httpConn.setConnectTimeout(CONNECT_TIMEOUT);
				httpConn.setReadTimeout(SOCKET_TIMEOUT);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();
				response = httpConn.getResponseCode();
				if (response == HttpURLConnection.HTTP_OK) {
					in = httpConn.getInputStream();
					bitmap = BitmapFactory.decodeStream(in);
				}
			} finally {
				if (in != null) try {
					in.close();
				} catch (IOException e) {
					/* ignore */
				}
			}
			
			if (bitmap == null) throw new IOException("Response Code: " + response);

			//render it to canvas, scaling if necessary
			if (
					bounds.right == bitmap.getWidth() &&
					bounds.bottom == bitmap.getHeight()) {
				canvas.drawBitmap(bitmap, 0, 0, null);
			} else {
				Rect dest;
				if (preserveAspectRatio) {
					dest = new Rect(bounds);
					dest.bottom = bitmap.getHeight() * bounds.right / bitmap.getWidth();
					dest.offset(0, (bounds.bottom - dest.bottom)/2);
				} else {
					dest = bounds;
				}
				canvas.drawBitmap(bitmap, null, dest, paint);
			}

		} catch (RuntimeException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} catch (IOException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		}
		return true;
	}

	public void close() {
		/* nothing to do */
	}

	public boolean saveImage(String savePath, String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

}
