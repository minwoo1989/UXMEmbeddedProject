package com.uxm.embeddedproject.map;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.uxm.embeddedproject.R;
import com.uxm.embeddedproject.database.DatabaseHelper;

public class GMapActivity extends MapActivity implements LocationListener {

	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay myLocationOverlay;
	private LocationManager locationManager;
	private GMapItemizedOverlay itemizedOverlay;
	private List<Overlay> mapOverlays;
	private DatabaseHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// 초기화
		mapView = (MapView) findViewById(R.id.mapView);
		mapController = mapView.getController();
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mapOverlays = mapView.getOverlays();
		dbHelper = new DatabaseHelper(this);

		// 셋팅
		mapView.setBuiltInZoomControls(true);
		mapView.setVisibility(View.VISIBLE);

		mapController.setZoom(18);

		// mapOverlays 리스트에 myLocationOverlay 추가
		mapOverlays.add(myLocationOverlay);

		// 인텐트에 있는 title 정보를 받아 마커를 표시
		setMarker(getIntent().getExtras().getString("title"));
		
		// 버튼 세팅
		Button backButton = (Button) findViewById(R.id.backButton);
		
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void setMarker(String title) {
		// DB를 불러옴
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT latitude, longitude FROM location WHERE title=\""
						+ title + "\"", null);

		Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new GMapItemizedOverlay(drawable, this);

		// select문을 실행
		while (cursor.moveToNext()) {
			GeoPoint gp = new GeoPoint((int) (cursor.getDouble(0) * 1E6),
					(int) (cursor.getDouble(1) * 1E6));
			OverlayItem item = new OverlayItem(gp, "모임 장소", title);

			itemizedOverlay.addOverlay(item);
		}

		if (itemizedOverlay.size() > 0) {
			mapOverlays.add(itemizedOverlay);
		}

		cursor.close();
		db.close();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (myLocationOverlay != null) {
			myLocationOverlay.disableMyLocation();
			myLocationOverlay.disableCompass();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 종료 시 위치 정보 업데이트를 종료
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// GPS를 사용해서 마지막으로 알려진 위치를 받음
		Location loc = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// 위치를 받으면 위치를 변경
		if (loc != null)
			onLocationChanged(loc);

		// GPS, NETWORK 중 적합한 provider 선택
		String provider = locationManager.getBestProvider(new Criteria(), true);

		// 3초 또는 1m에 한 번씩 위치정보 업데이트
		locationManager.requestLocationUpdates(provider, 3000, 1, this);

		// 내 위치 정보 활성
		if (myLocationOverlay == null)
			myLocationOverlay = new MyLocationOverlay(this, mapView);

		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onLocationChanged(Location location) {
		if (location != null) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lon = (int) (location.getLongitude() * 1E6);

			GeoPoint gp = new GeoPoint(lat, lon);

			// 센터 설정 후 센터로 이동
			mapController.setCenter(gp);
			mapController.animateTo(gp);
		}
	}

	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "위치 공급자가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "위치 공급자가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}
