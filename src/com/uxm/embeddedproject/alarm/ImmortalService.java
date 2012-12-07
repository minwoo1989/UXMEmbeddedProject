package com.uxm.embeddedproject.alarm;

import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.data.GroupBoard;
import com.uxm.embeddedproject.data.User;
import com.uxm.embeddedproject.meeting.MeetingInfoActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;

public class ImmortalService extends Service {
	
	private static final int RESTART_TIME = 60 * 1000;
	
	private Context mContext = null;
	private AlarmBroadcastReceiver mBroadcastReceiver = null;
	private User user;
	private GroupBoard groupBoard;
	private String gName;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		//인텐트로 user, groupboard, gname 정보 받아서 복원
		user = (User) intent.getSerializableExtra("user");
		groupBoard = (GroupBoard) intent
				.getSerializableExtra("groupBoard");
		gName = intent.getStringExtra("gName");
		
		mContext = this;

		if (mBroadcastReceiver == null) {
			mBroadcastReceiver = new AlarmBroadcastReceiver();

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Constants.ACTION_ALARM_BR); // 수신할 방송 설정

			registerReceiver(mBroadcastReceiver, intentFilter); // 브로드캐스트 리시버 등록
		}

		// 서비스를 죽지 않게 해줌
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + RESTART_TIME, RESTART_TIME,
				PendingIntent.getService(mContext, 0, new Intent(mContext,
						ImmortalService.class), 0));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.onStart(intent, startId);

		return START_STICKY;	//시작에 실패해도 지속적으로 서비스를 재시작
	}

	private class AlarmBroadcastReceiver extends BroadcastReceiver {
		private static final int NOTIFICATION_ID = 80100; // 임의의 상수
		private static final int VIBRATE_TIME = 2000;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(Constants.ACTION_ALARM_BR)) {
				// Notification 아이콘, 문구, 시간 설정
				Notification notification = new Notification(
						R.drawable.bar, "모임 일정을 확인하세요!",
						System.currentTimeMillis());
				
				Intent i = new Intent(context,
						MeetingInfoActivity.class);
				
				i.putExtra("user", user);
				i.putExtra("groupBoard", groupBoard);
				i.putExtra("gName", gName);

				//알림메시지 설정, 이미 생성된 pendingintent가 있다면 해당 intent의 내용을 변경
				notification.setLatestEventInfo(context, "Easy Meeting",
						"모임 일정을 확인하세요", PendingIntent.getActivity(
								context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT));

				//통지 설정
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(NOTIFICATION_ID);
				notificationManager.notify(NOTIFICATION_ID, notification);

				//진동 설정
				Vibrator vibrator = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(VIBRATE_TIME);
			}
		}
	}
}
