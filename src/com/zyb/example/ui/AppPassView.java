package com.zyb.example.ui;

import com.zyb.example.model.IAlertDialogButtonListener;
import com.zyb.example.musicplayer.R;
import com.zyb.example.util.ExitApplication;
import com.zyb.example.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * 通关界面
 * @author Administrator
 *
 */
public class AppPassView extends Activity {

	private FrameLayout view;
	private ImageButton btn_goback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.all_pass_view);
		
		ExitApplication.getInstance().addActivity(this);
		
		//隐藏右上角的金币按钮
		view = (FrameLayout) findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
		
		//初始化返回按钮
		btn_goback = (ImageButton) findViewById(R.id.btn_bar_back);
		
		btn_goback.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Util.startActivity(AppPassView.this,MainActivity.class);
				
			}
			
		}) ;
	
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Util.showDialog(AppPassView.this,"您确定退出应用吗？",mBtnOkGetoutListener);
		
	}

	//自定义AlertDialog事件响应
	private IAlertDialogButtonListener mBtnOkGetoutListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			ExitApplication.getInstance().exit();
			
		}
		
	};
	
}
