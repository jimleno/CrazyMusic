package com.zyb.example.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.zyb.example.model.Const;
import com.zyb.example.model.IAlertDialogButtonListener;
import com.zyb.example.model.IWordButtonClickListener;
import com.zyb.example.model.Song;
import com.zyb.example.model.WordButton;
import com.zyb.example.musicplayer.R;
import com.zyb.example.myui.MyGridView;
import com.zyb.example.util.ExitApplication;
import com.zyb.example.util.MyMediaPlayer;
import com.zyb.example.util.Mylog;
import com.zyb.example.util.Util;

import android.support.v7.app.ActionBarActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements IWordButtonClickListener {

	public final static String TAG = "TAG";
	
	/**答案状态--正确 */
	public final static int STATUS_ANSWER_RIGHT=1;
	/**答案状态--错误 */
	public final static int STATUS_ANSWER_WRONG=0;
	/**答案状态--不完整 */
	public final static int STATUS_ANSWER_LACK=-1;
	
	//闪烁次数
	public final static int SPATH_TIMES = 6;
	
	public final static int ID_DIALOG_DELETE_WORD = -1;
	public final static int ID_DIALOG_TIP_ANSWER = 0;
	public final static int ID_DIALOG_LACK_COINS = 1;
	
	public final static int COUNTS_WORDS = 24;
	//唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	//过关界面
	private View mPassView;
	
	//返回按钮
	private ImageButton mbtn_goback;
		
	//Play 按键事件
	private ImageButton mBtnPlayStart;
	
	private ImageView mViewPan;
	private ImageView mViewPanBar;
	
	//当前关的索引
	private TextView mCurrentStagePassView;

	private TextView mCurrentStageView;
	
	//当前歌曲的名称
	private TextView mCurrentSongNamePassView;
	
	//设置播放状态
	private boolean mIsRunning;
	
	//文字框容器
	private ArrayList<WordButton> mAllWords;
	
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	//已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	
	//当前的歌曲
	private Song mCurrentSong;
	
	//当前的索引
	private int mCurrentStageindex = -1;
	
	//是否通关，1代表有，0代表没有
	private int mallpass = 0;
	
	//当前金币的数量
	private int mCurrentCoins = Const.TOTAL_COINS;
	
	//金币的VIEW
	private TextView mViewCurrentCoins;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		//注册Activity,方便退出应用时回收
		ExitApplication.getInstance().addActivity(this);
		
		//读取游戏数据
		int[] datas = Util.loadData(MainActivity.this);
		mCurrentStageindex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
		mallpass = datas[Const.INDEX_LOAD_DATA_PASS];
		
		//初始化控件
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		mbtn_goback = (ImageButton) findViewById(R.id.btn_bar_back);
		
		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins+"");
		
		//注册监听
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
		
		//初始化动画
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// 开启拨杆进入动画
				mViewPanBar.startAnimation(mBarOutAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);//播放完毕保持播放状态
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//动画播放完毕
				mViewPan.startAnimation(mPanAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);//播放完毕保持播放状态
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		//初始化播放按钮，设置按钮监听事件
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				handlePlayButton();
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_LONG).show();
			}
			
		});
		
		//初始化返回按钮，设置监听事件
		mbtn_goback.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// 当点击按钮后返回到详情界面
				Util.startActivity(MainActivity.this,GoBackView.class);
				
			}
			
		});
		
		//初始化数据
		initCurrentStageData();
		
		//处理删除按键事件
		handleDeleteWord();
		
		
		//处理提示按键事件
		handleTipAnswer();
		
		
		
	}
	
	

	/**
	 * 处理盘片中间的播放按钮，就是开始播放音乐
	 */
	private void handlePlayButton(){
		if(mViewPanBar != null)
		{
			if(!mIsRunning){
				mIsRunning = true;
				
				//开始拨杆进入动画
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);
				
				//开始播放音乐
				MyMediaPlayer.playSong(MainActivity.this,mCurrentSong.getSongFileName());
				mCurrentSong.getSongName();
				
			}
		}
		
	}
	
