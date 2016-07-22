package com.roy.gussmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roy.gussmusic.R;
import com.roy.gussmusic.data.Const;
import com.roy.gussmusic.model.IAlertDialogButtonListener;
import com.roy.gussmusic.model.IWordButtonClickListener;
import com.roy.gussmusic.model.Song;
import com.roy.gussmusic.model.WordButton;
import com.roy.gussmusic.myui.MyGridView;
import com.roy.gussmusic.util.MyLog;
import com.roy.gussmusic.util.MyPlayer;
import com.roy.gussmusic.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener {

	public static final String TAG = "MainActivity";
	/**
	 * 答案状态
	 */
	public static final int STATUS_ANSWER_RIGHT = 1;
	public static final int STATUS_ANSWER_WRONG = 2;
	public static final int STATUS_ANSWER_LACK = 3;

	public static final int SPAHS_TIMES = 6;

	public static final int ID_DIALOG_DELETE_WORD = 4;
	public static final int ID_DIALOG_TIP_ANSWER = 5;
	public static final int ID_DIALOG_LACK_COINS = 7;

	// 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	private ImageView mViewPan;
	private ImageView mViewPanBar;

	// play 按键时间
	private ImageButton mBtnPlayStart;

	// 返回上一首歌
	private ImageButton mBackStage;

	private boolean mIsRunning;

	// 过关界面
	private View mPassView;

	// 文字框容器
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;
	private MyGridView mMyGridView;
	private LinearLayout mViewWordContainer;

	// 当前的歌曲
	private Song mCurrentSong;
	// 当前关的索引
	private int mCurrentStageIndex = -1;

	// 当前金币的数量
	private int mCurrentCoins = Const.TOTAL_COINS;

	// 金币相关的view
	private TextView mViewCurrentCoins;
	private ImageButton mDeleteWord;
	private ImageButton mTipAnswer;

	// 过关切换
	private ImageButton mNextStage;
	private ImageButton mShareToWechat;

	// 当前关的索引
	private TextView mCurrentStagePassView;
	private TextView mCurrentStageView;

	private TextView mCurrentSongNamePassView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 读取数据
		int[] datas = Util.loadData(MainActivity.this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COIN];
		// 初始化动画
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setFillAfter(false);
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPanBar.startAnimation(mBarOutAnim);

			}
		});

		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPan.startAnimation(mPanAnim);

			}
		});

		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);

			}
		});
		// 初始化控件
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mViewPan = (ImageView) findViewById(R.id.imageview1);
		mViewPanBar = (ImageView) findViewById(R.id.imageview2);
		mViewWordContainer = (LinearLayout) findViewById(R.id.word_select_container);
		mPassView = (LinearLayout) findViewById(R.id.passview);
		mViewCurrentCoins = (TextView) findViewById(R.id.text_bar_coins);
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		mDeleteWord = (ImageButton) findViewById(R.id.btn_delete_word);
		mTipAnswer = (ImageButton) findViewById(R.id.btn_tip_answer);
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_pass);
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		mNextStage = (ImageButton) findViewById(R.id.btn_next);
		mShareToWechat = (ImageButton) findViewById(R.id.btn_share_wechat);
		mBackStage = (ImageButton) findViewById(R.id.btn_bar_back);
		mBackStage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentStageIndex > 0) {
					mCurrentStageIndex = mCurrentStageIndex - 2;
					mViewPan.clearAnimation();
					initCurrentStageData();
					MyPlayer.stopTheSong(MainActivity.this);
				}
				MyPlayer.palyTone(MainActivity.this, MyPlayer.INDEX_STONE_ENTER);
			}
		});
		mViewCurrentCoins.setText(mCurrentCoins + "");

		// 添加点击时间
		mMyGridView.setOnWordButtonClickListener(this);
		mBtnPlayStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handlePlayButton();
			}
		});
		// 加载数据
		initCurrentStageData();

		// 处理删除按钮事件
		handleDeleteWord();
		// 处理提示按键事件
		handleTipAnswer();

	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		setSelectWord(wordButton);
		// 检测答案状态
		int checkResult = checkTheAnswer();
		switch (checkResult) {
		case STATUS_ANSWER_RIGHT:
			// TODO 获得相应奖励过关
			handlePassEvent();
			break;
		case STATUS_ANSWER_LACK:
			// TODO
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
			break;
		case STATUS_ANSWER_WRONG:
			// TODO 闪烁提示用户
			sparkTheWords();
			break;
		}

	}

	/**
	 * 处理过关时间和界面
	 */
	private void handlePassEvent() {
		mPassView.setVisibility(View.VISIBLE);
		// TODO

		// 停止未完成的动画
		mViewPan.clearAnimation();

		// 停止正在播放的音乐
		MyPlayer.stopTheSong(MainActivity.this);

		// 播放音效
		MyPlayer.palyTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);

		// 增加金币
		handleCoins(3);
		// 处理当前关的索引
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText("" + (1 + mCurrentStageIndex));
		}

		// 显示歌曲名称
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		// 下关按键处理
		mNextStage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (judegAppPassed()) {
					// TODO 进入通关界面
					Util.statrActivity(MainActivity.this, AppPassView.class);
				} else {
					// 下一关
					mPassView.setVisibility(View.GONE);
					// 加载关卡数据
					initCurrentStageData();
				}
			}
		});

		// 分享到微信按键处理
		mShareToWechat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

	}

	/**
	 * 判断是否通关
	 * 
	 * @return
	 */
	private boolean judegAppPassed() {
		return mCurrentStageIndex == Const.SONG_INFO.length - 1;
	}

	/**
	 * 清除答案
	 * 
	 * @param button
	 */
	private void clearTheAnswer(WordButton button) {
		button.mViewButton.setText("");
		// button.mViewButton.setTextColor(Color.WHITE);
		button.mWordString = "";
		button.mIsVisiable = false;

		// 设置待选框的可见性
		setButtonVisiable(mAllWords.get(button.mIndex), View.VISIBLE);
	}

	/**
	 * 清除答案
	 * 
	 * @param button
	 */
	/*
	 * private void clearTheAnswer(WordButton button){
	 * button.mViewButton.setText("");
	 * //button.mViewButton.setTextColor(Color.WHITE); button.mWordString = "";
	 * button.mIsVisiable = false;
	 * 
	 * //设置待选框的可见性 setButtonVisiable(mAllWords.get(button.mIndex),
	 * View.VISIBLE); }
	 */
	/**
	 * 设置答案
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 设置答案文字框的内容和可见性
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;

				// 记录索引
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");

				// 设置待选框的可见性
				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			}
		}
	}

	/**
	 * 设置待选文字框是否可见
	 * 
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = visibility == View.VISIBLE ? true : false;
		MyLog.d(TAG, button.mIsVisiable + "");

	}

	private void handlePlayButton() {
		if (!mIsRunning) {
			mIsRunning = true;
			mViewPanBar.startAnimation(mBarInAnim);
			mBtnPlayStart.setVisibility(View.INVISIBLE);

			// 播放音乐
			MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
		}
	}

	@Override
	protected void onPause() {
		// 保存数据
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		mViewPan.clearAnimation();
		mIsRunning = false;

		// 暂停音乐
		MyPlayer.stopTheSong(MainActivity.this);
		super.onPause();
	}

	/**
	 * 获取当前关的歌曲数据
	 * 
	 * @param stageIndex
	 * @return
	 */
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		return song;
	}

	/**
	 * 初始化当前文字数据
	 */
	private void initCurrentStageData() {
		// 读取当前关的歌曲信息
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
		// 初始化已经选择框
		mBtnSelectWords = initWordSelected();
		// 清空原来的答案
		mViewWordContainer.removeAllViews();

		LayoutParams params = new LayoutParams(140, 140);
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		// 显示当前关的索引
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		// 获得数据
		mAllWords = initAllWord();
		// 更新数据MyGridView
		mMyGridView.upateData(mAllWords);

		// 播放音乐
		handlePlayButton();
	}

	/**
	 * 初始化待选文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		// 获得所有待选文字
		String[] word = generateWords();

		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();
			button.mWordString = word[i];
			data.add(button);
		}
		return data;
	}

	/**
	 * 初始化已经选择文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelected() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);

			final WordButton holder = new WordButton();

			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText(" ");
			holder.mIsVisiable = false;
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clearTheAnswer(holder);
				}
			});
			data.add(holder);
		}
		return data;
	}

	/**
	 * 生成所有的待选文字
	 */
	private String[] generateWords() {
		String[] words = new String[MyGridView.COUNT_WORDS];
		Random random = new Random();

		// 存入歌名
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		// 获取随机文字
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNT_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}
		// 打乱顺序：首先随机选取一个与第一元素交换
		// 在第二个数据之后选择一个与第二个数据进行交换，一次类推
		// 这个算法能够保证每个元素在每个位置的概率都是1/n
		for (int i = MyGridView.COUNT_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		return words;
	}

	/**
	 * 随机生成字符
	 * 
	 * @return
	 */
	public char getRandomChar() {
		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();

		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos).byteValue());
		b[1] = (Integer.valueOf(lowPos).byteValue());

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str.charAt(0);
	}

	/**
	 * 根据答案返回不同的状态
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// 先检查长度
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// 如果有空的，说明答案不完整
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		// 答案完整,继续检查争取性
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).mWordString);
		}

		return (sb.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
	}

	/**
	 * 闪烁文字
	 */
	private void sparkTheWords() {
		// 定时器相关的内容
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (++mSpardTimes > SPAHS_TIMES) {
							return;
						}
						// 执行闪烁逻辑交替显示红色和白色
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton
									.setTextColor(mChange ? Color.RED
											: Color.WHITE);
						}
						mChange = !mChange;
					}
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	/**
	 * 增加或者减少制定数量的金币
	 * 
	 * @param data
	 * @return
	 */
	private boolean handleCoins(int data) {
		// 判断当前总的金币数量是否可被减少
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;
			mViewCurrentCoins.setText("" + mCurrentCoins);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		mDeleteWord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// deleteOneWord();
				showConfirmDialog(ID_DIALOG_DELETE_WORD);

			}
		});
	}

	/**
	 * 处理提示事件
	 */
	private void handleTipAnswer() {
		mTipAnswer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// tipAnswer();
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
			}
		});
	}

	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * 自动选择一个答案
	 */
	private void tipAnswer() {

		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {

				tipWord = true;
				// 减少金币数量
				if (!handleCoins(-getTipCoins())) {
					// TODO 金币不够现实对话框
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				// 根据当前答案框的条件选择文字并且填入
				onWordButtonClick(findIsAnswerWord(i));
				break;
			}
		}
		// 没有找到可以填充的答案
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkTheWords();
		}
	}

	/**
	 * 删除一个文字
	 */
	private void deleteOneWord() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// TODO 金币不够显示提示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		// 将索引对应的wordButton设置为不可见
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * 找到一个不是答案的word，并且这个文字是可见的
	 * 
	 * @return
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;

		while (true) {
			int index = random.nextInt(MyGridView.COUNT_WORDS);
			buf = mAllWords.get(index);
			if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
				return buf;
			}
		}
	}

	/**
	 * 找到一个是答案的word，
	 * 
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;
		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			buf = mAllWords.get(i);
			if (buf.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}
		return null;
	}

	/**
	 * 判断某个文字是否是正确答案
	 * 
	 * @param button
	 * @return
	 */
	private boolean isTheAnswerWord(WordButton button) {
		boolean result = false;
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (button.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	// 自定义对话框的事件响应
	// 删除错误答案
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {
		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}
	};
	// 答案提示
	private IAlertDialogButtonListener mBtnOkTipWordListener = new IAlertDialogButtonListener() {
		@Override
		public void onClick() {
			tipAnswer();
		}
	};
	// 金币不足
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {
		@Override
		public void onClick() {

		}
	};

	/**
	 * 显示对话框
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins()
					+ "个金币去除错误答案？", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "确认花掉" + getTipCoins()
					+ "个金币获得一个文字提示？", mBtnOkTipWordListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "金币不去，去商店补充？",
					mBtnOkLackCoinsListener);
			break;
		}
	}

}
