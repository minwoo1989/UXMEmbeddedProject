package com.uxm.embeddedproject;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.widget.Toast;

import com.uxm.embeddedproject.data.GroupUser;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.group.NoGroupActivity;
import com.uxm.embeddedproject.meeting.MeetingListActivity;

@SuppressLint({ "HandlerLeak" })
public class NFCReaderActivity extends Activity {

	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] readerFilters;
	private String id;
	private String pw;
	private ArrayList<GroupUser> groupUserList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 쓰레드 사용을 명시
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Foreground Dispatch System을 위한 PendingIntent, IntentFilter 설정
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// 인텐트 필터 추가
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);

		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}

		readerFilters = new IntentFilter[] { ndefDetected };
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = getNdefMessages(intent);

			// 읽어 온 id와 pw로 로그인
			id = new String(msgs[0].getRecords()[0].getPayload());
			pw = new String(msgs[0].getRecords()[1].getPayload());

			// MainActivity의 id, pw 체크 부분
			if (id != null && pw != null) {
				try {
					checkingGroup();

					com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
					msg.setMsg("getUser");
					msg.setObj(id);

					ProjectManager manager = new ProjectManager(
							Config.SERVER_ADDRESS, Config.SERVER_PORT, handler,
							msg);

					manager.execute();
				} catch (Exception e) {
					Toast.makeText(NFCReaderActivity.this,
							"서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Foreground Dispatch System 비활성화
		nfcAdapter.disableForegroundDispatch(this);

		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Foreground Dispatch System 활성화
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, readerFilters,
				null);
	}

	private NdefMessage[] getNdefMessages(Intent intent) {
		// Intent 파싱
		NdefMessage[] msgs = null;
		String action = intent.getAction();

		// NFC 액션은 찾았을 때
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];

				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type 일 때
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			finish();
		}

		return msgs;
	}

	/* 로그인 위해 보냈던 메시지 리턴 받고, 다음 스텝 넘어가는 부분 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			User user = (User) msg.obj; // 서버로부터 메시지를 받음
			try {
				if (user.getUserId().equals(id)) { // 받아온 메시지와 입력된 아이디 일치여부 확인
					if (user.getPassword().equals(pw)) { // 일치하면 패스워드가 일치하는지 확인
						if (groupUserList.size() == 0) { // 그룹이 하나도 없으면
							Intent intent = new Intent(NFCReaderActivity.this,
									NoGroupActivity.class); // NoGroupActivity로
															// 이동
							intent.putExtra("user", user); // 받은 user의 객체를 넘김
							startActivity(intent);

							finish();
						} else { // 그룹이 있을 때
							for (int i = 0; i < groupUserList.size(); i++) {// 내가
																			// 가입된
																			// 그룹이
																			// 있으면
								if (groupUserList.get(i).getuId() == user
										.getId()) {
									Intent intent = new Intent(
											NFCReaderActivity.this,
											MeetingListActivity.class); // MeetingListActivity로
																		// 이동
									intent.putExtra("user", user); // 받은 user의
																	// 객체 넘김
									startActivity(intent);

									finish();
									break; // 그룹이 있으면 반복문 중지
								} else { // 내가 가입된 그룹이 없다면
									Intent intent = new Intent(
											NFCReaderActivity.this,
											NoGroupActivity.class); // NoGroupActivity로
																	// 이동
									intent.putExtra("user", user);
									startActivity(intent);

									finish();
								}
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
			groupUserList = (ArrayList<GroupUser>) msg.obj;
		}
	};

}
