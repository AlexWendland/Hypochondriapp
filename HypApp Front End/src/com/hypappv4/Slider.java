package com.hypappv4;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.Marker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class Slider extends SeekBar {

	private GroundOverlay[] overlays;
	private int lastOverlay = 0;
	private boolean overlaysLoaded = false;
	private TextView timeBox;
	private Marker openMarker;
	
	private int[] markerDataIn;
	private int[] markerDataOut;
	
	private Runnable snippetUpdater;
	private TextView bigBox;
	
	public void setOverlays (GroundOverlay[] inOverlays){
		
		//this.setProgress(0);
		
		this.setMax(inOverlays.length-1);
		
		if(overlaysLoaded){
			overlays[lastOverlay].setVisible(false);
		}
		
		overlays = inOverlays;
		
		overlays[this.getProgress()].setVisible(true);
		
		overlaysLoaded = true;
	}
	
	public void setTimeBox (TextView inTimeBox, TextView inBigBox){
		timeBox = inTimeBox;
    	setTimeBoxText("Displaying real-time data");
    	bigBox = inBigBox;
    	setBigBoxText("Now");
	}
	
	
	private Context mContext;

	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.setProgress(0);
		this.setMax(0);
		this.setOnSeekBarChangeListener(listener);
	}
	
	public void setSnippetUpdater (Runnable newSnippetUpdater){
		snippetUpdater = newSnippetUpdater;
	}

	OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    		if(overlaysLoaded){
    			
    			/*Runnable hideOverlay = new Runnable (){
    				GroundOverlay go = overlays[lastOverlay];
    				@Override
    				public void run() {
    	        		go.setVisible(false);
    	        	}
    	        };
    			
    			handler.postDelayed(hideOverlay, 0);
    			*/
    			overlays[lastOverlay].setVisible(false);
    			overlays[progress].setVisible(true);
    			
    			
    	        lastOverlay = progress;
    			
    	        
    	        if(progress == 0){
    	        	setBigBoxText("Now");
    	        	setTimeBoxText("Displaying real-time data");
    	        }else{
    	        	setBigBoxText("Prediction");
	    	        SimpleDateFormat df = new SimpleDateFormat("EEEE MMMM d 'at' K:MM aa");
	    	        String dateString = df.format(getDateFromHours(getHoursFromNow(progress)));
	    	        setTimeBoxText(dateString);
    	        }
    	        
    	        //
    	        
    	        snippetUpdater.run();
    	        
    	        /*openMarker.setSnippet("In: "+ makerDataIn[progress] 
						+", Out: "+makerDataOut[progress] 
						+", Estimated "+ (Math.round(ratio*10)/10)+"% people ill");*/
    		}
        }
        
        public Handler handler = new Handler();
	};
	
	
	public void setOpenMarker (Marker m){
		openMarker = m;
	}
	
	public void setOpenMarkerData (int[] newMarkerDataIn, int[] newMarkerDataOut){
		markerDataIn = newMarkerDataIn;
		markerDataOut = newMarkerDataOut;
	}
	
	private void setTimeBoxText (String text){
		timeBox.setText(text);
	}
	private void setBigBoxText (String text){
		bigBox.setText(text);
	}
	
	private float getHoursFromNow (int index) {
		float count = 0;
		
		for(int i = 0; i<index; i++){
			if(count < 24){
				count += 0.25;
				continue;
			}
			if(count < 24*2){
				count += 0.5;
				continue;
			}
			if(count < 24*3){
				count += 1;
				continue;
			}
			if(count < 24*4){
				count += 2;
				continue;
			}
			if(count < 24*5){
				count += 4;
				continue;
			}
			if(count < 24*6){
				count += 6;
				continue;
			}
			if(count < 24*7){
				count += 12;
				continue;
			}
			count += 24;
		}
		
		return count;
	}
	
	private Date getDateFromHours (float hours){
		Date date = new Date();
		Date currentTime = new Date();
		
		date.setTime((long) (currentTime.getTime() + hours*3600000));
		
		return date;
	}
	
	/*private float getHoursFromNow (int i){
		
		float running = 0;
		float runningI = i;
		
		if(i == 0){
			return 0;
		}
		
		if(i <= 24*4){
			return 0.25f * i;
		}
		running += 24*4;
		runningI -= 24*4;
		
		if(i <= running + 24*2){
			return (running + runningI * 0.5f);
		}
		running += 24*2;
		runningI -= 24*2;
		
		if(i <= running + 24){
			return (running + runningI * 1);
		}
		running += 24;
		runningI -= 24;
		
		if(i <= running + 12){
			return (running + runningI * 2);
		}
		running += 12;
		runningI -= 12;
		
		if(i <= running + 6){
			return (running + runningI * 4);
		}
		running += 6;
		runningI -= 6;
		
		if(i <= running + 4){
			return (running + runningI * 6);
		}
		running += 4;
		runningI -= 4;
		
		if(i <= running + 2){
			return (running + runningI * 12);
		}
		running += 2;
		runningI -= 2;
		
		if(i <= running + 7){
			return (running + runningI * 24);
		}
		running += 24;
		runningI -= 24;
		
		return running;
	}*/
}












