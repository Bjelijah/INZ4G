package com.inz.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.inz.inz4g.R;


//import android.view.View.OnClickListener;


public class AlerDialogUtils {


	public static void postDialogMsg(Context context,String title,String msg,OnClickListener ob){
		Builder builder = new Builder(context);
		builder.setTitle(title).setMessage(msg)
				.setPositiveButton(context.getResources().getString(R.string.ok),ob)
				.create()
				.show();
	}

	public static void postDialogMsg(Context context,String title,String msg,OnClickListener positiveBtn,OnClickListener negativeBtn){
		Builder builder = new Builder(context);
		builder.setTitle(title).setMessage(msg)
				.setPositiveButton(context.getResources().getString(R.string.ok),positiveBtn)
				.setNegativeButton(context.getResources().getString(R.string.cancel),negativeBtn)
				.create()
				.show();
	}

	public static void postDialogMsg(Context context,String title,String msg,String pos,String neg,OnClickListener posBtn,OnClickListener negBtn){
		Builder builder = new Builder(context);
		builder.setTitle(title).setMessage(msg)
				.setPositiveButton(pos,posBtn)
				.setNegativeButton(neg,negBtn)
				.create()
				.show();
	}



}
