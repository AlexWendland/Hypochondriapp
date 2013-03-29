package com.group19.hypochondriapp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AppNetworkManager implements Runnable
{
	public static final int PORT = 2002;
	public static final int NUM_THREADS = 4;
	
	ServerSocket socket;
	
	Object currentDataModel = new String("Test Data\n" +
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" + 
											"########################################\n" );
											
	
	public AppNetworkManager()
	{
		//Create single threaded execution
		//Multithread code
	}
	
	public void updateModel(Object newModel)
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
			
			while(!MainManager.isShutdown())
			{
				do
				{
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
