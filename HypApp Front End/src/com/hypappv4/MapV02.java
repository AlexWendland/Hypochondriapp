package com.hypappv4;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.group19.hypochondriapp.AppDataPacket;

public class MapV02 extends Activity {

	private GoogleMap gmap;
	static final LatLng CENTRALLDN = new LatLng(51.5171, -0.1062);
	static final LatLng TOPRIGHT = new LatLng(51.7, 0.3);
	static final LatLng TOPLEFT = new LatLng(51.7, -0.5);
	static final LatLng BOTTOMRIGHT = new LatLng(51.3, 0.3);
	static final LatLng BOTTOMLEFT = new LatLng(51.3, -0.5);
	
	static final int totalPredictions = 24*8+7;
	
	private DataManager dataManager;
	private MapV02 thisMap;
	
	public Bitmap[] bitms;
	public Bitmap[] wbitms;
	public GroundOverlay[] quantityOverlays;
	public GroundOverlay[] ratioOverlays;
	public GroundOverlay[] wquantityOverlays;
	public GroundOverlay[] wratioOverlays;
	
	public float[][] decodedIll;
	public AppDataPacket data;
	
	private Slider slide;
	
	public Context context;
	
	private MapOverlayData ratioOverlayData;
	private MapOverlayData quantityOverlayData;
	
	private Vector<StationData> stations;
	private StationLocationReader slr;
	private Marker[] stationMarkers;
	Vector<StationData> stationdata;
	Vector<Marker> shitStations;
	
	private float[] averageIll;
	
