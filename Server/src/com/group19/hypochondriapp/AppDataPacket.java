package com.group19.hypochondriapp;

import java.io.Serializable;

public class AppDataPacket implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	double[][] data = new double[24*4*14][1600];
	
	public AppDataPacket()
	{
		
	}
}
