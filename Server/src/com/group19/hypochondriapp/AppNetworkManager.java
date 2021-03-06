package com.group19.hypochondriapp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AppNetworkManager implements Runnable
{
	public static final int PORT = 8033;
	public static final int NUM_THREADS = 4;
	
	ServerSocket socket;
	
	AppDataPacket currentDataModel = null;
	
	private volatile int connectionCount = 0;								
	
	public AppNetworkManager()
	{
		//Create single threaded execution
		//Multithread code
	}
	
	public void updateModel(AppDataPacket newModel)
	{
		currentDataModel = newModel;
	}
	
	public void run()
	{
		try
		{
			socket = new ServerSocket(PORT);
			socket.setSoTimeout(100);
		}
		catch(IOException e)
		{
			MainManager.logMessage("#AppNetworkManager: Could not create server socket, exiting...");
			e.printStackTrace();
			return;
		}
		
		Thread[] threads = new Thread[NUM_THREADS];
		
		for(int i = 0; i < threads.length; i++)
		{
			threads[i] = new Thread(new AppNetworkThread(socket));
			threads[i].start();
		}
		
		
		for(int i = 0; i < threads.length; i++)
		{
			try 
			{
				threads[i].join();
			} catch (InterruptedException e) {}
		}
		
		try
		{
			socket.close();
		}
		catch(Exception e){}
		
		MainManager.logMessage("#AppNetworkManager: Serviced " + connectionCount + " information requests.");
		MainManager.logMessage("#AppNetworkManager: Shutting down ...");
		
	}
	

	
	
	
	
	private class AppNetworkThread implements Runnable
	{
		ServerSocket server;
		
		public AppNetworkThread(ServerSocket ss)
		{
			server = ss;
		}
		
		public void run()
		{
			Socket socket = null;
			boolean appConnect = false;
			
			while((!MainManager.isShutdown()) && (!MainManager.isAppNetworkShutdown()))
			{
				do
				{
					if((MainManager.isShutdown()) || (MainManager.isAppNetworkShutdown())) return;
					try
					{
						socket = server.accept();
						appConnect = true;
					}
					catch(SocketTimeoutException e)
					{
						appConnect = false;
					}
					catch(Exception e)
					{
						MainManager.logMessage("#AppNetworkManager: Could not accept incoming connection");
					}
				}
				while(!appConnect);
				
				ObjectOutputStream toApp = null;
				
				try
				{
					toApp = new ObjectOutputStream(socket.getOutputStream());
					toApp.flush();
					toApp.writeObject(currentDataModel);
					toApp.flush();
					MainManager.logMessage("#AppNetworkManager: App request serviced");
					connectionCount++;
				}
				catch(IOException e)
				{
					MainManager.logMessage("#AppNetworkManager: Could not send data to device");
					continue;
				}
				finally
				{
					try
					{
						toApp.close();
						socket.close();
					}
					catch(Exception e){}
				}
			}
			
		}
	}
}
