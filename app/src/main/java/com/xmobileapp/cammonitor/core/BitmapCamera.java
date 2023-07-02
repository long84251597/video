package com.xmobileapp.cammonitor.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * A CameraSource implementation that repeatedly captures a single bitmap.
 * 
 * @author Tom
 *
 */

public class BitmapCamera implements CameraSource {

	private final Bitmap bitmap;
	private final Rect bounds;
	private final Paint paint = new Paint();

	public BitmapCamera(Bitmap bitmap, int width, int height) {
		this.bitmap = bitmap;
		bounds = new Rect(0, 0, width, height);

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
		return true;
	}
	
	public boolean capture(Canvas canvas) {
		if (
				bounds.right == bitmap.getWidth() &&
				bounds.bottom == bitmap.getHeight()) {
			canvas.drawBitmap(bitmap, 0, 0, null);
		} else {
			canvas.drawBitmap(bitmap, null, bounds, paint);
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
