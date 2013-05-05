package com.group19.hypochondriapp;

import java.io.Serializable;

public class AppDataPacket implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	byte[][] data = new byte[24*4*14][1600];
	float[] scalar = new float[1600];
	
	public AppDataPacket()
	{
		
	}
}
