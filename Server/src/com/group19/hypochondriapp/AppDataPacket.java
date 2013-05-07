package com.group19.hypochondriapp;

import java.io.Serializable;

public class AppDataPacket implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	byte[][] illData = new byte[24*8+7][1600];
	float[] illScalar = new float[24*8+7];
	short[][] stationsData = new short[24*8+7][269*2];
	byte[][] ratioData = new byte[24*8+7][1600];
	float[] ratioScalar = new float[24*8+7];
	
	public AppDataPacket()
	{
		
		
		
	}
}
