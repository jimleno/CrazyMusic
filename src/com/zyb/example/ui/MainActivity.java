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
	
	/**��״̬--��ȷ */
	public final static int STATUS_ANSWER_RIGHT=1;
	/**��״̬--���� */
	public final static int STATUS_ANSWER_WRONG=0;
	/**��״̬--������ */
	public final static int STATUS_ANSWER_LACK=-1;
	
	//��˸����
	public final static int SPATH_TIMES = 6;
	
	public final static int ID_DIALOG_DELETE_WORD = -1;
	public final static int ID_DIALOG_TIP_ANSWER = 0;
	public final static int ID_DIALOG_LACK_COINS = 1;
	
	public final static int COUNTS_WORDS = 24;
	//��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	//���ؽ���
	private View mPassView;
	
	//���ذ�ť
	private ImageButton mbtn_goback;
		
	//Play �����¼�
	private ImageButton mBtnPlayStart;
	
	private ImageView mViewPan;
	private ImageView mViewPanBar;
	
	//��ǰ�ص�����
	private TextView mCurrentStagePassView;

	private TextView mCurrentStageView;
	
	//��ǰ����������
	private TextView mCurrentSongNamePassView;
	
	//���ò���״̬
	private boolean mIsRunning;
	
	//���ֿ�����
	private ArrayList<WordButton> mAllWords;
	
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	//��ѡ�����ֿ�UI����
	private LinearLayout mViewWordsContainer;
	
	//��ǰ�ĸ���
	private Song mCurrentSong;
	
	//��ǰ������
	private int mCurrentStageindex = -1;
	
	//�Ƿ�ͨ�أ�1�����У�0����û��
	private int mallpass = 0;
	
	//��ǰ��ҵ�����
	private int mCurrentCoins = Const.TOTAL_COINS;
	
	//��ҵ�VIEW
	private TextView mViewCurrentCoins;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		//ע��Activity,�����˳�Ӧ��ʱ����
		ExitApplication.getInstance().addActivity(this);
		
		//��ȡ��Ϸ����
		int[] datas = Util.loadData(MainActivity.this);
		mCurrentStageindex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
		mallpass = datas[Const.INDEX_LOAD_DATA_PASS];
		
		//��ʼ���ؼ�
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		mbtn_goback = (ImageButton) findViewById(R.id.btn_bar_back);
		
		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins+"");
		
		//ע�����
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
		
		//��ʼ������
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// �������˽��붯��
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
		mBarInAnim.setFillAfter(true);//������ϱ��ֲ���״̬
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//�����������
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
		mBarOutAnim.setFillAfter(true);//������ϱ��ֲ���״̬
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
		
		//��ʼ�����Ű�ť�����ð�ť�����¼�
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				handlePlayButton();
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_LONG).show();
			}
			
		});
		
		//��ʼ�����ذ�ť�����ü����¼�
		mbtn_goback.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// �������ť�󷵻ص��������
				Util.startActivity(MainActivity.this,GoBackView.class);
				
			}
			
		});
		
		//��ʼ������
		initCurrentStageData();
		
		//����ɾ�������¼�
		handleDeleteWord();
		
		
		//������ʾ�����¼�
		handleTipAnswer();
		
		
		
	}
	
	

	/**
	 * ������Ƭ�м�Ĳ��Ű�ť�����ǿ�ʼ��������
	 */
	private void handlePlayButton(){
		if(mViewPanBar != null)
		{
			if(!mIsRunning){
				mIsRunning = true;
				
				//��ʼ���˽��붯��
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);
				
				//��ʼ��������
				MyMediaPlayer.playSong(MainActivity.this,mCurrentSong.getSongFileName());
				mCurrentSong.getSongName();
				
			}
		}
		
	}
	
