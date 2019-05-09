package com.xmobileapp.cammonitor.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class ActivtyUtil {

	  public static void showAlert(Context context,CharSequence title,CharSequence message,CharSequence btnTitle){
	    	new AlertDialog.Builder(context).setTitle(title)
	    	.setMessage(message).setPositiveButton(btnTitle, new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					
				}
	    		
	    	}).show();
	    }
	    public static void openToast(Context context,String str){
	    	Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	    }
}
