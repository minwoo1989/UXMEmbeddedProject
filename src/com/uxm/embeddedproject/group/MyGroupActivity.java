package com.uxm.embeddedproject.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.uxm.embeddedproject.Config;
import com.uxm.embeddedproject.ProjectManager;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupUser;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.meeting.MeetingListActivity;
import com.uxm.embeddedproject.menuitem.MenuActivity;

@SuppressLint("UseValueOf")
public class MyGroupActivity extends MenuActivity implements
		View.OnClickListener {

	// 액티비티가 스택에 쌓였을 때 지우기 위한 static 객체
	public static Activity myGroupActivity = null;

	Button makeGroupButton, findGroupButton, secedeGroupButton;
	User user;
	Context context;
	Intent intent;
	HashMap<String, Integer> map;	//나의 그룹이름과 그룹 id를 한번에 담아오기 위한 hashmap
	ArrayList<String> myGroupList;
	ArrayList<Integer> myGid;	
	int mSelected;
	GroupUser groupUser;
	ArrayAdapter<String> adapter, myGroupListAdapter;
	ListView myJoinedGroupList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.my_group);

		this.context = this;
		user = (User) getIntent().getSerializableExtra("user");

		makeGroupButton = (Button) findViewById(R.id.makeGroupButton);
		findGroupButton = (Button) findViewById(R.id.findGroupButton);
		secedeGroupButton = (Button) findViewById(R.id.secedeGroupButton);

		makeGroupButton.setOnClickListener(this);
		findGroupButton.setOnClickListener(this);
		secedeGroupButton.setOnClickListener(this);
		myJoinedGroupList = (ListView) findViewById(R.id.myJoinedGroupList);
		// 유저가 가입되어 있는 그룹을 모두 찾기 위한 메서드를 호출
		findMyGroup(user.getId());
		
		myGroupActivity = this;
	}

	// 그룹들을 찾기 위해 서버에 요청
	void findMyGroup(int uId) {
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("findCurUserGrp");
		msg.setObj(uId);

		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handler, msg);
		manager.execute();
	}

	// 서버로부터 리턴
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			map = (HashMap<String, Integer>) msg.obj; // 서버로부터 그룹의 이름과 그룹의 아이디가
														// 담긴 hashmap 받아옴
			Iterator<String> itName = map.keySet().iterator();
			Iterator<Integer> inId = map.values().iterator();
			myGroupList = new ArrayList<String>();
			myGid = new ArrayList<Integer>();
			myGroupList.clear();
			myGid.clear();// 두 어레이리스트 초기화 한 후

			while (itName.hasNext()) { // 그룹의 이름을 어레이리스트에 담음
				String str = itName.next();
				myGroupList.add(new String(str));
			}

			while (inId.hasNext()) { // 그룹의 id를 어레이리스트에 담음
				int i = inId.next();
				myGid.add(new Integer(i));
			}
			// 이 때, 두 어레이리스트의 size가 같으며, 같은 index라면 id와 그룹 이름이 한 그룹을 지칭함.

			// 리스트뷰
			myGroupListAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, myGroupList);
			myJoinedGroupList.setAdapter(myGroupListAdapter);
			myJoinedGroupList.setDivider(new ColorDrawable(Color.BLACK));
			myJoinedGroupList.setDividerHeight(2);
		}
	};

	public void onClick(View v) {
		if (v.getId() == R.id.makeGroupButton) {
			intent = new Intent(context, CreateGroupActivity.class);
			intent.putExtra("user", user);
			
			startActivity(intent); // 그룹 새로 생성하러
		} else if (v.getId() == R.id.findGroupButton) {
			intent = new Intent(context, GroupListActivity.class);
			intent.putExtra("user", user);
			startActivity(intent); // 기존에 있는 그룹을 찾으러
		} else if (v.getId() == R.id.secedeGroupButton) { // 그룹 탈퇴하기 위해
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			// 다이얼로그 생성 및 리스트 생성
			final ArrayAdapter<String> aa1 = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_single_choice,
					myGroupList);
			builder.setSingleChoiceItems(aa1, 0,
					new DialogInterface.OnClickListener() {
						// 다이얼로그의 리스트에서 하나의 아이템만을 선택할 수 있게 함
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mSelected = which;
						}
					})
					.setPositiveButton("탈퇴",
							new DialogInterface.OnClickListener() {
								// 탈퇴신청을 서버에 요청.
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									groupUser = new GroupUser();
									groupUser.setuId(user.getId());
									groupUser.setgId(myGid.get(mSelected));	//탈퇴시 그룹유저로부터 정보 지워지므로, 검색시 쓸 내용을 셋
									com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
									msg.setMsg("leaveGroup");
									msg.setObj(groupUser);
									ProjectManager manager = new ProjectManager(
											Config.SERVER_ADDRESS,
											Config.SERVER_PORT, leave_handler,
											msg);
									manager.execute();
									
									intent = new Intent(context,
											MeetingListActivity.class);
									intent.putExtra("user", user);
									
									startActivity(intent);
									
									// 이전에 생성되어 스택에 쌓인 액티비티를 finish
									MeetingListActivity meetingListActivity = (MeetingListActivity) MeetingListActivity.meetingListActivity;
									meetingListActivity.finish();
									
									finish();
								}
							}).setNegativeButton("취소", null).create().show();

		}
	}

	Handler leave_handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Boolean ok = (Boolean) msg.obj;
			if (ok) {
				Toast.makeText(context, "그룹을 탈퇴했습니다.", Toast.LENGTH_SHORT)
						.show();
				intent = new Intent(context, MeetingListActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
			} else {
				Toast.makeText(context, "실패했습니다.", Toast.LENGTH_SHORT).show();
			}
		}
	};

}
