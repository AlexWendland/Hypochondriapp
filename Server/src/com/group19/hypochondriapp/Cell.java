package com.group19.hypochondriapp;

//Basic Cell in my London Grid. To be expanded for further use.

public class Cell {

	private int Bor;
	private int Pop;
	private double Ill;
	
	public Cell()
	{
		
		Bor = 0;
		Pop = 0;
		Ill = 0;
		
	}
	
	public void setBor(int bor)	{ Bor = bor;	}
	
	public void setPop(int pop)	{ Pop = pop;	}
	
	public void setIll(double ill)	{ Ill = ill;}
	
	public double getIll()	{ return Ill;	}
	
	public int getPop()	{ return Pop;	}
	
	public int getBor()	{ return Bor;	}
	
	public void addPop(int pop)
	{
		
		Pop += pop;
		
		if(Pop < 0)
			Pop = 0;
		
	}
	
	public void addIll(double ill)
	{
		
		Ill += ill;
		
		if(Ill < 0)
			Ill = 0;
		
	}
	
}
