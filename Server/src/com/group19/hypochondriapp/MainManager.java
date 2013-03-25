package com.group19.hypochondriapp;

import java.io.IOException;

//Class that handles and maintains references (and threads) to all modules of the system.
public class MainManager 
{
	//List of all modules
	static TwitterManager twitterManager;
	
	public static void main(String[] args) throws IOException 
	{
		twitterManager = new TwitterManager();
		
		//Should be a thread pool
		Thread twitter = new Thread(twitterManager);
		twitter.start();
		
		
		
		//Tests for the twitter miner.
		System.in.read();
		
		twitterManager.shutdown();
		try 
		{
			twitter.join();
		} 
		catch (InterruptedException e) {}


	}

}
