package com.uxm.embeddedproject.group;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uxm.embeddedproject.Config;
import com.uxm.embeddedproject.ProjectManager;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupInfo;
import com.uxm.embeddedproject.data.GroupUser;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.meeting.MeetingInfoActivity;
import com.uxm.embeddedproject.meeting.MeetingListActivity;
import com.uxm.embeddedproject.menuitem.MenuActivity;

public class CreateGroupActivity extends MenuActivity implements
		OnClickListener {

	Button createGrp, createGrpBc;
	EditText createGrpName, createGrpPwd, createGrpChkPwd, createGrpInfo;
	User user;	//유저 객체 받아오기 위함
	String gName, gPwd, gInfo, gDate, gCheck;
	private Context context;
	Calendar now;	//현재 날짜 받아오기 위해
	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.create_group);

		user = (User) getIntent().getSerializableExtra("user");	//이전 인텐트로부터 유저객체 받아옴

		this.context = this;
		createGrpName = (EditText) findViewById(R.id.createGrpName);
		createGrpPwd = (EditText) findViewById(R.id.createGrpPwd);
		createGrpChkPwd = (EditText) findViewById(R.id.createGrpChkPwd);
		createGrpInfo = (EditText) findViewById(R.id.createGrpInfo);

		createGrp = (Button) findViewById(R.id.createGrp);
		createGrpBc = (Button) findViewById(R.id.createGrpBc);

		createGrp.setOnClickListener(this);
		createGrpBc.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.createGrpBc) {
			finish();
		}
		if (v.getId() == R.id.createGrp) {

			now = Calendar.getInstance();
			// 그룹 생성을 위해 grouoinfo객체에 필요한 정보를 set
			gName = createGrpName.getText().toString();
			gPwd = createGrpPwd.getText().toString();
			gInfo = createGrpInfo.getText().toString();
			gDate = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ (now.get(Calendar.DATE));
			gCheck = createGrpChkPwd.getText().toString();

			if (gPwd.equals(gCheck)) { // 두 비밀번호가 일치하는 경우
				GroupInfo groupInfo = new GroupInfo();
				groupInfo.setgName(gName);
				groupInfo.setgPasswd(gPwd);
				groupInfo.setgMaster(user.getNickName());
				groupInfo.setgInfo(gInfo);
				groupInfo.setgDate(gDate);
				// 그룹 생성을 위해 groupinfo객체를 서버에 전달
				com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
				msg.setMsg("createGroup");
				msg.setObj(groupInfo);
				ProjectManager manager = new ProjectManager(
						Config.SERVER_ADDRESS, Config.SERVER_PORT, handler, msg);
				manager.execute();

				findGIds(gName); // 그룹 생성과 동시에 마스터를 가입시키기 위해 groupinfo에서 id찾아오기
									// 위한 메서드
			} else { // 두 비밀번호가 일치하지 않는 경우 메시지 출력
				Toast.makeText(context, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// 그룹 생성 요청에 대한 전달을 받음
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Boolean ok = (Boolean) msg.obj;
			if (ok) { // 그룹이 성공적으로 생성되면
				Toast.makeText(context, "그룹이 생성되었습니다.", Toast.LENGTH_SHORT)
						.show();
				intent = new Intent(context, MeetingListActivity.class);
				intent.putExtra("user", user);	//다음 인텐트에 user객체 넘김
				startActivity(intent);

				// 이전에 생성되어 스택에 쌓인 액티비티를 finish
				try {
				MeetingListActivity meetingListActivity = (MeetingListActivity) MeetingListActivity.meetingListActivity;
				meetingListActivity.finish();
				} catch (Exception e) {
				}
				
				try {
					MyGroupActivity myGroupActivity = (MyGroupActivity) MyGroupActivity.myGroupActivity;
					myGroupActivity.finish();
				} catch (Exception e) {
				}

				try {
					MeetingInfoActivity meetingInfoActivity = (MeetingInfoActivity) MeetingInfoActivity.meetingInfoActivity;
					meetingInfoActivity.finish();
				} catch (Exception e) {
				}
				
				try {
					NoGroupActivity noGroupActivity = (NoGroupActivity) NoGroupActivity.noGroupActivity;
					noGroupActivity.finish();
				} catch (Exception e) {
				}

				finish();
			} else { // 그룹 생성에 실패하면(그룹명 중복)
				Toast.makeText(context, "그룹 생성에 실패하였습니다. 그룹명을 확인해주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	public void findGIds(String getGName) {

		// gname으로 groupinfo의 id를 찾기 위해
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("getGroup");
		msg.setObj(getGName);
		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, getGHandler, msg);
		manager.execute();
	}

	// 그룹에 대한 정보를 받아옴
	Handler getGHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			GroupInfo groupInfo2 = new GroupInfo();
			groupInfo2 = (GroupInfo) msg.obj;
			insertGroupUser(groupInfo2.getId());
		}
	};

	// 받은 id를 groupuser에 set해 가입을 요청
	public void insertGroupUser(int getafterGId) {
		GroupUser groupUser = new GroupUser();
		groupUser.setgId(getafterGId);
		groupUser.setuId(user.getId());
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("registerGroup");
		msg.setObj(groupUser);
		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, setGUHandler, msg);
		manager.execute();
	}

	Handler setGUHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		}
	};

}
