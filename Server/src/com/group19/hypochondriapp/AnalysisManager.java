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

	private int[] pop;
	private int[] setPop;
	private float[] ill;
	private byte[] borough;
	private SentData toBeSent = new SentData();
	private boolean updated;
	File tempStore = new File("./res/tempStore.txt");
	File currentStore = new File("./res/currnetStore.txt");
	
	/*
	
	FileWriter tempFW = new FileWriter(tempStore.getAbsoluteFile());
	BufferedWriter tempBW = new BufferedWriter(tempFW);
	FileWriter currentFW = new FileWriter(currentStore.getAbsoluteFile());
	BufferedWriter currentBW = new BufferedWriter(currentFW);
	
	*/
			
	public static final int TWITSCALAR = 30;
	public static final int DAY_OF_UPDATE = 0;
	
	public void init() 
	{
		
		int[] popDen = MainManager.getDataManager().getBoroughDensities();
		borough = MainManager.getDataManager().getBoroughPlaces();	
		for(int i = 0; i < 1600; i++)	{ setPop[i] = popDen[borough[i]];	}
		pop = setPop;
		updated = true;
		
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
	
	public SentData getData()	{ return toBeSent;	}
	
	public void setBoroughs()
	{
		
		int[] bor = new int[1600];
		
		//Manual Method.
		
		/*
		
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
		
		*/
		
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
			MainManager.logMessage("#AnalysisManager: Failed to write ./res/BoroughPlace.txt.");
		}
		
	}
	
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
	
	//Function will be called, to add a tweet.
	
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
				
				for(int i = 0; i < 33; i++)
				{
				
					if(Place.contains(PlaceNames[i]))
					{
						
						int[] Cells = findBoroughCells(i+1);
						
						for(int j = 0; j < Cells.length; j++)	
						{
						
							ill[Cells[j]] += (TWITSCALAR*Cells.length);
						
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
						
						ill[800 + i*40+j] += (TWITSCALAR/800);
						
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
	
	//Function will be called with train travel data.
	
	public void movePeople(float y, float x, float num, float averageIll)
	{
	
		int pos = cordConv(y, x);
		float illMove = num*averageIll/9;
		
		if (pos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = cordConv((float) (y + (i*0.01)), (float) (x + 0.02));
				
				if(a > -1)
				{
					
					pop[a] += ((int)num/9);
					if(pop[a] < 0)
						pop[a] = 0;
					ill[a] += (illMove);
					if(ill[a] < 0)
						ill[a] = 0;
					
				}
				
				int b = cordConv((float) (y + (i*0.01)), x);
				
				if(b > -1)
				{
					
					pop[b] += ((int)num/9);
					if(pop[b] < 0)
						pop[b] = 0;
					ill[b] += (illMove);
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
	
	public void takeAverageAndSave()
	{
		
		float[] average = new float[1600];
		
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
			
			MainManager.logMessage("#AnalysisManager: Could not read ./res/TempData.txt, recommend shut down.");
		
		}
		
		String content = new String();
		
		for (int i = 0; i < 1600; i++)
		{
			
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
	
	public float[] getPreviousData(int cell)
	{
		
		float[] pastData = new float[5];
		
		try 
		{

			BufferedReader br = new BufferedReader(new FileReader(currentStore.getAbsoluteFile()));
			String Temp1 = br.readLine();
			br.close();
			String[] Temp2 = Temp1.split(" - ");
			int length = Temp2.length;

			for(int i = 0; i < 5; i++)
			{
				
				String[] Temp3 = Temp2[length - i].split(" ");
				pastData[i] = Float.valueOf(Temp3[cell]);
				
			}
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#AnalysisManager: Could not read ./res/CurrentData.txt, recommend shut down.");
		
		}
		
		return pastData;
		
	}
	
	public void addTransport(Calendar date)
	{
		int Day = date.get(Calendar.DAY_OF_WEEK);
		int MinRep = (int)(date.get(Calendar.MINUTE)/15);
		int HourRep = date.get(Calendar.HOUR) - 2;
		float average = getAverageIll();
		
		if(((Day == 0) && (HourRep >= 0)) || ((Day == 1) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel("Out", "Sun");
			
			for (int i = 0; i < 268; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
					
				}
				
			}
			
			MainManager.getDataManager().loadStationTravel("In", "Sun");
			
			for (int i = 0; i < 268; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
					
				}
				
			}
			
		}else if(((Day == 7) && (HourRep >= 0)) || ((Day == 0) && (HourRep < 0)))
		{
			
			MainManager.getDataManager().loadStationTravel("Out", "Sat");
			
			for (int i = 0; i < 268; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
					
				}
				
			}
			
			MainManager.getDataManager().loadStationTravel("In", "Sat");
			
			for (int i = 0; i < 268; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
					
				}
				
			}
			
		} else
		{
			
			MainManager.getDataManager().loadStationTravel("Out", "Week");
			
			for (int i = 0; i < 268; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
					
				}
				
			}
			
			MainManager.getDataManager().loadStationTravel("In", "Week");
			
			for (int i = 0; i < 268; i++) 
			{
				
				StationInfo currentStation = MainManager.getDataManager().getNextStation();
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(currentStation.coordinates[0], currentStation.coordinates[1], currentStation.people[j], average);
					
				}
				
			}
			
		}
		
	}

	public void update()
	{
		
		float[][] DataToBeSent = new float[14*24*4][1600];
		
	    Calendar CurrentDate = Calendar.getInstance();
	    CurrentDate.setTime(new Date());
		
		setTweets();
		addTransport(CurrentDate);
		
		DataToBeSent[0] = ill;
		
		if((!updated) && (CurrentDate.get(Calendar.DAY_OF_WEEK) == DAY_OF_UPDATE))
		{
			
			takeAverageAndSave();
			updated = true;
			
		}
		
		if( (CurrentDate.get(Calendar.DAY_OF_WEEK) != DAY_OF_UPDATE) && (updated))
		{
			
			updated = false;
			
		}
		
		recordData(ill);
		
		resetPop();
		
		float[][] PredictedState = new float[14*24*4][1600];
		
		for(int i = 0; i < 1600; i++)
		{
			
			float[] predictedData = prediction(getPreviousData(i), CurrentDate, borough[i]);
			
			for(int j = 0; j < 7*24*4; j++)
			{
				
				PredictedState[j][i] = ill[i]*((7*24*4-j)/7*24*4) + predictedData[0]*((j)/7*24*4);
				
			}
			
			for(int j = 0; j < 7*24*4; j++)
			{
				
				PredictedState[7*24*4 + j][i] = predictedData[0]*((7*24*4-j)/7*24*4) + predictedData[1]*((j)/7*24*4);
				
			}
			
		}
		
		for(int i = 1; i < 14*24*4; i++)
		{
			
			CurrentDate.add(Calendar.MINUTE, 15);
			ill = PredictedState[i];
			addTransport(CurrentDate);
			DataToBeSent[i] = ill;
			resetPop();
			
		}
		
		toBeSent.data = DataToBeSent;
		
	}
	
	public void run()
	{
		
		init();
		
		while(!MainManager.isShutdown())
		{
			
			if(MainManager.getTwitterManager().isUpdated())
			{
			
				update();
				
			}
							
		}
		
	}

}
