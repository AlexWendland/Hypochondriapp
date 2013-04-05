package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

//Class that handles and maintains references (and threads) to all modules of the system.
public class MainManager 
{
	private static boolean shutdown = false;
	
	//List of all modules.
	private static TwitterManager twitterManager;
	private static GoogleManager googleManager;
	private static TravelManager travelManager;
	private static AppNetworkManager appNetworkManager;
	private static DataManager dataManager;
	private static AnalysisManager analysisManager;
	
	private static Thread[] managerThreads;
	
	//Objects for storing logged messages.
	private static ConcurrentLinkedQueue<String> log;
	private static SimpleDateFormat timeFormat;
	
	public static void init()
	{
		log = new ConcurrentLinkedQueue<String>();
		timeFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS - ");
		
		twitterManager = new TwitterManager();
		appNetworkManager = new AppNetworkManager();
		analysisManager = new AnalysisManager();
		
		googleManager = new GoogleManager();
		travelManager = new TravelManager();
		dataManager = new DataManager();
		
		managerThreads = new Thread[3];
		
		managerThreads[0] = new Thread(twitterManager);
		managerThreads[1] = new Thread(appNetworkManager);
		managerThreads[2] = new Thread(analysisManager);
	}
	
	public static void cleanup()
	{
		writeLog();
	}
	
	public static void logMessage(String message)
	{
		String toLog = new String(timeFormat.format(Calendar.getInstance().getTime()) + message);
		log.add(toLog);
		System.out.println(toLog);
	}
	
	private static void writeLog()
	{
		if(log.isEmpty()) return;
		
		File file = new File("./res/log.txt");
		
		if(file.exists()) file.delete();
		
		try
		{
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			while(!log.isEmpty())
			{
				writer.write(log.poll() + "\r\n");
			}
			
			writer.close();
		}
		catch(IOException e)
		{
			logMessage("#MainManager: Could not write log to file");
		}
		
	}
	
	public static void main(String[] args) throws IOException 
	{
		init();
		
		logMessage("#MainManager: Server initialised, ready to accept commands");
	
		String command = new String();
		
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		
		while(!command.startsWith("shutdown"))
		{
			command = console.readLine().toLowerCase();
			
			interpretCmd(command);
			
			if(command.startsWith("update")) System.out.println("Okay");
		}
		
		cleanup();
	}
	
	//Performs currently available actions from console
	private static void interpretCmd(String command)
	{
		if(command.startsWith("start"))
		{
			if(command.contains("twittermanager"))
			{
				if(managerThreads[0].isAlive())
				{
					logMessage("#MainManager: Cannot start TwitterManager as it is already alive");
				}
				else
				{
					managerThreads[0].start();
					logMessage("#MainManager: TwitterManager thread started");
				}
			}
			
			else if(command.contains("appnetworkmanager"))
			{
				if(managerThreads[1].isAlive())
				{
					logMessage("#MainManager: Cannot start AppNetworkManager as it is already alive");
				}
				else
				{
					managerThreads[1].start();
					logMessage("#MainManager: AppNetworkManager thread started");
				}
			}
			
			else if(command.contains("analysismanager"))
			{
				if(managerThreads[2].isAlive())
				{
					logMessage("#MainManager: Cannot start AnalysisManager as it is already alive");
				}
				else
				{
					managerThreads[2].start();
					logMessage("#MainManager: AnalysisManager thread started");
				}
			}
			
			else
			{
				System.out.println("Unknown start command");
				return;
			}
		}
		
		else if(command.startsWith("update"))
		{
			if(command.contains("googlemanager"))
			{
				String[] arguments = command.split(" ");
				
				try
				{
					googleManager.setUpdateYear(Integer.parseInt(arguments[2]));
				}
				catch(Exception e)
				{
					System.out.println("Syntax: update googlemanager <year>");
					return;
				}
				
				Thread google = new Thread(googleManager);
				google.start();
				logMessage("#MainManager: GoogleManager thread updating");
			}
			
			else if(command.contains("travelmanager"))
			{
				Thread travel = new Thread(travelManager);
				travel.start();
				logMessage("#MainManager: TravelManager thread updating");
			}
			
			else
			{
				System.out.println("Unknown update command");
				return;
			}
		}
		
		else if (command.startsWith("shutdown"))
		{
			shutdown();
		}
		
		else
		{
			System.out.println("Unknown command");
			return;
		}
	}
	
	private static void shutdown()
	{
		logMessage("#MainManager: Shutting down");
		shutdown = true;
		
		for(int i = 0; i < managerThreads.length; i++)
		{
			managerThreads[i].interrupt();
		}
	}
	
	public static TwitterManager getTwitterManager() { return twitterManager; }
	public static GoogleManager getGoogleManager() { return googleManager; }
	public static AppNetworkManager getAppNetworkManager() { return appNetworkManager; }
	public static DataManager getDataManager() { return dataManager; }
	public static AnalysisManager getAnalysisManager() { return analysisManager; }
	
	public static boolean isShutdown() { return shutdown; }
	
}
