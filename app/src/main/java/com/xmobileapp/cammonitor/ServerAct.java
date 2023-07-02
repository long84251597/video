package com.xmobileapp.cammonitor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xmobileapp.cammonitor.CamMonitorView.CamMonitorThread;
import com.xmobileapp.cammonitor.config.CamMonitorParameter;
import com.xmobileapp.cammonitor.util.ActivtyUtil;
import com.xmobileapp.cammonitor.util.DatabaseHelper;
import com.xmobileapp.cammonitor.util.MessageListener;

public class ServerAct extends Activity {
	
	public static String TAG = "ServerAct";
	
	private CamMonitorView cmView;
	private Button btnDownload;
	private TextView textMessage;
	private Button btnDisconnect;

	private CamMonitorParameter param;
	public static final int TRY_TIMES = 3;
	public int current_times = 1;

	private int userid;

	private ScrollView scrollView;

	public Handler handler = new Handler();

	public List<String> downloadList = new ArrayList<String>();
	
	public ProgressDialog	downloadProgressDialog = null;
	
//	private String local_dir="/sdcard/";
	
	public void onCreate(Bundle icicle) {
		try {
			super.onCreate(icicle);
			setContentView(R.layout.server);
			findView();
			setListener();
			fillData();
		} catch (Exception e) {
			Log.e(ServerAct.TAG, e.getMessage(), e);
			ActivtyUtil
					.showAlert(ServerAct.this, "Error", e.getMessage(), "确定");
		}
	}

	private void fillData() throws Exception{
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			userid = bundle.getInt("id");
			param = DatabaseHelper.query(ServerAct.this, userid);
			cmView.setCmPara(param);
		} else {
			throw new Exception("没有发现id");
		}
		appendMessage("system ready to connect to " + param.getIp());
		
	}

	private void findView() {
		btnDownload = (Button) findViewById(R.id.btn_save);
		textMessage = (TextView) findViewById(R.id.Message);
		scrollView = (ScrollView) findViewById(R.id.ScrollViewMessage);
		btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
		cmView = (CamMonitorView)findViewById(R.id.cmView);
		
	}
	
	private void setListener() {
		btnDownload.setOnClickListener(btnDownloadListener);
		btnDisconnect.setOnClickListener(btnDisConnect);
	}
	
	public View.OnClickListener btnDisConnect = new View.OnClickListener(){

		public void onClick(View v) {

			cmView.setRunning(false);
			
			finish();
		}
		
	};

	private View.OnClickListener btnDownloadListener = new View.OnClickListener() {

		public void onClick(View v) {

//			downloadProgressDialog = new ProgressDialog(ServerAct.this);
//			downloadProgressDialog.setMax(downloadList.size());
//			downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			downloadProgressDialog.show();
			DownloadThread downloadThread =new DownloadThread(ServerAct.this);
			downloadThread.start();
		}

	};
	
	MessageListener messageListener = new MessageListener() {
		
		public void appendMessage(final String message) {

			scrollView.post(new Runnable() {

				public void run() {
					textMessage.setText(textMessage.getText() + message);
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}

			});

		}

	};

	class DownloadThread extends Thread{
		ServerAct server = null;
    	public DownloadThread(ServerAct mAct){
    		server = mAct;
    	}
    	
		public void run() {
			
			CamMonitorThread cmt = cmView.getThread();
			if(cmt.saveImage()){
				ActivtyUtil.openToast(ServerAct.this, "保存成功！");
			}
			else{
				ActivtyUtil.openToast(ServerAct.this, "保存失败！");
			}
//			handler.post(new Runnable(){
//
//				public void run() {
//					if(downloadProgressDialog!=null){
//						downloadProgressDialog.dismiss();
//					}
//					ActivtyUtil.openToast(ServerAct.this, "下载完成");
//					try{
//						getData(handler);
//					}catch (Exception e) {
//						ActivtyUtil.openToast(ServerAct.this, e.getMessage());
//					}
////					thread.stop();
//				}
				
//			});
		}
    	
    	
    };

	

	public void appendMessage(final String message) {
		textMessage.setText(textMessage.getText() + message);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}

		});
	}

	public void showStatus(final String mstatus){
		handler.post(new Runnable(){

			public void run() {
				if("LogIn".equalsIgnoreCase(mstatus)){
					downloadProgressDialog.dismiss();
				}else{
					downloadProgressDialog.setMessage(  mstatus + "..." ); 
				}
				
			}
			
		});
	}

	
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

}
