package com.group19.hypochondriapp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager implements Runnable
{
	public static final String RESOURCES = "./res/";
	
	private static boolean shutdown = false;
	
	private Properties twitterProperties;
	
	public TwitterManager()
	{
		init();
	}
	
	public void init() //Should read in properties file to get tokens.
	{
		twitterProperties = new Properties();
		File file = new File(RESOURCES + "twitter.properties");
		
		try
		{
			if(!file.exists())
			{
				System.err.println("File \"twitter.properties\" cannot be found, unable to contact Twitter servers.\nExiting...");
				System.exit(-1);
			}
			else
			{
				twitterProperties.load(new FileInputStream(file));
			}
		}
		catch(Exception e)
		{
			System.err.println("An exception happened loading.");
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void shutdown()
	{
		shutdown = true;
	}
	
	@Override
	public void run() 
	{
		
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
    	configBuilder.setOAuthConsumerKey(twitterProperties.getProperty("oauth.consumerKey"));
    	configBuilder.setOAuthConsumerSecret(twitterProperties.getProperty("oauth.consumerSecret"));
    	configBuilder.setOAuthAccessToken(twitterProperties.getProperty("oauth.accessToken"));
    	configBuilder.setOAuthAccessTokenSecret(twitterProperties.getProperty("oauth.accessTokenSecret"));
        TwitterStream twitterStream = new TwitterStreamFactory(configBuilder.build()).getInstance();
		
		FilterQuery query = new FilterQuery();
		double[][] location = {{51.3, -0.5}, {51.7, 0.3}};
		query.locations(location);
		String[] temp = {"feel sick", "feeling sick", "gotten sick","flu","influenza","bedridden", "cough"};
		query.track(temp);
		
		StatusListener listener = new StatusListener()
		{
            @Override
            public void onStatus(Status status) {
            	if(status.getGeoLocation() == null) return;
            	
                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getPlace() + ":" + status.getGeoLocation() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
		
        
        twitterStream.addListener(listener);
        twitterStream.filter(query);
        
        while(!shutdown)
        try
        {
        	synchronized(this)
        	{
        		this.wait(5000);
        	}
        }
        catch(InterruptedException e){}
        
        twitterStream.shutdown();
        twitterStream.cleanUp();

	}
}

