package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataManager
{
	
	private TreeMap<String, float[]> stations;
	
	public DataManager() {}
	
	public void init()
	{
		stations = new TreeMap<String, float[]>();
	}
	
	
	
	public String[] getBoroughNames()
	{
		
		String[] BoroughNames = new String[33];
		BufferedReader br = null;
		
		try 
		{
 
			String Temp;
			br = new BufferedReader(new FileReader("./res/BoroughKey.txt"));
			Temp = br.readLine();
			br.close();
			BoroughNames = Temp.split(", ");
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/BoroughKey.txt.");
		
		}
		
		return BoroughNames;
		
	}
	
	public int[] getBoroughDensities()
	{
	
		int[] PopDen = new int[33];
		BufferedReader br = null;
		
		//Takes in Densities, in sets it to the array, then sets borough and pop to cells.
		
		try 
		{
	
			String BoroughPos;
			br = new BufferedReader(new FileReader("./res/BoroughDensities.txt"));
			BoroughPos = br.readLine();
			br.close();
			String[] tokens = BoroughPos.split(" ");
			
			for(int i = 0; i < 33; i++)	{ PopDen[i] = 259*Integer.valueOf(tokens[i]);	}
	
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/BoroughDensities.txt.");
			
		}
		
		return PopDen;
		
	}
	
	public int[] getBoroughPlaces()
	{
		
		int[] BoroughPlaces = new int[1600];
		BufferedReader br = null;
		
		try 
		{
 
			String BoroughPos;
			br = new BufferedReader(new FileReader("./res/BoroughPlace.txt"));
			BoroughPos = br.readLine();
			br.close();
			String[] tokens = BoroughPos.split(" ");
				
			for(int i = 0; i < 1600; i++)
			{
				
				BoroughPlaces[i] =  Integer.valueOf(tokens[i]);
					
			}
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/BoroughPlace.txt.");
		
		}
		
		return BoroughPlaces;
		
	}
	
	/*
	 
	Will return the travel data in the form of int[Stations][time step in order].
	
	*/
	
	public int[][] getWeekTravelInData()
	{
		
		int[][] TravelData = new int[268][24*4];
		
		return TravelData;
		
	}
	
	public int[][] getSatTravelInData()
	{
		
		int[][] TravelData = new int[268][24*4];
		
		return TravelData;
		
	}
	
	public int[][] getSunTravelInData()
	{
		
		int[][] TravelData = new int[268][24*4];
		
		return TravelData;
		
	}
	
	public int[][] getWeekTravelOutData()
	{
		
		int[][] TravelData = new int[268][24*4];
		
		return TravelData;
		
	}
	
	public int[][] getSatTravelOutData()
	{
		
		int[][] TravelData = new int[268][24*4];
		
		return TravelData;
		
	}
	
	public int[][] getSunTravelOutData()
	{
		int[][] TravelData = new int[268][24*4];
		
		return TravelData;
	}
	
	
	//Gets the insight for the given day
	//Probably contains a bug where record will not be found as 01-01-2011 is in flu2010.csv
	public byte getGoogleInsights(Calendar date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Byte returnVal = -1;
		String dir = new String("./res/GoogleManager/flu" + date.get(Calendar.YEAR) + ".csv");
		File csv = new File(dir);
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(csv));
		}
		catch(FileNotFoundException e)
		{
			MainManager.logMessage("#DataManager: Google Insights does not exist for that year, try updating GoogleManager");
			e.printStackTrace();
			return -1;
		}
		
		String record = new String();
		
		try
		{
			do
			{
				record = reader.readLine();
				if(record.length() == 0) continue;
				
				if(Character.isDigit(record.charAt(0)))
				{
					String[] pair = record.split(",");
					String[] dates = pair[0].split(" - ");
					
					long earlyDate = format.parse(dates[0]).getTime();
					long laterDate = format.parse(dates[1]).getTime();
					
					if(date.getTimeInMillis() < laterDate && date.getTimeInMillis() >= earlyDate)
					{
						returnVal = Byte.parseByte(pair[1]);
						break;
					}
				}
			}
			while(record != null);
		}
		catch(IOException e)
		{
			MainManager.logMessage("#DataManager: Could not read Google Insights, encountered IOException");
			e.printStackTrace();
			returnVal = -1;
		}
		catch(ParseException e)
		{
			MainManager.logMessage("#DataManager: Unable to parse dates in Google Insights file");
			e.printStackTrace();
			returnVal = -1;
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Exception e){}
		}
		
		return returnVal;
	}
	
	/*
	 
	Will put station i's x, y coordinate in TrainStations[i*2], TrainStations[i*2 + 1].
	 
	*/
	
	public int[] getTrainStations()
	{
		int[] TrainStations = new int[268*2];
		
		return TrainStations;
		
	}
	
	public float[] getStationLocation(String name)
	{
		return stations.get(name.toUpperCase());
	}
	
	//Loads the station locations from a KML file into a TreeMap for later retrieval by AnalysisManager
	public void loadStations()
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("./res/TravelManager/stations.kml"));
			
			doc.getDocumentElement().normalize();
			
			NodeList stationList = doc.getElementsByTagName("Placemark");
			
			for(int i = 0; i < stationList.getLength(); i++)
			{
				Node stationNode = stationList.item(i);
				
				NodeList stationInfo = stationNode.getChildNodes();
				
				String stationName = null;
				float[] coordinates = new float[2];
				
				for(int j = 0; j < stationInfo.getLength(); j++)
				{
					if(stationInfo.item(j).getNodeName() == "name")
					{
						stationName = stationInfo.item(j).getTextContent();
					}
					else if(stationInfo.item(j).getNodeName() == "Point")
					{
						String coordStr = stationInfo.item(j).getTextContent();
						String[] coords = coordStr.split(",");
						coordinates[0] = Float.parseFloat(coords[0]);
						coordinates[1] = Float.parseFloat(coords[1]);
					}
				}
				
				stations.put(stationName.toUpperCase(), coordinates);
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#DataManager: Could not parse station KML file");
			e.printStackTrace();
		}
	}
}
