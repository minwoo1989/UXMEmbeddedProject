package com.uxm.embeddedproject.alarm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupBoard;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.meeting.MeetingListActivity;

public class AlarmSetActivity extends Activity {
	private Context mContext = null;
	private AlarmManager mAlarmManager = null;
	private EditText mYearEditText = null, mMonthEditText = null,	//희망 알람시간을 입력받기 위한 edittext
			mDateEditText = null, mHourEditText = null, mMinuteEditText = null;
	private Button mSetAlarmBtn = null;
	
	private User user;
	private GroupBoard groupBoard;
	private String gName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.alarm_set);

		Intent preIntent = getIntent();
		
		//인텐트로 user, groupboard, gname 정보 받아서 복원
		user = (User) preIntent.getSerializableExtra("user");
		groupBoard = (GroupBoard) preIntent
				.getSerializableExtra("groupBoard");
		gName = getIntent().getStringExtra("gName");

		mContext = this;
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); //알람매니져에 인텐트를 갖고옴

		//edittext에서 입력받은 값 변수에 저장
		mYearEditText = (EditText) findViewById(R.id.main_layout_input_year);
		mMonthEditText = (EditText) findViewById(R.id.main_layout_input_month);
		mDateEditText = (EditText) findViewById(R.id.main_layout_input_date);
		mHourEditText = (EditText) findViewById(R.id.main_layout_input_hour);
		mMinuteEditText = (EditText) findViewById(R.id.main_layout_input_minute);

		mSetAlarmBtn = (Button) findViewById(R.id.main_layout_set_alarm);

		mSetAlarmBtn.setOnClickListener(new ButtonEventHandler());

		
		//이전 인텐트 값을 받아옴
		int year = preIntent.getIntExtra("year", 2012);
		int month = preIntent.getIntExtra("month", 12);
		int day = preIntent.getIntExtra("day", 0);
		int hour = preIntent.getIntExtra("hour", 0);
		int minute = preIntent.getIntExtra("minute", 0);

		
		//edittext 창에 값을 설정해놓음
		mYearEditText.setText("" + year);
		mMonthEditText.setText("" + month);
		mDateEditText.setText("" + day);
		mHourEditText.setText("" + hour);
		mMinuteEditText.setText("" + minute);
		
		
		//ImmortalService에 인텐트 값 넘겨줌
		Intent i = new Intent(AlarmSetActivity.this, ImmortalService.class);
		
		i.putExtra("user", user);
		i.putExtra("groupBoard", groupBoard);
		i.putExtra("gName", gName);
		
		startService(i);
	}

	private long getAlarmTime() {
		GregorianCalendar today = new GregorianCalendar(Locale.KOREA);	//한국시각

		//edittext에 사용자가 입력한 값을 변수에 저장
		int year = Integer.parseInt(mYearEditText.getText().toString());
		int month = Integer.parseInt(mMonthEditText.getText().toString());
		int date = Integer.parseInt(mDateEditText.getText().toString());
		int hour = Integer.parseInt(mHourEditText.getText().toString());
		int minute = Integer.parseInt(mMinuteEditText.getText().toString());

		//알람설정시간이 현재시간보다 미래일때
		if (today.get(Calendar.YEAR) <= year
				&& today.get(Calendar.MONTH) + 1 <= month
				&& today.get(Calendar.DATE) <= date
				&& today.get(Calendar.HOUR) <= hour
				&& today.get(Calendar.MINUTE) < minute) {
			
			//리턴할 알람시간 객체 사용자가 입력한 시간으로 설정
			GregorianCalendar alarmTime = new GregorianCalendar();
			alarmTime.set(Calendar.YEAR, year);
			alarmTime.set(Calendar.MONTH, month - 1);
			alarmTime.set(Calendar.DATE, date);
			alarmTime.set(Calendar.HOUR, hour);
			alarmTime.set(Calendar.MINUTE, minute);
			alarmTime.set(Calendar.SECOND, 0);

			return alarmTime.getTimeInMillis();
		}

		return -1;
	}

	private class ButtonEventHandler implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int id = v.getId();

			if (id == R.id.main_layout_set_alarm) {
				long alarmTime = getAlarmTime();

				//알람시간 설정이 제대로 되었을때 받아온 알람타임으로 알람을 설정
				if (alarmTime != -1) {
					Intent broadcastIntent = new Intent(
							Constants.ACTION_ALARM_BR);
					mAlarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime,
							PendingIntent.getBroadcast(mContext, 0,
									broadcastIntent, 0));
					Toast.makeText(mContext, "알람 설정 완료!", Toast.LENGTH_SHORT)
							.show();
					
					//알람 설정이 되면 MeetingListActivity로 이동
					Intent intent = new Intent(mContext, MeetingListActivity.class);
					intent.putExtra("user", user);
					startActivity(intent);

					finish();
				} else {
					Toast.makeText(mContext, "현재시간 이후의 시간을 입력해주세요!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	}
}
