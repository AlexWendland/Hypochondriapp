package com.group19.hypochondriapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AnalysisManager implements Runnable {

	@SuppressWarnings("deprecation")
	private int[] Pop;
	private int[] SetPop;
	private double[] Ill;
	private int[] Borough;
	private SentData ToBeSent = new SentData();
	
	public static final int TWITSCALAR = 30;
	
	public void init() 
	{
		
		int[] PopDen = MainManager.getDataManager().getBoroughDensities();
		Borough = MainManager.getDataManager().getBoroughPlaces();
		SentData ToBeSent = new SentData();	
		for(int i = 0; i < 1600; i++)	{ SetPop[i] = PopDen[Borough[i]];	}
		Pop = SetPop;
		
	}
	
	public void resetPop()	{ Pop = SetPop;	}
	
	//Converts from Long and latitude to cell position.
	
	public int cordConv(double y, double x) 
	{
		
		x = (x + 0.5)/0.02;
		y = (y - 51.3)/0.01;
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
	
	public double getAverageIll()
	{
		
		double average = 0;
		double Fails = 0;
		
		for(int i = 0; i < 1600; i++)	
		{ 
			try{ average += Ill[i]/Pop[i];	}
			catch (Exception e) 	{ Fails++;	}
		}
		try { return average/(1600 - Fails);	}
		catch (Exception e)	{ return 0;	}
		
	}
	
	
	public void filSqu(int Star, int Hig, int Len, int Val, int[] Bor)
	{
		
		int Num = Star;
		
		for (int i = 0; i < Hig; i++)
		{
		
			for (int j = 0; j < Len; j++)
			{
				
				Bor[Num] = Val;
				Num ++;
				
			}
		
			Num += 40 - Len;
		
		}
		
	}
	
	public SentData getData()	{ return ToBeSent;	}
	
	public void setBoroughs()
	{
		
		int[] Bor = new int[1600];
		
		//Manual Method.
		
		/*
		
		//Boroughs.
		
		//City of London.
		
		FilSqu(864, 2, 2, 1, Bor);
		
		//Barking and Dagenham.
		
		FilSqu(869, 6, 5, 2, Bor);
		
		FilSqu(1112, 6, 2, 2, Bor);
				
		//Barnet.
		
		FilSqu(1172, 11, 5, 3, Bor);
		
		//Bexley.
		
		FilSqu(631, 6, 9, 4, Bor);
		
		//Brent.
		
		FilSqu((24*40 + 10), 2, 5, 5, Bor);
		
		FilSqu((26*40 + 12), 3, 3, 5, Bor);
		
		//Bromley.
		
		FilSqu(24, 15, 16, 6, Bor);
		
		//Camden.
		
		FilSqu((24*40 + 15), 5, 9, 7, Bor);
		
		FilSqu((22 + 20*40), 4, 2, 7, Bor);
		
		//Crydon.
		
		FilSqu(18, 15, 6, 8, Bor);  
		
		//Ealing.
		
		FilSqu((19*40), 1, 5, 9, Bor);
		
		FilSqu((19*40 + 5), 7, 5, 9, Bor);
		
		FilSqu((19*40+ 10), 5, 1, 9, Bor);
		
		//Enfield.
		
		FilSqu((31*40 + 17), 9, 6, 10, Bor);
		
		//Greenwich.
		
		FilSqu((18*40 + 27), 3, 4, 11, Bor);
		
		FilSqu((15*40 + 29), 3, 2, 11, Bor);
		
		//Hackney.
		
		FilSqu((23*40 + 25), 3, 1, 12, Bor);
		
		FilSqu((26*40 + 25), 3, 2, 12, Bor);
		
		//Hammersmith and Fulham.
		
		FilSqu((16*40 + 11), 8, 3, 13, Bor);
		
		//Haringey.
		
		FilSqu((29*40 + 17), 2, 6, 14, Bor);
		
		//Harrow.
		
		FilSqu((26*40 + 5), 14, 7, 15, Bor);
		
		//Havering.
		
		FilSqu((21*40 + 34), 19, 6, 16, Bor);
		
		//Hillingdon.
		
		FilSqu((20*40), 20, 5, 17, Bor);
		
		//Hounslow.
		
		FilSqu((13*40), 6, 11, 18, Bor);
		
		//Islington.
		
		FilSqu((23*40 + 24), 6, 1, 19, Bor);
		
		//Kensington and Chelsea.
		
		FilSqu((16*40 + 14), 5, 4, 20, Bor);
		
		FilSqu((21*40 + 14), 3, 2, 20, Bor);
		
		//Kingston upon Thames.
		
		FilSqu(0, 10, 11, 21, Bor);
		
		//Lambeth.
		
		FilSqu((15*40 + 18), 5, 6, 22, Bor);
		
		//Lewisham.
		
		FilSqu((15*40 + 25), 3, 4, 23, Bor);
		
		FilSqu((18*40 + 25), 3, 2, 23, Bor);
		
		//Merton.
		
		FilSqu((8*40 + 11), 6, 7, 24, Bor);
		
		//Newham.
		
		FilSqu((21*40 + 27), 6, 2, 25, Bor);
		
		//Redbridge.
		
		FilSqu((27*40 + 27), 6, 5, 26, Bor);
		
		FilSqu((33*40 + 27), 7, 7, 26, Bor);
		
		//Richmond upon Thames.
		
		FilSqu((10*40), 3, 11, 27, Bor);
		
		//Southwark.
		
		FilSqu((15*40 + 24), 6, 1, 28, Bor);
		
		//Sutton
		
		FilSqu(11, 8, 7, 29, Bor);
		
		//Tower Hamlets.
		
		FilSqu((21*40 + 26), 5, 1, 30, Bor);
		
		//Waltham Forest.
		
		FilSqu((29*40 + 23), 11, 4, 31, Bor);
		
		//Wandsworth.
		
		FilSqu((14*40 + 11), 2, 7, 32, Bor);
		
		//Westminster.
		
		FilSqu((20*40 + 18), 4, 4, 33, Bor);
		
		FilSqu((21*40 + 16), 3, 2, 33, Bor);
		
		*/
		
		//Twitter method.
		
		String[] BoroughNames = MainManager.getDataManager().getBoroughNames();
		
		for(int i = 0; i < 33; i++)	{ BoroughNames[i] = BoroughNames[i].toUpperCase();	}
		
		for(int i = 0; i < 40; i++)
		{
			
			for(int j = 0; j < 40; j++)
				
			{
				
				String[] Boroughs = MainManager.getTwitterManager().getLocation(51.305 + i*0.01, -0.49 + j*0.02);
				
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
				
					if(Boroughs[0].contains(BoroughNames[k]))
					{
					
						Bor[i + j*40] = k;
						break;
					
					}
					else
					{
						
						Bor[i + j*40] = 0;
						
					}
				
				}
				
			}
			
		}
		
		MainManager.logMessage("#AnalysisManager: Attempting to allocate Boroughs.");
		String content = new String();
		
		for(int i = 0; i < 1600; i++)
		{
			
			if(Bor[i] == 0)
			{
				MainManager.logMessage("#AnalysisManager: Cell " + i + " is unallocated." );
			}
	
			content += Bor[i] + " ";
			
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
	
	public int[] findBoroughCells(int borough)
	{
	
		int[] temp = new int[1600];
		int Num = 0;
		
		for(int i = 0; i < 1600; i++)
		{
			
			if(Borough[i] == borough)
			{
				
				temp[Num] = i;
				Num++;
				
			}
			
		}
		
		int[] Return = new int[Num+1];
		for (int i = 0; i <= Num; i++)	{ Return[i] = temp[i];	}
		return Return;
		
	}
	
	//Function will be called, to add a tweet.
	
	public double[] getTweetArray()
	{
		
		ArrayList<String> Tweets = MainManager.getTwitterManager().getTweets();
		String[] PlaceNames = MainManager.getDataManager().getBoroughNames();
		double[] Return = new double[1600];
		
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
						
							Return[Cells[j]] += (TWITSCALAR*Cells.length);
						
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
						
						Return[20+i*40+j] += (TWITSCALAR/800);
						
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
						
						Return[i*40+j] += (TWITSCALAR/800);
						
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
						
						Return[i*40+j] += (TWITSCALAR/800);
						
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
						
						Return[800 + i*40+j] += (TWITSCALAR/800);
						
					}
					
				}
				
				continue;
				
			}
			
			for(int i = 0; i < 1600; i++)
			{
				
				Return[i] += (TWITSCALAR/1600);
				
			}
			
		}
		
		return Return;
		
	}
	
	//Function will be called with train travel data.
	
	public void movePeople(double Y, double X, double Num)
	{
	
		int Pos = cordConv(Y, X);
		double IllMove = Num*getAverageIll()/9;
		
		if (Pos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = cordConv(Y + (i*0.01), X + 0.02);
				
				if(a > -1)
				{
					
					Pop[a] += ((int)Num/9);
					if(Pop[a] < 0)
						Pop[a] = 0;
					Ill[a] += (IllMove);
					if(Ill[a] < 0)
						Ill[a] = 0;
					
				}
				
				int b = cordConv(Y + (i*0.01), X);
				
				if(b > -1)
				{
					
					Pop[b] += ((int)Num/9);
					if(Pop[b] < 0)
						Pop[b] = 0;
					Ill[b] += (IllMove);
					if(Ill[b] < 0)
						Ill[b] = 0;
					
				}
				
				int c = cordConv(Y + (i*0.01), X - 0.02);
				
				if(c > -1)
				{
					
					Pop[c] += ((int)Num/9);
					if(Pop[c] < 0)
						Pop[c] = 0;
					Ill[c] += (IllMove);
					if(Ill[c] < 0)
						Ill[c] = 0;
					
				}
				
			}
			
		} else
		{
			
			MainManager.logMessage("#AnalysisManager: Train station not in map.");

		}
		
	}

	public void addTransport(Calendar date)
	{
		int Day = date.get(Calendar.DAY_OF_WEEK);
		int MinRep = (int)(date.get(Calendar.MINUTE)/15);
		int HourRep = date.get(Calendar.HOUR) - 2;
		int[] TrainStations = MainManager.getDataManager().getTrainStations();
		
		
		if(((Day == 0) && (HourRep >= 0)) || ((Day == 1) && (HourRep < 0)))
		{
			
			int[][] TravelDataOut = MainManager.getDataManager().getSunTravelOutData();
			int[][] TravelDataIn = MainManager.getDataManager().getSunTravelInData();
			double Average = getAverageIll();
			
			for (int i = 0; i < 268; i++) 
			{
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(TrainStations[i*2], TrainStations[i*2+1], TravelDataIn[i][j]);
					movePeople(TrainStations[i*2], TrainStations[i*2+1], -TravelDataOut[i][j]);
					
				}
				
			}
			
		}else if(((Day == 7) && (HourRep >= 0)) || ((Day == 0) && (HourRep < 0)))
		{
			
			int[][] TravelDataOut = MainManager.getDataManager().getSatTravelOutData();
			int[][] TravelDataIn = MainManager.getDataManager().getSatTravelInData();
			double Average = getAverageIll();
			
			for (int i = 0; i < 268; i++) 
			{
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(TrainStations[i*2], TrainStations[i*2+1], TravelDataIn[i][j]);
					movePeople(TrainStations[i*2], TrainStations[i*2+1], -TravelDataOut[i][j]);
					
				}
				
			}
			
		} else
		{
			
			int[][] TravelDataOut = MainManager.getDataManager().getWeekTravelOutData();
			int[][] TravelDataIn = MainManager.getDataManager().getWeekTravelInData();
			double Average = getAverageIll();
			
			for (int i = 0; i < 268; i++) 
			{
				
				for(int j = 0; j < 4*(HourRep%24) + MinRep; j++)
				{
					
					movePeople(TrainStations[i*2], TrainStations[i*2+1], TravelDataIn[i][j]);
					movePeople(TrainStations[i*2], TrainStations[i*2+1], -TravelDataOut[i][j]);
					
				}
				
			}
			
		}
		
	}

	public void update()
	{
		
		double[][] DataToBeSent = new double[14*24*4][1600];
		
	    Calendar CurrentDate = Calendar.getInstance();
	    CurrentDate.setTime(new Date());
		
		Ill = getTweetArray();
		addTransport(CurrentDate);
		
		DataToBeSent[0] = Ill;
		
		resetPop();
		
		double[][] PredictedState = Prediction.getPredictedState();
		
		for(int i = 1; i < 14*24*4; i++)
		{
			
			CurrentDate.add(Calendar.MINUTE, 15);
			Ill = PredictedState[i];
			addTransport(CurrentDate);
			DataToBeSent[i] = Ill;
			resetPop();
			
		}
		
		ToBeSent.Data = DataToBeSent;
		
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
