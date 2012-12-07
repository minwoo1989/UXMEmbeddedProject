package com.uxm.embeddedproject.paint;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class SignView extends View {
	public Paint mPaint;
	public MaskFilter mEmboss;
	public MaskFilter mBlur;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	public void colorChanged(int color) {
		// 구문이 길어지지만 향후 디버깅 제외 할때 메인액티비티의 D를 false로 하면됨
		mPaint.setColor(color);
	}

	// 생성자2
	public SignView(Context context, AttributeSet attrs) {
		super(context, attrs);
 
		// 레이아웃 크기만큼 뷰영역을 지정해야됨
		// 동적으로 크기를 늘려주고 싶다면 
		// 뷰를 동적으로 만들고 가로/세로모드일때 각각의 크기를 가지고와서 정해야됨.
		// 뷰의 크기와 캔버스 크기 중  뷰의 크기가 우선됨.
		mBitmap = Bitmap.createBitmap(480, 600, Bitmap.Config.ARGB_8888); // 영역크기

		mCanvas = new Canvas(mBitmap);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		// mBitmapPaint = new Paint(Paint.STRIKE_THRU_TEXT_FLAG);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		// mPaint.setColor(0xFF000000); // 검정색
		mPaint.setColor(Color.BLACK); // 검정색
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(2); // 굵기

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	}

	/*
	 * // 생성자1 public signView(Context context) { super(context); }
	 * 
	 * // 생성자3 public signView(Context context, AttributeSet attrs, int
	 * defStyle) { super(context, attrs, defStyle); }
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	// 뷰를 담당하는 클래스
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);
	}
	
	// 터치 시작
	private void touch_start(float x, float y) {
		mPath.reset();		// 패스를 초기화
		mPath.moveTo(x, y); // 좌표로 이동
		mX = x;				// 다음을 위해 x좌표를 mX에 저장
		mY = y;				// 다음을 위해 y좌표를 mY에 저장
	}

	// 터치후 이동시
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}

	}

	private void touch_up() {
		mPath.lineTo(mX, mY); // 줄긋기
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate(); // 갱신
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate(); // 갱신
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate(); // 갱신
			break;
		}
		return true;
	}
}
