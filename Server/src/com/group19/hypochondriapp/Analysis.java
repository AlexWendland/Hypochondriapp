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
	
}