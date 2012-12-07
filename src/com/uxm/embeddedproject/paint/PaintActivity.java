package com.uxm.embeddedproject.paint;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.paint.ColorPickerDialog.OnColorChangedListener;

public class PaintActivity extends Activity implements OnClickListener, OnColorChangedListener{

	private static final int COLOR_MENU_ID = Menu.FIRST; // 첫번째 메뉴
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1; // 두번째 메뉴
	private static final int BLUR_MENU_ID = Menu.FIRST + 2; // 세번째 메뉴
	private static final int ERASE_MENU_ID = Menu.FIRST + 3; // 네번째 메뉴
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 4; // 다섯번째 메뉴
	private SignView sV; // 그려질 signView뷰영역
	private Button btn_save; // 저장 버튼
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);
        
        sV = (SignView) findViewById(R.id.signView);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_save) {
			saveView(sV);
		}
	}
	
	private void saveView(View view) {
		Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		view.draw(c);

		// 날짜 포맷
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
		Date date = new Date(System.currentTimeMillis());

		// 파일 이름 형식 지정
		String name = "ezmeeting" + dateFormat.format(date) + ".jpg";

		// 저장위치는 /mnt/sdcard/Android/data/com.uxm.embeddedproject/files
		File imgFile = new File(getExternalFilesDir(null), name);
		try {
			FileOutputStream fos = new FileOutputStream(imgFile);
			if (fos != null) {
				b.compress(Bitmap.CompressFormat.JPEG, 85, fos);// 압축률85%
				fos.close();
			}
		} catch (Exception e) {
			Toast.makeText(this, "dkseo",Toast.LENGTH_SHORT).show(); // 0: LENGTH_SHORT
		}

		Toast.makeText(this, "저장완료:" + name, Toast.LENGTH_SHORT).show(); // 0: LENGTH_SHORT
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "색상").setShortcut('3', 'c');
		menu.add(0, EMBOSS_MENU_ID, 0, "파스칼").setShortcut('4', 's');
		menu.add(0, BLUR_MENU_ID, 0, "흐림").setShortcut('5', 'z');
		menu.add(0, ERASE_MENU_ID, 0, "지우개").setShortcut('5', 'z');
		menu.add(0, SRCATOP_MENU_ID, 0, "겹쳐그리기").setShortcut('5', 'z');

		return true;
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	// 메뉴 아이템 선택시
	public boolean onOptionsItemSelected(MenuItem item) {
		sV.mPaint.setStrokeWidth(2); // 굵기 초기화
		sV.mPaint.setXfermode(null);// 지우개 초기화
		sV.mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, sV.mPaint.getColor()).show();
			return true;
		case EMBOSS_MENU_ID: // 토글
			if (sV.mPaint.getMaskFilter() != sV.mEmboss) {
				sV.mPaint.setMaskFilter(sV.mEmboss);
			} else {
				sV.mPaint.setMaskFilter(null); // 없앰
			}
			return true;
		case BLUR_MENU_ID: // 토글
			if (sV.mPaint.getMaskFilter() != sV.mBlur) {
				sV.mPaint.setMaskFilter(sV.mBlur);
			} else {
				sV.mPaint.setMaskFilter(null); // 없앰
			}
			return true;
		case ERASE_MENU_ID:
			sV.mPaint.setStrokeWidth(12); // 굵기 변경
			// 사실 지우개라기보단. 마스크 처럼 선위에 투명선을 그리는것과 같다.
			sV.mPaint
					.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));// 지우개
			return true;
		case SRCATOP_MENU_ID:
			sV.mPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.SRC_ATOP));// 겹쳐그리기
			sV.mPaint.setAlpha(0x80);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// 색상 변경
	public void colorChanged(int color) {
		sV.colorChanged(color);
	}
}
