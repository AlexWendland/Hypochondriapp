package com.group19.hypochondriapp;

import java.io.Serializable;

public class AppDataPacket implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public byte[][] illData = new byte[24*8+7][1600];
	public float[] illScalar = new float[24*8+7];
	public int[][] stationsData = new int[269*2][24*8+7];
	public byte[][] ratioData = new byte[24*8+7][1600];
	public float[] ratioScalar = new float[24*8+7];
	
	public AppDataPacket()
	{
		
		
		
	}
	
	public float[][] decodeIll()
	{
	
		float[][] convertedData = new float[24*8+7][1600];
		
		for(int i = 0; i < 24*8 + 7; i++)
		{
			
			for(int j = 0; j < 1600; j++)
			{
				
				convertedData[i][j] = ((float)(illData[i][j] + 128)/255.0f)*illScalar[i];
						
			}
		
		}
		
		return convertedData;
		
	}
	
	public float[][] decodeRatio()
	{
	
		float[][] convertedData = new float[24*8+7][1600];
		
		for(int i = 0; i < 24*8 + 7; i++)
		{
			
			for(int j = 0; j < 1600; j++)
			{
				
				convertedData[i][j] = ((float)(ratioData[i][j] + 128)/255.0f)*ratioScalar[i];
						
			}
		
		}
		
		return convertedData;
		
	}
	
}
