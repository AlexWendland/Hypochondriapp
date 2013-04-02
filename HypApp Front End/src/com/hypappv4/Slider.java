package com.hypappv4;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

public class Slider extends SeekBar {

	ZoomableMap zm;
	
	public void setZM (ZoomableMap zoomMap){
		zm = zoomMap;
	}
	
	private Context mContext;

	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.setProgress(20);
		this.setMax(100);
		this.setOnSeekBarChangeListener(listener);
	}

	OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		Log.v("Shi changed yo", ""+getProgress());
        		
        		if(getProgress() > 50){
        			zm.changeMapOverlayNo(1);
        		}else{
        			zm.changeMapOverlayNo(0);
        		}
        }
	};
	

}
