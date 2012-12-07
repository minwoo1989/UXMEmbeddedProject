package com.uxm.embeddedproject;

import com.uxm.embeddedproject.data.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class JoinActivity extends Activity implements OnClickListener {

	private Context context;

	Button joinBackButton, joinNowButton;
	EditText joinId, joinPassword, joinCheckPassword, joinNickname, joinPhone;
	String userId, password, checkPassword, nickName, phoneNum;
	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.join_user);

		this.context = this;

		joinId = (EditText) findViewById(R.id.joinId);
		joinPassword = (EditText) findViewById(R.id.joinPassword);
		joinCheckPassword = (EditText) findViewById(R.id.joinCheckPassword);
		joinNickname = (EditText) findViewById(R.id.joinNickname);
		joinPhone = (EditText) findViewById(R.id.joinPhone);

		joinNowButton = (Button) findViewById(R.id.joinNowButton);
		joinBackButton = (Button) findViewById(R.id.joinBackButton);
		joinNowButton.setOnClickListener(this);
		joinBackButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		if (v.getId() == R.id.joinBackButton) {
			finish();
		}
		if (v.getId() == R.id.joinNowButton) {
			// 회원 가입에 필요한 정보들을 user 객체에 set.
			userId = joinId.getText().toString();
			password = joinPassword.getText().toString();
			nickName = joinNickname.getText().toString();
			phoneNum = joinPhone.getText().toString();
			checkPassword = joinCheckPassword.getText().toString();
			
			if (password.equals(checkPassword)) { // 비밀번호 두 개가 모두 일치면
				User user = new User();
				user.setUserId(userId);
				user.setPassword(password);
				user.setNickName(nickName);
				user.setPhoneNum(phoneNum);
				// 회원가입을 위해 set한 user객체를 서버에 전달
				com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
				msg.setMsg("registerUser");
				msg.setObj(user);
				ProjectManager manager = new ProjectManager(
						Config.SERVER_ADDRESS, Config.SERVER_PORT, handler, msg);
				manager.execute();
			} else { // 비밀번호가 일치하지 않으면
				Toast.makeText(context, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// 요청한 메시지를 리턴받음
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Boolean ok = (Boolean) msg.obj;
			if (ok) {
				Toast.makeText(context, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT)
						.show();
				intent = new Intent(context, MainActivity.class);
				startActivity(intent);
				
				finish();
			} else {
				Toast.makeText(context, "회원가입이 실패했습니다.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
}
