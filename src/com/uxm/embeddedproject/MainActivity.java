package com.uxm.embeddedproject;

import java.util.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.*;

import com.uxm.embeddedproject.data.*;
import com.uxm.embeddedproject.group.NoGroupActivity;
import com.uxm.embeddedproject.meeting.MeetingListActivity;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements View.OnClickListener {

	GroupUser groupUser;	//그룹의 유저를 검사하기 위해 받아오는 객체
	Intent intent;
	EditText loginId, loginPassword;	
	Button loginButton, joinButton;
	String inputId, inputPassword;	//입력할 아이디, 패스워드
	ArrayList<GroupUser> groupUserList;	//어레이리스트로 담아온 그룹유저 리스트를 담음
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.main);
		this.context = this;

		startActivity(new Intent(this, LoadingScreen.class)); // 메인 시작 전에 로고를
																// 띄어줌

		loginButton = (Button) findViewById(R.id.loginButton);
		joinButton = (Button) findViewById(R.id.joinButton);

		loginId = (EditText) findViewById(R.id.loginId);
		loginPassword = (EditText) findViewById(R.id.loginPassword);

		loginButton.setOnClickListener(this);
		joinButton.setOnClickListener(this);
	}

	public void onClick(View v) {

		if (v.getId() == R.id.joinButton) {
			/* 아이디 새로 생성 */
			intent = new Intent(context, JoinActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.loginButton) {
//			try {
				checkingGroup(); // 그룹의 여부를 체크

				/* 로그인 */
				inputId = loginId.getText().toString();
				inputPassword = loginPassword.getText().toString();

				/* 로그인시 아이디, 비번 체크 위해 서버에 메시지 보냄 */
				com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();

				msg.setMsg("getUser");	//서버에 보낼 요청 메시지
				msg.setObj(inputId);	//함께 보내는 객체

				ProjectManager manager = new ProjectManager(
						Config.SERVER_ADDRESS, Config.SERVER_PORT, handler, msg);	//서버에 연결
				manager.execute();	//수행
//			} catch (Exception e) {
//				Toast.makeText(MainActivity.this, "서버와의 통신이 원활하지 않습니다.",
//						Toast.LENGTH_SHORT).show();
//			}
		}
	}

	/* 로그인 위해 보냈던 메시지 리턴 받고, 다음 스텝 넘어가는 부분 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			User user = (User) msg.obj; // 서버로부터 메시지를 받음

			try {
				if (user.getUserId().equals(inputId)) { // 받아온 메시지와 입력된 아이디 일치여부
														// 확인
					if (user.getPassword().equals(inputPassword)) { // 일치하면
																	// 패스워드가
																	// 일치하는지 확인
						if (groupUserList.size() == 0) { // 그룹이 하나도 없으면
							intent = new Intent(MainActivity.this,
									NoGroupActivity.class); // NoGroupActivity로
															// 이동
							intent.putExtra("user", user); // 받은 user의 객체를 넘김
							startActivity(intent);

							finish();
						} else { // 그룹이 있을 때
							boolean breakFlag = false; // 그룹에 가입되어있다면 
														//NoGroupActivity를 실행하지 않기 위해 
														// 사용하는 Flag

							for (int i = 0; i < groupUserList.size(); i++) {// 내가
																			// 가입된
																			// 그룹이
																			// 있으면
								if (groupUserList.get(i).getuId() == user
										.getId()) {
									intent = new Intent(MainActivity.this,
											MeetingListActivity.class); // MeetingListActivity로
																		// 이동
									intent.putExtra("user", user); // 받은 user의
																	// 객체 넘김
									startActivity(intent);

									finish();

									breakFlag = true;
									break; // 그룹이 있으면 반복문 중지
								}
							}

							if (!breakFlag) { // 내가 가입된 그룹이 없다면
								intent = new Intent(MainActivity.this,
										NoGroupActivity.class); // NoGroupActivity로
																// 이동
								intent.putExtra("user", user);
								startActivity(intent);

								finish();
							}
						}
					} else { // 비밀번호가 일치하지 않는 경우 메시지 출력
						Toast.makeText(getApplicationContext(),
								"비밀번호를 잘못 입력하셨습니다.", Toast.LENGTH_SHORT).show();
					}
				}
			} catch (NullPointerException e) { // 입력한 아이디가 존재하지 않는 경우 메시지 출력
				Toast.makeText(getApplicationContext(), "아이디가 없습니다.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	public void checkingGroup() { // 그룹의 유무를 체크하기 위해
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("getGroupUsers"); // 서버로부터 그룹과, 해당 그룹의 유저 정보를 가져오기 위한 메시지 보냄

		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handlerGU, msg);
		manager.execute();
	}

	Handler handlerGU = new Handler() { // 요청했던 메시지를 받고 어레이리스트에 넣음
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			groupUserList = (ArrayList<GroupUser>) msg.obj;	//받아온 객체를 타입 캐스팅 
		}
	};
}
