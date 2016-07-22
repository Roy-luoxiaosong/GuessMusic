package com.roy.gussmusic.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.roy.gussmusic.R;
import com.roy.gussmusic.data.Const;
import com.roy.gussmusic.model.IAlertDialogButtonListener;

public class Util {
	private static AlertDialog mAlertDialog;
	public static View getView(Context context,int layoutId){
		LayoutInflater inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, null);
		return layout;
		
	}
	public static void statrActivity(Context from,Class to){
		Intent intent = new Intent(from,to);
		from.startActivity(intent);
		
		((Activity)from).finish();
	}
	public static void showDialog(final Context context,String message,final IAlertDialogButtonListener listener){
		View dialogView = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view);
		
		ImageButton btnOkView = (ImageButton) dialogView.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCancleView = (ImageButton) dialogView.findViewById(R.id.btn_dialog_cancle);
		TextView textView =  (TextView) dialogView.findViewById(R.id.text_dialog_message);
		textView.setText(message);
		btnOkView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAlertDialog!=null){
					mAlertDialog.cancel();
				}
				if(listener!=null){
					listener.onClick();
				}
				//播放音效
				MyPlayer.palyTone(context, MyPlayer.INDEX_STONE_ENTER);
			}
		});
		btnCancleView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//播放音效
				MyPlayer.palyTone(context, MyPlayer.INDEX_STONE_CANCEL);
				if(mAlertDialog!=null){
					mAlertDialog.cancel();
				}
			}
		});
		builder.setView(dialogView);
		mAlertDialog = builder.create();
		mAlertDialog.show();
		}
	
	
	public static void saveData(Context context,int stageIndex,int coins){
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(Const.FILE_NAME_SAVE_DATA, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(stageIndex);
			dos.writeInt(coins);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public static int[] loadData(Context context){
		FileInputStream fis = null;
		int[] datas = {-1,Const.TOTAL_COINS};
		try {
			fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);
			DataInputStream dis = new DataInputStream(fis);
			datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
			datas[Const.INDEX_LOAD_DATA_COIN] = dis.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return datas;
	}
}
