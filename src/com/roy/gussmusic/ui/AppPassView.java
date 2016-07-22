package com.roy.gussmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.roy.gussmusic.R;
import com.roy.gussmusic.util.Util;

public class AppPassView extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_pass_view);
		
		FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
		ImageButton backButton = (ImageButton) findViewById(R.id.btn_bar_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.statrActivity(AppPassView.this, MainActivity.class);
			}
		});
	}
}
