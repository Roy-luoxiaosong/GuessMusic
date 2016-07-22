package com.roy.gussmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

public class MyPlayer {
	public static final int INDEX_STONE_ENTER = 0;
	public static final int INDEX_STONE_CANCEL = 1;
	public static final int INDEX_STONE_COIN = 2;
	//��Ч�ļ�����
	private static final String[] SONG_NAME = {
		"enter.mp3","cancel.mp3","coin.mp3",
		};
	//��������
	private static MediaPlayer mMusicMediaPlayer;
	//��Ч
	private static MediaPlayer[] mToneMediaPlayers = new MediaPlayer[SONG_NAME.length];
	
	/**
	 * ��������
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context,String fileName){
		if(mMusicMediaPlayer == null){
			mMusicMediaPlayer = new MediaPlayer();
		}
		//ǿ������mMusicMediaPlayer��״̬
		mMusicMediaPlayer.reset();
		//��������
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
					fileDescriptor.getStartOffset(),
					fileDescriptor.getLength());
			mMusicMediaPlayer.prepare();
			mMusicMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//��ͣ����
	public static void stopTheSong(Context context){
		if(mMusicMediaPlayer!=null){
			mMusicMediaPlayer.stop();
		}
	}
	public static void palyTone(Context context,int index){
		
		//��������
		AssetManager assetManager = context.getAssets();
		if(mToneMediaPlayers[index]==null){
			mToneMediaPlayers[index] = new MediaPlayer();
			try {
				AssetFileDescriptor fileDescriptor = assetManager.openFd(SONG_NAME[index]);
				mToneMediaPlayers[index].setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				mToneMediaPlayers[index].prepare();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mToneMediaPlayers[index].start();
		
	}
}
