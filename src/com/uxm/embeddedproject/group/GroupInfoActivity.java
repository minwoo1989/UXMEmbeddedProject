package com.uxm.embeddedproject.group;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uxm.embeddedproject.Config;
import com.uxm.embeddedproject.ProjectManager;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupInfo;
import com.uxm.embeddedproject.data.GroupUser;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.meeting.MeetingListActivity;
import com.uxm.embeddedproject.menuitem.MenuActivity;

@SuppressLint("HandlerLeak") 
public class GroupInfoActivity extends MenuActivity implements
		View.OnClickListener {

	Button joinButton, backButton;
	User user; // 가입을 하기위해 필요
	GroupInfo groupInfo;
	GroupUser groupUser;
	String gName;
	TextView info_name, info_create_date, info_group_number, info_info;
	ArrayList<GroupUser> groupUserList;		//그룹의 총 인원을 세기 위해 groupuser객체를 담을 어레이리스트
	Context context;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.group_info);

		this.context = this;
		joinButton = (Button) findViewById(R.id.joinButton);
		backButton = (Button) findViewById(R.id.backButton);

		joinButton.setOnClickListener(this);
		backButton.setOnClickListener(this);

		info_name = (TextView) findViewById(R.id.info_name);
		info_create_date = (TextView) findViewById(R.id.info_create_date);
		info_group_number = (TextView) findViewById(R.id.info_group_number);
		info_info = (TextView) findViewById(R.id.info_info);

		user = (User) getIntent().getSerializableExtra("user");
		gName = getIntent().getStringExtra("gName");
		// 그룹의 정보를 가져오기 위해 서버에 요청. 그룹의 이름으로 검색하므로 gname을 같이 보냄.
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("getGroup");
		msg.setObj(gName);
		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handler, msg);
		manager.execute();

	}

	// 서버로부터 그룹에 대한 정보를 가져옴
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			groupInfo = new GroupInfo();
			groupInfo = (GroupInfo) msg.obj;

			settingForCount(); // 그룹의 총 인원수를 세기 위한 메서드
		}
	};

	// 그룹에 가입된 모든 유저의 정보를 서버에 요청
	public void settingForCount() {
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("getGroupUsers");

		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handlerGU, msg);
		manager.execute();
	}

	// 리턴받음
	Handler handlerGU = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			int count = 0;
			groupUserList = (ArrayList<GroupUser>) msg.obj;
			for (int i = 0; i < groupUserList.size(); i++) {
				if (groupUserList.get(i).getgId() == groupInfo.getId()) {
					count++;
				}
			} // 리턴받은 유저에서 해당 그룹에 대한 유저만 셈
			printText(count);
		}
	};

	// 그룹에 대한 정보를 출력
	public void printText(int count) {
		info_name.setText(groupInfo.getgName());
		info_create_date.setText(groupInfo.getgDate());
		info_group_number.setText(count + "명");
		info_info.setText(groupInfo.getgInfo());
	}

	public void onClick(View v) {
		if (v.getId() == R.id.joinButton) {
			// 그룹에 가입을 서버에 요청
			groupUser = new GroupUser();
			groupUser.setgId(groupInfo.getId());
			groupUser.setuId(user.getId());
			com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
			msg.setMsg("registerGroup");
			msg.setObj(groupUser);
			ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
					Config.SERVER_PORT, register_handler, msg);
			manager.execute();

		} else if (v.getId() == R.id.backButton) {
			finish();
		}
	}

	// 가입되었다는 메시지를 받고 meetingListActivity로 이동
	Handler register_handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			intent = new Intent(context, MeetingListActivity.class);
			intent.putExtra("user", user);
			Toast.makeText(context, "가입 되었습니다.", Toast.LENGTH_SHORT).show();
			startActivity(intent);
		};
	};
}
