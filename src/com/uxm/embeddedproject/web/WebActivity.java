package com.uxm.embeddedproject.web;

import java.util.concurrent.ExecutorService;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uxm.embeddedproject.R;

public class WebActivity extends Activity implements CordovaInterface,
		View.OnClickListener {

	private CordovaWebView webView;
	private double latitude = 0.0;
	private double longitude = 0.0;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);

		// PhoneGap의 CordovaWebView 사용
		webView = (CordovaWebView) findViewById(R.id.webView);

		webView.loadUrl("file:///android_asset/www/index.html");

		// 자바스크립트 활성화
		webView.getSettings().setJavaScriptEnabled(true);

		// 자바스크립트 인터페이스를 추가
		webView.addJavascriptInterface(new JavaScriptI(), "javaScriptI");

		// 버튼 세팅
		Button completeButton = (Button) findViewById(R.id.completeButton);
		Button backButton = (Button) findViewById(R.id.backButton);

		completeButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (webView != null) {
			webView.handleDestroy();
			webView.removeAllViews();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.completeButton) {
			Intent i = getIntent();

			i.putExtra("lat", latitude);
			i.putExtra("lon", longitude);

			setResult(RESULT_OK, i);

			finish();
		} else if (v.getId() == R.id.backButton) {
			finish();
		}
	}

	// 자바스크립트 인터페이스 클래스
	private class JavaScriptI {

		@SuppressWarnings("unused")
		public void clickButton(final double lat, final double lon) {
			latitude = lat;
			longitude = lon;

			Toast.makeText(WebActivity.this, "장소가 등록되었습니다. 완료버튼을 눌러주세요.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void cancelLoadUrl() {
		Toast.makeText(this, "URL 읽기 취소", Toast.LENGTH_SHORT).show();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public Context getContext() {
		return getContext();
	}

	@Override
	public ExecutorService getThreadPool() {
		return getThreadPool();
	}

	@Override
	public Object onMessage(String arg0, Object arg1) {
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin arg0) {

	}

	@Override
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {

	}
}
