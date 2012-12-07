package com.uxm.embeddedproject.meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uxm.embeddedproject.Config;
import com.uxm.embeddedproject.ProjectManager;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupBoard;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.database.DatabaseHelper;
import com.uxm.embeddedproject.menuitem.MenuActivity;
import com.uxm.embeddedproject.web.WebActivity;

@SuppressLint({ "HandlerLeak", "UseValueOf" }) 
public class WriteActivity extends MenuActivity implements View.OnClickListener {

	Intent intent;
	Button completeButton, cancelButton, searchLoca, meetingListBtn;
	Calendar now;
	private int gCreateY, gCreateM, gCreateD, gCreateH, gCreateMI;
	private int gMeetY, gMeetM, gMeetD, gMeetH, gMeetMI;
	EditText meetingContent, meetingTitle;
	Context context;
	User user;
	ArrayList<String> myGroupList;
	ArrayList<Integer> myGid;
	HashMap<String, Integer> map;
	int mSelected;
	GroupBoard groupBoard;
	DatePicker mDatePicker;
	TimePicker mTimePicker;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private TextView latText;
	private TextView lonText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().permitAll().build());
		setContentView(R.layout.write);

		this.context = this;

		user = (User) getIntent().getSerializableExtra("user");

		meetingTitle = (EditText) findViewById(R.id.meeting_title);
		meetingContent = (EditText) findViewById(R.id.meetingContents);
		completeButton = (Button) findViewById(R.id.completeButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		searchLoca = (Button) findViewById(R.id.searchLoca);
		meetingListBtn = (Button) findViewById(R.id.meetingListBtn);
		mDatePicker = (DatePicker) findViewById(R.id.mDatePicker);
		mTimePicker = (TimePicker) findViewById(R.id.mTimePicker);
		latText = (TextView) findViewById(R.id.latText);
		lonText = (TextView) findViewById(R.id.lonText);

		mDatePicker.init(mDatePicker.getYear(), mDatePicker.getMonth(),
				mDatePicker.getDayOfMonth(),
				new DatePicker.OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						gMeetY = year;
						gMeetM = monthOfYear + 1;
						gMeetD = dayOfMonth;
					}
				});
		mTimePicker
				.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

					@Override
					public void onTimeChanged(TimePicker view, int hourOfDay,
							int minute) {
						gMeetH = hourOfDay;
						gMeetMI = minute;
					}
				});

		searchLoca.setOnClickListener(this);
		meetingListBtn.setOnClickListener(this);
		completeButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		findMyGroup(user.getId());	//그룹 리스트 검색 위해 호출

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				latitude = data.getDoubleExtra("lat", 0.0);
				longitude = data.getDoubleExtra("lon", 0.0);
				
				latText.setText("Lat = " + latitude);
				lonText.setText("Lon = " + longitude);
			}
		}
	}

	void findMyGroup(int uId) {
		com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
		msg.setMsg("findCurUserGrp");	//요청할 메시지
		msg.setObj(uId);	

		ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
				Config.SERVER_PORT, handler, msg);
		manager.execute();
	}

	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			map = (HashMap<String, Integer>) msg.obj;
			Iterator<String> itName = map.keySet().iterator();
			Iterator<Integer> inId = map.values().iterator();	//받은 그룹이름, ID
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

		}
	};

	public void onClick(View v) {
		// 내가 속한 그룹 리스트로 출력
		if (v.getId() == R.id.meetingListBtn) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			final ArrayAdapter<String> aa1 = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_single_choice,
					myGroupList);
			builder.setSingleChoiceItems(aa1, 0,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mSelected = which;

						}
					})
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									meetingListBtn.setText(myGroupList
											.get(mSelected));
								}
							}).setNegativeButton("취소", null).create().show();

		}
		// 맵에서 위치정보를 받음
		else if (v.getId() == R.id.searchLoca) {
			Intent i = new Intent(WriteActivity.this, WebActivity.class);
			startActivityForResult(i, 1);
		}
		// 완료 버튼 클릭
		else if (v.getId() == R.id.completeButton) {
			groupBoard = new GroupBoard();
			now = Calendar.getInstance();

			String gTitle, gContent;

			gTitle = meetingTitle.getText().toString();
			gContent = meetingContent.getText().toString();

			gCreateY = now.get(Calendar.YEAR);
			gCreateM = (now.get(Calendar.MONTH) + 1);
			gCreateD = now.get(Calendar.DATE);
			gCreateH = now.get(Calendar.HOUR);
			gCreateMI = now.get(Calendar.MINUTE);
			
			if (gMeetY == 0) {
				gMeetY = gCreateY;
			}

			if (gMeetM == 0) {
				gMeetM = gCreateM;
			}

			if (gMeetD == 0) {
				gMeetD = gCreateD;
			}

			if (gMeetH == 0) {
				gMeetH = gCreateH;
			}
			if (gMeetMI == 0) {
				gMeetMI = gCreateMI;
			}

			groupBoard.setgId(myGid.get(mSelected));
			groupBoard.setuId(user.getId());
			groupBoard.setgTitle(gTitle);
			groupBoard.setgContent(gContent);
			groupBoard.setgCreateY(gCreateY);
			groupBoard.setgCreateM(gCreateM);
			groupBoard.setgCreateD(gCreateD);
			groupBoard.setgCreateH(gCreateH);
			groupBoard.setgCreateMI(gCreateMI);
			groupBoard.setgMeetY(gMeetY);
			groupBoard.setgMeetM(gMeetM);
			groupBoard.setgMeetD(gMeetD);
			groupBoard.setgMeetH(gMeetH);
			groupBoard.setgMeetMI(gMeetMI);
			groupBoard.setgLatitude(latitude);
			groupBoard.setgLongtitude(longitude);
			
			com.uxm.embeddedproject.data.EmbedMessage msg = new com.uxm.embeddedproject.data.EmbedMessage();
			msg.setMsg("writeText");
			msg.setObj(groupBoard);
			
			ProjectManager manager = new ProjectManager(Config.SERVER_ADDRESS,
					Config.SERVER_PORT, handler_write, msg);
			
			manager.execute();

		}
		// 취소 버튼 클릭
		else if (v.getId() == R.id.cancelButton) {
			Toast.makeText(this, "모임 등록 취소", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	Handler handler_write = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Boolean ok = (Boolean) msg.obj;
			
			if (ok) {
				DatabaseHelper helper = new DatabaseHelper(context);
				SQLiteDatabase db = helper.getWritableDatabase();
				ContentValues cv = new ContentValues();
				
				cv.put("title", meetingTitle.getText().toString());
				cv.put("latitude", latitude);
				cv.put("longitude", longitude);
				
				db.insert("location", null, cv);
				
				db.close();
				helper.close();
				
				Toast.makeText(context, "모임 등록 완료", Toast.LENGTH_SHORT).show();
				
				intent = new Intent(context, MeetingListActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);

				// 이전에 생성되어 스택에 쌓인 액티비티를 finish
				MeetingListActivity meetingListActivity = (MeetingListActivity) MeetingListActivity.meetingListActivity;
				meetingListActivity.finish();
				
				finish();
			} else {
				Toast.makeText(context, "모임 등록 실패", Toast.LENGTH_SHORT).show();
				
				finish();
			}
		}
	};
	
	
	
	
}
