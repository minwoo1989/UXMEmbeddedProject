package com.uxm.embeddedproject.menuitem;

import com.uxm.embeddedproject.MainActivity;
import com.uxm.embeddedproject.bluetooth.*;
import com.uxm.embeddedproject.camera.CameraActivity;
import com.uxm.embeddedproject.paint.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MenuActivity extends Activity {

	Intent intent;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "전화");
		menu.add(0, 2, 0, "카메라");
		menu.add(0, 3, 0, "블루투스");
		menu.add(0, 4, 0, "어플 사용법");
		menu.add(0, 5, 0, "로그아웃");
		menu.add(0, 6, 0, "정보");
		menu.add(0, 7, 0, "낙서장");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			intent = new Intent(context, CallActivity.class);
			startActivity(intent);
			return true;
		case 2:
			intent = new Intent(context, CameraActivity.class);
			startActivity(intent);
			return true;
		case 3:
			intent = new Intent(context, BluetoothChatActivity.class);
			startActivity(intent);
			return true;
		case 4:
			intent = new Intent(context, VideoActivity.class);
			startActivity(intent);
			return true;
		case 5:
			intent = new Intent(context, MainActivity.class);
			startActivity(intent);
			return true;
		case 6:
			intent = new Intent(context, InfoActivity.class);
			startActivity(intent);
			return true;
		case 7:
			intent = new Intent(context, PaintActivity.class);
			startActivity(intent);
			return true;

		}

		return false;
	}
}
