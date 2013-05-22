package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.group19.hypochondriapp.DataManager.StationInfo;

public class AnalysisManager implements Runnable {

	//Model variables.
	private int[] pop;
	private float[] ill;
	private byte[] borough;
	
	//The variables to do with model updates and output of AnalysisManager.
	private AppDataPacket toBeSent = new AppDataPacket();
	private boolean updated;
	
	//Files used and constants.
	File dataStore = new File("./res/AnalysisManager/DataStore.txt");
	File stations = new File("./res/AnalysisManager/Stations.txt");	
	File johnsFile = new File("./res/AnalysisManager/JohnsFile.txt");	
	
	public static short TWITSCALAR = 100;
	public static final byte TWITTER_CELLS = 100;  
	public static final byte DAY_OF_UPDATE = 0;
	public static final short CELL_EFFECT = 1000;
	public static final short SMOOTHING_SCALAR_1 = 1000;
	public static final short SMOOTHING_SCALAR_2 = 100;
	
	//Initiation of the model.
	public void init() 
	{
		
		int[] popDen = MainManager.getDataManager().getBoroughDensities();
		borough = MainManager.getDataManager().getBoroughPlaces();	
		ill = new float[1600];
		pop = new int[1600];
		updated = true;
		
		for(int i = 0; i < 1600; i++)	
		{ 
			
			pop[i] = popDen[borough[i] - 1];	
			ill[i] = 0;
			
		}
		
		try
		{
			
			if (!dataStore.exists()) 
				dataStore.createNewFile();
			
		
		} catch (Exception e)
		{
			
			MainManager.logMessage("#AnalysisManager: No data stores for generated data, recomend shutdown." );
			
		}
			
		
	}
	
	
	public void johnsFunction()
	{
		
		String[] Temp = new String[269];
		try{
			BufferedReader br = new BufferedReader(new FileReader(stations));
			for(int i = 0; i < 269; i++)
			{
				Temp[i] = br.readLine();
				Temp[i] = Temp[i].trim();
			}
			br.close();
		} catch (Exception e) 
		{ 
			
			System.out.println("ARGHHH");
			
		}
		
		String output = new String();
		
		MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.SUN);
		
		for (int i = 0; i < 269; i++) 
		{
			
			StationInfo currentStation = MainManager.getDataManager().getNextStation();
			if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
			{
				output += Temp[i] + ":" + currentStation.coordinates[0] + "," + currentStation.coordinates[1] + "\n";
			} else
			{
				output += Temp[i] + ":0,0\n"; 
			}
			
		}
		try{
			if (!johnsFile.exists()) 
				johnsFile.createNewFile();
		} catch (Exception e)
		{
			
			System.out.println("Silly");
			
		}
		
