package com.hypappv4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ZoomableMap extends ImageView {
	
	public int xPos = 0;
	public int yPos = 0;
	public float scale = 1;
	private Handler h;
	private Context mContext;
	public int mapBMWidth;
	public int mapBMHeight;
	private final int FRAME_RATE = 5;
	public Bitmap illBM;
	private FakeData fd;
	private int [][][] illnessData;
	//private SeekBar slider;// = (SeekBar)(View.findViewById(R.id.slider1));
	public Bitmap[] illnessBMs;
	public int illnessBmNo = 0;
	
	
	
	public ZoomableMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		h = new Handler();
		
		BitmapDrawable map = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.londonmap);
		Bitmap mapBM = map.getBitmap();
		mapBMWidth = mapBM.getWidth();
		mapBMHeight = mapBM.getHeight();
		illBM = Bitmap.createBitmap(mapBMWidth, mapBMHeight, Bitmap.Config.ARGB_8888);
		
		Log.v("EXAMPLE", "aaaa");
		fd = new FakeData();
		Log.v("EXAMPLE", "bbbb");
		illnessData = new int [2][][];
		illnessData[1] = fd.getData(2);
		illnessData[0] = fd.getData(1);
		Log.v("EXAMPLE", "cccc");
		
		illnessBMs = new Bitmap[2];
		illnessBMs[0] = setupIllnessBM(0);
		Log.v("EXAMPLE", "dddd");
		illnessBMs[1] = setupIllnessBM(1);
		Log.v("EXAMPLE", ""+illnessData[0][0][0]);
	}

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			invalidate(); 
		}
	};
	
	public void changeMapOverlayNo (int which){
		illnessBmNo = which;
	}
	
	@SuppressLint("DrawAllocation")
	protected void onDraw (Canvas c){
		//Drawing the background map...
		BitmapDrawable map = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.londonmap);
		c.drawBitmap(map.getBitmap(), xPos, yPos, null);
		
		//drawing the illness spread...
		drawIllness(c);
	
		h.postDelayed(r, FRAME_RATE);
	}
	
	
	
	//Returns a bitmap corresponding to the which'th index of illnessData
	public Bitmap setupIllnessBM (int which){
		Bitmap bitm = Bitmap.createBitmap(mapBMWidth, mapBMHeight, Bitmap.Config.ARGB_8888);
		Log.e("EXAMPLE", "1");
		int xDiff = (int)(mapBMWidth/illnessData[which][0].length);
		int yDiff = (int)(mapBMHeight/illnessData[which].length);
		
		int[][] pixels = new int[4][xDiff * yDiff];
		for(int i = 0; i<pixels[0].length; i++){
			pixels[1][i] = 0x550000ff;
			pixels[0][i] = 0x220000ff;
			pixels[2][i] = 0x770000ff;
			pixels[3][i] = 0x990000ff;
		}
		Log.e("EXAMPLE", "2");
		
		for(int y = 0; y<illnessData[which].length; y++){
			for(int x = 0; x < illnessData[which][y].length; x ++){
				//NOTE: at the moment it's backwards, it goes [which][y][x], not [which][x][y], to be fixed
				
				try{
					bitm.setPixels(pixels[illnessData[which][y][x]], 0, xDiff, x*xDiff, y*yDiff, xDiff, yDiff);
				}catch (Exception excp){
					Log.v("SetPixels error: ", ""+excp.getLocalizedMessage());
					Log.v("Pos:", "x:"+x+", xdiff: "+xDiff+", bitwid:"+mapBMWidth); 
				}
				//hard coded values to make it look nicer, this will change when standard sizes are set
			}
		}
		Log.e("EXAMPLE", "3");
		
		return bitm;
	}
	
	
	public void drawIllness (Canvas c){
		c.drawBitmap(illnessBMs[illnessBmNo], xPos, yPos, null);
	}
	
	//useless for now
	public int scaleHeight (int width){
		float ratio = (float)width/(float)mapBMWidth;
		return (int) ((float)mapBMHeight * ratio);
	}
	
	
	//useless for now
	public Rect getSrcRect (int x, int y, float scal){
		
		int rectWidth = 2339;
		int rectHeight = 1363;
		
		if(x < 0){
			x = 0;
		}
		if(y < 0){
			y = 0;
		}
		
		Rect rec = new Rect(x, y, x+rectWidth, y+rectHeight);
		
		return rec;
	}
	
	@Override
	public boolean onTouchEvent (MotionEvent e){
		//Moving the map around, needs to incorporate zooming
		float oldX;
		float oldY;
		
		if(e.getAction() == MotionEvent.ACTION_MOVE){
			try {
				oldX = e.getHistoricalX((int)(e.getHistorySize()/2));
				oldY = e.getHistoricalY((int)(e.getHistorySize()/2));
			}catch (Exception excep) {
				Log.v("Error Message: ", excep.getLocalizedMessage());
				oldX = e.getX();
				oldY = e.getY();
			}
		
			xPos += (e.getX() - (int)oldX);
			yPos += (e.getY() - (int)oldY);
		}
		
		return true;
	}
}
