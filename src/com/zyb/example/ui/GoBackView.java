package com.zyb.example.ui;

import com.zyb.example.model.Const;
import com.zyb.example.model.IAlertDialogButtonListener;
import com.zyb.example.musicplayer.R;
import com.zyb.example.util.ExitApplication;
import com.zyb.example.util.Util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * ���ؽ���
 * @author Administrator
 *
 */
public class GoBackView extends Activity {

	
	private ImageButton btn_goback_pan;
	private TextView mCurrentStageIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.goback_view);
		
		ExitApplication.getInstance().addActivity(this);
		
		//���õ�ǰ�ؿ�������
		mCurrentStageIndex = (TextView) findViewById(R.id.goback_current_stage);
		
		int datas[] = Util.loadData(GoBackView.this);
		int stageindex = datas[Const.INDEX_LOAD_DATA_STAGE];
		
		if(mCurrentStageIndex != null){
			mCurrentStageIndex.setText((stageindex + 2)+"");
		}
		
		btn_goback_pan = (ImageButton) findViewById(R.id.goback_pan_button);
		btn_goback_pan.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// �����ť�󷵻�������
				Util.startActivity(GoBackView.this, MainActivity.class);
				
			}
			
		});
		
		
		
								
	}






	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Util.showDialog(GoBackView.this,"��ȷ���˳�Ӧ����",mBtnOkGetoutListener);
		
	}


	//�Զ���AlertDialog�¼���Ӧ
	private IAlertDialogButtonListener mBtnOkGetoutListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			ExitApplication.getInstance().exit();
			
		}
		
	};
}