		try{
			
			BufferedWriter tempBW = new BufferedWriter(new FileWriter(johnsFile.getAbsoluteFile()));
			tempBW.write(output);
			tempBW.close();
			
		} catch (Exception e)
		{
			
			MainManager.logMessage("ARGGGGGGHHHHHH");
			
		}
	}
	
	
	public void checkIll()
	{
		
		for(int i = 0; i < 1600; i++)
		{
			
			if((ill[i] < 0) || Float.isNaN(ill[i]))
				ill[i] = 0;
		}
		
	}
	
	
	//Gets the cells around any cell
	public short[] getAroundCells(int num)
	{
		
		short[] returnedCells = new short[8];
		
		if(num > -1 && num < 1600)
		{
		
			double y = ((int)(num/40))*0.01 + 51.3 + 0.005;
			double x = (num%40)*0.02 - 0.5 + 0.01;		
				
			returnedCells[0] = (short) cordConv((float) (y - 0.01), (float) (x + 0.02));
			returnedCells[1] = (short) cordConv((float) (y), (float) (x + 0.02));
			returnedCells[2] = (short) cordConv((float) (y + 0.01), (float) (x + 0.02));
			returnedCells[3] = (short) cordConv((float) (y - 0.01), (float) (x));
			returnedCells[4] = (short) cordConv((float) (y + 0.01), (float) (x));
			returnedCells[5] = (short) cordConv((float) (y - 0.01), (float) (x - 0.02));
			returnedCells[6] = (short) cordConv((float) (y), (float) (x - 0.02));
			returnedCells[7] = (short) cordConv((float) (y + 0.01), (float) (x - 0.02));
		} else
		{
			
			for(int i = 0; i < 8; i++)
			{
				
				returnedCells[i] = -1;
				
			}
			
		}
		
		return returnedCells;
		
	}
	
	
	//Smooths data.
	public float[] smoothData(float[] input)
	{
		
		float[] returnData = new float[input.length];
		
		for(int i = 0; i < input.length; i++)
		{
			
			
			short[] aroundCells = getAroundCells(i);
			int count = 0;
			//float max = 0;
			//float min = 9999999;
			returnData[i] = (float) Math.pow(input[i], 2)*SMOOTHING_SCALAR_1;
			
			for(int j = 0; j < aroundCells.length; j++)
			{
				
				if(aroundCells[j] != -1)
				{
					
					/*
					
					if(input[aroundCells[j]] > max)
						max = input[aroundCells[j]];
					
					if(input[aroundCells[j]] < min)
						min = input[aroundCells[j]];
					
					*/
					
					count++;
					returnData[i] += (float) Math.pow(input[aroundCells[j]], 2)*SMOOTHING_SCALAR_2;
					
				}
				
			}
			/*
			if((input[i] <= min) || (input[i] >= max))
				returnData[i] = input[i];
			else
			{
			
			*/
			
				float divisableScalar = (float) (SMOOTHING_SCALAR_1 + count*SMOOTHING_SCALAR_2);
				returnData[i] = (float) ((float) Math.pow(returnData[i], 0.5)/Math.pow(divisableScalar, 0.5));
			
			/*
		
			}
			
			
			*/
		}
		
		return returnData;
		
	}
	
	
	//Resets the model to the old pop.
	public void resetPop()	
	{ 
		
		int[] popDen = MainManager.getDataManager().getBoroughDensities();
		for (int i = 0; i < 1600; i++)
			pop[i] = popDen[borough[i] - 1];		
		
	}
	
	
	//Converts from Long and latitude to cell position.
	public int cordConv(float y, float x) 
	{
		
		x = (float) ((x + 0.5)/0.02);
		y = (float) ((y - 51.3)/0.01);
		int pos = 0;
			
		if( (x <= 0) || (x >= 40) )
			return(-1);
		else
			pos = (int)x;
			
		if( (y <= 0) || (y >= 40) )
			return(-1);
		else
			pos += ((int)y)*40;
			       
		return(pos);
		
	}
	
	
	//Outputs the average ill of the current model.
	public float getAverageIll()
	{
		
		float average = 0;
		float fails = 0;
		
		for(int i = 0; i < 1600; i++)	
		{ 
			try{ average += ill[i]/pop[i];	}
			catch (Exception e) 	{ fails++;	}
		}
		try { return average/(1600 - fails);	}
		catch (Exception e)	{ return 0;	}
		
	}
	
	
	//Finds the cells allocated to a certain borough and returns them in a array.
	public int[] findBoroughCells(int boroughNum)
	{
	
		int[] temp = new int[1600];
		int num = 0;
		
		for(int i = 0; i < 1600; i++)
		{
			
			if(borough[i] == boroughNum)
			{
				
				temp[num] = i;
				num++;
				
			}
			
		}
		
		int[] Return = new int[num+1];
		for (int i = 0; i <= num; i++)	{ Return[i] = temp[i];	}
		return Return;
		
	}
	
	
	//Generates random cells to put ill people in.
	private int[] getRandomCells()
	{
		
		int[] randomCells = new int[TWITTER_CELLS];
		Random generator = new Random();
		
		for (int i = 0; i < TWITTER_CELLS; i++)	{ randomCells[i] = generator.nextInt(800);	}
		
		return randomCells;
		
	}
	
	
	//Function will be called, to sort and allocated expected ill from the twitter data.
	public void setTweets()
	{
		
		ArrayList<String> Tweets = MainManager.getTwitterManager().getTweets();
		String[] PlaceNames = MainManager.getDataManager().getBoroughNames();
		int geoUsed = 0;
		
		if(Tweets.size() < 1000)
			TWITSCALAR = (short) (100*(2000/Tweets.size()));
		
		for(int k = 0; k < Tweets.size(); k++)
		{
			
			String Place = Tweets.get(k);
			boolean ContinueNeeded = false;
			try
			{
				
				if(Character.isDigit(Place.charAt(0)))
				{
					
					String[] coords = Place.split(",");
					int cell = cordConv(Float.valueOf(coords[0]), Float.valueOf(coords[1]));
					
					if(cell != -1)
					{
						
						short[] cellsAround = getAroundCells(cell);
						int count = 0;
						
						for(int i = 0; i < cellsAround.length; i++)
						{
							
							if(cellsAround[i] != -1)
							{
								
								count++;
								ill[cellsAround[i]] += TWITSCALAR/12;
								
							}
							
						}
							
						ill[cell] += (TWITSCALAR)*((12 - count)/12);
						
						geoUsed++;
						ContinueNeeded = true;
						
					}
					
				}
				
			} catch (Exception e) { }
			
			if (ContinueNeeded)
				continue;
			
			try
			{
				
				for(int i = 0; i < PlaceNames.length; i++)
				{
				
					if(Place.contains(PlaceNames[i]))
					{
						
						int[] Cells = findBoroughCells(i+1);
						
						for(int j = 0; j < Cells.length; j++)	
						{
						
							ill[Cells[j]] += (TWITSCALAR/Cells.length);
						
						}
						
						ContinueNeeded = true;
						
					}
					
				}
				
			} catch(Exception e) 
			{ 
				
				MainManager.logMessage("#AnalysisManager: Failed to identify buroughs, skipping step.");
				
			}
			
			if (ContinueNeeded)
				continue;
			
			if(Place.contains("EAST"))
			{
				
				int[] cells = getRandomCells();
				
				for(int i = 0; i < TWITTER_CELLS; i++)
				{
					
					ill[((int)(cells[i]/20))*40 + 20 + (cells[i]%20)] += TWITSCALAR/TWITTER_CELLS;
					
				}
				
				continue;
				
			}
			
			if(Place.contains("WEST"))
			{
				
				int[] cells = getRandomCells();
				
				for(int i = 0; i < TWITTER_CELLS; i++)
				{
					
					ill[((int)(cells[i]/20))*40 + (cells[i]%20)] += TWITSCALAR/TWITTER_CELLS;
					
				}
				
				continue;
				
			}
			
			if(Place.contains("SOUTH"))
			{
				
				int[] cells = getRandomCells();
				
				for(int i = 0; i < TWITTER_CELLS; i++)
				{
					
					ill[cells[i]] += TWITSCALAR/TWITTER_CELLS;
					
				}
				
				continue;
				
			} 
			
			if(Place.contains("NORTH"))
			{
				
				int[] cells = getRandomCells();
				
				for(int i = 0; i < TWITTER_CELLS; i++)
				{
					
					ill[cells[i]+800] += TWITSCALAR/TWITTER_CELLS;
					
				}
				
				continue;
				
			}
			
			if(Place.contains("CENTRAL"))
			{
				
				int[] cells = getRandomCells();
				
				for(int i = 0; i < TWITTER_CELLS; i++)
				{
					
					ill[((int)(cells[i]/40))*40 + 410 + (cells[i]%40)] += TWITSCALAR/TWITTER_CELLS;
					
				}
				
				continue;
				
			}
				
			int[] cells = getRandomCells();
				
			for(int i = 0; i < TWITTER_CELLS; i++)
			{
					
				ill[cells[i]*2] += TWITSCALAR/(TWITTER_CELLS*2);
					
			}
				
			cells = getRandomCells();
				
			for(int i = 0; i < TWITTER_CELLS; i++)
			{
					
				ill[cells[i]*2 + 1] += TWITSCALAR/(TWITTER_CELLS*2);
					
			}
			
		}
		
		TWITSCALAR = 100;
		MainManager.logMessage("#AnalysisManager: " + geoUsed + " Geo Location where used");
		
	}
	
	
	//Function is called to add or remove people from a certain position.
	public void movePeople(float x, float y, float num, float averageIll)
	{
	
		int pos = cordConv(y, x);
		float illMove = num*averageIll/12;
		
		if (pos > -1)
		{
			
			short[] cellAround = getAroundCells(pos);
			
			int count = 0;
			
			for(int i = 0; i < cellAround.length; i++)
			{
				
				if(cellAround[i] > -1)
				{
					
					pop[cellAround[i]] += ((int)num/12);
					if(pop[cellAround[i]] < 0)
						pop[cellAround[i]] = 0;
					ill[cellAround[i]] += (illMove);
					if(ill[cellAround[i]] < 0)
						ill[cellAround[i]] = 0;
					
					count++;
					
				}
				
			}
			
			pop[pos] += ((int)(num*(12-count))/12);
			if(pop[pos] < 0)
				pop[pos] = 0;
			ill[pos] += (illMove)*(12-count);
			if(ill[pos] < 0)
				ill[pos] = 0;
			
		} else
		{
			
			//MainManager.logMessage("#AnalysisManager: Train station not in map.");

		}
		
	}

	
	//Returns the usefull google insight data.
	public byte[] getGoogleInsight()
	{
		
	    Calendar currentDate = Calendar.getInstance();
	    currentDate.setTime(new Date());
	    
	    currentDate.add(Calendar.WEEK_OF_YEAR, -2);
	    
	    ArrayList<Byte> data = new ArrayList<Byte>();
	    byte temp = 0;
	    
		while (((temp = MainManager.getDataManager().getGoogleInsights(currentDate)) != -1))
		{
			
			data.add(temp);
			currentDate.add(Calendar.WEEK_OF_YEAR, -1);
			
		}
		
		byte[] returnData = new byte[data.size()];
		
		for(int i = 0; i < data.size(); i++)
			returnData[i] = data.get(i);
		
		return returnData;
		
	}
	
	
	//Function is a called to provide a predicted future for a certain cell.
	public static float[] prediction(float[] previousAverage, byte[] insightData, float[] dataLondon)
	{
		
		float currentMinError = 999999999;
		float[] currentEstimate = new float[2];
		
		currentEstimate[1] = currentEstimate[0] = 0;
		
		for(int i = 0; i < insightData.length - 7; i++)
		{
			
			float error = 0;
			
			float scalar = previousAverage[0]/insightData[2 + i];
				
			if (scalar <= 0) 	continue;
				
			for(int j = 0; j < 4; j++)
			{
					
				error += Math.pow((previousAverage[j+1] - insightData[3+i+j]*scalar),2);
					
			}
				
			error = (float) Math.pow(error, 0.5);
				
			if(error < currentMinError)
			{
					
				currentMinError = error;
				currentEstimate[0] = insightData[1+i]*scalar;
				currentEstimate[1] = insightData[i]*scalar;
					
			}
			
		}
			
		if((dataLondon != null) && (dataLondon.length > 6))
		{
			
			for(int i = 0; i < dataLondon.length - 6; i++)
			{
				
				if((dataLondon[i] != -1) && (dataLondon[i+1] != -1) && (dataLondon[i+2] != -1) && (dataLondon[i+3] != -1) && (dataLondon[i+4] != -1) && (dataLondon[i+5] != -1) && (dataLondon[i+6] != -1))
				{
					
					float error = 0;
					
					float scalar = previousAverage[0]/dataLondon[i + 4];
						
					if (scalar <= 0) 	continue;
						
					for(int j = 0; j < 4; j++)
					{
							
						error += Math.pow((previousAverage[j+1] - dataLondon[3+i-j]*scalar),2);
							
					}
						
					error = (float) Math.pow(error, 0.5);
						
					if(error < currentMinError)
					{
							
						currentMinError = error;
						currentEstimate[0] = dataLondon[5+i]*scalar;
						currentEstimate[1] = dataLondon[6+i]*scalar;
							
					}
					
				}
				
			}
		}
		return currentEstimate;
		
	}
	
	
	//Function is called to save current model to the data store.
	public void recordData(Calendar currentDate)
	{
		
		boolean needToUpdate = false;
		
		if((!updated) && (currentDate.get(Calendar.DAY_OF_WEEK) == DAY_OF_UPDATE))
		{
			
			needToUpdate = true;
			updated = true;
			
		}
		
		if( (currentDate.get(Calendar.DAY_OF_WEEK) != DAY_OF_UPDATE) && (updated))
		{
			
			updated = false;
			
		}
		
		String[] readInData;
		
		try 
		{

			BufferedReader br = new BufferedReader(new FileReader(dataStore.getAbsoluteFile()));
			String Temp = br.readLine();
			br.close();
			
			if ((Temp != null) && (Temp.length() != 0))
			{
				
				readInData = Temp.split(" - ");
				
			} else
			{
				
				readInData = new String[1];
				
			}
			
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#AnalysisManager: Could not read ./res/AnalysisManager/DataStore.txt, recommend shut down.");
			return;
		
		}
		
		String content = new String();
		
		for(int i = 0; i < readInData.length - 1; i++)
			content += readInData[i] + " - ";
		
		String[] Temp = new String[0];
		
		try
		{
			Temp = readInData[readInData.length - 1].split(" ");
		} catch (Exception e) { }
		
		if(needToUpdate)
		{
			
			for(int i = 1; i < Temp.length; i++)
				content += Temp[i] + " ";
			
			content += "- 1 ";
			
			for(int i = 0; i < 1600; i++)
				content += ill[i] + " ";
			
			content += "- ";
			
		} else
		{
			
			int num = 1;
			if(Temp.length > 0)
			{
				num = Integer.valueOf(Temp[0]) + 1;
			
				content += num + " ";
			
				for(int i = 0; i < 1600; i++)
					content += ((ill[i] + (Float.valueOf(Temp[i])*(num-1)))/num) + " ";
			
				content += "- ";
			} else
			{
				
				content += num + " ";
				
				for(int i = 0; i < 1600; i++)
					content += (ill[i] + " ");
			
				content += "- ";
				
			}
			
		}
		
		try{
		
			BufferedWriter tempBW = new BufferedWriter(new FileWriter(dataStore.getAbsoluteFile()));
			tempBW.write(content);
			tempBW.close();
			
		} catch (Exception e)
		{
			
			MainManager.logMessage("#AnalysisManager: Failed to save current itteration of model.");
			
		}
		
	}
	
	
	//Function is called to read the store for the previous infomation about that cell.
	public float[][] getPreviousData()
	{
		
		float[][] pastData = new float[1600][5];
		String[] scannedInData = new String[5];
		
		try 
		{

			BufferedReader br = new BufferedReader(new FileReader(dataStore.getAbsoluteFile()));
			String Temp1 = br.readLine();
			br.close();
			
			if ((Temp1 != null) && (Temp1.length() != 0))
			{
				
				scannedInData = Temp1.split(" - ");
				
			}
			
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#AnalysisManager: Could not read ./res/AnalysisManager/DataStore.txt, recommend shut down.");
		
		}
		
		for(int cell = 0; cell < 1600; cell++)
		{
		
			pastData[cell][0] = pastData[cell][1] =  pastData[cell][2] = pastData[cell][3] = pastData[cell][4] = 0;
			
			try 
			{
	
				int length = scannedInData.length;
				for(int i = 0; i < Math.min(5, length - 1); i++)
				{
					String[] Temp3 = scannedInData[length - (2+i)].split(" ");
					
					if(Temp3.length > 1599)
						pastData[cell][i] = Float.valueOf(Temp3[cell]);
					else
						pastData[cell][i] = 0;
	
				}
				
			} 
			catch (Exception e) 
			{
				
				MainManager.logMessage("#AnalysisManager: Could not read ./res/AnalysisManager/DataStore.txt, recommend shut down.");
			
			}
			
		}
		
		return pastData;
		
	}
	
	
	//Add the NHS data.
	public void addNHS()
	{
		
		float[] NHS = MainManager.getDataManager().getNHS();
		
		for(int i = 0; i < 1600; i++)
		{
			
			ill[i] += NHS[borough[i] - 1]*pop[i]/1000;
			
		}
		
	}
	
	
	public float[] backtrackTravel()
	{
		
		Calendar date = Calendar.getInstance();
	    date.setTime(new Date());
		
	    float[] stationPositions = new float[269*2];
		int Day = date.get(Calendar.DAY_OF_WEEK);
		int MinRep = (int)(date.get(Calendar.MINUTE)/15);
		int HourRep = date.get(Calendar.HOUR_OF_DAY) - 2;
		float average = getAverageIll();
		
		for(int i = 0; i < 269*2; i++)
			stationPositions[i] = 0;
		
		if(((Day == 0) && (HourRep >= 0)) || ((Day == 1) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.SUN);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep - 1; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
					stationPositions[i*2] = currentStation.coordinates[0];
					stationPositions[i*2 + 1] = currentStation.coordinates[1];
					
				}
				
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.SUN);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep - 1; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
		}else if(((Day == 7) && (HourRep >= 0)) || ((Day == 0) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.SAT);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep - 1; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
					stationPositions[i*2] = currentStation.coordinates[0];
					stationPositions[i*2 + 1] = currentStation.coordinates[1];
					
				}
				
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.SAT);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep - 1; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
		} else
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.WEEK);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep - 1; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
					stationPositions[i*2] = currentStation.coordinates[0];
					stationPositions[i*2 + 1] = currentStation.coordinates[1];
				
				}
					
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.WEEK);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep - 1; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
		}
		
		return stationPositions;
		
	}
	
	
	//Allocates relivant station data
	public int[] setStation(int[] stationData, String day)
	{
		
		Calendar currentDate = Calendar.getInstance();
	    currentDate.setTime(new Date());
	    int[] returnData = new int[24*8 + 7];
	    
		for(int i = 0; i < 24*4*14; i++)
		{
			
			if(i < 24*8 + 7)
			{
				
				returnData[i] = 0;
				
			}
			
			int tempData = 0;
			
			int Day = currentDate.get(Calendar.DAY_OF_WEEK);
			int MinRep = (int)(currentDate.get(Calendar.MINUTE)/15);
			int HourRep = currentDate.get(Calendar.HOUR_OF_DAY) - 2;
			
			currentDate.add(Calendar.MINUTE, 15);
			
			if(((Day == 0) && (HourRep >= 0)) || ((Day == 1) && (HourRep < 0)))
			{
				
				if(day == DataManager.SUN)
				{
					
					tempData = stationData[((HourRep + 24)%24)*4 + MinRep];
					
				}
				
			}else if(((Day == 7) && (HourRep >= 0)) || ((Day == 0) && (HourRep < 0)))
			{
				
				if(day == DataManager.SAT)
				{
					
					tempData = stationData[((HourRep + 24)%24)*4 + MinRep];
					
				}
				
			} else
			{
				
				if(day == DataManager.WEEK)
				{
					
					tempData = stationData[((HourRep + 24)%24)*4 + MinRep];
					
				}
				
			}
			
			if(i < 24*4)
				returnData[i] = tempData;
			else if (i < 24*4*2)
				returnData[24*4 + (int)(i - 24*4)/2] += tempData;
			else if (i < 24*4*3)
				returnData[24*6 + (int)(i - 24*4*2)/4] += tempData;
			else if (i < 24*4*4)
				returnData[24*7 + (int)(i - 24*4*3)/8] += tempData;
			else if (i < 24*4*5)
				returnData[24*7 + 12 + (int)(i - 24*4*4)/16] += tempData;
			else if (i < 24*4*6)
				returnData[24*7 + 18 + (int)(i - 24*4*5)/24] += tempData;
			else if (i < 24*4*7)
				returnData[24*7 + 22 + (int)(i - 24*4*6)/48] += tempData;
			else
				returnData[24*8 + (int)(i - 24*4*7)/96] += tempData;
			
			
		}
	    
	    return returnData;
		
	}
	
	
	//Gets the travel data I require to send.
	public int[][] getTravel()
	{
		
		int[][] currentTransport = new int[269*2][24*8 + 7];
		
		for(int k = 0; k < 6; k++)
		{
			
			String day = new String();
			String inOut = new String();
			
			if((k==0)||(k==1))
				day = DataManager.SUN;
			
			if((k==2)||(k==3))
				day = DataManager.WEEK;
				
			else
				day = DataManager.SAT;
			
			if((k== 0) || (k==2) || (k==4))
				inOut = DataManager.ENTER;
			
			else
				inOut = DataManager.EXIT;
			
			MainManager.getDataManager().loadStationTravel(inOut, day);
		
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				int add = 0;
				
				if((k== 0) || (k==2) || (k==4))
					add = 0;
				else
					add = 1;
				
				if ((currentStation != null) && (currentStation.people != null) && (currentStation.people.length > (24*4 - 1)))
				{
					
					int[] temp = setStation(currentStation.people, day);
					
					for(int j = 0; j < 24*8 + 7; j++)
					{
						currentTransport[i*2 + add][j] += temp[j];
					}
					
				}
				else
				{
					for(int j = 0; j < 24*8 + 7; j++)
					{
						
						currentTransport[i*2 + add][j] += 0;
						
					}
				}		
			}
	    
		}
			
	    return currentTransport;
		
	}
	
	
	//If the model is required to generate a new packet for the app, this will run.
	public void update()
	{
		
		ill = new float[1600];
		
		resetPop();
		
		for(int i = 0; i < 1600; i++) { ill[i] = 0;	}
		
	    Calendar currentDate = Calendar.getInstance();
	    currentDate.setTime(new Date());
		
		setTweets();
		addNHS();
		
		float[][] change = new float[2][1600];
		
		byte[] insightData = getGoogleInsight();
		float[][] previousData = getPreviousData();
		String[] boroughNames = MainManager.getDataManager().getBoroughNames();
		float[][] dataLondon = new float[33][0];
		
		for(int i = 0; i < 33; i++)
		{
			
			dataLondon[i] = MainManager.getDataManager().getFluRates(boroughNames[i]);
			
		}
		
		for(int i = 0; i < 1600; i++)
		{
			
			float[] predictedData = prediction(previousData[i], insightData, dataLondon[borough[i] - 1]);
				
			currentDate = Calendar.getInstance();
		    currentDate.setTime(new Date());
			
			change[0][i] = ((ill[i] - predictedData[0])/(7*24*4 - 1));
			change[1][i] = (predictedData[0] - predictedData[1])/7;
			
			
		}
		
		int[][] transportData = getTravel();
		float[] stationPosition = backtrackTravel();
		float[][] dataToBeSent = new float[24*8+7][1600];
		float[][] ratioData = new float[24*8+7][1600];
		dataToBeSent[0] = ill;
		
		recordData(currentDate);
		
		for(int i = 0; i < 1600; i++)
			ratioData[0][i] = (ill[i]*10000)/pop[i];
		
		
		for(int i = 1; i < 24*8 + 7; i++)
		{
			
			if(i < 24*4)
			{
				
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - change[0][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + count);
					
				}
				
			}
			else if (i < 24*6)
			{
				
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - 2*change[0][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += 2*ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + 2*count);
					
				}
				
			}
			else if (i < 24*7)
			{
				
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - 4*change[0][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += 4*ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + 4*count);
					
				}
				
			}
			else if (i < 24*7 + 12)
			{
				
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - 8*change[0][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += 8*ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + 8*count);
					
				}
				
			}
			else if (i < 27*7 + 18)
			{
			
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - 16*change[0][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += 16*ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + 16*count);
					
				}
				
			}
			else if (i < 27*7 + 22)
			{
				
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - 24*change[0][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += 24*ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + 24*count);
					
				}
				
			}
			else if (i < 24*8)
			{
			
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - 48*change[0][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += 48*ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + 48*count);
					
				}
				
			}
			else
			{
				
				for(int j = 0; j < 1600; j++)
				{
					
					dataToBeSent[i][j] = (ill[j] - change[1][j])*CELL_EFFECT;
					
					short[] AroundCells = getAroundCells(j);
					
					int count = 0;
							
					for(int k = 0; k < AroundCells.length; k++)
					{
						
						if(AroundCells[k] > -1)
						{
							
							count++;
							dataToBeSent[i][j] += 96*ill[AroundCells[k]];
							
						}
						
					}
					
					dataToBeSent[i][j] /= (CELL_EFFECT + 96*count);
					
				}
				
			}
			
			ill = dataToBeSent[i];
			
			for(int j = 0; j < 269; j++)
			{	
				
				movePeople(stationPosition[j*2], stationPosition[j*2 + 1], transportData[j*2][i], getAverageIll());
				movePeople(stationPosition[j*2], stationPosition[j*2 + 1], -transportData[j*2 + 1][i], getAverageIll());
				
			}
			
			checkIll();
			
			dataToBeSent[i] = ill;
			
			for(int j = 0; j < 1600; j++)	
				ratioData[i][j] = (ill[j]*10000)/pop[j];
			
		}
		
		float[] newIllScalar = new float[24*8+7];
		byte[][] newIllData = new byte[24*8+7][1600];
		float[] newRatioScalar = new float[24*8+7];
		byte[][] newRatioData = new byte[24*8+7][1600];
		
		for(int i = 0; i < 24*8 + 7; i++)
		{
			
			dataToBeSent[i] = smoothData(dataToBeSent[i]);
			ratioData[i] = smoothData(ratioData[i]);
			
			float max1 = 0;
			float max2 = 0;
			
			for(int j = 0; j < 1600; j++)
			{
				
				if(dataToBeSent[i][j] > max1)
				{
					max1 = dataToBeSent[i][j];
					
				}	
					
				if(ratioData[i][j] > max2)
				{
					max2 = ratioData[i][j];
				}
					
			}
					
			newIllScalar[i] = max1;
			newRatioScalar[i] = max2;
			
			for(int j = 0; j < 1600; j++)
			{
				
				if(max1 == 0)
					newIllData[i][j] = -128;
				else
					newIllData[i][j] = (byte) ((dataToBeSent[i][j]*255/max1) - 128);
				
				if(max2 == 0)
					newRatioData[i][j] = -128;
				else
					newRatioData[i][j] = (byte) ((ratioData[i][j]*255/max2) - 128);
				
			}	
			
		}
		
		toBeSent.stationsData = transportData;
		toBeSent.illData = newIllData;
		toBeSent.illScalar = newIllScalar;
		toBeSent.ratioData = newRatioData;
		toBeSent.ratioScalar = newRatioScalar;		
		
	}
	
	
	//The runnable.
	public void run()
	{
		
		init();
		
		
		while((!MainManager.isShutdown()) && (!MainManager.isAnalysisShutdown()))
		{
			
			try
			{
					
				if(MainManager.getTwitterManager().isUpdated())
				{
					
					
					
					MainManager.logMessage("#AnalysisManager: Starting prediction");
						
					update();
						
					MainManager.logMessage("#AnalysisManager: Prediction ended");
					
					for(int i = 0; i < 1600; i++)
						System.out.println(toBeSent.illData[4][i]);
					
					MainManager.getAppNetworkManager().updateModel(toBeSent);
					
				}
					
			} catch (Exception e)
			{
						
				MainManager.logMessage("#AnalysisManager: Prediction failed");
				e.printStackTrace();
			}			
			
			if((!MainManager.isShutdown()) && (!MainManager.isAnalysisShutdown()))
			{
				synchronized(this){
				
					try
					{ 
						
						//wait(MainManager.UPDATE_TIME);
						wait(1000);
						
					}
					catch (InterruptedException e) { }
					
				}
				
			}
					
		}
		
		MainManager.logMessage("#AnalysisManager: Shutting down ...");
		
	}
	
	//Code I made to allocate the boroughs, not used in the actual model but saved incase we lose the made document.
	/*
	public void filSqu(int star, int hig, int len, int val, int[] bor)
	{
		
		int num = star;
		
		for (int i = 0; i < hig; i++)
		{
		
			for (int j = 0; j < len; j++)
			{
				
				bor[num] = val;
				num ++;
				
			}
		
			num += 40 - len;
		
		}
		
	}
	
	public void setBoroughs()
	{
		
		int[] bor = new int[1600];
		
		//Manual Method.
		
		//Boroughs.
		
		//City of London.
		
		FilSqu(864, 2, 2, 1, bor);
		
		//Barking and Dagenham.
		
		FilSqu(869, 6, 5, 2, bor);
		
		FilSqu(1112, 6, 2, 2, bor);
				
		//Barnet.
		
		FilSqu(1172, 11, 5, 3, bor);
		
		//Bexley.
		
		FilSqu(631, 6, 9, 4, bor);
		
		//Brent.
		
		FilSqu((24*40 + 10), 2, 5, 5, bor);
		
		FilSqu((26*40 + 12), 3, 3, 5, bor);
		
		//Bromley.
		
		FilSqu(24, 15, 16, 6, bor);
		
		//Camden.
		
		FilSqu((24*40 + 15), 5, 9, 7, bor);
		
		FilSqu((22 + 20*40), 4, 2, 7, bor);
		
		//Crydon.
		
		FilSqu(18, 15, 6, 8, bor);  
		
		//Ealing.
		
		FilSqu((19*40), 1, 5, 9, bor);
		
		FilSqu((19*40 + 5), 7, 5, 9, bor);
		
		FilSqu((19*40+ 10), 5, 1, 9, bor);
		
		//Enfield.
		
		FilSqu((31*40 + 17), 9, 6, 10, bor);
		
		//Greenwich.
		
		FilSqu((18*40 + 27), 3, 4, 11, bor);
		
		FilSqu((15*40 + 29), 3, 2, 11, bor);
		
		//Hackney.
		
		FilSqu((23*40 + 25), 3, 1, 12, bor);
		
		FilSqu((26*40 + 25), 3, 2, 12, bor);
		
		//Hammersmith and Fulham.
		
		FilSqu((16*40 + 11), 8, 3, 13, bor);
		
		//Haringey.
		
		FilSqu((29*40 + 17), 2, 6, 14, bor);
		
		//Harrow.
		
		FilSqu((26*40 + 5), 14, 7, 15, bor);
		
		//Havering.
		
		FilSqu((21*40 + 34), 19, 6, 16, bor);
		
		//Hillingdon.
		
		FilSqu((20*40), 20, 5, 17, bor);
		
		//Hounslow.
		
		FilSqu((13*40), 6, 11, 18, bor);
		
		//Islington.
		
		FilSqu((23*40 + 24), 6, 1, 19, bor);
		
		//Kensington and Chelsea.
		
		FilSqu((16*40 + 14), 5, 4, 20, bor);
		
		FilSqu((21*40 + 14), 3, 2, 20, bor);
		
		//Kingston upon Thames.
		
		FilSqu(0, 10, 11, 21, bor);
		
		//Lambeth.
		
		FilSqu((15*40 + 18), 5, 6, 22, bor);
		
		//Lewisham.
		
		FilSqu((15*40 + 25), 3, 4, 23, bor);
		
		FilSqu((18*40 + 25), 3, 2, 23, bor);
		
		//Merton.
		
		FilSqu((8*40 + 11), 6, 7, 24, bor);
		
		//Newham.
		
		FilSqu((21*40 + 27), 6, 2, 25, bor);
		
		//Redbridge.
		
		FilSqu((27*40 + 27), 6, 5, 26, bor);
		
		FilSqu((33*40 + 27), 7, 7, 26, bor);
		
		//Richmond upon Thames.
		
		FilSqu((10*40), 3, 11, 27, bor);
		
		//Southwark.
		
		FilSqu((15*40 + 24), 6, 1, 28, bor);
		
		//Sutton
		
		FilSqu(11, 8, 7, 29, bor);
		
		//Tower Hamlets.
		
		FilSqu((21*40 + 26), 5, 1, 30, bor);
		
		//Waltham Forest.
		
		FilSqu((29*40 + 23), 11, 4, 31, bor);
		
		//Wandsworth.
		
		FilSqu((14*40 + 11), 2, 7, 32, bor);
		
		//Westminster.
		
		FilSqu((20*40 + 18), 4, 4, 33, bor);
		
		FilSqu((21*40 + 16), 3, 2, 33, bor);
		
		
		
		//Twitter method.
		
		String[] boroughNames = MainManager.getDataManager().getBoroughNames();
		
		for(int i = 0; i < 33; i++)	{ boroughNames[i] = boroughNames[i].toUpperCase();	}
		
		for(int i = 0; i < 40; i++)
		{
			
			for(int j = 0; j < 40; j++)
				
			{
				
				String[] boroughs = MainManager.getTwitterManager().getLocation(51.305 + i*0.01, -0.49 + j*0.02);
				
				try
				{
					synchronized(this)
					{
						this.wait(10000);
					}
				}
				catch(Exception e)	{}
				
				for(int k = 0; k < 33; k++)
				{
				
					if(boroughs[0].contains(boroughNames[k]))
					{
					
						bor[i + j*40] = k;
						break;
					
					}
					else
					{
						
						bor[i + j*40] = 0;
						
					}
				
				}
				
			}
			
		}
		
		MainManager.logMessage("#AnalysisManager: Attempting to allocate Boroughs.");
		String content = new String();
		
		for(int i = 0; i < 1600; i++)
		{
			
			if(bor[i] == 0)
			{
				MainManager.logMessage("#AnalysisManager: Cell " + i + " is unallocated." );
			}
	
			content += bor[i] + " ";
			
		}
			
		try {
				 
	 
			File file = new File("./res/BoroughPlace.txt");
				
			if (!file.exists()) {
				file.createNewFile();
			}
	 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
	 
			MainManager.logMessage("#AnalysisManager: Successfully allocated Boroughs.");
	 
		} catch (Exception e) {
			MainManager.logMessage("#AnalysisManager: Failed to write ./res/DataManager/BoroughPlace.txt.");
		}
		
	}
	*/
	
	//Depricated code
	/*
	
	//Function is called to collate the tempory store and save the average data.
	public void takeAverageAndSave()
	{
		
		float[] average = new float[1600];
		
		for(int i = 0; i < 1600; i++)	{ average[i] = 0;	}
		
		try 
		{

			BufferedReader br = new BufferedReader(new FileReader(tempStore.getAbsoluteFile()));
			String Temp1 = br.readLine();
			br.close();
			String[] Temp2 = Temp1.split(" ");
			int num = (int) Temp2.length/1600;
			
			for(int i = 0; i < num; i++)
			{
				
				for(int j = 0; j < 1600; j++)
				{
					
					average[j] += Float.valueOf(Temp2[j + i*1600])/num;
					
				}
				
			}
			
			tempStore.delete();
			tempStore.createNewFile();
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#AnalysisManager: Could not read ./res/Analysis/tempStore.txt, recommend shut down.");
		
		}
		
		String content = new String();
		
		for (int i = 0; i < 1600; i++)
		{
			
			content += average[i]; 
			
			if(i != 1599)
				content += " ";
			else
				content += " - ";
			
		}
		
		
		try{
		
			BufferedWriter currentBW = new BufferedWriter(new FileWriter(currentStore.getAbsoluteFile(), true));
			currentBW.write(content);
			currentBW.close();
			
			
		} catch (Exception e)
		{
			
			MainManager.logMessage("#AnalysisManager: Failed to save current week average of model.");
			
		}
		
	}
	
		//Function is called to save current model to a tempory store.
	public void recordData(float[] data)
	{
		
		String content = new String();
		
		for (int i = 0; i < 1600; i++)
		{
			
			content += data[i] + " ";
			
		}
		
		
		
		try{
		
			BufferedWriter tempBW = new BufferedWriter(new FileWriter(tempStore.getAbsoluteFile(), true));
			tempBW.write(content);
			tempBW.close();
			
			
		} catch (Exception e)
		{
			
			MainManager.logMessage("#AnalysisManager: Failed to save current itteration of model.");
			
		}
		
	}
	
	
	//old update function
	 
		//If the model is required to generate a new packet for the app, this will run.
	public void update()
	{
		
		ill = new float[1600];
		
		for(int i = 0; i < 1600; i++) { ill[i] = 0;	}
		
		float[][] dataToBeSent = new float[24*8+7][1600];
		
	    Calendar currentDate = Calendar.getInstance();
	    currentDate.setTime(new Date());
		
		setTweets();
		addNHS();
		
		addTransport(currentDate);
		
		dataToBeSent[0] = ill;
		
		float[][] ratioState = new float[24*8+7][1600];
		
		for(int i = 0; i < 1600; i++)
		{
			
			ratioState[0][i] = (ill[i]/pop[i])*100;
			
		}
		
		recordData(currentDate);
		
		resetPop();
		
		float[][] PredictedState = new float[24*8+7][1600];
		
		for(int i = 0; i < 24*8 + 7; i++)
		{
			
			for(int j = 0; j < 1600; j++)
			{
				
				PredictedState[i][j] = 0;
				
			}
			
		}
		
		for(int i = 0; i < 1600; i++)
		{
			
			currentDate = Calendar.getInstance();
		    currentDate.setTime(new Date());
			
			float[] predictedData = prediction(getPreviousData(i), currentDate, borough[i]);
			
			for(int j = 0; j < 24*4; j++)
			{
				
				PredictedState[j][i] = ill[i]*((7*24*4-j)/7*24*4) + predictedData[0]*((j)/7*24*4);
				
			}
			
			for(int j = 0; j < 24*2; j++)
			{
				
				PredictedState[24*4 + j][i] = ill[i]*((7*24*2-(24*2 + j))/7*24*2) + predictedData[0]*((24*2 + j)/7*24*2);
				
			}
			
			for(int j = 0; j < 24; j++)
			{
				
				PredictedState[24*6 + j][i] = ill[i]*((7*24-(24*2 + j))/7*24) + predictedData[0]*((24*2 + j)/7*24);
				
			}
			
			for(int j = 0; j < 12; j++)
			{
				
				PredictedState[24*7 + j][i] = ill[i]*((7*24-(24*3 + 2*j))/7*24) + predictedData[0]*((24*3 + 2*j)/7*24);
				
			}
			
			for(int j = 0; j < 6; j++)
			{
				
				PredictedState[24*7 + 12 + j][i] = ill[i]*((7*24-(24*4 + 4*j))/7*24) + predictedData[0]*((24*4 + 4*j)/7*24);
				
			}
			
			for(int j = 0; j < 4; j++)
			{
				
				PredictedState[24*7 + 18 + j][i] = ill[i]*((7*24-(24*5 + 6*j))/7*24) + predictedData[0]*((24*5 + 6*j)/7*24);
				
			}
			
			for(int j = 0; j < 2; j++)
			{
				
				PredictedState[24*7 + 22 + j][i] = ill[i]*((7*24-(24*6 + 12*j))/7*24) + predictedData[0]*((24*6 + 12*j)/7*24);
				
			}
			
			for(int j = 0; j < 7; j++)
			{
				
				PredictedState[24*8 + j][i] = predictedData[0]*((7-j)/7) + predictedData[1]*(j/7);
				
			}
			
		}
		
		currentDate = Calendar.getInstance();
	    currentDate.setTime(new Date());
	    short[][] transportMove = new short[24*8+7][269*2];
	    transportMove[0] = getTransportData(currentDate);
	    
		for(int i = 1; i < 8*24 + 7; i++)
		{
			
			if(i < 24*4)
				currentDate.add(Calendar.MINUTE, 15);
			else if (i < 24*6)
				currentDate.add(Calendar.MINUTE, 30);
			else if (i < 24*7)
				currentDate.add(Calendar.HOUR, 1);
			else if (i < 24*7 + 12)
				currentDate.add(Calendar.HOUR, 2);
			else if (i < 27*7 + 18)
				currentDate.add(Calendar.HOUR, 4);
			else if (i < 27*7 + 22)
				currentDate.add(Calendar.HOUR, 6);
			else if (i < 24*8)
				currentDate.add(Calendar.HOUR, 12);
			else
				currentDate.add(Calendar.DAY_OF_YEAR, 1);
			
			ill = PredictedState[i];
			addTransport(currentDate);
			dataToBeSent[i] = ill;
			
			for(int j = 0; j < 1600; j++)
			{
				
				ratioState[i][j] = (ill[j]/pop[j])*100;
				
			}
			
			resetPop();
			transportMove[i] = getTransportData(currentDate);
			
		}
		
		float[] newIllScalar = new float[24*8+7];
		byte[][] newIllData = new byte[24*8+7][1600];
		float[] newRatioScalar = new float[24*8+7];
		byte[][] newRatioData = new byte[24*8+7][1600];
		
		for(int i = 0; i < 24*8 + 7; i++)
		{
			
			float max1 = 0;
			float max2 = 0;
			
			for(int j = 0; j < 1600; j++)
			{
				
				if(dataToBeSent[i][j] > max1)
					max1 = dataToBeSent[i][j];
				
				if(ratioState[i][j] > max2)
					max2 = ratioState[i][j];
				
			}
			
			newIllScalar[i] = max1;
			newRatioScalar[i] = max2;
			
			for(int j = 0; j < 1600; j++)
			{
				
				if(max1 == 0)
					newIllData[i][j] = -128;
				else
					newIllData[i][j] = (byte) ((dataToBeSent[i][j]*255/max1) - 128);
				
				if(max2 == 0)
					newRatioData[i][j] = -128;
				else
					newRatioData[i][j] = (byte) ((dataToBeSent[i][j]*255/max2) - 128);
				
			}	
			
		}
		
		for(int i = 0; i < 24*8+7; i++)
		{
			
			System.out.println((newIllScalar[i]));
			
			synchronized(this){
				
				try{ wait(5000);	} 
				catch (Exception e) 
				{ 
					
					e.printStackTrace();
					
					MainManager.logMessage("#AnalysisManager: System couldn't wait");
					
				}
			}
		
		}
		
		toBeSent.stationsData = transportMove;
		toBeSent.illData = newIllData;
		toBeSent.illScalar = newIllScalar;
		toBeSent.ratioData = newRatioData;
		toBeSent.ratioScalar = newRatioScalar;		
		
	}
	
	//Function is called to add the travel data to the model.
	public void addTransport(Calendar date)
	{
		int Day = date.get(Calendar.DAY_OF_WEEK);
		int MinRep = (int)(date.get(Calendar.MINUTE)/15);
		int HourRep = date.get(Calendar.HOUR_OF_DAY) - 2;
		float average = getAverageIll();
		
		if(((Day == 0) && (HourRep >= 0)) || ((Day == 1) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.SUN);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.SUN);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
		}else if(((Day == 7) && (HourRep >= 0)) || ((Day == 0) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.SAT);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.SAT);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
		} else
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.WEEK);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
				
				}
					
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.WEEK);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				if((currentStation != null) && (currentStation.coordinates != null) && (currentStation.people != null) && (cordConv(currentStation.coordinates[1], currentStation.coordinates[0]) > -1))
				{
				
					for(int j = 0; j < 4*((HourRep+24)%24) + MinRep; j++)
					{
						
						movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
						
					}
					
				}
				
			}
			
		}
		
	}

	
	//Function is called to get the information about the stations that will be sent over the network.
	public short[] getTransportData(Calendar date)
	{
		
		short[] currentTransport = new short[538];
		
		int Day = date.get(Calendar.DAY_OF_WEEK);
		int MinRep = (int)(date.get(Calendar.MINUTE)/15);
		int HourRep = date.get(Calendar.HOUR) - 2;
		
		if(((Day == 0) && (HourRep >= 0)) || ((Day == 1) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.SUN);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				if ((currentStation != null) && (currentStation.people != null) && (currentStation.people.length > (24*4 - 1)))
					currentTransport[i*2+1] = (short) currentStation.people[4*((HourRep+24)%24) + MinRep];
				else
					currentTransport[i*2+1] = 0;
				
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.SUN);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				if ((currentStation != null) && (currentStation.people != null) && (currentStation.people.length > (24*4 - 1)))
					currentTransport[i*2] = (short) currentStation.people[4*((HourRep+24)%24) + MinRep];
					currentTransport[i*2] = 0;
				
			}
			
		}else if(((Day == 7) && (HourRep >= 0)) || ((Day == 0) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.SAT);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				if ((currentStation != null) && (currentStation.people != null) && (currentStation.people.length > (24*4 - 1)))
					currentTransport[i*2+1] = (short) currentStation.people[4*((HourRep+24)%24) + MinRep];
				else
					currentTransport[i*2+1] = 0;
				
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.SAT);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				if ((currentStation != null) && (currentStation.people != null) && (currentStation.people.length > (24*4 - 1)))
					currentTransport[i*2] = (short) currentStation.people[4*((HourRep+24)%24) + MinRep];
				else
					currentTransport[i*2] = 0;
				
			}
			
		} else
		{
			
			MainManager.getDataManager().loadStationTravel(DataManager.EXIT, DataManager.WEEK);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				if ((currentStation != null) && (currentStation.people != null) && (currentStation.people.length > (24*4 - 1)))
					currentTransport[i*2+1] = (short) currentStation.people[4*((HourRep + 24)%24) + MinRep];
				else
					currentTransport[i*2+1] = 0;
				
			}
			
			MainManager.getDataManager().loadStationTravel(DataManager.ENTER, DataManager.WEEK);
			
			for (int i = 0; i < 269; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				if ((currentStation != null) && (currentStation.people != null) && (currentStation.people.length > (24*4 - 1)))
					currentTransport[i*2] = (short) currentStation.people[4*((HourRep+24)%24) + MinRep];
				else
					currentTransport[i*2] = 0;
				
			}
			
		}
		
		return currentTransport;
		
	}
	
	
	 */
	
}
