package com.group19.hypochondriapp;

import java.io.FileReader;
import java.io.BufferedReader;

//Function that will run.

public class Analysis {
	
	private Cell[] Grid;
	
	//Raw data, on Uk and twitter use.
	
	public static final int UKPOP = 62641000;
	public static final int UKTWIT = 10000000;
	
	public void initiate() 
	{
		
		Grid = new Cell[1600];
		
		int[] PopDen = new int[33];
		
		BufferedReader br = null;
		
		//Takes in Densities, in sets it to the array, then sets borough and pop to cells.
		
		try 
		{
 
			String BoroughPos;
 
			br = new BufferedReader(new FileReader("./res/BoroughDensities.txt"));
 
			BoroughPos = br.readLine();
				
			String[] tokens = BoroughPos.split(" ");
				
			for(int i = 0; i < 33; i++)
			{
					
				PopDen[i] = 259*Integer.valueOf(tokens[i]);
					
			}
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#Analysis: Could not read ./res/BoroughDensities.txt.");
		
		}
		
		try 
		{
 
			String BoroughPos;
 
			br = new BufferedReader(new FileReader("./res/BoroughPlace.txt"));
 
			BoroughPos = br.readLine();
				
			String[] tokens = BoroughPos.split(" ");
				
			for(int i = 0; i < 1600; i++)
			{
				
				int a = Integer.valueOf(tokens[i]);
				
				Grid[i].setBor(a);
				Grid[i].setPop(PopDen[a]);
					
			}
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#Analysis: Could not read ./res/BoroughPlace.txt.");
		
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
	
	//Function will be called, to add a tweet.
	
	public void addTweet(double x, double y)
	{
		
		int Pos = cordConv(x, y);
		
		if (Pos > -1)
		{
			
			Grid[Pos].addIll((int)(UKPOP/UKTWIT));

		}
		
	}
	
	//Function will be called with train travel data.
	
	public void move(double xIn, double yIn, double xOut, double yOut, double Num)
	{
	
		int InPos = cordConv(xIn, yIn);
		int OutPos = cordConv(xOut, yOut);
		int IllMove = 0;
		
		if (InPos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = cordConv(xIn + 0.02, yIn + (i*0.01));
				
				if(a > -1)
				{
					
					Grid[a].addPop((int)-Num/9);
					int ToAdd = Grid[a].getIll()/18;
					Grid[a].addIll(-ToAdd);
					IllMove += ToAdd;
					
				}
				
				int b = cordConv(xIn, yIn + (i*0.01));
				
				if(b > -1)
				{
					
					Grid[b].addPop((int)-Num/9);
					int ToAdd = Grid[b].getIll()/18;
					Grid[b].addIll(-ToAdd);
					IllMove += ToAdd;
					
				}
				
				int c = cordConv(xIn - 0.02, yIn + (i*0.01));
				
				if(c > -1)
				{
					
					Grid[c].addPop((int)-Num/9);
					int ToAdd = Grid[c].getIll()/18;
					Grid[c].addIll(-ToAdd);
					IllMove += ToAdd;
					
				}
				
			}
			
		}
		
		if(OutPos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = cordConv(xOut + 0.02, yOut + (i*0.01));
				
				if(a > -1)
				{
					
					Grid[a].addPop((int)Num/9);
					Grid[a].addIll((int)IllMove/9);
					
				}
				
				int b = cordConv(xOut, yOut + (i*0.01));
				
				if(b > -1)
				{
					
					Grid[b].addPop((int)Num/9);
					Grid[b].addIll((int)IllMove/9);
					
				}
				
				int c = cordConv(xOut - 0.02, yOut + (i*0.01));
				
				if(c > -1)
				{
					
					Grid[c].addPop((int)Num/9);
					Grid[c].addIll((int)IllMove/9);
					
				}
				
			}
			
		}
		
	}
	
}