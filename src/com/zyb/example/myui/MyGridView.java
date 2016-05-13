package com.zyb.example.myui;

import java.util.ArrayList;
import java.util.List;

import com.zyb.example.model.IWordButtonClickListener;
import com.zyb.example.model.WordButton;
import com.zyb.example.musicplayer.R;
import com.zyb.example.util.Util;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView {

	private List<WordButton> mList = new ArrayList<WordButton>();

	private MyGridAdapter mAdapter ;
	private Context mcontext;
	
	private Animation mScaleAnimation;
	
	private IWordButtonClickListener mWordButtonListener;
	

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mcontext = context;
		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
		
		
	}
	
	public void updateData(ArrayList<WordButton> list)
	{
		mList = list;
		
		//重新设置数据源
		setAdapter(mAdapter);
	}

	class MyGridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup p) {
			 final WordButton holder;
			if(v == null)
			{
				
				v=Util.getView(mcontext,R.layout.self_ui_gridview_item);
			
				holder = mList.get(position);
				
				//加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mcontext, R.anim.scale);
				//设置动画的延迟时间
				mScaleAnimation.setStartOffset(position*100);
				
				
				holder.mIndex=position;
				holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
			
				holder.mViewButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						mWordButtonListener.onWordButtonClick(holder);
						
					}
					
				});
				v.setTag(holder);
			}else
			{
				holder = (WordButton) v.getTag();
			}
			
			holder.mViewButton.setText(holder.mWordString);
			
			//播放动画
			v.startAnimation(mScaleAnimation);
			return v;
		}
		
	}
	
	/**
	 * 注册监听接口
	 * @param listener
	 */
	public void registOnWordButtonClick(IWordButtonClickListener listener){
		mWordButtonListener = listener;
	}
}
