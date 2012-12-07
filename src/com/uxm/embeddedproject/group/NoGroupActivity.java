package com.uxm.embeddedproject.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.menuitem.MenuActivity;

public class NoGroupActivity extends MenuActivity implements
		View.OnClickListener {

	// 액티비티가 스택에 쌓였을 때 지우기 위한 static 객체
	public static Activity noGroupActivity = null;

	User user;
	Button makeGroupButton, findGroupButton;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.no_group);

		// user 객체를 받아옴
		user = (User) getIntent().getSerializableExtra("user");

		// 버튼 세팅
		makeGroupButton = (Button) findViewById(R.id.makeGroupButton);
		findGroupButton = (Button) findViewById(R.id.findGroupButton);

		makeGroupButton.setOnClickListener(this);
		findGroupButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.makeGroupButton) { // 내 그룹을 새로 생성함
			intent = new Intent(NoGroupActivity.this, CreateGroupActivity.class);
			intent.putExtra("user", user);
			startActivity(intent);
		} else if (v.getId() == R.id.findGroupButton) { // 기존에 있는 그룹 중 가입을 하기 위해
			intent = new Intent(NoGroupActivity.this, GroupListActivity.class);
			intent.putExtra("user", user);
			startActivity(intent);
		}
	}
}
