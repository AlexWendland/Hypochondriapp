package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataManager
{
	//To be used with loadStationTravel to specify the info desired
	public static final String ENTER = "EN11";
	public static final String EXIT = "EX11";
	public static final String WEEK = "Week";
	public static final String SAT = "sat";
	public static final String SUN = "sun";
	
	
	private TreeMap<String, float[]> stations;
	private LinkedList<StationInfo> currentStations;
	
	public DataManager() 
	{
		init();
	}
	
	public void init()
	{
		stations = new TreeMap<String, float[]>();
		currentStations = new LinkedList<StationInfo>();
		loadStations();
	}
	
	
	
	public String[] getBoroughNames()
	{
		
		String[] BoroughNames = new String[33];
		
		try 
		{
 
			String Temp;
			BufferedReader br = new BufferedReader(new FileReader("./res/BoroughKey.txt"));
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
			
			for(int i = 0; i < tokens.length; i++)	{ PopDen[i] = (int)(259*Double.valueOf(tokens[i]));	}
	
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/BoroughDensities.txt.");
			
		}
		
		return PopDen;
		
	}
	
	public byte[] getBoroughPlaces()
	{
		
		byte[] BoroughPlaces = new byte[1600];
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
				
				BoroughPlaces[i] =  Byte.valueOf(tokens[i]);
					
			}
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/BoroughPlace.txt.");
		
		}
		
		return BoroughPlaces;
		
	}
	

	
	//Gets the next StationInfo as long as loadStationTravel has been called and there are more stations available to get
	public StationInfo getNextStation()
	{
		if(currentStations.isEmpty()) return null;
		else return currentStations.poll();
	}
	
	//Loads from the file next to be used by Analysis to enable getNextStation()
	public void loadStationTravel(String direction, String time)
	{
		currentStations.clear();
		File csv = new File("./res/TravelManager/CSVTravelData/" + direction + time + ".xls.csv");
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(csv));
		}
		catch(FileNotFoundException e)
		{
			MainManager.logMessage("#DataManager: Could not find file \"" + csv.getPath() + "\"");
			e.printStackTrace();
			return;
		}
		
		String record = new String();
		
		try
		{
			do
			{
				record = reader.readLine();
				if(record == null) break;
				if(record.length() == 0) continue;
				
				if(Character.isDigit(record.charAt(0)))
				{
					String[] attributes = record.split(",");
					String stationName = attributes[1];
					stationName = stationName.replaceAll("\"", "").trim();
					float[] coords = getStationLocation(stationName);
					
					int[] values = new int[96]; //96 sets of 15 minutes per day
					
					for(int i = 0; i < 96; i++)
					{
						values[i] = Integer.parseInt(attributes[4 + i]);
					}
					
					currentStations.add(new StationInfo(coords, values));
				}
			}
			while(record != null);
		}
		catch(IOException e)
		{
			MainManager.logMessage("#DataManager: Could not read \"" + csv.getPath() + "\", encountered IOException");
			e.printStackTrace();
		}
		catch(NumberFormatException e)
		{
			MainManager.logMessage("#DataManager: Unable to parse values in\"" + csv.getPath() + "\"");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Exception e){}
		}
		
		return;
		
		
	}
	
	
	//Gets the insight for the given day
	//Probably contains a bug where record will not be found as 01-01-2011 is in flu2010.csv
	//Returns -1 if nothing found in file
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
	
	public float[] getStationLocation(String name)
	{
		return stations.get(name.toUpperCase());
	}
	
	//Loads the station locations from a KML file into a TreeMap for later retrieval by AnalysisManager
	private void loadStations()
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
						if(stationName.contains(" Station")) stationName = stationName.replaceFirst(" Station", "");
					}
					else if(stationInfo.item(j).getNodeName() == "Point")
					{
						String coordStr = stationInfo.item(j).getTextContent();
						String[] coords = coordStr.split(",");
						coordinates[0] = Float.parseFloat(coords[0]);
						coordinates[1] = Float.parseFloat(coords[1]);
					}
				}
				
				//System.out.println(stationName.toUpperCase().trim());
				stations.put(stationName.toUpperCase().trim(), coordinates);
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#DataManager: Could not parse station KML file");
			e.printStackTrace();
		}
	}
	
	//Class containing coordinates and movement of people for a single station
	public class StationInfo
	{
		private StationInfo(float[] coords, int[] peeps)
		{
			coordinates = coords;
			people = peeps;
		}
		
		public float[] coordinates;
		public int[] people;
	}
}
