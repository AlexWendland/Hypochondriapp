package com.group19.hypochondriapp;

//Function that will run.

public class Analysis {
	
	private Cell[] Grid;
	
	public void Initiate() 
	{
		
		Grid = new Cell[1600];
		
		int i = 0;
		
		while( i < 371)
		{
			
			Grid[i].SetBor(1);
			
			i++;
			
			if(i%40 > 10)
				i += 29;
			
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
	
}