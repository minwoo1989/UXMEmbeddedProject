package com.uxm.embeddedproject.meeting;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uxm.embeddedproject.Config;
import com.uxm.embeddedproject.ProjectManager;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.alarm.AlarmSetActivity;
import com.uxm.embeddedproject.data.Attendance;
import com.uxm.embeddedproject.data.GroupBoard;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.group.MyGroupActivity;
import com.uxm.embeddedproject.map.GMapActivity;
import com.uxm.embeddedproject.menuitem.MenuActivity;

@SuppressLint({ "HandlerLeak" })
public class MeetingInfoActivity extends MenuActivity implements
		View.OnClickListener {

	// 액티비티가 스택에 쌓였을 때 지우기 위한 static 객체
	public static Activity meetingInfoActivity = null;

	private Button joinSecedeButton, mapButton, myGroupButton, alarmSetButton;

	User user;
	GroupBoard groupBoard;
	Context context;
	Intent intent;
	String gName;
	TextView meetingTitle, meetingUser, meetingCreateDate, meetingContent,
			meetingDate, meetingLoca, meetingGroup;
	String userId;
	HashMap<String, Integer> map;	//참여 취소를 위하 서버에 보낼 인자인 gbid와 uid를 담을 hashmap
	ArrayList<Integer> forDecideAttendance; // 참석 여부를 검색하기 위한 인자를 넣어줄 어레이리스트

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.meeting_info);

		this.context = this;

		user = (User) getIntent().getSerializableExtra("user");
		groupBoard = (GroupBoard) getIntent()
				.getSerializableExtra("groupBoard");
		gName = getIntent().getStringExtra("gName");

		try {
			findUserId(groupBoard.getuId());
		} catch (Exception e) {
			Toast.makeText(MeetingInfoActivity.this, "서버와의 통신이 원활하지 않습니다.",
					Toast.LENGTH_SHORT).show();
		}

		// 버튼
		mapButton = (Button) findViewById(R.id.mapButton);
		joinSecedeButton = (Button) findViewById(R.id.joinSecedeButton);
		myGroupButton = (Button) findViewById(R.id.myGroupButton);
		alarmSetButton = (Button) findViewById(R.id.alarmSetButton);

		// 텍스트뷰
		meetingTitle = (TextView) findViewById(R.id.meetingTitle);
		meetingUser = (TextView) findViewById(R.id.meetingUser);
		meetingGroup = (TextView) findViewById(R.id.meetingGroup);
		meetingCreateDate = (TextView) findViewById(R.id.meetingCreateDate);
		meetingContent = (TextView) findViewById(R.id.meetingContent);
		meetingDate = (TextView) findViewById(R.id.meetingDate);
		meetingLoca = (TextView) findViewById(R.id.meetingLoca);

		// 리스너 세팅
		mapButton.setOnClickListener(this);
		joinSecedeButton.setOnClickListener(this);
		myGroupButton.setOnClickListener(this);
		alarmSetButton.setOnClickListener(this);

		// 참여 여부를 확인
		forDecideAttendance = new ArrayList<Integer>();
		forDecideAttendance.add(groupBoard.getId());
		forDecideAttendance.add(user.getId());

		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("decideAttendance");
		msg.setObj(forDecideAttendance);

		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handler_attendacne, msg);

		manager.execute();

		meetingInfoActivity = this;
	}

	Handler handler_attendacne = new Handler() {
		public void handleMessage(Message msg) {
			Boolean isAttendance = (Boolean) msg.obj;
			//참석 여부를 확인해서 버튼 기능을 결정함
			if (!isAttendance) {
				joinSecedeButton.setText("참여 취소");
			} else {
				joinSecedeButton.setText("참여");
			}
		}
	};

	// 유저의 아이디 받아오기 위해 서 버에 요청
	private void findUserId(int getuId) {
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("findUser");
		msg.setObj(getuId);
		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handler, msg);
		manager.execute();
	}

	// 리턴받음. 출력 메서드 호출
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			userId = (String) msg.obj;

			printText();	//모임의 정보를 가져오기 위해 메서드를 호출
		}

		// 정보 호출
		private void printText() {
			meetingTitle.setText(groupBoard.getgTitle());
			meetingUser.setText(userId);
			meetingGroup.setText(gName);
			meetingCreateDate
					.setText(groupBoard.getgCreateY() + "/"
							+ groupBoard.getgCreateM() + "/"
							+ groupBoard.getgCreateD()); // 글 쓴 날짜
			meetingContent.setText(groupBoard.getgContent()); // 내용
			meetingDate.setText(groupBoard.getgMeetY() + "/"
					+ groupBoard.getgMeetM() + "/" + groupBoard.getgMeetD()
					+ "  " + groupBoard.getgMeetH() + ":"
					+ groupBoard.getgMeetMI()); // 만날 날짜
			meetingLoca.setText(groupBoard.getgLatitude() + " "
					+ groupBoard.getgLongtitude());

		}
	};

	public void onClick(View v) {
		if (v.getId() == R.id.mapButton) {
			intent = new Intent(MeetingInfoActivity.this, GMapActivity.class);

			intent.putExtra("title", meetingTitle.getText().toString());

			startActivity(intent);
		} else if (v.getId() == R.id.joinSecedeButton) {
			if (joinSecedeButton.getText().toString().equals("참여 취소")) {
				/* 모임 참여 목록에서 유저를 삭제 */
				com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
				msg.setMsg("dropAttend");
				map = new HashMap<String, Integer>();
				map.put("uId", user.getId());
				map.put("gbid", groupBoard.getId());	//hashmap에 유저id와 그룹보드id를 담음
				msg.setObj(map);

				ProjectManager manager = new ProjectManager(
						Config.SERVER_ADDRESS, Config.SERVER_PORT,
						handler_cancle, msg);

				manager.execute();
				joinSecedeButton.setText("참여");	//버튼을 다시 참여로 바꿈
			} else if (joinSecedeButton.getText().toString().equals("참여")) {
				/* 모임 참여 목록에 유저를 추가 */
				Attendance attendance = new Attendance();

				attendance.setUid(user.getId());
				attendance.setGbid(groupBoard.getId());
				attendance.setAttendance(true);	//참여를 위한 객체를 만듦

				com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
				msg.setMsg("masterAttendance");
				msg.setObj(attendance);

				ProjectManager manager = new ProjectManager(
						Config.SERVER_ADDRESS, Config.SERVER_PORT,
						handler_attend, msg);

				manager.execute();
				joinSecedeButton.setText("참여 취소");//버튼을 참여취소로 바꿈
			}
		} else if (v.getId() == R.id.myGroupButton) {
			intent = new Intent(MeetingInfoActivity.this, MyGroupActivity.class);

			intent.putExtra("user", user);

			startActivity(intent);
		} else if (v.getId() == R.id.alarmSetButton) {

			// 알람설정 액티비티로 약속날짜 정보를 보내고 실행
			intent = new Intent(MeetingInfoActivity.this,
					AlarmSetActivity.class);

			intent.putExtra("user", user);
			intent.putExtra("groupBoard", groupBoard);
			intent.putExtra("gName", gName);

			intent.putExtra("year", groupBoard.getgMeetY());
			intent.putExtra("month", groupBoard.getgMeetM());
			intent.putExtra("day", groupBoard.getgMeetD());
			intent.putExtra("hour", groupBoard.getgMeetH());
			intent.putExtra("minute", groupBoard.getgMeetMI());

			startActivity(intent);
		}

	}

	Handler handler_attend = new Handler() {
		public void handleMessage(Message msg) {
			Boolean ok = (Boolean) msg.obj;
			if (ok) {
				Toast.makeText(context, "모임에 참여합니다.", Toast.LENGTH_SHORT)
						.show();
				intent = new Intent(context, MeetingListActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);

				// 이전에 생성되어 스택에 쌓인 액티비티를 finish
				MeetingListActivity meetingListActivity = (MeetingListActivity) MeetingListActivity.meetingListActivity;
				meetingListActivity.finish();

				finish();
			} else {
				Toast.makeText(context, "모임에 참여할 수 없습니다.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	Handler handler_cancle = new Handler() {
		public void handleMessage(Message msg) {
			Boolean ok = (Boolean) msg.obj;
			if (ok) {
				Toast.makeText(context, "참여를 취소합니다.", Toast.LENGTH_SHORT)
						.show();

				intent = new Intent(context, MeetingListActivity.class);
				intent.putExtra("user", user);

				startActivity(intent);

				// 이전에 생성되어 스택에 쌓인 액티비티를 finish
				MeetingListActivity meetingListActivity = (MeetingListActivity) MeetingListActivity.meetingListActivity;
				meetingListActivity.finish();

				finish();
			}
		}
	};
}