/**
 * 当点击系统返回按钮时退出应用，并提示用户
 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Util.showDialog(MainActivity.this,"您确定退出应用吗？",mBtnOkGetoutListener);
		
	}


	//自定义AlertDialog事件响应
	private IAlertDialogButtonListener mBtnOkGetoutListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			ExitApplication.getInstance().exit();
			
		}
		
	};
	
	/**
	 * 保存游戏数据，防止手机掉电导致数据丢失
	 */
	@Override
	protected void onPause() {
		//保存游戏数据
		Util.saveData(MainActivity.this,mCurrentStageindex -1,mCurrentCoins,mallpass);
		
		mViewPan.clearAnimation();//盘片动画停止
		
		//停止音乐
		MyMediaPlayer.stopTheSong(MainActivity.this);
		super.onPause();
	}
	private Song loadStageSongInfo(int stageindex)
	{
		Song song = new Song();
		
		String[] stage = Const.SONG_INFO[stageindex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		return song;
		
	}
	/**
	 * 加载当前关的数据
	 */
	private void initCurrentStageData()
	{
		//读取当前关的歌曲信息
		mCurrentSong = loadStageSongInfo(++mCurrentStageindex);
		//初始化已选择框
		mBtnSelectWords = initWordSelect();
		
		LayoutParams params = new LayoutParams(140,140);
		
		//一开始就播放音乐
		handlePlayButton();
		
		//清空原来的答案
	    mViewWordsContainer.removeAllViews();
				
		//增加新的答案框		
		for(int i=0;i<mBtnSelectWords.size();i++){
			mViewWordsContainer.addView(
					mBtnSelectWords.get(i).mViewButton,params);
		}
		
		//显示当前关的索引
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		
		if(mCurrentStageView != null){
			mCurrentStageView.setText((mCurrentStageindex + 1)+"");
		}
		
		//获得数据
		mAllWords = initAllWord();
		//更新数据-MyGridView
		mMyGridView.updateData(mAllWords);
	}
	
	/**
	 *初始化待选文字框 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord(){
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		//获得所有待选文字
		String[] words = generateWords();
		
		for(int i=0;i<COUNTS_WORDS;i++){
			WordButton button = new WordButton();
			button.mWordString = words[i];
			data.add(button);
		}
		
		return data;
		
	}
	
	/**
	 * 初始化已选文字框
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect(){
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		for(int i = 0;i<mCurrentSong.getNameLength();i++)
		{
			View view = Util.getView(MainActivity.this,R.layout.self_ui_gridview_item);
		
			final WordButton holder = new WordButton();
			
			holder.mViewButton = (Button)view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable=false;
					
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					clearTheAnswer(holder);
					
				}
				
			});
			data.add(holder);
		}
		
		
		return data;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onWordButtonClick(WordButton wordbutton) {
		setSelectWord(wordbutton);
		//获得答案状态
		int checkResult = checkTheAnswer();
		//检查答案
		if(checkResult==STATUS_ANSWER_RIGHT){
			//过关并获得奖励
			handlePassEvent();
		}else if(checkResult==STATUS_ANSWER_WRONG){
			//闪烁文字提示用户
			sparkWords();
		}else if(checkResult==STATUS_ANSWER_LACK){
			//重置所有已选框文字颜色,设置颜色为白色(Normal)
			for(int i=0;i<mBtnSelectWords.size();i++){
				mBtnSelectWords.get(i).mViewButton.setTextColor
				(Color.WHITE);
			}
		}
		
	}
	
	/**
	 * 处理所有的过关界面及事件
	 */
	private void handlePassEvent(){
		//显示过关界面
		mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		
		//增加用户的金币数量
		handleCoins(getPassEventCoins());
		
		//停止未完成的动画
		mViewPan.clearAnimation();
		
		//停止正在播放的音乐
		MyMediaPlayer.stopTheSong(MainActivity.this);
		
		//播放音效
		MyMediaPlayer.playTone(MainActivity.this,MyMediaPlayer.INDEX_STONE_COIN);
		
		//当前关的索引
		mCurrentStagePassView = (TextView) 
				findViewById(R.id.text_current_stage_pass);
		if(mCurrentStagePassView!=null){
			mCurrentStagePassView.setText((mCurrentStageindex+1)+"");
		}
		//显示歌曲名称
		mCurrentSongNamePassView =(TextView) 
				findViewById(R.id.text_current_song_name_pass);
		if(mCurrentSongNamePassView!=null){
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		//下一关按键处理
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(judgeAppPassed()){
					//保存游戏数据，设置通关属性为1
					Util.saveData(MainActivity.this, mCurrentStageindex, mCurrentCoins,1);
				// 进入通关界面
				Util.startActivity(MainActivity.this, AppPassView.class);
				
					
			}else{
				//开始下一关
				mPassView.setVisibility(View.GONE);
				
				//加载关卡数据
				initCurrentStageData();
			}
			
			}
			
		});
	}
	
	/**
	 * 判断是否通关
	 * @return
	 */
	private boolean judgeAppPassed(){
		return (mCurrentStageindex == Const.SONG_INFO.length -1);
		
	}
	/**
	 * 清除已选框答案
	 * @param wordbutton
	 */
	private void clearTheAnswer(WordButton wordbutton){
		wordbutton.mViewButton.setText("");
		wordbutton.mWordString="";
		wordbutton.mIsVisiable=false;
		
		
		
		//设置待选框
		setButtonVisiable(mAllWords.get(wordbutton.mIndex), View.VISIBLE);
	}
	
	/**
	 * 设置答案
	 * @param wordbutton
	 */
	private void setSelectWord(WordButton wordbutton)
	{
		for(int i=0;i<mBtnSelectWords.size();i++){
			if(mBtnSelectWords.get(i).mWordString.length()==0){
				//设置答案文字框的内容及可见性
				mBtnSelectWords.get(i).mViewButton.setText(wordbutton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable=true;
				mBtnSelectWords.get(i).mWordString = wordbutton.mWordString;
				//记录索引
				mBtnSelectWords.get(i).mIndex=wordbutton.mIndex;
			
				if(wordbutton.mIndex == 0){
					mAllWords.get(0).mViewButton.setText("111");
				}
				//Log.i(TAG, wordbutton.mIndex+"");
				//Log.i("TAG",mBtnSelectWords.get(i).mIndex+"");
				//Mylog.i(TAG,mBtnSelectWords.get(i).mIndex+"");
				
				//设置待选框可见性
				setButtonVisiable(wordbutton,View.INVISIBLE);
				break;
			}
		}
	}
	/**
	 * 设置待选文字框是否可见
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button,int visibility){
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable=(visibility == View.VISIBLE)?true:false;
	
		//Log
		//Mylog.i(TAG, button.mIsVisiable+"");
	
	}
	/**
	 * 生成所有的待选文字
	 */
	private String[] generateWords(){
		Random random = new Random();
		String[] words = new String[COUNTS_WORDS];
		
		//存入歌名
		for(int i = 0;i<mCurrentSong.getNameLength();i++){
			words[i] = mCurrentSong.getNameCharacters()[i]+"";
		}
		//获取随机文字存入数组
		for(int i = mCurrentSong.getNameLength();i<COUNTS_WORDS;i++){
			words[i] = getRandomChar()+"";
		}
		
		//打乱文字顺序,首先从所有的元素中随机选取一个与第一个交换
		//然后在第二个之后选择一个与第二个交换，直到最后一个
		for(int i=COUNTS_WORDS-1;i>=0;i--){
			int index = random.nextInt(i+1);
			String buf = words[index];
			words[index] = words[i];
			words[i]=buf;
			
		}
		
		
		
		return words;
	}
	
	/**
	 * 生成随机汉子
	 * @return
	 */
	private char getRandomChar(){
		String str = "";
		int hightPos;
		int lowPos;
		
		Random random = new Random();
		
		hightPos = (176+Math.abs(random.nextInt(39)));
		lowPos = (161+Math.abs(random.nextInt(93)));
		
		byte[] b = new byte[2];
		b[0]=(Integer.valueOf(hightPos)).byteValue();
		b[1]=(Integer.valueOf(lowPos)).byteValue();
		
		try {
			str = new String(b,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str.charAt(0);
	}
	
	/**
	 * 检查答案
	 * @return
	 */
	private int checkTheAnswer(){
		//先检查长度
		for(int i=0;i<mBtnSelectWords.size();i++){
			//如果有空的，说明答案不完整
			if(mBtnSelectWords.get(i).mWordString.length()==0){
				return STATUS_ANSWER_LACK;
			}
		}
		//答案完整，继续检查正确性
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<mBtnSelectWords.size();i++){
			sb.append(mBtnSelectWords.get(i).mWordString);
		}
		return (sb.toString().equals(mCurrentSong.getSongName()))?STATUS_ANSWER_RIGHT:STATUS_ANSWER_WRONG;
		
	}
	
	/**
	 * 闪烁文字
	 */
	private void sparkWords(){
		//声明定时器
		TimerTask task = new TimerTask(){

			boolean mChange = false;
			int mSparkTimes = 0;
			@Override
			public void run() {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
				if(++mSparkTimes>SPATH_TIMES){
					return;
				}
				//执行闪烁逻辑，交替显示红色和白色变化
				for(int i=0;i<mBtnSelectWords.size();i++){
					mBtnSelectWords.get(i).mViewButton.setTextColor
					(mChange?Color.RED:Color.WHITE);
				}
					mChange = !mChange;	
					}
					
				});{
					
				}
				
			}
	};
	Timer timer = new Timer();
	timer.schedule(task, 1,150);
	}
	
	/**
	 * 自动选择一个答案
	 */
	private void tipAnswer(){
		
		
		boolean tipWord = false;
		//减少金币数量
		if(!handleCoins(-getTipCoins())){
			//金币不够,显示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		
		for(int i=0;i<mBtnSelectWords.size();i++){
			if(mBtnSelectWords.get(i).mWordString.length()==0){
				
				//根据当前的答案框条件选择对应的文字并填入
				onWordButtonClick(findIsAnswerWord(i));
				tipWord = true;
				
				break;
			}
		}
		//没有找到可以填充的答案
		if(!tipWord){
			//闪烁文字提示用户
			sparkWords();
		}
		
	}
	
	/**
	 * 删除文字
	 */
	private void deleteOneWord(){
		//减少金币
		if(!handleCoins(-getDeleteWordCoins())){
			//金币不够，显示提示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		//将这个索引对应的WordButton设置为不可见
		setButtonVisiable(findNotAnswerWord(),View.INVISIBLE);
		
	}
	
	/**
	 * 找到一个不是答案的文字，并且当前是可见的
	 * @return
	 */
	private WordButton findNotAnswerWord(){
		Random random = new Random();
		WordButton buf = null;
		
		while(true){
			int index = random.nextInt(COUNTS_WORDS);
			
			buf = mAllWords.get(index);
			if(buf.mIsVisiable&&isTheAnswerWord(buf)==false){
				return buf;
			}
		}
	}
	
	/**
	 * 找到一个是答案的文字
	 * index 当前要填入答案框的索引
	 * @return
	 */
	private WordButton findIsAnswerWord(int index){
		WordButton buf = null;
		for(int i=0;i<COUNTS_WORDS;i++){
			buf = mAllWords.get(i);
			if(buf.mWordString.equals(""+mCurrentSong.getNameCharacters()[index])){
				
				return buf;
			}
		}
		
		
		return null;
		
	}
	
	/**
	 * 判断某个文字是否为答案
	 * @param button
	 * @return
	 */
	private boolean isTheAnswerWord(WordButton word){
		boolean result = false;
		
		for(int i=0;i<mCurrentSong.getNameLength();i++){
			if(word.mWordString.equals(""+
		mCurrentSong.getNameCharacters()[i])){
			result = true;
			break;
			}
		}
		return result;
	}
	
	/**
	 * 增加或者减少指定数量的金币
	 * @param data
	 * @return true 增加、减少成功，false 失败
	 */
	private boolean handleCoins(int data){
		//判断当前总的金币数量是否可被减少
		if(mCurrentCoins + data>=0){
			mCurrentCoins+=data;
			mViewCurrentCoins.setText(mCurrentCoins+"");
			
			return true;
		}else{
			//金币不够
			return false;
		}
		
	}
	
	/**
	 * 从配置文件中读取删除操作要的金币数
	 * @return
	 */
	private int getDeleteWordCoins(){
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}
	

	/**
	 * 从配置文件中读取提示操作要的金币数
	 * @return
	 */
	private int getTipCoins(){
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * 从配置文件中读取奖励的金币数
	 * @return
	 */
	private int getPassEventCoins(){
		return this.getResources().getInteger(R.integer.get_passevent_coins);
	}
	
	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord(){
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
	
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// deleteOneWord();
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
				
			}
			
		});
	
	}
	
	/**
	 * 处理提示按键事件
	 */
	private void handleTipAnswer(){
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// tipAnswer();
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
				
			}
			
		});
	}
	
	//自定义AlertDialog事件响应
	//删除错误答案
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}
		
	};
	//答案提示
	private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// 执行事件
			tipAnswer();
		}
		
	};
	//金币不足
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// 执行事件
			
		}
		
	};
	
	
	/**
	 * 显示对话框
	 * @param id
	 */
	private void showConfirmDialog(int id){
		switch(id){
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this,"确认花掉"+getDeleteWordCoins()+"个金币去掉一个错误答案？",mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this,"确认花掉"+getTipCoins()+"个金币获得一个文字提示？",mBtnOkTipAnswerListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this,"金币不足，去商店补充？",mBtnOkLackCoinsListener);
			break;
		}
	}
}
