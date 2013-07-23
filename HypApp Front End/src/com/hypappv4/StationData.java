package com.hypappv4;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class StationData {

	public LatLng location;
	public int[] peopleIn;
	public int[] peopleOut;
	public String name;
	public Marker marker;
	
	public StationData (String inName, LatLng inLocation, int[] inPeopleIn, int[] inPeopleOut){
		name = inName;
		location = inLocation;
		peopleIn = inPeopleIn;
		peopleOut = inPeopleOut;
	}
	
	public void setMarker (Marker m){
		marker = m;
	}
}





