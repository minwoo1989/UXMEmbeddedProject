package com.uxm.embeddedproject.group;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.uxm.embeddedproject.Config;
import com.uxm.embeddedproject.ProjectManager;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupInfo;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.menuitem.MenuActivity;

public class GroupListActivity extends MenuActivity implements OnClickListener {

	Button backButton;
	Intent intent;
	ListView groupList;
	ArrayList<String> list;
	ArrayList<GroupInfo> groupInfoList;	//그룹의 정보를 받아오기 위한 어레이리스트
	ArrayAdapter<String> adapter;	//리스트뷰 연결 위한 어댑터
	GroupInfo groupInfo;
	Context context;
	User user;

	/* 내가 가입하지 않은 그룹의 리스트를 뽑아오는 클래스 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.group_list);

		user = (User) getIntent().getSerializableExtra("user");

		this.context = this;
		groupList = (ListView) findViewById(R.id.groupList);

		backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		// getGroups는 내가 가입되어 있지 않은 그룹들만을 뽑아오기 위해 서버에 요청하는 메시지
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("getGroups");
		msg.setObj(user.getId());

		ProjectManager projectManager = new ProjectManager(
				Config.SERVER_ADDRESS, Config.SERVER_PORT, handler, msg);
		projectManager.execute();

	}

	// 리턴받은 걸 groupinfo를 담는 어레이리스트에 넣음.
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			groupInfoList = (ArrayList<GroupInfo>) msg.obj;
			lookList(); // 리스트 보여주기 위한 메서드 호출
		}
	};

	// 그룹의 이름을 string형 어레이리스트에 담음
	public void lookList() {
		list = new ArrayList<String>();
		for (int i = 0; i < groupInfoList.size(); i++) {	//리스트에 띄워줄 그룹의 이름을 그룹인포로부터 받음
			list.add(groupInfoList.get(i).getgName());
		}
		if (list.size() == 0) {

		} else {// 어댑터에 리스트 연결
			adapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, list);
			groupList.setAdapter(adapter);
			groupList.setDivider(new ColorDrawable(Color.BLACK));
			groupList.setDividerHeight(2);
			groupList.setOnItemClickListener(itemClickListenerOfList);

		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.backButton) {
			finish();
		}

	}

	// 리스트를 클릭하면 해당 그룹에 대한 자세한 정보를 출력하는 groupinfoactivity로 이동
	private OnItemClickListener itemClickListenerOfList = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {

			intent = new Intent(context, GroupInfoActivity.class);
			intent.putExtra("gName", list.get(position));
			intent.putExtra("user", user);	//받은 그룹이름과 유저를 다음 인텐트에 보냄.
			startActivity(intent);

		}
	};
}
