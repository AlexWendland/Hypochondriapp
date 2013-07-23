package com.hypappv4;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class StationLocationReader {
	
	//StationData[] stations;
	Vector<StationData> stations;
	Context context;
	
	public StationLocationReader (Context inContext, int[][] inOutData){
		Log.v("Into station location reader", "");
		
		context = inContext;
		
		//stations = new StationData[inOutData.length/2];
		stations = new Vector<StationData>();
		
		Log.v("Into station location reader", "");
		
		try {
			
			Log.v("", "1");
			InputStream stationLocations = context.getResources().openRawResource(R.raw.stationlocationinorder);
			Log.v("", "2");
			InputStreamReader streamReader = new InputStreamReader(stationLocations);
			Log.v("", "3");
			BufferedReader orderReader = new BufferedReader(streamReader);
			Log.v("", "4");
			String line;
			int i = 0;
			while ((line = orderReader.readLine()) != null) {
				
				String[] parts = line.split(":");
				String[] strCoords = parts[1].split(",");
				
				LatLng loc = new LatLng(Float.parseFloat(strCoords[1]), Float.parseFloat(strCoords[0]));
				
				stations.add(new StationData(parts[0], loc, inOutData[i*2], inOutData[i*2+1]));
				
				/*Log.v("Station: "+stations[i].location.toString(), "5");
				Log.v("Station: "+stations[i].peopleIn[0], "5");
				Log.v("Station: "+stations[i].peopleOut[0], "5");*/
				i++;
				
			}
			Log.v("", "6");
			orderReader.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Vector<StationData> getStations (){
		return stations;
	}
}