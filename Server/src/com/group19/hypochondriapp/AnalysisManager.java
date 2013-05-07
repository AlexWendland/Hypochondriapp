package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.group19.hypochondriapp.DataManager.StationInfo;

public class AnalysisManager implements Runnable {

	//Model variables.
	private int[] pop;
	private int[] setPop;
	private float[] ill;
	private byte[] borough;
	
	//The variables to do with model updates and output of AnalysisManager.
	private AppDataPacket toBeSent = new AppDataPacket();
	private boolean updated;
	private boolean newData;
	
	//Files used and constants.
	File tempStore = new File("./res/AnalysisManager/tempStore.txt");
	File currentStore = new File("./res/AnalysisManager/currentStore.txt");
	public static final int TWITSCALAR = 1000;
	public static final int DAY_OF_UPDATE = 0;
	
	//Initiation of the model.
	public void init() 
	{
		
		int[] popDen = MainManager.getDataManager().getBoroughDensities();
		borough = MainManager.getDataManager().getBoroughPlaces();	
		setPop = new int[1600];
		ill = new float[1600];
		updated = true;
		newData = false;
		
		for(int i = 0; i < 1600; i++)	
		{ 
			
			setPop[i] = popDen[borough[i] - 1];	
			ill[i] = 0;
			
		}
		
		pop = setPop;
		
		try
		{
		
			if (!tempStore.exists()) 
				tempStore.createNewFile();
			
			if (!currentStore.exists()) 
				currentStore.createNewFile();
		
		} catch (Exception e)
		{
			
			MainManager.logMessage("#AnalysisManager: No data stores for generated data, recomend shutdown." );
			
		}
			
		
	}
	
	
	public void resetPop()	{ pop = setPop;	}
	
	
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
	
	
	//Function will be called, to sort and allocated expected ill from the twitter data.
	public void setTweets()
	{
		
		ArrayList<String> Tweets = MainManager.getTwitterManager().getTweets();
		String[] PlaceNames = MainManager.getDataManager().getBoroughNames();
		
		for(int k = 0; k < Tweets.size(); k++)
		{
			
			String Place = Tweets.get(k);
			
			boolean ContinueNeeded = false;
			
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
				
				for(int i = 0; i < 40; i++)
				{
					
					for(int j = 0; j < 20; j++)
					{
						
						ill[20+i*40+j] += (TWITSCALAR/800);
						
					}
					
				}
				
				continue;
				
			}
			
			if(Place.contains("WEST"))
			{
				
				for(int i = 0; i < 40; i++)
				{
					
					for(int j = 0; j < 20; j++)
					{
						
						ill[i*40+j] += (TWITSCALAR/800);
						
					}
					
				}
				
				continue;
				
			}
			
			if(Place.contains("SOUTH"))
			{
				
				for(int i = 0; i < 20; i++)
				{
					
					for(int j = 0; j < 40; j++)
					{
						
						ill[i*40+j] += (TWITSCALAR/800);
						
					}
					
				}
				
				continue;
				
			} 
			
			if(Place.contains("NORTH"))
			{
				
				for(int i = 0; i < 20; i++)
				{
					
					for(int j = 0; j < 40; j++)
					{
						
						ill[800 + i*40 + j] += (TWITSCALAR/800);
						
					}
					
				}
				
				continue;
				
			}
			
			for(int i = 0; i < 1600; i++)
			{
				
				ill[i] += (TWITSCALAR/1600);
				
			}
			
		}
		
	}
	
	
	//Function is called to add or remove people from a certain position.
	public void movePeople(float x, float y, float num, float averageIll)
	{
	
		int pos = cordConv(y, x);
		float illMove = num*averageIll/12;
		
		if (pos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = cordConv((float) (y + (i*0.01)), (float) (x + 0.02));
				
				if(a > -1)
				{
					
					pop[a] += ((int)num/12);
					if(pop[a] < 0)
						pop[a] = 0;
					ill[a] += (illMove);
					if(ill[a] < 0)
						ill[a] = 0;
					
				}
				
				int b = cordConv((float) (y + (i*0.01)), x);
				
				if(b > -1)
				{
					if (i != 0)
						pop[b] += ((int)num/12);
					else
						pop[b] += ((int)num/3);
					if(pop[b] < 0)
						pop[b] = 0;
					if(i != 0)
						ill[b] += (illMove);
					else
						ill[b] += (illMove)*4;
					if(ill[b] < 0)
						ill[b] = 0;
					
				}
				
				int c = cordConv((float) (y + (i*0.01)),(float) (x - 0.02));
				
				if(c > -1)
				{
					
					pop[c] += ((int)num/9);
					if(pop[c] < 0)
						pop[c] = 0;
					ill[c] += (illMove);
					if(ill[c] < 0)
						ill[c] = 0;
					
				}
				
			}
			
		} else
		{
			
			MainManager.logMessage("#AnalysisManager: Train station not in map.");

		}
		
	}

	
	//Function is a called to provide a predicted future for a certain cell.
	public float[] prediction(float[] previousAverage, Calendar currentDate, byte borough)
	{
		
		float currentMinError = 999999;
		float[] currentEstimate = new float[2];
		
		for(int i = 0; i < 9; i++)
		{
			
			currentDate.add(Calendar.YEAR, -1);
			
			float error = 0;
			try
			{
				float scalar = previousAverage[0]/MainManager.getDataManager().getGoogleInsights(currentDate);
				
				for(int j = 0; j < 4; j++)
				{
					
					currentDate.add(Calendar.WEEK_OF_YEAR, -1);
					error += previousAverage[j+1] - MainManager.getDataManager().getGoogleInsights(currentDate)*scalar;
					
				}
				
				currentDate.add(Calendar.WEEK_OF_YEAR, 4);
				
				if(error < currentMinError)
				{
					
					currentMinError = error;
					currentDate.add(Calendar.WEEK_OF_YEAR, 1);
					currentEstimate[0] = MainManager.getDataManager().getGoogleInsights(currentDate)*scalar;
					currentDate.add(Calendar.WEEK_OF_YEAR, 1);
					currentEstimate[1] = MainManager.getDataManager().getGoogleInsights(currentDate)*scalar;
					currentDate.add(Calendar.WEEK_OF_YEAR, -2);
					
				}
			} catch (Exception e)
			{
				continue;
			}
			
		}
			
		return currentEstimate;
		
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
	
	
	//Function is called to read the store for the previous infomation about that cell.
	public float[] getPreviousData(int cell)
	{
		
		float[] pastData = new float[5];
		
		try 
		{

			BufferedReader br = new BufferedReader(new FileReader(currentStore.getAbsoluteFile()));
			String Temp1 = br.readLine();
			br.close();
			
			if ((Temp1 != null) && (Temp1.length() != 0))
			{
				String[] Temp2 = Temp1.split(" - ");
				int length = Temp2.length;
				for(int i = 0; i < Math.min(5, length); i++)
				{
					String[] Temp3 = Temp2[length - (1+i)].split(" ");
					
					if(Temp3.length > 1599)
						pastData[i] = Float.valueOf(Temp3[cell]);

				}
				
			}
			
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#AnalysisManager: Could not read ./res/AnalysisManager/currentStore.txt, recommend shut down.");
		
		}
		
		return pastData;
		
	}
	
	
	//Function is called to add the travel data to the model.
	public void addTransport(Calendar date)
	{
		int Day = date.get(Calendar.DAY_OF_WEEK);
		int MinRep = (int)(date.get(Calendar.MINUTE)/15);
		int HourRep = date.get(Calendar.HOUR) - 2;
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

	
	//Function is called to get the infomation about the stations that will be sent over the network.
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
				else
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
	
	
	//Function to be used to retrive data from Analysis Manager.
	public AppDataPacket getAppDataPacket() { return toBeSent;	}
	
	
	//Function to be used to check if the model has changed.
	public boolean isNewData()
	{
	
		if(newData)
		{
			
			newData = false;
			return true;
			
		} else
			return false;
		
	}
	
	
	//If the model is required to generate a new packet for the app, this will run.
	public void update()
	{
		
		ill = new float[1600];
		
		for(int i = 0; i < 1600; i++) { ill[i] = 0;	}
		
		float[][] dataToBeSent = new float[24*8+7][1600];
		
	    Calendar currentDate = Calendar.getInstance();
	    currentDate.setTime(new Date());
		
		setTweets();
		addTransport(currentDate);
		
		dataToBeSent[0] = ill;
		
		if((!updated) && (currentDate.get(Calendar.DAY_OF_WEEK) == DAY_OF_UPDATE))
		{
			
			takeAverageAndSave();
			updated = true;
			
		}
		
		if( (currentDate.get(Calendar.DAY_OF_WEEK) != DAY_OF_UPDATE) && (updated))
		{
			
			updated = false;
			
		}
		
		float[][] ratioState = new float[24*8+7][1600];
		
		for(int i = 0; i < 1600; i++)
		{
			
			ratioState[0][i] = (ill[i]/pop[i])*100;
			
		}
		
		recordData(ill);
		
		resetPop();
		
		float[][] PredictedState = new float[24*8+7][1600];
		
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
				
				if(PredictedState[i][j] > max1)
					max1 = PredictedState[i][j];
				
				if(PredictedState[i][j] > max2)
					max2 = ratioState[i][j];
				
			}
			
			newIllScalar[i] = max1;
			newRatioScalar[i] = max2;
			
			for(int j = 0; j < 1600; j++)
			{
				
				if(max1 == 0)
					newIllData[i][j] = -128;
				else
					newIllData[i][j] = (byte) ((PredictedState[i][j]*255/max1) - 128);
				
				if(max2 == 0)
					newRatioData[i][j] = -128;
				else
					newRatioData[i][j] = (byte) ((PredictedState[i][j]*255/max2) - 128);
				
			}	
			
		}
		
		toBeSent.stationsData = transportMove;
		toBeSent.illData = newIllData;
		toBeSent.illScalar = newIllScalar;
		toBeSent.ratioData = newRatioData;
		toBeSent.ratioScalar = newRatioScalar;		
		
	}
	
	
	//The runnable.
	public void run()
	{
		
		init();
		
		while(!MainManager.isShutdown())
		{
			
			/*
			
			synchronized(this){
			
				try{ wait(MainManager.UPDATE_TIME);	} 
				catch (Exception e) 
				{ 
					
					e.printStackTrace();
					
					MainManager.logMessage("#AnalysisManager: System couldn't wait");
					
				}
			}
			
			*/
			try
			{
				
				if(MainManager.getTwitterManager().isUpdated())
				{
					
					MainManager.logMessage("#AnalysisManager: Starting prediction");
					
					update();
					newData = true;
					
					MainManager.logMessage("#AnalysisManager: Prediction ended");
					
				}
				
			} catch (Exception e)
			{
				
				MainManager.logMessage("#AnalysisManager: Prediction failed because of " + e.getMessage());
				
			}
							
		}
		
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
}