	public Marker openMarker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_v02);
		thisMap = this;
		
		slide = (Slider)(findViewById(R.id.slider));
		slide.setTimeBox((TextView)this.findViewById(R.id.timeOutPut), (TextView)this.findViewById(R.id.timeheader));
		slide.setSnippetUpdater(new SnippetUpdater());
		
		context = this.getBaseContext();
		
		Thread thread = new Thread (new runDataManager());
		thread.start();
		
		//(new runDataManager()).run();
		
		/*MapFragment mapfrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
		MapView mapview = (MapView) mapfrag.getView();*/
		
		gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();
		
		
		/*gmap.addMarker(new MarkerOptions().position(CENTRALLDN)
		 * 
		        .title("City of London"));
		gmap.addMarker(new MarkerOptions().position(TOPRIGHT)
			        .title("top right"));
		gmap.addMarker(new MarkerOptions().position(TOPLEFT)
			        .title("top left"));
		gmap.addMarker(new MarkerOptions().position(BOTTOMRIGHT)
			        .title("bottom right"));
		gmap.addMarker(new MarkerOptions().position(BOTTOMLEFT)
		        .title("bottom left"));
		 */
		
		gmap.addPolyline(new PolylineOptions()
			.add(TOPLEFT, TOPRIGHT, BOTTOMRIGHT, BOTTOMLEFT, TOPLEFT)
			.width(5)
			.color(0xFF888888));
		
		    // Move the camera instantly to hamburg with a zoom of 15.
		//gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTRALLDN, 15));
		
		//gmap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		
		CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(CENTRALLDN.latitude-0.04f, CENTRALLDN.longitude))
			.zoom(10)
			.tilt(30)                   // Sets the tilt of the camera to 30 degrees
	    	.build();                   // Creates a CameraPosition from the builder
		
		gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		// Zoom in, animating the camera.
		
		//gmap.animateCamera(CameraUpdateFactory., 2000, null);
	}
	
	public class runDataManager implements Runnable {
		NetworkReceiver netReceiver;
		private MapV02 map;
		String fileName = "HypAppCache";
		//private Thread thread;
		
		
		@Override
		public void run (){
			Log.v("SUM SHIT", "A");
			
			fileName = "HypAppCache";
			changeLoadText("Downloading Data...");
			Log.v("DOING SHIT", "A");
			netReceiver = new NetworkReceiver();
			
			netReceiver.update();
			
			
			Log.v("going into the loop", "wait up a little");
			boolean isDone = false;
			
			while(!isDone){
				if(netReceiver.IOexcep || netReceiver.CNFexcep || netReceiver.UHexcep){
					Log.e("Can't get shit", "Loading data...");
					changeLoadText("Download Error, loading cached data...");
					data = loadSavedData();
					break;
				}
				isDone = netReceiver.isDone();
			}
			
			Log.v("BUTTS", "sdfsf");
			
			if(isDone){
				Log.e("Data Successfully downloaded", "");
				data = netReceiver.getData();
			}
			
			
			
			changeLoadText("Decoding data...");
			
			
			
			decodedIll = data.decodeIll();
			float[][] decodedRatio = data.decodeRatio();
			
			quantityOverlayData = new MapOverlayData(1500, decodedIll, "q");
			ratioOverlayData = new MapOverlayData(500, decodedRatio, "r");
			
			averageIll = new float[decodedRatio.length];
			
			for(int pred = 0; pred<totalPredictions; pred++){
				averageIll[pred] = 0;
				for(int i = 0; i<decodedRatio[0].length; i++){
					averageIll[pred] += decodedRatio[pred][i];
				}
				averageIll[pred] = averageIll[pred]/((float)decodedRatio[pred].length);
			}
			
			slr = new StationLocationReader(context, data.stationsData);
			
			stations = slr.getStations();
			
			/*for(int i = 0; i<stations.length; i++){
				gmap.addMarker(new MarkerOptions().position(stations[i].location));
			}*/
			
			Message msg = new Message();
			msg.obj = stations;
			stationHandler.sendMessage(msg);
			
			Log.e("Decoded...", "butts");
			
			quantityOverlays = new GroundOverlay[decodedIll.length];
			ratioOverlays = new GroundOverlay[decodedIll.length];
			wquantityOverlays = new GroundOverlay[decodedIll.length];
			wratioOverlays = new GroundOverlay[decodedIll.length];
			
			
			makeOverlay(quantityOverlayData);
			makeOverlay(ratioOverlayData);
			
			changeLoadText("Caching data...");
			Log.e("Caching data", "aaaa");
			
			saveData(data);
			
			//changeLoadText(" ");
	 	}
		
		
		public AppDataPacket loadSavedData(){
			AppDataPacket readData;
			FileInputStream fis;
			ObjectInputStream is;
			try{
				
				fis = context.openFileInput(fileName);
				Log.v("fis", fis.toString());
				
				is = new ObjectInputStream(fis);
				Log.v("is", is.toString());
				
				readData = (AppDataPacket) is.readObject();
				Log.v("readdata", readData.toString());
				
				is.close();
				
				Log.v("File Read!", " ");
				
			}catch (Exception e){
				Log.e("FILE READING ERROR", ":(");
				
				Log.e("Error message:", e.getStackTrace().toString());
				e.printStackTrace();
				readData = new AppDataPacket();
			}
			return readData;
		}
		
		public void saveData (AppDataPacket a){
			FileOutputStream fos;
			try {
				Log.v("a", a.toString());
				
				fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				Log.v("fos", fos.toString());
				
				ObjectOutputStream os = new ObjectOutputStream(fos);
				Log.v("os", os.toString());
				
				os.writeObject(a);
				Log.v("a", a.toString());
				
				os.close();
				Log.v("File Written!", " ");
			} catch (Exception e) {
				Log.e("FILE WRITING ERROR", ":(");
				e.printStackTrace();
			}
		}
		
		private void changeLoadText (String text){
			Message msg = new Message();
			msg.obj = text;
			loadTextHandler.sendMessage(msg);
		}
		
		public void makeOverlay (MapOverlayData overlayData){
			Message msg = new Message();
			msg.obj = overlayData;//new MapOverlayData(1000, data.decodeIll());
			overlayHandler.sendMessage(msg);
		}
	}
	
	
	
	@SuppressLint("HandlerLeak")
	private Handler overlayHandler = new Handler () {
		
		@Override
		public void handleMessage(Message msg){
			
			MapOverlayData overlayData = (MapOverlayData) msg.obj;
			float[][] drawData = overlayData.getData();
			float drawScale = overlayData.getScale();
			
			GroundOverlay[] overlays = new GroundOverlay[totalPredictions];
			GroundOverlay[] woverlays = new GroundOverlay[totalPredictions];
			
			bitms = new Bitmap[totalPredictions];
			wbitms = new Bitmap[totalPredictions];
			
			OverlayBuilder overlayBuilder = new OverlayBuilder();
			
			LatLngBounds bitmBounds = new LatLngBounds(BOTTOMLEFT, TOPRIGHT);
			
			for(int i = 0; i<decodedIll.length; i++){
				
				Bitmap[] bmps = overlayBuilder.setupIllnessBM(drawData[i], drawScale);
				
				bitms[i] = bmps[0];
				wbitms[i] = bmps[1];
				
				BitmapDescriptor image = BitmapDescriptorFactory.fromBitmap(bitms[i]);
				
				overlays[i] = gmap.addGroundOverlay(new GroundOverlayOptions() 
					.image(image)
					.positionFromBounds(bitmBounds));
				
				overlays[i].setVisible(true);
				
				image = BitmapDescriptorFactory.fromBitmap(wbitms[i]);
				
				woverlays[i] = gmap.addGroundOverlay(new GroundOverlayOptions() 
					.image(image)
					.positionFromBounds(bitmBounds));
				
				woverlays[i].setVisible(true);
				
				
				if(overlayData.getType().equals("q")){
					quantityOverlays[i] = overlays[i];
					wquantityOverlays[i] = woverlays[i];
				}else{
					ratioOverlays[i] = overlays[i];
					wratioOverlays[i] = woverlays[i];
				}
			}
			//overlays[0].setVisible(true)
			if(overlayData.getType().equals("q")){
				slide.setOverlays(quantityOverlays);	
			}
			slide.setOverlays(quantityOverlays);
			
			hideOverlayHandler.sendEmptyMessageDelayed(0, 3000);
		}
	};
	
	
	@SuppressLint("HandlerLeak")
	private Handler hideOverlayHandler = new Handler () {
		
		@Override
		public void handleMessage (Message msg){
			for(int i = 0; i<quantityOverlays.length; i++){
				quantityOverlays[i].setVisible(false);
				ratioOverlays[i].setVisible(false);
				wquantityOverlays[i].setVisible(false);
				wratioOverlays[i].setVisible(false);
			}
			quantityOverlays[0].setVisible(true);
			
			
			Message msag = new Message();
			msag.obj = " ";
			loadTextHandler.sendMessage(msag);
		}
		
	};
	
	
	
	@SuppressLint("HandlerLeak")
	private Handler stationHandler = new Handler () {
		
		@Override
		public void handleMessage(Message msg){
			
			gmap.setOnMarkerClickListener(new MarkerClickCheck());
			
			BitmapDescriptor stationicon = BitmapDescriptorFactory.fromResource(R.drawable.transporticonseethough);
			
			stationdata = (Vector<StationData>) msg.obj;
			stationMarkers = new Marker[stationdata.size()];
			shitStations = new Vector<Marker>();
			
			for(int i = 0; i<stationdata.size(); i++){
				//gmap.addMarker(new MarkerOptions().position(stationdata[i].location));
				try{
					Log.v("Location:"+stationdata.get(i).location.latitude, ""+stationdata.get(i).location.longitude);
					if((stationdata.get(i).location.latitude == stationdata.get(i).location.longitude) && 
							stationdata.get(i).location.latitude == 0){
						
						stationMarkers[i] = gmap.addMarker(new MarkerOptions().position(stationdata.get(i).location)
						        .title(stationdata.get(i).name)
						        .snippet("In: "+stationdata.get(i).peopleIn[0]+", Out: "+stationdata.get(i).peopleOut[0])
						        .icon(stationicon));
						
						shitStations.add(stationMarkers[i]);
						stationMarkers[i].setVisible(false);
						
						continue;
					}
					
					stationMarkers[i] = gmap.addMarker(new MarkerOptions().position(stationdata.get(i).location)
				        .title(stationdata.get(i).name)
				        .snippet("In: "+stationdata.get(i).peopleIn[0]+", Out: "+stationdata.get(i).peopleOut[0])
				        .icon(stationicon));
					
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
	};
	
	@SuppressLint("HandlerLeak")
	private Handler loadTextHandler = new Handler () {
		@Override
		public void handleMessage(Message msg){
			String text = (String)msg.obj;
			setLoadMessage(text);
		}
	};
	
	public void setLoadMessage (CharSequence message){
		RelativeLayout splash = (RelativeLayout) this.findViewById(R.id.splash);
		TextView msgBox = (TextView) splash.findViewById(R.id.textView1);
		
		ProgressBar throbber = (ProgressBar) this.findViewById(R.id.progressBar1);
		if(message.equals(" ")){
			/*msgBox.setVisibility(View.INVISIBLE);
			throbber.setVisibility(View.INVISIBLE);*/
			splash.setVisibility(View.INVISIBLE);
		}else{
			splash.setVisibility(View.VISIBLE);
			msgBox.setVisibility(View.VISIBLE);
			throbber.setVisibility(View.VISIBLE);
			msgBox.setText("\n"+message);
		}
	}
	
	
	public void hideMarkers (){
		for(int i = 0; i<stationMarkers.length; i++){
			stationMarkers[i].setVisible(false);
		}
	}
	
	public void showMarkers (){
		for(int i = 0; i<stationMarkers.length; i++){
			stationMarkers[i].setVisible(true);
		}
		for (int i = 0; i<shitStations.size(); i++){
			shitStations.get(i).setVisible(false);
		}
	}
	
	public class MarkerClickCheck implements OnMarkerClickListener{
	
		@Override
	    public boolean onMarkerClick(final Marker marker) {
		
			openMarker = marker;
			updateSnippet(marker);
			
			return false;
		}
	}
	
	public void updateSnippet (Marker marker){
		
		if(marker == null){
			return;
		}
		int index = -1;
		for(int i = 0; i < stationMarkers.length; i++){
			if(stationMarkers[i].getPosition().equals(marker.getPosition())){
				index = i;
				Log.v("Found marker! ", "No:"+index);
				break;
			}
		}
		if(index == -1){
			return;
		}
		
		float peoplein = (float) stationdata.get(index).peopleIn[slide.getProgress()];
		float peopleout = (float) stationdata.get(index).peopleOut[slide.getProgress()];
		//float ratio = getGridValue(marker.getPosition(), true) * (0.0001f) * ((float)peoplein);
		double illQuant = (getGridValue(marker.getPosition(), true) * peoplein * 0.0003f ) + 
							averageIll[slide.getProgress()] * peopleout * 0.0003f;
		Log.v("Ill quanta:", ""+illQuant);
		Log.v("Average Ill:", ""+averageIll);
		illQuant = Math.round(illQuant);
		
		Log.v("Ill quantb:", ""+illQuant);
		
		openMarker.setSnippet("In: "+ stationdata.get(index).peopleIn[slide.getProgress()] 
						+", Out: "+stationdata.get(index).peopleOut[slide.getProgress()]
						+",\nEstimated "+ (int)illQuant +" people ill");
		
		/*Log.e("Station: "+index, "In: "+ stationdata.get(index).peopleIn[slide.getProgress()] 
				+", Out: "+stationdata.get(index).peopleOut[slide.getProgress()]
				+", Estimated "+ slide.getProgress() +"% people ill");*/
		
		if(openMarker.isInfoWindowShown()){
			openMarker.showInfoWindow();
		}
		
	}
	
	///////
	public class SnippetUpdater implements Runnable{
		public void run (){
			Log.v("TESADASKLASDLF", "DASDSDASD");
			updateSnippet(openMarker);
		}
	}
	
	
	public float getGridValue (LatLng pos, Boolean ratio){ //long = horo
		float lat = (float) pos.latitude;
		float lng = (float) pos.longitude;
		int x = (int)(40*(lng - TOPLEFT.longitude)/(TOPRIGHT.longitude - TOPLEFT.longitude));
		int y = (int)(40*(lat - BOTTOMLEFT.latitude)/(TOPRIGHT.latitude - BOTTOMRIGHT.latitude));
		
		Log.v("x: "+x, "y:"+y);
		Log.v("getgridpos", ""+getGridPos(x, y));
		
		if(!ratio){
			return decodedIll[slide.getProgress()][y*40 + x];
		}
		return data.decodeRatio()[slide.getProgress()][y*40 + x];
	}
	
	public int getGridPos (int x, int y){
		return (40*40 - (y*40)) + x;
	}
	
	/////////////// OPTIONS MENU STUFF
	
	
	public Menu menu;
	
	@Override
	public boolean onCreateOptionsMenu(Menu inMenu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu = inMenu;
		getMenuInflater().inflate(R.menu.map_v02, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Message msg;
	    switch (item.getItemId()) {
	    /*case R.id.Demo:
	    	setLoadMessage("DEMO SELECTED");
	    	return true;*/
	    	
	    case R.id.ratioMenuItem:
	    	if(gmap.getMapType() == gmap.MAP_TYPE_NORMAL){
	    		slide.setOverlays(ratioOverlays);
	    	}else{
	    		slide.setOverlays(wratioOverlays);
	    	}
	    	
	    	item.setChecked(true);
	    	((MenuItem)menu.findItem(R.id.quantityMenuItem)).setChecked(false);
	    	return true;
	    
	    case R.id.quantityMenuItem:
	    	
	    	if(gmap.getMapType() == gmap.MAP_TYPE_NORMAL){
	    		slide.setOverlays(quantityOverlays);
	    	}else{
	    		slide.setOverlays(wquantityOverlays);
	    	}
	    	item.setChecked(true);
	    	((MenuItem)menu.findItem(R.id.ratioMenuItem)).setChecked(false);
	    	return true;
	    	
	    case R.id.showStationMarkers:
	    	if(item.isChecked()){
	    		hideMarkers();
	    		item.setChecked(false);
	    		return true;
	    	}
	    	showMarkers();
	    	item.setChecked(true);
	    	return true;
	    	
	    case R.id.mapTypeSwitch:
	    	
	    	//MapView mapview = (MapView) this.findViewById(R.id.map);
	    	if(gmap.getMapType() == gmap.MAP_TYPE_NORMAL){
	    		gmap.setMapType(gmap.MAP_TYPE_SATELLITE);
	    		item.setTitle("Switch to Road View");
	    		if(((MenuItem)menu.findItem(R.id.quantityMenuItem)).isChecked()){
	    			slide.setOverlays(wquantityOverlays);
	    		}else{
	    			slide.setOverlays(wratioOverlays);
	    		}
	    	}else{
	    		gmap.setMapType(gmap.MAP_TYPE_NORMAL);
	    		item.setTitle("Switch to Satellite View");
	    		
	    		if(((MenuItem)menu.findItem(R.id.quantityMenuItem)).isChecked()){
	    			slide.setOverlays(quantityOverlays);
	    		}else{
	    			slide.setOverlays(ratioOverlays);
	    		}
	    		
	    	}
	    	//mapview.setSatellite(true);
	    	return true;
	    	
	    default:
            return super.onOptionsItemSelected(item);
	    }
	}
}
