package com.uxm.embeddedproject.meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.uxm.embeddedproject.Config;
import com.uxm.embeddedproject.ProjectManager;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupBoard;
import com.uxm.embeddedproject.data.GroupInfo;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.group.MyGroupActivity;
import com.uxm.embeddedproject.menuitem.MenuActivity;

@SuppressLint({ "UseSparseArrays", "UseValueOf", "HandlerLeak"})
public class MeetingListActivity extends MenuActivity implements
		View.OnClickListener {

	// 액티비티가 스택에 쌓였을 때 지우기 위한 static 객체
	public static Activity meetingListActivity = null;

	Button writeButton, myGroupButton;
	User user;
	Intent intent;
	ListView newGroupBoard, attendanceGroup;
	ArrayAdapter<String> adapter, myAdapter;
	ArrayList<String> list, myList, gName;
	ArrayList<GroupBoard> groupBoardList;	//내가 그룹에서 쓴 글을 담을 리스트
	ArrayList<GroupBoard> groupTextList, myGroupTextList;	//그룹텍스트리스트는 날짜걸러낸 보드리스트, MYGROUPTEXTLIST는 내가 참여하기로 한 
																//리스트
	int todayY, todayM, todayD;
	Context context;
	Calendar now;
	GroupInfo groupInfo;
	HashMap<String, Integer> map;
	ArrayList<String> myGroupList;	//그룹 이름 담을 리스트
	ArrayList<Integer> myGid;	//그룹아이디 담을 리스트
	ArrayList<GroupBoard> myMeetList;	//내가 참여하기로 한 그룹의 리스트를 가져오기 위한것
	ArrayList<String> myMeetListTitle; //내가 참여하기로 한 그룹의 리스트에 들어갈 글
	HashMap<Integer, String> myGroupMap;

	ArrayAdapter<String> myMeetAdapter;	//내가 참여하기로 한 그룹의 리스트를 등록하기 위한 어댑터
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.meeting_list);

		now = Calendar.getInstance();

		this.context = this;

		user = (User) getIntent().getSerializableExtra("user");
		groupTextList = new ArrayList<GroupBoard>();
		
		writeButton = (Button) findViewById(R.id.writeButton);
		myGroupButton = (Button) findViewById(R.id.myGroupButton);
		newGroupBoard = (ListView) findViewById(R.id.new_group_board);
		attendanceGroup = (ListView) findViewById(R.id.attendance_group);

		writeButton.setOnClickListener(this);
		myGroupButton.setOnClickListener(this);

		// 내가 가입된 그룹에 쓰여진 글들을 출력하기 위한 내가 가입된 그룹을 먼저 찾아옴.
		findMyGroup(user.getId());
		lookMyList();	//내가 참석하려는 모임의 리스트

		meetingListActivity = this;
	}
	//내가 가입된 그룹을 찾아오기 위한 메소드
	void findMyGroup(int uId) {
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("findCurUserGrp");	//서버에 보낼 메시지
		msg.setObj(uId);	//user의 id로 검색해야 하므로 값 SET

		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handler_map, msg);
		manager.execute();	//요청
	}

	// 가입된 그룹의 이름과 ID를 리턴받음.
	Handler handler_map = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			map = (HashMap<String, Integer>) msg.obj;
			Iterator<String> itName = map.keySet().iterator();	//그룹의 이름을 담음
			Iterator<Integer> inId = map.values().iterator();	//그룹의 ID를 담음
			
			myGroupMap = new HashMap<Integer, String>();
			while (itName.hasNext()) {
				String str = itName.next();
				//myGroupList.add(new String(str));
				myGroupMap.put(map.get(str), str);
			}
			
			myGroupList = new ArrayList<String>();
			myGid = new ArrayList<Integer>();
			while (itName.hasNext()) {
				String str = itName.next();
				myGroupList.add(new String(str));
			}
			while (inId.hasNext()) {
				int i = inId.next();
				myGid.add(new Integer(i));
			}

			getGroupBoardList(user.getId());
		}
	};

	// 내가 가입된 그룹에서 쓰여진 글들을 가져오기 위해 서버에 요청
	public void getGroupBoardList(int uId) {
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("getWrites");
		msg.setObj(uId);

		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handler, msg);
		manager.execute();
	}

	// 내가 가입된 그룹에스 쓰여진 글들을 리턴받음.
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			groupBoardList = (ArrayList<GroupBoard>) msg.obj;
			// 날짜 비교
			todayY = now.get(Calendar.YEAR);
			todayM = (now.get(Calendar.MONTH) + 1);
			todayD = now.get(Calendar.DATE); // 현재 날짜

			gName = new ArrayList<String>();

			groupTextList.clear();
			for (int i = 0; i < groupBoardList.size(); i++) {
				if (groupBoardList.get(i).getgMeetY() == todayY) {
					if ((groupBoardList.get(i).getgMeetM() == todayM)) {
						if (groupBoardList.get(i).getgMeetD() >= todayD) {
							groupTextList.add(groupBoardList.get(i));
						}
					} else if (groupBoardList.get(i).getgMeetM() > todayM) {
						groupTextList.add(groupBoardList.get(i));
					}
				} else if (groupBoardList.get(i).getgMeetY() > todayY) {
					groupTextList.add(groupBoardList.get(i));
				}
			} // 모임 예정일이 현재 날짜보다 뒤인 경우를 걸러내기 위해. 예정일이 현재 날짜보다 뒤에 있다면 새로운 어레이리스트에
				// 담음 걸러져서 나온게 GROUPTextList 임.
			gName.clear();
			boolCheck();
		}
	};

	public void boolCheck() {
		gName.clear();
		Log.i("SIZE : ", groupTextList.size() + "");
		for (int i = 0; i < groupTextList.size(); i++) {
			for (int j = 0; j < myGid.size(); j++) {
				if (groupTextList.get(i).getgId() == myGid.get(j)) {
					Log.i("INDEX : ", i + ", " + j);
					gName.add(myGroupMap.get(myGid.get(j)));
					//gName.add(myGroupList.get(i));
				}
			}
		}
		Log.e("size1",
				groupTextList.size() + "/" + gName.size() + "/" + myGid.size()
						+ "/");
		lookGroupList(); // 그룹의 최신글
	}

	// 내가 참여하기로 한 모임의 글
	public void lookMyList() {
		
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("getMyWrites");
		msg.setObj(user.getId());
		
		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS, Config.SERVER_PORT, handler_myList, msg);
		manager.execute();
	}
	
	Handler handler_myList = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			myMeetList = new ArrayList<GroupBoard>();
			myMeetList.clear();	//날짜가 지나면 리스트상에서만 제거되므로  초기화 해둠
			myMeetList = (ArrayList<GroupBoard>) msg.obj;	//그룹보드 리스트를 담아옴
			myMeetListTitle = new ArrayList<String>();
			myMeetListTitle.clear();
			for(int i = 0; i < myMeetList.size(); i++) {
				myMeetListTitle.add(myMeetList.get(i).getgTitle() + "\t"
						+ myMeetList.get(i).getgMeetY() + "/"
						+ myMeetList.get(i).getgMeetM() + "/"
						+ myMeetList.get(i).getgMeetD() + "  "
						+ myMeetList.get(i).getgMeetH() + ":"
						+ myMeetList.get(i).getgMeetMI());
			}
			if(myMeetList.size() == 0) {
				
			} else {
				myMeetAdapter = new ArrayAdapter<String>(context, R.layout.meeting_list_style, myMeetListTitle);
				attendanceGroup.setAdapter(myMeetAdapter);
				attendanceGroup.setDivider(new ColorDrawable(Color.BLACK));
				attendanceGroup.setDividerHeight(2);
				attendanceGroup.setOnItemClickListener(itemClickListenerOfMyList);
			}
			
			
		}
	};
	//내가 속한 그룹에 업로드된 글을 모두 가져옴
	public void lookGroupList() {
		list = new ArrayList<String>();
		for (int i = 0; i < groupTextList.size(); i++) {
			list.add( gName.get(i) + " - "
					+ groupTextList.get(i).getgTitle()
					+ "\t"
					+ (groupTextList.get(i).getgCreateY() + "/"
							+ groupTextList.get(i).getgCreateM() + "/" + groupTextList
							.get(i).getgCreateD()));
		}
		if (list.size() == 0) {

		} else {
			adapter = new ArrayAdapter<String>(context,
					R.layout.meeting_list_style, list);
			newGroupBoard.setAdapter(adapter);
			newGroupBoard.setDivider(new ColorDrawable(Color.BLACK));
			newGroupBoard.setDividerHeight(2);
			newGroupBoard.setOnItemClickListener(itemClickListenerOfList);
		}

	}

	public void onClick(View v) {
		if (v.getId() == R.id.writeButton) {
			intent = new Intent(MeetingListActivity.this, WriteActivity.class);
			intent.putExtra("user", user);
			startActivity(intent);
		} else if (v.getId() == R.id.myGroupButton) {
			intent = new Intent(MeetingListActivity.this, MyGroupActivity.class);
			intent.putExtra("user", user);
			startActivity(intent);
		}
	}

	// 각 리스트의 아이템을 선택하면 글에 대한 자세한 정보를 볼 수 있음.
	private OnItemClickListener itemClickListenerOfList = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			intent = new Intent(context, MeetingInfoActivity.class);
			intent.putExtra("user", user);
			intent.putExtra("groupBoard", groupTextList.get(position));
			 intent.putExtra("gName", gName.get(position));
			startActivity(intent);
		}
	};

	private OnItemClickListener itemClickListenerOfMyList = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			intent = new Intent(context, MeetingInfoActivity.class);
			intent.putExtra("user", user);
			intent.putExtra("groupBoard", myMeetList.get(position));
			intent.putExtra("gName", gName.get(position));
			startActivity(intent);
		}
	};
}
