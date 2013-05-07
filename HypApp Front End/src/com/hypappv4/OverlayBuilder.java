package com.hypappv4;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;


public class OverlayBuilder {

	final int gridWidth = 40;
	final int gridHeight = 40;
	
	final int pixSize = 1;
	
	int bitMWidth;
	int bitMHeight;
	
	Bitmap bm;
	
	float[] data;
	
	
	public Bitmap setupIllnessBM (float[] data, float scale){
		bitMWidth = gridWidth * pixSize;
		bitMHeight = gridHeight * pixSize;
		
		bm = Bitmap.createBitmap(bitMWidth, bitMHeight, Bitmap.Config.ARGB_8888);
		
		int x; 
		int y;
		for(int i = 0; i < data.length; i++){
			x = i%gridWidth;
			y = (int) Math.floor(i/40);
			
			//int col = 0xff000000 + (int) (((int)(0xff)*data[i])*0x10);
			int col = Math.round(0xff*(data[i]/scale))*0x1000000;
			//int col = 0xff000000;
			
			bm.setPixel(x, y, col);
		}
		return bm;
	}
	
	
}
