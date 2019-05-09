package com.xmobileapp.cammonitor.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmobileapp.cammonitor.CamMonitorClient;
import com.xmobileapp.cammonitor.R;

/**
	*
	*实现带editext的AlertDialog
	*按照此例子，可以实现任意格式的AlertDialog
	*/
public class EditDialog extends AlertDialog implements DialogInterface.OnClickListener{

	 private String text = "";
	 private EditText edit;
	 private OnDataSetListener mCallback;
	 private LinearLayout layout;
	 /**
	  *构造一个回调接口
	  */
	public interface OnDataSetListener{
		void onDataSet(String text);
	}
	public EditDialog(Context context,String title,String value,OnDataSetListener callback) {
			super(context);
	        mCallback = callback;
	        TextView label = new TextView(context);
	        label.setText("hint");
	        edit = new EditText(context);
	        edit.setText(value);
	        edit.setPadding(30, 0, 0, 0);
	        layout = new LinearLayout(context);
	        layout.setOrientation(LinearLayout.VERTICAL);
	        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(200,70);
	        param2.leftMargin=30;
	        layout.addView(edit, param2);
	        setView(layout);
	        setTitle(title);
	        setButton(context.getText(R.string.btn_ok), this);
	        setButton2(context.getText(R.string.btn_cancle), (OnClickListener) null);
	}

	public void onClick(DialogInterface dialog, int which) {
		 text = edit.getText().toString();
	     Log.d(CamMonitorClient.TAG, "U click text=" + text);
	     if (mCallback != null)
	          mCallback.onDataSet(text);
		
	}

}
