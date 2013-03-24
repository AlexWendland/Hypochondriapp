package com.group19.hypochondriapp;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

//Function that will run.

public class Analysis {
	
	private Cell[] Grid;
	
	public static final int UKPOP = 62641000;
	public static final int UKTWIT = 10000000;
	
	public void Initiate() 
	{
		
		Grid = new Cell[1600];
		
		int[] PopDen = new int[33];
		
		BufferedReader br = null;
		
		try 
		{
 
			String BoroughPos;
 
			br = new BufferedReader(new FileReader("BoroughDensities.txt"));
 
			BoroughPos = br.readLine();
				
			String[] tokens = BoroughPos.split(" ");
				
			for(int i = 0; i < 33; i++)
			{
					
				PopDen[i] = 259*Integer.valueOf(tokens[i]);
					
			}
 
		} 
		catch (IOException e) 
		{
			
			e.printStackTrace();
		
		}
		
		try 
		{
 
			String BoroughPos;
 
			br = new BufferedReader(new FileReader("BoroughPlace.txt"));
 
			BoroughPos = br.readLine();
				
			String[] tokens = BoroughPos.split(" ");
				
			for(int i = 0; i < 1600; i++)
			{
				
				int a = Integer.valueOf(tokens[i]);
				
				Grid[i].SetBor(a);
				Grid[i].SetPop(PopDen[a]);
					
			}
 
		} 
		catch (IOException e) 
		{
			
			e.printStackTrace();
		
		} 
		
	}
	
	public int CordConv(double x, double y) 
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
	
	public void AddTweet(double x, double y)
	{
		
		int Pos = CordConv(x, y);
		
		if (Pos > -1)
		{
			
			Grid[Pos].AddIll((int)(UKPOP/UKTWIT));

		}
		
	}
	
	public void Move(double xIn, double yIn, double xOut, double yOut, double Num)
	{
	
		int InPos = CordConv(xIn, yIn);
		int OutPos = CordConv(xOut, yOut);
		int IllMove = 0;
		
		if (InPos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = CordConv(xIn + 0.02, yIn + (i*0.01));
				
				if(a > -1)
				{
					
					Grid[a].AddPop((int)-Num/9);
					int ToAdd = Grid[a].GetIll()/18;
					Grid[a].AddIll(-ToAdd);
					IllMove += ToAdd;
					
				}
				
				int b = CordConv(xIn, yIn + (i*0.01));
				
				if(b > -1)
				{
					
					Grid[b].AddPop((int)-Num/9);
					int ToAdd = Grid[b].GetIll()/18;
					Grid[b].AddIll(-ToAdd);
					IllMove += ToAdd;
					
				}
				
				int c = CordConv(xIn - 0.02, yIn + (i*0.01));
				
				if(c > -1)
				{
					
					Grid[c].AddPop((int)-Num/9);
					int ToAdd = Grid[c].GetIll()/18;
					Grid[c].AddIll(-ToAdd);
					IllMove += ToAdd;
					
				}
				
			}
			
		}
		
		if(OutPos > -1)
		{
			
			for(int i = -1; i < 2; i++)
			{
				
				int a = CordConv(xOut + 0.02, yOut + (i*0.01));
				
				if(a > -1)
				{
					
					Grid[a].AddPop((int)Num/9);
					Grid[a].AddIll((int)IllMove/9);
					
				}
				
				int b = CordConv(xOut, yOut + (i*0.01));
				
				if(b > -1)
				{
					
					Grid[b].AddPop((int)Num/9);
					Grid[b].AddIll((int)IllMove/9);
					
				}
				
				int c = CordConv(xOut - 0.02, yOut + (i*0.01));
				
				if(c > -1)
				{
					
					Grid[c].AddPop((int)Num/9);
					Grid[c].AddIll((int)IllMove/9);
					
				}
				
			}
			
		}
		
	}
	
	
	
}