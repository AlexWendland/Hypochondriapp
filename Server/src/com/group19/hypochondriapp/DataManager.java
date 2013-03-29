package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.FileReader;

public class DataManager{
	
	public DataManager() {}
	
	public String[] getBoroughNames()
	{
		
		String[] BoroughNames = new String[33];
		BufferedReader br = null;
		
		try 
		{
 
			String Temp;
			br = new BufferedReader(new FileReader("./res/BoroughKey.txt"));
			Temp = br.readLine();
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
	
}
