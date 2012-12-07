package com.uxm.embeddedproject.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.uxm.embeddedproject.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraActivity extends Activity {
	private Preview mPreview;
	private byte[][] mImageData;
	private boolean gFocussed = false;
	private boolean gCameraPressed = false;

	private static SharedPreferences sPrefs = null;
	public static final String KEY_POPUP_ENV = "key_env";
	public static final String KEY_POPUP_ENV_RUN_MODE = "key_env_run";

	public String mFilename;
	private int mFileNameYear;
	private int mFileNameMonth;
	private int mFileNameDay;
	private int mFileNameCount;

	public static final String SAVE_FILE_YEAR = "sava_file_year";
	public static final String SAVE_FILE_MONTH = "sava_file_month";
	public static final String SAVE_FILE_DATE = "sava_file_date";
	public static final String SAVE_FILE_COUNT = "sava_file_count";

	//저장경로 지정
	private String mFileimageRoute = "/mnt/sdcard/Android/data/com.uxm.embeddedproject/file";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		mPreview = (Preview) findViewById(R.id.cameraPreview);
		mImageData = new byte[6][];
	}
	
//터치 시 사진을 찍음
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (mPreview.mCamera != null) {
				mPreview.mCamera.takePicture(mShutterCallback,
						mPictureCallbackRaw, mPictureCallbackJpeg);
			}
		}

		return false;
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mPreview != null) {
			if (mPreview.mCamera != null) {
				mPreview.mCamera.stopPreview();
				mPreview.mCamera.release();
				mPreview.mCamera = null;
			}
		}
		super.onDestroy();
	}

	private int getPreFileName() {
		if (sPrefs == null) {
			sPrefs = getSharedPreferences(KEY_POPUP_ENV, Context.MODE_PRIVATE);
		}
		mFileNameYear = sPrefs.getInt(SAVE_FILE_YEAR, 0);
		mFileNameMonth = sPrefs.getInt(SAVE_FILE_MONTH, 0);
		mFileNameDay = sPrefs.getInt(SAVE_FILE_DATE, 0);
		mFileNameCount = sPrefs.getInt(SAVE_FILE_COUNT, 0);
		return mFileNameCount;
	}

	private String getRealFileName() {
		MakeFileName_Demo();
		return mFilename;
	}

	private void MakeFileName_Demo() {
		DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
		DecimalFormat NumFormat = new DecimalFormat("0000");// 4자리로 표현 한다.
		Calendar rightNow = Calendar.getInstance();// 날짜 불러오는 함수
		int year = rightNow.get(Calendar.YEAR) % 100;// 년도를 네자리에서 끝 두자리로 바꿈
		int month = rightNow.get(Calendar.MONTH);// 달
		int date = rightNow.get(Calendar.DATE);// 일
		int num = getPreFileName(); // xml에 저장 되어있는 인덱스 값을 넣는다.
		String result = decimalFormat.format(month)
				+ decimalFormat.format(date);
		String FormatNum = NumFormat.format(num);
		mFilename = result + "_" + FormatNum;

		File[] files = new File(getExternalFilesDir(null).toString()).listFiles();

		if (files.length == 0) {
			num++;
		} else if (files.length > 0) {

			if (CompareDate(year, month, date) == true) {
				num++;
			} else if (CompareDate(year, month, date) == false) {

				num = 0;
			}
		}

		SaveFileName(year, month, date, num);// xml에 저장
	}

	private boolean CompareDate(int year, int month, int date) {
		boolean ret = false;

		if (year == getFileNameYear()) {
			if (month == getFileNameMonth()) {
				if (date == getFileNameDay()) {
					ret = true;
				}
			}
		}

		return ret;
	}

	private int getFileNameYear() {
		return mFileNameYear;
	}

	private int getFileNameMonth() {
		return mFileNameMonth;
	}

	private int getFileNameDay() {
		return mFileNameDay;
	}

	private void SaveFileName(int year, int month, int date, int num) {
		// XML에 저장 putInt...
		SharedPreferences.Editor editor = sPrefs.edit();
		// putInt(값을 받을 환경변수 , 값을 얻어오는 인자값)
		editor.putInt(SAVE_FILE_YEAR, year);
		editor.putInt(SAVE_FILE_MONTH, month);
		editor.putInt(SAVE_FILE_DATE, date);
		editor.putInt(SAVE_FILE_COUNT, num);
		editor.commit();// 저장할땐 반드시 commit()을 시킨다.

	}

	public int SaveImage() {

		int ret = 0;

		try {
			
			FileOutputStream fileoutstream = new FileOutputStream(
					mFileimageRoute + getRealFileName() + ".jpg");

			fileoutstream.write(mImageData[0]);
			fileoutstream.close();
			System.gc();
		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
			ret = -1;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			ret = -1;
		} catch (Exception e) {
			ret = -1;
		}

		return ret;

	}

	Camera.PictureCallback mPictureCallbackRaw = new Camera.PictureCallback() {

		public void onPictureTaken(byte[] data, Camera c) {

		}
	};

	Camera.PictureCallback mPictureCallbackJpeg = new Camera.PictureCallback() {

		public void onPictureTaken(byte[] data, Camera c) {


			mImageData[0] = data;

			Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE))
					.getDefaultDisplay();
			int width = defaultDisplay.getWidth();
			int height = defaultDisplay.getHeight();

			ShowSaveDailog();

		}

	};

	Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
		}
	};
	Camera.AutoFocusCallback cb = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera c) {

			if (success) {

				ToneGenerator tg = new ToneGenerator(
						AudioManager.STREAM_SYSTEM, 100);
				if (tg != null)
					tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
				gFocussed = true;
				try {
					if (gCameraPressed) {
						if (mPreview.mCamera != null) {
							mPreview.mCamera.takePicture(mShutterCallback,
									mPictureCallbackRaw, mPictureCallbackJpeg);
						}
					}
				} catch (Exception e) {
				}
			} else {
				ToneGenerator tg = new ToneGenerator(
						AudioManager.STREAM_SYSTEM, 100);
				if (tg != null)
					tg.startTone(ToneGenerator.TONE_PROP_BEEP2);

				try {
					if (gCameraPressed) {
						if (mPreview.mCamera != null) {
							mPreview.mCamera.takePicture(mShutterCallback,
									mPictureCallbackRaw, mPictureCallbackJpeg);
						}
					}
				} catch (Exception e) {
				}
			}

		}
	};

	private void ShowSaveDailog() {
		Toast.makeText(this, "save image", Toast.LENGTH_SHORT).show();
		SaveImage();
		mImageData[0] = null;

		if (mPreview.mCamera != null) {
			try {
				mPreview.mCamera.startPreview();
			} catch (Exception e) {

			}
		}
	}
}
