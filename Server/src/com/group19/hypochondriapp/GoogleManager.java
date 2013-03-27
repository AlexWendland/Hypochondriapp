package com.group19.hypochondriapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

public class GoogleManager implements Runnable
{
	public static final long UPDATE = 2419200000l;
	public static final String SERVICE = "trendspro";
	public static final String ACCOUNT_TYPE = "HOSTED_OR_GOOGLE";
	public static final String LOGIN_URL = "https://www.google.com/accounts/ClientLogin";
	public static final String SOURCE = "86.17.74.14";
	public static final String CONTENT_TYPE = "x-www-form-urlencoded";
	public static final String CHARSET = "UTF-8";
	public static final int DEFAULT_READ_TIMEOUT = 5000;
	public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
	
	private Properties googleProperties;
	private String authToken;
	
	private long lastUpdate;
	private boolean updateRequired = false;
	
	public GoogleManager()
	{
		init();
	}
	
	public void init()
	{
		googleProperties = new Properties();
		File file = new File("./res/google.properties");
		
		try
		{
			if(!file.exists())
			{
				MainManager.logMessage("#GoogleManager: File \"google.properties\" cannot be found");
				System.exit(-1);
			}
			else
			{
				googleProperties.load(new FileInputStream(file));
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#GoogleManager: Could not load file \"google.properties\"");
			e.printStackTrace();
			System.exit(-1);
		}
		
		lastUpdate = Long.parseLong(googleProperties.getProperty("g.lastUpdate"));
		
		if((Calendar.getInstance().getTimeInMillis() - lastUpdate) > UPDATE) updateRequired = true;
	}
	
	public void cleanup()
	{
		
	}
	
	public void authenticate()
	{
		String query = new String("accountType=" + ACCOUNT_TYPE + "&Email=" + googleProperties.getProperty("g.username")
						+ "&Passwd=" + googleProperties.getProperty("g.password") + "&service=" + SERVICE
						+ "&source=" + SOURCE);
		
		URLConnection connection = null;
		
		try
		{
			connection = new URL(LOGIN_URL).openConnection();
			connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
			connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", CHARSET);
			connection.setRequestProperty("Content-Type", "application/" + CONTENT_TYPE);
			OutputStream output = null;
			try 
			{
				output = connection.getOutputStream();
				output.write(query.getBytes(CHARSET));
			} 
			finally 
			{
				if(output != null) output.close();
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#GoogleManager: Unable to login");
			e.printStackTrace();
		}
		
		String responseMessage = null;
		
		try
		{
			StringBuffer sb = new StringBuffer();
			InputStream dis = ((HttpsURLConnection)connection).getInputStream();
			int chr;
			while ((chr = dis.read()) != -1) 
			{
				sb.append((char) chr);
			}
			if (sb != null) 
			{
				responseMessage = sb.toString();
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#GoogleManager: Unable to retrieve login response");
			e.printStackTrace();
		}
		
		String[] lines = responseMessage.split(System.getProperty("line.separator"));
		for (String line : lines) 
		{
			if (line.startsWith("Auth=")) 
			{
				authToken = line.substring(5);
			}
		}
		
		MainManager.logMessage("#GoogleManager: Got authorisation token from Google: " + authToken);
	}
	
	public void updateCSV()
	{
		//Needs to replace existing csv and update properties to new last updated time
	}
	
	@Override
	public void run() 
	{
		if(updateRequired) updateCSV();
		
	}

}
