package com.group19.hypochondriapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Properties;

public class GoogleManager implements Runnable
{
	private static final long UPDATE = 2419200000l;
	
	private Properties googleProperties;
	
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
	
	
	
	public void updateCSV(int year)
	{
		URLConnection connection = null;
		String responseMessage = null;
		
		try
		{
			connection = new URL("https://www.google.com/accounts/ClientLogin?accountType=GOOGLE&Email=" + googleProperties.getProperty("g.username") + "&Passwd=" + googleProperties.getProperty("g.password") + "&service=trendspro&source=test-test-v1").openConnection();
			
			StringBuffer sb = new StringBuffer();
			InputStream dis = ((HttpURLConnection)connection).getInputStream();
			int chr;
			while ((chr = dis.read()) != -1) 
			{
				sb.append((char) chr);
			}
			if (sb != null) 
			{
				responseMessage = sb.toString();
			}
			
			String auth = responseMessage.split("\n")[2];
			
			MainManager.logMessage("#GoogleManager: Google account authenticated");
			
			URLConnection toCSV = new URL("http://www.google.co.uk/trends/trendsReport?hl=en-US&q=flu&geo=GB&date=1%2F" + year + "%2012m&cmpt=q&content=1&export=1").openConnection();
			toCSV.addRequestProperty("Authorization", "GoogleLogin " + auth);
			
			InputStream csvStream = toCSV.getInputStream();
			
			MainManager.logMessage("#GoogleManager: Latest CSV downloaded");
			
			File csv= new File("./res/GoogleManager/flu" + year + ".csv");
			
			csv.createNewFile();
			
			FileOutputStream toFile = new FileOutputStream(csv);
			
			byte[] buffer = new byte[1024];
			int len = csvStream.read(buffer);
			while (len != -1) 
			{
			    toFile.write(buffer, 0, len);
			    len = csvStream.read(buffer);
			}
			
			MainManager.logMessage("#GoogleManager: Latest CSV saved to disk in \"flu" + year + ".csv\"");
			
			toFile.close();
			csvStream.close();
			
		}
		catch(Exception e)
		{
			MainManager.logMessage("#GoogleManager: Unable to update CSV"); 
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		//if(updateRequired) updateCSV();
	}

}

/*
private boolean authenticate()
{
	String query = new String("accountType=" + ACCOUNT_TYPE + "&Email=" + googleProperties.getProperty("g.username")
					+ "&Passwd=" + googleProperties.getProperty("g.password") + "&service=" + SERVICE);
	
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
		return false;
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
		return false;
	}
	
	if(!responseMessage.contains("Auth=")) return false;
	
	int index = responseMessage.lastIndexOf("Auth=");
	authToken = responseMessage.substring(index+5);
	
	MainManager.logMessage("#GoogleManager: Got authorisation token from Google: " + authToken);
	
	return true;
}

public void updateCSV()
{
	//Needs to replace existing csv and update properties to new last updated time
	boolean authSucceed = authenticate();
	
	if(!authSucceed)
	{
		MainManager.logMessage("#GoogleManager: Authentication failed, unable to update insights");
		return;
	}
	else
	{
		URLConnection connection = null;
		
		try
		{
			connection = new URL(CSV_URL).openConnection();
			connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
			connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
			connection.setDoInput(false);
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Authorization", "GoogleLogin Auth=" +  authToken);
			//connection.setRequestProperty("Accept-Charset", CHARSET);
			//connection.setRequestProperty("Content-Type", "application/" + CONTENT_TYPE);
			//connection.setRequestProperty("Auth", authToken);
			
			String responseMessage = null;
			
			StringBuffer sb = new StringBuffer();
			InputStream dis = ((HttpURLConnection)connection).getInputStream();
			int chr;
			while ((chr = dis.read()) != -1) 
			{
				sb.append((char) chr);
			}
			if (sb != null) 
			{
				responseMessage = sb.toString();
			}
			
			System.out.println(responseMessage);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
*/
