package com.group19.hypochondriapp;

import java.io.IOException;

public class MainManager 
{
	
	public static void main(String[] args) throws IOException 
	{
		TwitterManager twitterManager = new TwitterManager();
		
		Thread twitter = new Thread(twitterManager);
		twitter.start();
		
		System.in.read();
		
		twitterManager.shutdown();
		try 
		{
			twitter.join();
		} 
		catch (InterruptedException e) {}


	}

}
