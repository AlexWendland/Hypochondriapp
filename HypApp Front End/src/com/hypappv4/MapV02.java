package com.hypappv4;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
//import android.view.MotionEvent;
import com.hypappv4.ZoomableMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapV02 extends Activity {

	private GoogleMap map;
	static final LatLng CENTRALLDN = new LatLng(51.5171, -0.1062);
	static final LatLng TOPRIGHT = new LatLng(51.7, 0.3);
	static final LatLng TOPLEFT = new LatLng(51.7, -0.5);
	static final LatLng BOTTOMRIGHT = new LatLng(51.3, 0.3);
	static final LatLng BOTTOMLEFT = new LatLng(51.3, -0.5);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_v02);
		/*ZoomableMap zm = (ZoomableMap)(findViewById(R.id.zoomableMap1));
		zm.changeMapOverlayNo(1);*/
		Slider slide = (Slider)(findViewById(R.id.slider));
		//slide.setZM(zm);
		
		///
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();
		Marker city = map.addMarker(new MarkerOptions().position(CENTRALLDN)
		        .title("City of London"));
		Marker topRight = map.addMarker(new MarkerOptions().position(TOPRIGHT)
			        .title("top right"));
		Marker topLeft = map.addMarker(new MarkerOptions().position(TOPLEFT)
			        .title("top left"));
		Marker bottomRight = map.addMarker(new MarkerOptions().position(BOTTOMRIGHT)
			        .title("bottom right"));
		Marker bottomLeft = map.addMarker(new MarkerOptions().position(BOTTOMLEFT)
		        .title("bottom left"));
		    
		    // Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTRALLDN, 15));

		    // Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		
		//
		//Bitmap overlayBM = (Bitmap) getResources().getDrawable(R.drawable.londonmap);
		
		DataManager dataManager = new DataManager(getBaseContext());
		
		OverlayBuilder overlayBuilder = new OverlayBuilder();
		
		Bitmap bm = overlayBuilder.setupIllnessBM(dataManager.decodedIll[0], dataManager.data.illScalar[0]);
		
		BitmapDescriptor image = BitmapDescriptorFactory.fromBitmap(bm);
		LatLngBounds bitmBounds = new LatLngBounds(BOTTOMLEFT, TOPRIGHT);
		
		GroundOverlay overlay = map.addGroundOverlay(new GroundOverlayOptions() 
			.image(image)
			.positionFromBounds(bitmBounds));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_v02, menu);
		return true;
	}
	
}
