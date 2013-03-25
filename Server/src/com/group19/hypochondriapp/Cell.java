package com.group19.hypochondriapp;

//Basic Cell in my London Grid. To be expanded for further use.

public class Cell {

	
	private int Bor;
	private int Pop;
	private int Ill;
	
	public Cell()
	{
		
		Bor = 0;
		Pop = 0;
		Ill = 0;
		
	}
	
	public void SetBor(int bor)
	{
		
		Bor = bor;
		
	}
	
	public void SetPop(int pop)
	{
		
		Pop = pop;
		
	}
	
	public void SetIll(int ill)
	{
	
		Ill = ill;
		
	}
	
	public void AddPop(int pop)
	{
		
		Pop += pop;
		
		if(Pop < 0)
			Pop = 0;
		
	}
	
	public void AddIll(int ill)
	{
		
		Ill += ill;
		
		if(Ill < 0)
			Ill = 0;
		
	}
	
	public int GetIll()
	{
		
		return Ill;
		
	}
	
	public int GetPop()
	{
		
		return Pop;
		
	}
	
	public int GetBor()
	{
		
		return Bor;
		
	}
	
	
}
