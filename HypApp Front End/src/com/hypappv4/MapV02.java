package com.hypappv4;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import com.hypappv4.ZoomableMap;

public class MapV02 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_v02);
		ZoomableMap zm = (ZoomableMap)(findViewById(R.id.zoomableMap1));
		zm.changeMapOverlayNo(1);
		Slider slide = (Slider)(findViewById(R.id.slider));
		slide.setZM(zm);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_v02, menu);
		return true;
	}
	
}
