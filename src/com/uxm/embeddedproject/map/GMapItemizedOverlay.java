package com.uxm.embeddedproject.map;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class GMapItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private Context context;
	private ArrayList<OverlayItem> items;

	// 생성자
	public GMapItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenter(defaultMarker));

		this.context = context;
		items = new ArrayList<OverlayItem>();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	// 마커를 클릭했을 때
	@Override
	public boolean onTap(int index) {
		OverlayItem i = items.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);

		// 다이얼로그를 띄움
		dialog.setTitle(i.getTitle());
		dialog.setMessage(i.getSnippet());
		dialog.show();

		return true;
	}

	public void addOverlay(OverlayItem overlay) {
		items.add(overlay);

		populate();
	}

}
