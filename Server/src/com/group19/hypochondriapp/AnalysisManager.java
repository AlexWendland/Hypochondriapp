package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AnalysisManager implements Runnable {

	private Cell[] Grid;
	
	public static final int TWITSCALAR = 30;
	
	public void init() 
	{
		
		Grid = new Cell[1600];
		int[] PopDen = MainManager.getDataManager().getBoroughDensities();
		int[] BoroughPlaces = MainManager.getDataManager().getBoroughPlaces();
				
		for(int i = 0; i < 1600; i++)
		{
	
			Grid[i].setBor(BoroughPlaces[i]);
			Grid[i].setPop(PopDen[BoroughPlaces[i]]);
					
		}
		
	}
	
	//Converts from Long and latitude to cell position.
	
	public int cordConv(double x, double y) 
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
			try{ average += Grid[i].getIll()/Grid[i].getPop();	}
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
	
	public void setBoroughs()
	{
		
		int[] Bor = new int[1600];
		
		//Manual Method.
		
		/*
		
		//Boroughs.
		
		//City of London.
		
		FilSqu(864, 2, 2, 1, Bor);
		
		//Barking and Dagenham.
		
		FilSqu(869, 6, 5, 2);
		
		FilSqu(1112, 6, 2, 2);
				
		//Barnet.
		
		FilSqu(1172, 11, 5, 3);
		
		//Bexley.
		
		FilSqu(631, 6, 9, 4);
		
		//Brent.
		
		FilSqu((24*40 + 10), 2, 5, 5);
		
		FilSqu((26*40 + 12), 3, 3, 5);
		
		//Bromley.
		
		FilSqu(24, 15, 16, 6);
		
		//Camden.
		
		FilSqu((24*40 + 15), 5, 9, 7);
		
		FilSqu((22 + 20*40), 4, 2, 7);
		
		//Crydon.
		
		FilSqu(18, 15, 6, 8);  
		
		//Ealing.
		
		FilSqu((19*40), 1, 5, 9);
		
		FilSqu((19*40 + 5), 7, 5, 9);
		
		FilSqu((19*40+ 10), 5, 1, 9);
		
		//Enfield.
		
		FilSqu((31*40 + 17), 9, 6, 10);
		
		//Greenwich.
		
		FilSqu((18*40 + 27), 3, 4, 11);
		
		FilSqu((15*40 + 29), 3, 2, 11);
		
		//Hackney.
		
		FilSqu((23*40 + 25), 3, 1, 12);
		
		FilSqu((26*40 + 25), 3, 2, 12);
		
		//Hammersmith and Fulham.
		
		FilSqu((16*40 + 11), 8, 3, 13);
		
		//Haringey.
		
		FilSqu((29*40 + 17), 2, 6, 14);
		
		//Harrow.
		
		FilSqu((26*40 + 5), 14, 7, 15);
		
		//Havering.
		
		FilSqu((21*40 + 34), 19, 6, 16);
		
		//Hillingdon.
		
		FilSqu((20*40), 20, 5, 17);
		
		//Hounslow.
		
		FilSqu((13*40), 6, 11, 18);
		
		//Islington.
		
		FilSqu((23*40 + 24), 6, 1, 19);
		
		//Kensington and Chelsea.
		
		FilSqu((16*40 + 14), 5, 4, 20);
		
		FilSqu((21*40 + 14), 3, 2, 20);
		
		//Kingston upon Thames.
		
		FilSqu(0, 10, 11, 21);
		
		//Lambeth.
		
		FilSqu((15*40 + 18), 5, 6, 22);
		
		//Lewisham.
		
		FilSqu((15*40 + 25), 3, 4, 23);
		
		FilSqu((18*40 + 25), 3, 2, 23);
		
		//Merton.
		
		FilSqu((8*40 + 11), 6, 7, 24);
		
		//Newham.
		
		FilSqu((21*40 + 27), 6, 2, 25);
		
		//Redbridge.
		
		FilSqu((27*40 + 27), 6, 5, 26);
		
		FilSqu((33*40 + 27), 7, 7, 26);
		
		//Richmond upon Thames.
		
		FilSqu((10*40), 3, 11, 27);
		
		//Southwark.
		
		FilSqu((15*40 + 24), 6, 1, 28);
		
		//Sutton
		
		FilSqu(11, 8, 7, 29);
		
		//Tower Hamlets.
		
		FilSqu((21*40 + 26), 5, 1, 30);
		
		//Waltham Forest.
		
		FilSqu((29*40 + 23), 11, 4, 31);
		
		//Wandsworth.
		
		FilSqu((14*40 + 11), 2, 7, 32);
		
		//Westminster.
		
		FilSqu((20*40 + 18), 4, 4, 33);
		
		FilSqu((21*40 + 16), 3, 2, 33);
		
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
	
	public int[] findBoroughCells(int Borough)
	{
	
		int[] temp = new int[1600];
		int Num = 0;
		
		for(int i = 0; i < 1600; i++)
		{
			
			if(Grid[i].getBor() == Borough)
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
	
	public void addTweet(String Place)
	{
		
		String[] PlaceNames = MainManager.getDataManager().getBoroughNames();
		
		try
		{
			
			for(int i = 0; i < 33; i++)
			{
			
				if(Place.contains(PlaceNames[i]))
				{
					
					int[] Cells = findBoroughCells(i+1);
					
					for(int j = 0; j < Cells.length; j++)	
					{
					
						Grid[Cells[j]].addIll(TWITSCALAR*Cells.length);
					
					}
					
					return;
					
				}
				
			}
			
		} catch(Exception e) 
		{ 
			
			MainManager.logMessage("#AnalysisManager: Failed to identify buroughs, skipping step.");
			
		}
		
		if(Place.contains("EAST"))
		{
			
			for(int i = 0; i < 40; i++)
			{
				
				for(int j = 0; j < 20; j++)
				{
					
					Grid[20+i*40+j].addIll(TWITSCALAR/800);
					
				}
				
			}
			
			return;
			
		}
		
		if(Place.contains("WEST"))
		{
			
			for(int i = 0; i < 40; i++)
			{
				
				for(int j = 0; j < 20; j++)
				{
					
					Grid[i*40+j].addIll(TWITSCALAR/800);
					
				}
				
			}
			
			return;
			
		}
		
		if(Place.contains("SOUTH"))
		{
			
			for(int i = 0; i < 20; i++)
			{
				
				for(int j = 0; j < 40; j++)
				{
					
					Grid[i*40+j].addIll(TWITSCALAR/800);
					
				}
				
			}
			
			return;
			
		} 
		
		if(Place.contains("NORTH"))
		{
			
			for(int i = 0; i < 20; i++)
			{
				
				for(int j = 0; j < 40; j++)
				{
					
					Grid[800 + i*40+j].addIll(TWITSCALAR/800);
					
				}
				
			}
			
			return;
			
		}
		
		for(int i = 0; i < 1600; i++)
		{
			
			Grid[i].addIll(TWITSCALAR/1600);
			
		}
		
		return;
		
	}
	
	//Function will be called with train travel data.
	
	public void movePeople(double X, double Y, double Num)
	{
	
		int Pos = cordConv(X, Y);
		double IllMove = Num*getAverageIll()/9;
		
		if (Pos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = cordConv(X + 0.02, Y + (i*0.01));
				
				if(a > -1)
				{
					
					Grid[a].addPop((int)Num/9);
					Grid[a].addIll(IllMove);
					
				}
				
				int b = cordConv(X, Y + (i*0.01));
				
				if(b > -1)
				{
					
					Grid[a].addPop((int)Num/9);
					Grid[a].addIll(IllMove);
					
				}
				
				int c = cordConv(X - 0.02, Y + (i*0.01));
				
				if(c > -1)
				{
					
					Grid[a].addPop((int)Num/9);
					Grid[a].addIll(IllMove);
					
				}
				
			}
			
		}
		
	}
	
	public void run()
	{
		
		while(!MainManager.isShutdown())
		{
			
			if(MainManager.getTwitterManager().isUpdated())
			{
				
				init();
				
				ArrayList<String> Tweets = MainManager.getTwitterManager().getTweets();
				for(int i = 0; i < Tweets.size(); i++)	{ addTweet(Tweets.get(i));	}
				
				Date CurrentData = Calendar.getInstance().getTime();
				
				
				
			}
			
		}
		
	}

}
