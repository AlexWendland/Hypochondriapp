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
	private static boolean shutdown = false;
	
	private Properties twitterProperties;
	
	public TwitterManager()
	{
		init();
	}
	
	public void init() //Retrives tokens from twitter.properties
	{
		twitterProperties = new Properties();
		File file = new File("./res/twitter.properties");
		
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
			System.err.println("Could not load file \"twitter.properties\", unable to continue.\nExiting...");
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void shutdown() //Tells the scraper to stop and clean up properly.
	{
		shutdown = true;
	}
	
	@Override
	public void run() 
	{
		//Sets required configurations.
		ConfigurationBuilder configBuilder = new ConfigurationBuilder(); //Check for some settings I may have missed.
    	configBuilder.setOAuthConsumerKey(twitterProperties.getProperty("oauth.consumerKey"));
    	configBuilder.setOAuthConsumerSecret(twitterProperties.getProperty("oauth.consumerSecret"));
    	configBuilder.setOAuthAccessToken(twitterProperties.getProperty("oauth.accessToken"));
    	configBuilder.setOAuthAccessTokenSecret(twitterProperties.getProperty("oauth.accessTokenSecret"));
        TwitterStream twitterStream = new TwitterStreamFactory(configBuilder.build()).getInstance();
		
        //Query to search for.
		FilterQuery query = new FilterQuery();
		double[][] location = {{51.3, -0.5}, {51.7, 0.3}};
		query.locations(location);
		String[] temp = {"feel sick", "feeling sick", "gotten sick","flu","influenza","bedridden", "cough"}; //Need refining
		query.track(temp);
		//Query does keywords OR location, not both, need to figure out a way to get required tweets. Perhaps keywords in query then remove any with incorrect locations from that point.
		
		//Callback functions to certain events (onStatus is the important one).
		StatusListener listener = new StatusListener()
		{
            @Override
            public void onStatus(Status status) { //Need to alter this method to send tweets to Analysis module (just geolocation data).
            	if(status.getGeoLocation() == null) return;
                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getPlace() + ":" + status.getGeoLocation() + " - " + status.getText());
            }
            
            //Make all these save to log or something, maybe just do nothing
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
		
        
        twitterStream.addListener(listener); //Sets up the callbacks
        twitterStream.filter(query); //Starts the stream with the required query.
        
        while(!shutdown) //Waits until shutdown
        try
        {
        	synchronized(this)
        	{
        		this.wait(5000);
        	}
        }
        catch(InterruptedException e){}
        
        twitterStream.shutdown(); //Shuts down the stream nicely.
	}
}

