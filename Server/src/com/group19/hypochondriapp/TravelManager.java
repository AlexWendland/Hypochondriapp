package com.group19.hypochondriapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;

public class TravelManager implements Runnable
{
	private static final String STATIONS_URL = "http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=a.p.wendland@gmail.com&feedId=4";
	private static final String TRAVELDATA_URL = "http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=a.p.wendland@gmail.com&feedId=25";
	
	public void run()
	{
		getLatestTravelData();
		extractAndConvertTravel();
		
		getStationLocations();
	}
	
	private void getLatestTravelData()
	{
		try
		{
			URLConnection connection = new URL(TRAVELDATA_URL).openConnection();
			
			InputStream fileStream = connection.getInputStream();
			
			MainManager.logMessage("#TravelManager: Latest ZIP downloaded");
			
			File zip= new File("./res/TravelManager/traveldata.zip");
			
			zip.createNewFile();
			
			FileOutputStream toFile = new FileOutputStream(zip);
			
			byte[] buffer = new byte[1024];
			int len = fileStream.read(buffer);
			while (len != -1) 
			{
			    toFile.write(buffer, 0, len);
			    len = fileStream.read(buffer);
			}
			
			MainManager.logMessage("#TravelManager: Latest ZIP saved to disk in \"traveldata.zip\"");
			
			toFile.close();
			fileStream.close();
		}
		catch(IOException e)
		{
			MainManager.logMessage("#TravelManager: Unable to download latest travel data");
			e.printStackTrace();
		}
	}
	
	private void extractAndConvertTravel()
	{
		UtilityManager.unZip("./res/TravelManager/traveldata.zip", "./res/TravelManager/XLSTravelData/");
		
		File dir = new File("./res/TravelManager/XLSTravelData/");
		for (File child : dir.listFiles()) 
		{
			try
			{
				File csv = new File("./res/TravelManager/CSVTravelData/" + child.getName() + ".csv");
				UtilityManager.excelToCSV(child.getAbsolutePath(), new PrintStream(csv));
			}
			catch(Exception e)
			{
				MainManager.logMessage("#TravelManager: Could not convert travel data");
				e.printStackTrace();
			}
		}
	}
	
	private void getStationLocations()
	{
		try
		{
			URLConnection connection = new URL(STATIONS_URL).openConnection();
			
			InputStream fileStream = connection.getInputStream();
			
			MainManager.logMessage("#TravelManager: KML downloaded");
			
			File kml = new File("./res/TravelManager/stations.kml");
			
			kml.createNewFile();
			
			FileOutputStream toFile = new FileOutputStream(kml);
			
			byte[] buffer = new byte[1024];
			int len = fileStream.read(buffer);
			while (len != -1) 
			{
			    toFile.write(buffer, 0, len);
			    len = fileStream.read(buffer);
			}
			
			MainManager.logMessage("#TravelManager: KML saved to disk in \"stations.kml\"");
			
			toFile.close();
			fileStream.close();
		}
		catch(IOException e)
		{
			MainManager.logMessage("#TravelManager: Unable to download station location data");
			e.printStackTrace();
		}
	}
}
