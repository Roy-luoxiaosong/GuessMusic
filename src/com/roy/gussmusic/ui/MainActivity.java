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
	 * ��״̬
	 */
	public static final int STATUS_ANSWER_RIGHT = 1;
	public static final int STATUS_ANSWER_WRONG = 2;
	public static final int STATUS_ANSWER_LACK = 3;

	public static final int SPAHS_TIMES = 6;

	public static final int ID_DIALOG_DELETE_WORD = 4;
	public static final int ID_DIALOG_TIP_ANSWER = 5;
	public static final int ID_DIALOG_LACK_COINS = 7;

	// ��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	private ImageView mViewPan;
	private ImageView mViewPanBar;

	// play ����ʱ��
	private ImageButton mBtnPlayStart;

	// ������һ�׸�
	private ImageButton mBackStage;

	private boolean mIsRunning;

	// ���ؽ���
	private View mPassView;

	// ���ֿ�����
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;
	private MyGridView mMyGridView;
	private LinearLayout mViewWordContainer;

	// ��ǰ�ĸ���
	private Song mCurrentSong;
	// ��ǰ�ص�����
	private int mCurrentStageIndex = -1;

	// ��ǰ��ҵ�����
	private int mCurrentCoins = Const.TOTAL_COINS;

	// �����ص�view
	private TextView mViewCurrentCoins;
	private ImageButton mDeleteWord;
	private ImageButton mTipAnswer;

	// �����л�
	private ImageButton mNextStage;
	private ImageButton mShareToWechat;

	// ��ǰ�ص�����
	private TextView mCurrentStagePassView;
	private TextView mCurrentStageView;

	private TextView mCurrentSongNamePassView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ��ȡ����
		int[] datas = Util.loadData(MainActivity.this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COIN];
		// ��ʼ������
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
		// ��ʼ���ؼ�
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

		// ��ӵ��ʱ��
		mMyGridView.setOnWordButtonClickListener(this);
		mBtnPlayStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handlePlayButton();
			}
		});
		// ��������
		initCurrentStageData();

		// ����ɾ����ť�¼�
		handleDeleteWord();
		// ������ʾ�����¼�
		handleTipAnswer();

	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		setSelectWord(wordButton);
		// ����״̬
		int checkResult = checkTheAnswer();
		switch (checkResult) {
		case STATUS_ANSWER_RIGHT:
			// TODO �����Ӧ��������
			handlePassEvent();
			break;
		case STATUS_ANSWER_LACK:
			// TODO
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
			break;
		case STATUS_ANSWER_WRONG:
			// TODO ��˸��ʾ�û�
			sparkTheWords();
			break;
		}

	}

	/**
	 * �������ʱ��ͽ���
	 */
	private void handlePassEvent() {
		mPassView.setVisibility(View.VISIBLE);
		// TODO

		// ֹͣδ��ɵĶ���
		mViewPan.clearAnimation();

		// ֹͣ���ڲ��ŵ�����
		MyPlayer.stopTheSong(MainActivity.this);

		// ������Ч
		MyPlayer.palyTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);

		// ���ӽ��
		handleCoins(3);
		// ����ǰ�ص�����
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText("" + (1 + mCurrentStageIndex));
		}

		// ��ʾ��������
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		// �¹ذ�������
		mNextStage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (judegAppPassed()) {
					// TODO ����ͨ�ؽ���
					Util.statrActivity(MainActivity.this, AppPassView.class);
				} else {
					// ��һ��
					mPassView.setVisibility(View.GONE);
					// ���عؿ�����
					initCurrentStageData();
				}
			}
		});

		// ����΢�Ű�������
		mShareToWechat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

	}

	/**
	 * �ж��Ƿ�ͨ��
	 * 
	 * @return
	 */
	private boolean judegAppPassed() {
		return mCurrentStageIndex == Const.SONG_INFO.length - 1;
	}

	/**
	 * �����
	 * 
	 * @param button
	 */
	private void clearTheAnswer(WordButton button) {
		button.mViewButton.setText("");
		// button.mViewButton.setTextColor(Color.WHITE);
		button.mWordString = "";
		button.mIsVisiable = false;

		// ���ô�ѡ��Ŀɼ���
		setButtonVisiable(mAllWords.get(button.mIndex), View.VISIBLE);
	}

	/**
	 * �����
	 * 
	 * @param button
	 */
	/*
	 * private void clearTheAnswer(WordButton button){
	 * button.mViewButton.setText("");
	 * //button.mViewButton.setTextColor(Color.WHITE); button.mWordString = "";
	 * button.mIsVisiable = false;
	 * 
	 * //���ô�ѡ��Ŀɼ��� setButtonVisiable(mAllWords.get(button.mIndex),
	 * View.VISIBLE); }
	 */
	/**
	 * ���ô�
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// ���ô����ֿ�����ݺͿɼ���
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;

				// ��¼����
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");

				// ���ô�ѡ��Ŀɼ���
				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			}
		}
	}

	/**
	 * ���ô�ѡ���ֿ��Ƿ�ɼ�
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

			// ��������
			MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
		}
	}

	@Override
	protected void onPause() {
		// ��������
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		mViewPan.clearAnimation();
		mIsRunning = false;

		// ��ͣ����
		MyPlayer.stopTheSong(MainActivity.this);
		super.onPause();
	}

	/**
	 * ��ȡ��ǰ�صĸ�������
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
	 * ��ʼ����ǰ��������
	 */
	private void initCurrentStageData() {
		// ��ȡ��ǰ�صĸ�����Ϣ
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
		// ��ʼ���Ѿ�ѡ���
		mBtnSelectWords = initWordSelected();
		// ���ԭ���Ĵ�
		mViewWordContainer.removeAllViews();

		LayoutParams params = new LayoutParams(140, 140);
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		// ��ʾ��ǰ�ص�����
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		// �������
		mAllWords = initAllWord();
		// ��������MyGridView
		mMyGridView.upateData(mAllWords);

		// ��������
		handlePlayButton();
	}

	/**
	 * ��ʼ����ѡ���ֿ�
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		// ������д�ѡ����
		String[] word = generateWords();

		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();
			button.mWordString = word[i];
			data.add(button);
		}
		return data;
	}

	/**
	 * ��ʼ���Ѿ�ѡ�����ֿ�
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
	 * �������еĴ�ѡ����
	 */
	private String[] generateWords() {
		String[] words = new String[MyGridView.COUNT_WORDS];
		Random random = new Random();

		// �������
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		// ��ȡ�������
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNT_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}
		// ����˳���������ѡȡһ�����һԪ�ؽ���
		// �ڵڶ�������֮��ѡ��һ����ڶ������ݽ��н�����һ������
		// ����㷨�ܹ���֤ÿ��Ԫ����ÿ��λ�õĸ��ʶ���1/n
		for (int i = MyGridView.COUNT_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		return words;
	}

	/**
	 * ��������ַ�
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
	 * ���ݴ𰸷��ز�ͬ��״̬
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// �ȼ�鳤��
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// ����пյģ�˵���𰸲�����
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		// ������,���������ȡ��
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).mWordString);
		}

		return (sb.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
	}

	/**
	 * ��˸����
	 */
	private void sparkTheWords() {
		// ��ʱ����ص�����
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
						// ִ����˸�߼�������ʾ��ɫ�Ͱ�ɫ
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
	 * ���ӻ��߼����ƶ������Ľ��
	 * 
	 * @param data
	 * @return
	 */
	private boolean handleCoins(int data) {
		// �жϵ�ǰ�ܵĽ�������Ƿ�ɱ�����
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;
			mViewCurrentCoins.setText("" + mCurrentCoins);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ����ɾ����ѡ�����¼�
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
	 * ������ʾ�¼�
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
	 * �Զ�ѡ��һ����
	 */
	private void tipAnswer() {

		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {

				tipWord = true;
				// ���ٽ������
				if (!handleCoins(-getTipCoins())) {
					// TODO ��Ҳ�����ʵ�Ի���
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				// ���ݵ�ǰ�𰸿������ѡ�����ֲ�������
				onWordButtonClick(findIsAnswerWord(i));
				break;
			}
		}
		// û���ҵ��������Ĵ�
		if (!tipWord) {
			// ��˸������ʾ�û�
			sparkTheWords();
		}
	}

	/**
	 * ɾ��һ������
	 */
	private void deleteOneWord() {
		// ���ٽ��
		if (!handleCoins(-getDeleteWordCoins())) {
			// TODO ��Ҳ�����ʾ��ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		// ��������Ӧ��wordButton����Ϊ���ɼ�
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * �ҵ�һ�����Ǵ𰸵�word��������������ǿɼ���
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
	 * �ҵ�һ���Ǵ𰸵�word��
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
	 * �ж�ĳ�������Ƿ�����ȷ��
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

	// �Զ���Ի�����¼���Ӧ
	// ɾ�������
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {
		@Override
		public void onClick() {
			// ִ���¼�
			deleteOneWord();
		}
	};
	// ����ʾ
	private IAlertDialogButtonListener mBtnOkTipWordListener = new IAlertDialogButtonListener() {
		@Override
		public void onClick() {
			tipAnswer();
		}
	};
	// ��Ҳ���
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {
		@Override
		public void onClick() {

		}
	};

	/**
	 * ��ʾ�Ի���
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getDeleteWordCoins()
					+ "�����ȥ������𰸣�", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getTipCoins()
					+ "����һ��һ��������ʾ��", mBtnOkTipWordListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "��Ҳ�ȥ��ȥ�̵겹�䣿",
					mBtnOkLackCoinsListener);
			break;
		}
	}

}
