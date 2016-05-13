package com.zyb.example.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.zyb.example.model.Const;
import com.zyb.example.model.IAlertDialogButtonListener;
import com.zyb.example.musicplayer.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Util {
	
	private static AlertDialog mAlertDialog;

	public static View getView(Context context, int layoutId)
	{
		LayoutInflater inflater = (LayoutInflater) 
				context.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId,null);
		return layout;
		
	}
	
	/**
	 * 界面跳转
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context,Class desti){
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent);
		
		//关闭当前的Activity
		((Activity)context).finish();
	}
	
	/**
	 * 显示自定义对话框
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void showDialog(final Context context,String message,final IAlertDialogButtonListener listener){
		View dialogView  = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Theme_Transparent);
	
		dialogView = getView(context,R.layout.dialog_view);
		ImageButton btnOkView  = (ImageButton) dialogView.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCancelView  = (ImageButton) dialogView.findViewById(R.id.btn_dialog_cancel);
		TextView txtMessageView  = (TextView) dialogView.findViewById(R.id.text_dialog_message);
	
		txtMessageView.setText(message);
		btnOkView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// 关闭对话框
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}
				//事件回调
				if(listener != null){
				listener.onClick();
			}
				
				//播放音效
				MyMediaPlayer.playTone(context, MyMediaPlayer.INDEX_STONE_ENTER);
			}
		});
		
		btnCancelView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// 关闭对话框
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}
				
				//播放音效
				MyMediaPlayer.playTone(context, MyMediaPlayer.INDEX_STONE_CANCEL);
				
			}
			
		});
		//为dialog设置view
		builder.setView(dialogView);
		mAlertDialog = builder.create();
		
		//显示对话框
		mAlertDialog.show();
	}
	
	/**
	 * 游戏数据的保存
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static void saveData(Context context,int stageIndex,int coins,int isallpass){
		FileOutputStream fileStream = null;
		try {
			fileStream = context.openFileOutput(Const.FILE_NAME_SAVE_DATA,context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fileStream);
			
			//写数据
			dos.writeInt(stageIndex);
			dos.writeInt(coins);
			dos.writeInt(isallpass);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fileStream != null){
				try {
					fileStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static int[] loadData(Context context){
		FileInputStream fils = null;
		int[] datas={-1,Const.TOTAL_COINS,0};//初始化数据
		
		try {
			fils = context.openFileInput(Const.FILE_NAME_SAVE_DATA);
			DataInputStream dis = new DataInputStream(fils);
			
			datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
			datas[Const.INDEX_LOAD_DATA_COINS] = dis.readInt();
			datas[Const.INDEX_LOAD_DATA_PASS] = dis.readInt();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fils != null){
				try {
					fils.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return datas;
		
	}
}
