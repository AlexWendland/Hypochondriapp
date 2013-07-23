package com.hypappv4;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;


public class OverlayBuilder {

	final int gridWidth = 40;
	final int gridHeight = 40;
	
	final int pixSize = 2;
	
	int bitMWidth;
	int bitMHeight;
	
	Bitmap bm;
	Bitmap wbm;
	
	float[] data;
	Paint whitepaint;
	ColorFilter cf;
	
	public OverlayBuilder(){
		//Log.v("in overlay builder", "");
		whitepaint = new Paint(Color.WHITE);
		cf = new LightingColorFilter(Color.WHITE, 1);
		Log.v("out asff", "");
	}
	
	
	public Bitmap[] setupIllnessBM (float[] data, float scale){
		bitMWidth = gridWidth * pixSize;
		bitMHeight = gridHeight * pixSize;
		
		bm = Bitmap.createBitmap(bitMWidth, bitMHeight, Bitmap.Config.ARGB_8888);
		wbm = Bitmap.createBitmap(bitMWidth, bitMHeight, Bitmap.Config.ARGB_8888);
		
		int x; 
		int y;
		
		for(int i = 0; i < data.length; i++){
			x = i%gridWidth;
			y = (int) Math.floor(i/40);
			
			float cVal = Math.min(scale, data[i]);
			
			int col = Math.round(0xff*(cVal/scale))*0x1000000 + 0x000000;
			
			for(int xp = 0; xp<pixSize; xp++){
				for(int yp = 0; yp<pixSize; yp++){
					bm.setPixel(x*pixSize + xp, (39-y)*pixSize + yp, col);
					//wbm.setPixel(x*pixSize + xp, (39-y)*pixSize + yp, col+0xffffff);
				}
			}
			
			//int col = 0xff000000 + (int) (((int)(0xff)*data[i])*0x10);
			
			//bm = bm.extractAlpha(whitepaint, null);
			
			
			//int col = 0xff000000;
			
		}
		
		int[] pixels = new int[bitMWidth * bitMHeight];
		bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, bitMWidth, bitMHeight);
		
		for(int j = 0; j<pixels.length; j++){
			pixels[j] += 0xffffff;
		}
		
		wbm.setPixels(pixels, 0, bitMWidth, 0, 0, bitMWidth, bitMHeight);
		
		Bitmap[] ret = new Bitmap[2];
		ret[0] = bm;
		ret[1] = wbm;
		return ret;
	}
}
