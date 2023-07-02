package com.xmobileapp.cammonitor.core;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * A CameraSource implementation that obtains its bitmaps via a TCP connection
 * to a remote host on a specified address/port.
 * 
 * @author Tom Gibara
 *
 */

public class SocketCamera implements CameraSource {

	
	private static final int SOCKET_TIMEOUT = 1000;
	
	private final String address;
	private final int port;
	private final Rect bounds;
	private final boolean preserveAspectRatio;
	private final Paint paint = new Paint();

	private Socket socket = null;
	
	public SocketCamera(String address, int port, int width, int height, boolean preserveAspectRatio) {
		this.address = address;
		this.port = port;
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
			
			socket = new Socket();
			socket.bind(null);
			socket.setSoTimeout(SOCKET_TIMEOUT);
			socket.connect(new InetSocketAddress(address, port), SOCKET_TIMEOUT);

			//obtain the bitmap
			InputStream in =socket.getInputStream();
		 
			/*
			int count=0;
			
	    	//???????????????
			File f = new File("/sdcard/1.bmp"); 
	    	FileOutputStream fileOS = null;
			fileOS = new FileOutputStream(f);
	        byte[] buffer = new byte[8192];
	        try{ 
	           int readlen=in.read(buffer);
	           count+=readlen;
	           while(readlen>0){ 
	        	   fileOS.write(buffer,0,readlen);
	   	           readlen=in.read(buffer);
	   	        count+=readlen;
	            }
	           buffer.clone();
	           fileOS.close();	           
	           
	         } catch (FileNotFoundException e) { 
	             e.printStackTrace(); 
	           } catch (IOException e){ 
	            e.printStackTrace(); 
	          }
	        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/1.bmp");
	        */
			
			
			int count=40000;//1450054;//31000;
			int index =0;
			byte[] buffer=new byte[count];
			byte[] buffer1=new byte[8092];

	        int readlen=in.read(buffer1);
	        System.arraycopy(buffer1, 0, buffer, index, readlen);
	        index+=readlen;
	        while(readlen>0){ 
	        	readlen=in.read(buffer1);
	        	if (readlen>0)
	        	{
		          System.arraycopy(buffer1, 0, buffer, index, readlen);
		          index+=readlen;
	        	}
		    }
			
			//Bitmap bitmap = BitmapFactory.decodeStream(in);
			Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, count);
			
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
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				/* ignore */
			}	
		}
		return true;
	}

	public void close() {
		/* nothing to do */
	}

	public boolean saveImage(String savePath, String fileName) {

		//obtain the bitmap
		try {
			InputStream in = socket.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			FileOutputStream fos = new FileOutputStream(savePath + "/" + fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
		
		return true;
	}

}
