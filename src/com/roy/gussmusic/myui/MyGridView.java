package com.roy.gussmusic.myui;

import java.util.ArrayList;

import com.roy.gussmusic.R;
import com.roy.gussmusic.model.IWordButtonClickListener;
import com.roy.gussmusic.model.WordButton;
import com.roy.gussmusic.util.Util;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView{
	
	public static final int COUNT_WORDS = 24;
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	private MyGridAdapter mAdapter;
	private Context mContext;
	
	private Animation mScaleAnim;
	private IWordButtonClickListener mWordButtonListener;
	//接口初始化
	public void setOnWordButtonClickListener(IWordButtonClickListener listener){
		mWordButtonListener = listener;
	}
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
		mContext = context;
	}
	
	public void upateData(ArrayList<WordButton> list){
		mArrayList = list;
		setAdapter(mAdapter);
	}
	class MyGridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mArrayList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final WordButton holder;
			if(convertView == null){
				convertView = Util.getView(mContext, R.layout.self_ui_gridview_item);
				holder = mArrayList.get(position);
				
				//加载动画
				mScaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				//设置动画延迟时间
				mScaleAnim.setStartOffset(position*100);
				holder.mIndex = position;
				holder.mViewButton = (Button) convertView.findViewById(R.id.item_btn);
				holder.mViewButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mWordButtonListener!=null){
							mWordButtonListener.onWordButtonClick(holder);
						}
					}
				});
				convertView.setTag(holder);
			}else {
				holder = (WordButton) convertView.getTag();
			}
			holder.mViewButton.setText(holder.mWordString);
			
			//播放动画
			convertView.startAnimation(mScaleAnim);
			return convertView;
		}
	}
}
