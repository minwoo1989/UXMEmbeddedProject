package com.uxm.embeddedproject.menuitem;

import com.uxm.embeddedproject.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class InfoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		Button b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}

		});

	}

}