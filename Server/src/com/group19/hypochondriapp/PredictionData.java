package com.group19.hypochondriapp;

public class PredictionData {

	private double[] Data;
	private int Borough;
	private int Year;
	private double Rating;
	
	public PredictionData(double[] data, int borough, int start, int end, int year, int dataType, double rating)
	{
		
		if((borough < 0) || (borough > 33))
		{
			Borough = 0;
		} else
		{
			Borough = borough;
		}
		
		Year = year;
		Rating = rating;
		Data = new double[52];
		
		if (dataType == 0)
		{
			
			for(int i = 0; i < 52 ; i++)
			{	
				
				if((start > (i+1)*7) || (end < i*7 + 1))
				{
				
					Data[i] = -1;
				
				}
				else
				{
					
					int Average = 0;
					int Over = 0;
					
					for(int j = 0; j < 7; j++)
					{
						
						if(i*7 + j >= start - 1)
						{
							
							try
							{
							
								Average += data[i*7 + j + 1 - start];
								Over++;
								
							} catch (Exception e) 
							{ 
								 
								MainManager.logMessage("#PredictionData: Prediction data added incorrectly, assumptions made.");
								break;
								
							}
							
						}
						
					}
					
					if(Over == 0)	{ Data[i] = -1; 			}
					else			{ Data[i] = Average/Over;	}
					
				}
				
			}
			
		} else if (dataType == 1)
		{
			
			for (int i = 0; i < 52; i++)
			{
				
				if ((i +1 < start) || (i + 1 > end))	
				{ 
					
					Data[i] = -1;
							
				}
				else									
				{ 
					try { Data[i] = data[i + 1 - start]; }
					catch (Exception e) 
					{
						
						Data[i] = -1;
						MainManager.logMessage("#PredictionData: Prediction data added incorrectly, assumptions made.");
						
					}
				}
				
			}
			
		} else if (dataType == 2)
		{
			
			for(int i = 0; i < 12; i++)
			{
				
				if ((i +1 < start) || (i + 1 > end))	
				{ 
					if (Data[i*4] == 0)
						Data[i*4] = -1;
					Data[i*4 + 1] = -1;
					Data[i*4 + 2] = -1;
					Data[i*4 + 3] = -1;
							
				}
				else									
				{ 
					try 
					{ 
						
						Data[i*4] = data[i + 1 - start];
						Data[i*4 + 1] = data[i + 1 - start]*(3/4) + data[i + 2 - start]*(1/4);
						Data[i*4 + 2] = data[i + 1 - start]*(2/4) + data[i + 2 - start]*(2/4);
						Data[i*4 + 3] = data[i + 1 - start]*(1/4) + data[i + 2 - start]*(3/4);
						try
						{
							
							Data[(i+1)*4] = data[i + 2 - start];
							
						} catch(Exception e)
						{
							continue;
						}
						
						
					}
					catch (Exception e) 
					{
						
						Data[i*4] = -1;
						Data[i*4 + 1] = -1;
						Data[i*4 + 2] = -1;
						Data[i*4 + 3] = -1;
						MainManager.logMessage("#PredictionData: Prediction data added incorrectly, assumptions made.");
						
					}
				}
				
			}
			
		} else
		{
			
			MainManager.logMessage("#PredictionData: Prediction data got added as null because of incorrect Data type.");
			for(int i = 0; i < 52; i++)
			{
				
				Data[i] = -1;
				
			}
			
		}
	
	}
	
	public double[] getData()
	{
		
		return Data;
		
	}
	
	public int getBorough()
	{
		
		return Borough;
		
	}
	
	public int getYear()
	{
		
		return Year;
		
	}
	
	public double getRating()
	{
		
		return Rating;
		
	}
	
}
