package com.uxm.embeddedproject.menuitem;

import com.uxm.embeddedproject.R;


import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        
        VideoView vv = (VideoView)findViewById(R.id.videoView1);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(vv);

        vv.setMediaController(mc);
		vv.setVideoURI(Uri.parse("android.resource://com.uxm.embeddedproject/"
				+ R.raw.videod));
        
        vv.start();

    }

}
