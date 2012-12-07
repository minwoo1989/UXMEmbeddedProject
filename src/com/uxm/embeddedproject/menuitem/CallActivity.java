package com.uxm.embeddedproject.menuitem;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.uxm.embeddedproject.R;

public class CallActivity extends Activity implements OnClickListener {
	Button call_search;
	ArrayList<String> name_list;
	ArrayList<String> num_list;
	ArrayList<String> list;
	ListView listView;
	ArrayAdapter<String> adapter;
	Context context;
	Intent intent;
	int mSelected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call);
		this.context = this;
		call_search = (Button) findViewById(R.id.call_serach);
		listView = (ListView) findViewById(R.id.listNum);
		call_search.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.call_serach) {	//내 주소록ㄱ에 있는 번호를 검색함
			ContentResolver cr = getContentResolver();	//컨텐트 리졸버를 사용
			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
					null, null, null, null);	//주소록과 관련된 URI및 상수들으느 CONTRACTCONTENT에 들어있으므로

			int ididx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			int nameidx = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);	//저장된 이름

			name_list = new ArrayList<String>();
			num_list = new ArrayList<String>();	//이름 및 번호 를 저장하기 위한 리스트.. INDEX동일하면 같은 사람의 정보
			while (cursor.moveToNext()) {

				String id = cursor.getString(ididx);
				Cursor cursor2 = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[] { id }, null);

				int typeidx = cursor2
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
				int numidx = cursor2
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				while (cursor2.moveToNext()) {
					String num = cursor2.getString(numidx);
					switch (cursor2.getInt(typeidx)) {	//전화번호는 타입에 따라 여러가지가 존재함
					case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
						name_list.add(cursor.getString(nameidx));
						num_list.add(num);
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
						name_list.add(cursor.getString(nameidx));
						num_list.add(num);
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
						name_list.add(cursor.getString(nameidx));
						num_list.add(num);
						break;
					}	//이름 및 번호의 리스트를 ADD시킴
				}
				cursor2.close();
			}
			cursor.close();
			list = new ArrayList<String>();
			for (int i = 0; i < num_list.size(); i++) {
				list.add(name_list.get(i) + " : " + num_list.get(i));
			}
			adapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, list);
			listView.setAdapter(adapter);
			listView.setDivider(new ColorDrawable(Color.BLACK));
			listView.setDividerHeight(2);
			listView.setOnItemClickListener(itemClickListenerOfList);
			//만든 리스트를 리스트뷰에 등록시킴. 각 리스트 누르면 다이알로그창 띄움
		}
	}

	private OnItemClickListener itemClickListenerOfList = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			mSelected = position;
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("선택하세요")
					.setMessage("어떻게 하시겠습니까?")
					.setPositiveButton("전화",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Uri number;
									number = Uri.parse("tel:"
											+ num_list.get(mSelected));
									intent = new Intent(Intent.ACTION_CALL,
											number);
									startActivity(intent);
								}
							});
			builder.setNegativeButton("문자",	//메시지보내기
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									context);
							final LinearLayout linear = (LinearLayout) View
									.inflate(context, R.layout.dialog, null);
							builder.setTitle("문자 내용 입력")
									.setView(linear)
									.setPositiveButton(
											"보내기",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// method stub
													final EditText msg = (EditText) linear
															.findViewById(R.id.msg);
													String msg_str = msg
															.getText()
															.toString();

													SmsManager sms = SmsManager
															.getDefault();	//문자관리자는 시스템이 관리하므로 바로 사용 가능함
													PendingIntent SentIntent = PendingIntent
															.getBroadcast(
																	context,
																	0,
																	new Intent(
																			"ACTION_MESSAGE_SENT"),
																	0);
													sms.sendTextMessage(
															num_list.get(mSelected),
															null, msg_str,
															SentIntent, null);
												}
											});
							builder.setNegativeButton("취소",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									});
							builder.create().show();
						}
					});
			builder.create().show();

		}
	};
	//문자 전송에 성공하면 브로드캐스트리시버를 사용해 결과를 토스트에 띄워줌
	public void onResume() {
		super.onResume();
		registerReceiver(mSentBR, new IntentFilter("ACTION_MESSAGE_SENT"));
	}

	BroadcastReceiver mSentBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (getResultCode() == Activity.RESULT_OK) {
				Toast.makeText(context, "메시지 송신 성공", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "메시지 송신 실패", Toast.LENGTH_SHORT).show();
			}
		}
	};
}
