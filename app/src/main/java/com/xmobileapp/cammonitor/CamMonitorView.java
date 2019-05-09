package com.xmobileapp.cammonitor;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xmobileapp.cammonitor.config.CamMonitorParameter;
import com.xmobileapp.cammonitor.core.CameraSource;
import com.xmobileapp.cammonitor.core.SocketCamera;
import com.xmobileapp.cammonitor.util.ActivtyUtil;

public class CamMonitorView extends SurfaceView implements SurfaceHolder.Callback {

	/** The thread that actually draws the image stream */
    private CamMonitorThread thread;
    
    public static final String TAG = "CamMonitorView";
    
    private CamMonitorParameter cmPara;
	/**
     * The constructor called from the main ServerAct activity
     * 
     * @param context 
     * @param attrs 
     */
	public CamMonitorView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        thread = new CamMonitorThread(holder);
        
        Log.d(TAG, "@@@ done creating view!");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.closeCameraSource();
		
		 boolean retry = true;
	     while (retry) {
	    	 try {
	    		 thread.join();
	             retry = false;

	         } catch (InterruptedException e) {
	         }
	    }
	}

	class CamMonitorThread extends Thread{
		
		/** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;
        
        /**
         * Current height of the surface/canvas.
         * 
         * @see #setSurfaceSize
         */
        private int mCanvasHeight = 1;

        /**
         * Current width of the surface/canvas.
         * 
         * @see #setSurfaceSize
         */
        private int mCanvasWidth = 1;
        
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
        
        private CameraSource cs;
        
        private Canvas c = null;
  
		public CamMonitorThread(SurfaceHolder surfaceHolder) {
			super();
			mSurfaceHolder = surfaceHolder;
		}

		/**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         * 
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;

            if (mRun == false) {
                
            }
        }

		/**
         * the heart of the worker bee
         */
        public void run() {
        	
        	// while running do stuff in this loop...bzzz!
            while (mRun) {
                
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    // synchronized (mSurfaceHolder) {
//                    captureImage(cmPara.getIp(), cmPara.getPort());
                    captureImage(cmPara.getIp(), cmPara.getPort(), mCanvasWidth, mCanvasHeight);
                    // }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                        c = null;
                    }
                }// end finally block
                
            }
        }

		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                
            }
		}
		
//		private boolean captureImage(String ip, int port){
//			
//			cs = new SocketCamera(ip, port, c.getWidth(), c.getHeight(), true);
//	        if (!cs.open()) { /* deal with failure to obtain camera */ 
//	        	ActivtyUtil.showAlert(CamMonitorView.this.getContext(), "Error", "不能连接远端服务器！", "确定");
//	        	return false;
//	        }
//	        cs.capture(c); //capture the frame onto the canvas
//	        
//	        return true;
//		}
	
		private boolean captureImage(String ip, int port, int width, int height){
			
			cs = new SocketCamera(ip, port, width, height, true);
	        if (!cs.open()) { /* deal with failure to obtain camera */ 
	        	ActivtyUtil.showAlert(CamMonitorView.this.getContext(), "Error", "不能连接远端服务器！", "确定");
	        	return false;
	        }
	        cs.capture(c); //capture the frame onto the canvas
	        
	        return true;
		}
		
		public boolean saveImage(){
			
			String now = String.valueOf(System.currentTimeMillis());
			if(cs == null){
				return false;
			}
			cs.saveImage(cmPara.getLocal_dir(), now+".JPEG");
			
			return true;
		}
		
		public void closeCameraSource(){
			
			cs.close();
	    }

		public CameraSource getCameraSource() {
			return cs;
		}
		
		
	}

	public CamMonitorParameter getCmPara() {
		return cmPara;
	}

	public void setCmPara(CamMonitorParameter cmPara) {
		this.cmPara = cmPara;
	}
	
	public void setRunning(boolean b) {
        this.thread.setRunning(b);
    }

	public CamMonitorThread getThread() {
		return thread;
	}
	
	
	

}
