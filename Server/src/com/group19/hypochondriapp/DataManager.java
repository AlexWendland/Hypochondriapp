package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	
	/*
	 
	Will put station i's x, y coordinate in TrainStations[i*2], TrainStaitions[i*2 + 1].
	 
	*/
	
	public int[] getTrainStations()
	{
		
		
		
		
		int[] TrainStations = new int[268*2];
		
		return TrainStations;
		
	}
	
	public float[] getStationLocation(String name)
	{
		return stations.get(name);
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
						stationName = ((Element) stationInfo.item(j)).getTextContent();
						//stationName = stationInfo.item(j).getNodeValue();
						System.out.println(stationName);
					}
					else if(stationInfo.item(j).getNodeName() == "Point")
					{
						String coordStr = stationInfo.item(j).getTextContent();
						System.out.println(coordStr);
						String[] coords = coordStr.split(",");
						coordinates[0] = Float.parseFloat(coords[0]);
						coordinates[1] = Float.parseFloat(coords[1]);
					}
				}
				
				stations.put(stationName, coordinates);
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#DataManager: Could not parse station KML file");
			e.printStackTrace();
		}
	}
	
}
