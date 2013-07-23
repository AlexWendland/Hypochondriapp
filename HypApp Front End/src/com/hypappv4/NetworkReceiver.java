package com.hypappv4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.group19.hypochondriapp.AppDataPacket;

import android.util.Log;

public class NetworkReceiver 
{
	public static final int PORT = 8033; //Port to connect to on server
	public static final String IP_ADDRESS = "joshua.dcs.warwick.ac.uk"; //IP of server
	
	private boolean done = false;
	private Thread thread = null;
	private AppDataPacket dataModel = null;
	
	public boolean IOexcep = false;
	public boolean UHexcep = false;
	public boolean CNFexcep = false;
	
	public NetworkReceiver()
	{
		
	}
	
	public void update()
	{
		done = false;
		thread = new Thread(new ReceiverThread());
		thread.start();
	}
	
	public boolean isDone()
	{
		if(done)
		{
			done = false;
			return true;
		}
		else return false;
	}
	
	public AppDataPacket getData()
	{
		return dataModel;
	}
	
	private class ReceiverThread implements Runnable
	{
		@Override
		public void run() 
		{
			try
			{
				Socket socket = new Socket(InetAddress.getByName(IP_ADDRESS), PORT);
			
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			
				AppDataPacket data = (AppDataPacket) input.readObject();
				
				dataModel = data;
				
				done = true;
			}
			catch(UnknownHostException e)
			{
				UHexcep = true;
				Log.e(null, "Unknown Host");
			}
			catch(IOException e)
			{
				IOexcep = true;
				Log.e(null, "IO");
			}
			catch(ClassNotFoundException e)
			{
				CNFexcep = true;
				Log.e(null, "Class Not Found");
			}
		}
		
	}
}