/**
 * �����ϵͳ���ذ�ťʱ�˳�Ӧ�ã�����ʾ�û�
 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Util.showDialog(MainActivity.this,"��ȷ���˳�Ӧ����",mBtnOkGetoutListener);
		
	}


	//�Զ���AlertDialog�¼���Ӧ
	private IAlertDialogButtonListener mBtnOkGetoutListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			ExitApplication.getInstance().exit();
			
		}
		
	};
	
	/**
	 * ������Ϸ���ݣ���ֹ�ֻ����絼�����ݶ�ʧ
	 */
	@Override
	protected void onPause() {
		//������Ϸ����
		Util.saveData(MainActivity.this,mCurrentStageindex -1,mCurrentCoins,mallpass);
		
		mViewPan.clearAnimation();//��Ƭ����ֹͣ
		
		//ֹͣ����
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
	 * ���ص�ǰ�ص�����
	 */
	private void initCurrentStageData()
	{
		//��ȡ��ǰ�صĸ�����Ϣ
		mCurrentSong = loadStageSongInfo(++mCurrentStageindex);
		//��ʼ����ѡ���
		mBtnSelectWords = initWordSelect();
		
		LayoutParams params = new LayoutParams(140,140);
		
		//һ��ʼ�Ͳ�������
		handlePlayButton();
		
		//���ԭ���Ĵ�
	    mViewWordsContainer.removeAllViews();
				
		//�����µĴ𰸿�		
		for(int i=0;i<mBtnSelectWords.size();i++){
			mViewWordsContainer.addView(
					mBtnSelectWords.get(i).mViewButton,params);
		}
		
		//��ʾ��ǰ�ص�����
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		
		if(mCurrentStageView != null){
			mCurrentStageView.setText((mCurrentStageindex + 1)+"");
		}
		
		//�������
		mAllWords = initAllWord();
		//��������-MyGridView
		mMyGridView.updateData(mAllWords);
	}
	
	/**
	 *��ʼ����ѡ���ֿ� 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord(){
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		//������д�ѡ����
		String[] words = generateWords();
		
		for(int i=0;i<COUNTS_WORDS;i++){
			WordButton button = new WordButton();
			button.mWordString = words[i];
			data.add(button);
		}
		
		return data;
		
	}
	
	/**
	 * ��ʼ����ѡ���ֿ�
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
		//��ô�״̬
		int checkResult = checkTheAnswer();
		//����
		if(checkResult==STATUS_ANSWER_RIGHT){
			//���ز���ý���
			handlePassEvent();
		}else if(checkResult==STATUS_ANSWER_WRONG){
			//��˸������ʾ�û�
			sparkWords();
		}else if(checkResult==STATUS_ANSWER_LACK){
			//����������ѡ��������ɫ,������ɫΪ��ɫ(Normal)
			for(int i=0;i<mBtnSelectWords.size();i++){
				mBtnSelectWords.get(i).mViewButton.setTextColor
				(Color.WHITE);
			}
		}
		
	}
	
	/**
	 * �������еĹ��ؽ��漰�¼�
	 */
	private void handlePassEvent(){
		//��ʾ���ؽ���
		mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		
		//�����û��Ľ������
		handleCoins(getPassEventCoins());
		
		//ֹͣδ��ɵĶ���
		mViewPan.clearAnimation();
		
		//ֹͣ���ڲ��ŵ�����
		MyMediaPlayer.stopTheSong(MainActivity.this);
		
		//������Ч
		MyMediaPlayer.playTone(MainActivity.this,MyMediaPlayer.INDEX_STONE_COIN);
		
		//��ǰ�ص�����
		mCurrentStagePassView = (TextView) 
				findViewById(R.id.text_current_stage_pass);
		if(mCurrentStagePassView!=null){
			mCurrentStagePassView.setText((mCurrentStageindex+1)+"");
		}
		//��ʾ��������
		mCurrentSongNamePassView =(TextView) 
				findViewById(R.id.text_current_song_name_pass);
		if(mCurrentSongNamePassView!=null){
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		//��һ�ذ�������
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(judgeAppPassed()){
					//������Ϸ���ݣ�����ͨ������Ϊ1
					Util.saveData(MainActivity.this, mCurrentStageindex, mCurrentCoins,1);
				// ����ͨ�ؽ���
				Util.startActivity(MainActivity.this, AppPassView.class);
				
					
			}else{
				//��ʼ��һ��
				mPassView.setVisibility(View.GONE);
				
				//���عؿ�����
				initCurrentStageData();
			}
			
			}
			
		});
	}
	
	/**
	 * �ж��Ƿ�ͨ��
	 * @return
	 */
	private boolean judgeAppPassed(){
		return (mCurrentStageindex == Const.SONG_INFO.length -1);
		
	}
	/**
	 * �����ѡ���
	 * @param wordbutton
	 */
	private void clearTheAnswer(WordButton wordbutton){
		wordbutton.mViewButton.setText("");
		wordbutton.mWordString="";
		wordbutton.mIsVisiable=false;
		
		
		
		//���ô�ѡ��
		setButtonVisiable(mAllWords.get(wordbutton.mIndex), View.VISIBLE);
	}
	
	/**
	 * ���ô�
	 * @param wordbutton
	 */
	private void setSelectWord(WordButton wordbutton)
	{
		for(int i=0;i<mBtnSelectWords.size();i++){
			if(mBtnSelectWords.get(i).mWordString.length()==0){
				//���ô����ֿ�����ݼ��ɼ���
				mBtnSelectWords.get(i).mViewButton.setText(wordbutton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable=true;
				mBtnSelectWords.get(i).mWordString = wordbutton.mWordString;
				//��¼����
				mBtnSelectWords.get(i).mIndex=wordbutton.mIndex;
			
				if(wordbutton.mIndex == 0){
					mAllWords.get(0).mViewButton.setText("111");
				}
				//Log.i(TAG, wordbutton.mIndex+"");
				//Log.i("TAG",mBtnSelectWords.get(i).mIndex+"");
				//Mylog.i(TAG,mBtnSelectWords.get(i).mIndex+"");
				
				//���ô�ѡ��ɼ���
				setButtonVisiable(wordbutton,View.INVISIBLE);
				break;
			}
		}
	}
	/**
	 * ���ô�ѡ���ֿ��Ƿ�ɼ�
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
	 * �������еĴ�ѡ����
	 */
	private String[] generateWords(){
		Random random = new Random();
		String[] words = new String[COUNTS_WORDS];
		
		//�������
		for(int i = 0;i<mCurrentSong.getNameLength();i++){
			words[i] = mCurrentSong.getNameCharacters()[i]+"";
		}
		//��ȡ������ִ�������
		for(int i = mCurrentSong.getNameLength();i<COUNTS_WORDS;i++){
			words[i] = getRandomChar()+"";
		}
		
		//��������˳��,���ȴ����е�Ԫ�������ѡȡһ�����һ������
		//Ȼ���ڵڶ���֮��ѡ��һ����ڶ���������ֱ�����һ��
		for(int i=COUNTS_WORDS-1;i>=0;i--){
			int index = random.nextInt(i+1);
			String buf = words[index];
			words[index] = words[i];
			words[i]=buf;
			
		}
		
		
		
		return words;
	}
	
	/**
	 * �����������
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
	 * ����
	 * @return
	 */
	private int checkTheAnswer(){
		//�ȼ�鳤��
		for(int i=0;i<mBtnSelectWords.size();i++){
			//����пյģ�˵���𰸲�����
			if(mBtnSelectWords.get(i).mWordString.length()==0){
				return STATUS_ANSWER_LACK;
			}
		}
		//�����������������ȷ��
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<mBtnSelectWords.size();i++){
			sb.append(mBtnSelectWords.get(i).mWordString);
		}
		return (sb.toString().equals(mCurrentSong.getSongName()))?STATUS_ANSWER_RIGHT:STATUS_ANSWER_WRONG;
		
	}
	
	/**
	 * ��˸����
	 */
	private void sparkWords(){
		//������ʱ��
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
				//ִ����˸�߼���������ʾ��ɫ�Ͱ�ɫ�仯
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
	 * �Զ�ѡ��һ����
	 */
	private void tipAnswer(){
		
		
		boolean tipWord = false;
		//���ٽ������
		if(!handleCoins(-getTipCoins())){
			//��Ҳ���,��ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		
		for(int i=0;i<mBtnSelectWords.size();i++){
			if(mBtnSelectWords.get(i).mWordString.length()==0){
				
				//���ݵ�ǰ�Ĵ𰸿�����ѡ���Ӧ�����ֲ�����
				onWordButtonClick(findIsAnswerWord(i));
				tipWord = true;
				
				break;
			}
		}
		//û���ҵ��������Ĵ�
		if(!tipWord){
			//��˸������ʾ�û�
			sparkWords();
		}
		
	}
	
	/**
	 * ɾ������
	 */
	private void deleteOneWord(){
		//���ٽ��
		if(!handleCoins(-getDeleteWordCoins())){
			//��Ҳ�������ʾ��ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		//�����������Ӧ��WordButton����Ϊ���ɼ�
		setButtonVisiable(findNotAnswerWord(),View.INVISIBLE);
		
	}
	
	/**
	 * �ҵ�һ�����Ǵ𰸵����֣����ҵ�ǰ�ǿɼ���
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
	 * �ҵ�һ���Ǵ𰸵�����
	 * index ��ǰҪ����𰸿������
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
	 * �ж�ĳ�������Ƿ�Ϊ��
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
	 * ���ӻ��߼���ָ�������Ľ��
	 * @param data
	 * @return true ���ӡ����ٳɹ���false ʧ��
	 */
	private boolean handleCoins(int data){
		//�жϵ�ǰ�ܵĽ�������Ƿ�ɱ�����
		if(mCurrentCoins + data>=0){
			mCurrentCoins+=data;
			mViewCurrentCoins.setText(mCurrentCoins+"");
			
			return true;
		}else{
			//��Ҳ���
			return false;
		}
		
	}
	
	/**
	 * �������ļ��ж�ȡɾ������Ҫ�Ľ����
	 * @return
	 */
	private int getDeleteWordCoins(){
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}
	

	/**
	 * �������ļ��ж�ȡ��ʾ����Ҫ�Ľ����
	 * @return
	 */
	private int getTipCoins(){
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * �������ļ��ж�ȡ�����Ľ����
	 * @return
	 */
	private int getPassEventCoins(){
		return this.getResources().getInteger(R.integer.get_passevent_coins);
	}
	
	/**
	 * ����ɾ����ѡ�����¼�
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
	 * ������ʾ�����¼�
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
	
	//�Զ���AlertDialog�¼���Ӧ
	//ɾ�������
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼�
			deleteOneWord();
		}
		
	};
	//����ʾ
	private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼�
			tipAnswer();
		}
		
	};
	//��Ҳ���
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼�
			
		}
		
	};
	
	
	/**
	 * ��ʾ�Ի���
	 * @param id
	 */
	private void showConfirmDialog(int id){
		switch(id){
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this,"ȷ�ϻ���"+getDeleteWordCoins()+"�����ȥ��һ������𰸣�",mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this,"ȷ�ϻ���"+getTipCoins()+"����һ��һ��������ʾ��",mBtnOkTipAnswerListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this,"��Ҳ��㣬ȥ�̵겹�䣿",mBtnOkLackCoinsListener);
			break;
		}
	}
}
