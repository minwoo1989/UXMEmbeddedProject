package com.uxm.embeddedproject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class LoadingScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);

		ImageView iv = (ImageView) findViewById(R.id.imageView);	//이미지뷰 설정
		Drawable drawable = this.getResources().getDrawable(R.drawable.main_logo);	//메인로고 이미지를 가져옴
		iv.setImageDrawable(drawable);
		
		Handler handler = new Handler() {	//이미지뷰 시간을 핸들하기 위해 만듬
			public void handleMessage(Message msg) {
				finish();
			}
		};
		
		handler.sendEmptyMessageDelayed(0, 2000);	//2초간 로고화면으로 정지
	}

	@Override
	protected void onPause() {	//pause 할 경우 로딩화면을 멈추고 메인으로 바로 넘어감
		super.onPause();
		
		finish();
	}
}
