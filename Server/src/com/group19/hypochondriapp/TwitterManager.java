package com.group19.hypochondriapp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager implements Runnable
{
	private static final long MILLIS_IN_DAY = 86400000;
	private static final long UPDATE_TIME = 1200000; //Time between updates
	
	private Properties twitterProperties; //Data from twitter.properties
	private Configuration config; //Twitter configuration data
	
	private ArrayList<String> locations;
	private boolean updated = false;
	
	public TwitterManager()
	{
		init();
	}
	

	
	public void init() //Retrives tokens from twitter.properties and sets up configuration
	{
		twitterProperties = new Properties();
		File file = new File("./res/twitter.properties");
		
		try
		{
			if(!file.exists())
			{
				MainManager.logMessage("#TwitterManager: File \"twitter.properties\" cannot be found");
				System.exit(-1);
			}
			else
			{
				twitterProperties.load(new FileInputStream(file));
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#TwitterManager: Could not load file \"twitter.properties\"");
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Sets configuration details
		ConfigurationBuilder configBuilder = new ConfigurationBuilder(); //Check for some settings I may have missed.
    	configBuilder.setOAuthConsumerKey(twitterProperties.getProperty("oauth.consumerKey"));
    	configBuilder.setOAuthConsumerSecret(twitterProperties.getProperty("oauth.consumerSecret"));
    	configBuilder.setOAuthAccessToken(twitterProperties.getProperty("oauth.accessToken"));
    	configBuilder.setOAuthAccessTokenSecret(twitterProperties.getProperty("oauth.accessTokenSecret"));
    	
    	config = configBuilder.build();
	}
	
	@Override
	public void run()
	{
		while(!MainManager.isShutdown())
		{
			updateTweets();
			synchronized(this)
			{
				try
				{
					wait(UPDATE_TIME);
				}
				catch(InterruptedException e){}
			}
		}
	}

	//Gets all illness tweets from the past 48 hours from London
	private void updateTweets()
	{
		Twitter twitter = new TwitterFactory(config).getInstance();
		long currentTime = Calendar.getInstance().getTimeInMillis();
		boolean finished = false;
		locations = new ArrayList<String>();
		
		Query query = new Query();
		query.setLang("en");
		query.setResultType(Query.RECENT);
		query.setGeoCode(new GeoLocation(51.5, -0.1), 30.0, Query.MILES);
		query.setCount(100);
		query.setQuery("\"feel sick\" OR \"feeling sick\" OR \"sneezing\" OR \"gotten sick\" OR \"flu\" OR \"influenza\" OR \"bedridden\" OR \"cough\"");
		
		while(!finished)
		{
		
			QueryResult result = null;
			try 
			{	
				result = twitter.search(query);
			}
			catch (TwitterException e) 
			{
				MainManager.logMessage("#TwitterManager: Could not update tweets");
				return;
			}
		
		
			List<Status> resultList = result.getTweets();
		
			if(resultList.isEmpty()) MainManager.logMessage("#TwitterManager: No tweets received on update");
		
			
		
			for(int i = 0; i < resultList.size(); i++)
			{
				String location = null;
				
				long tweetTime = resultList.get(i).getCreatedAt().getTime();
				if(currentTime - tweetTime > MILLIS_IN_DAY*2) //Using 48 hours
				{
					finished = true;
					break;
				}
				
				location = resultList.get(i).getUser().getLocation();
				location = location.toUpperCase();
				if(!location.contains("LONDON")) continue;
				else locations.add(location);
			}
			
			if(!result.hasNext())
			{
				finished = true;
				break;
			}
			query = result.nextQuery();
		}
		
		twitter.shutdown();
		
		updated = true;
	}
	
	//Checks if a new update has arrived since the last call to getTweets()
	public boolean isUpdated()
	{
		return updated;
	}
	
	//Returns the latest tweet locations 
	public ArrayList<String> getTweets()
	{
		updated = false;
		return locations;
	}
	
	//Takes coordinates and returns locations
	public String[] getLocation(double lat, double lon)
	{
		GeoQuery query = new GeoQuery(new GeoLocation(lat, lon));
        
        Twitter twitter = new TwitterFactory(config).getInstance();
        ResponseList<Place> places = null;
        
        try
        {
        	places = twitter.searchPlaces(query);
        }
        catch(TwitterException e)
        {
        	MainManager.logMessage("#TwitterManager: Could not get location data");
        	e.printStackTrace();
        }
        
        if(places == null) return null;
        
        String[] placeNames = new String[places.size()];
        for(int i = 0; i < places.size(); i++)
        {
        	placeNames[i] = places.get(i).getFullName();
        }
        
        return placeNames;
	}
}

/*
Old code for streaming tweets, updated to use querying instead
@Override
public void run()
{
	MainManager.logMessage("#TwitterManager: Starting Twitter stream");
	
    TwitterStream twitterStream = new TwitterStreamFactory(config).getInstance();
	
    
    
    //Query to search for.
    
	FilterQuery query = new FilterQuery();
	double[][] location = {{51.3, -0.5}, {51.7, 0.3}};
	query.locations(location);
	
	//String[] temp = {"feel sick", "feeling sick", "gotten sick","flu","influenza","bedridden", "cough"}; //Need refining
	//query.track(temp);
	//Query does keywords OR location, not both, need to figure out a way to get required tweets. Perhaps keywords in query then remove any with incorrect locations from that point.
	
	//Callback functions to certain events (onStatus is the important one).
	StatusListener listener = new StatusListener()
	{
        @Override
        public void onStatus(Status status) 
        { //Need to alter this method to send tweets to Analysis module (just geolocation data).
        	//if(status.getGeoLocation() == null) return;
        	
        	MainManager.logMessage("#TwitterManager: Tweet received");
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getPlace() + ":" + status.getGeoLocation() + " - " + status.getText());
            
            //Most tweets have absolutely no location data, perhaps a function that can retrieve the place of a person and then assume they are at home (good assumption because they are sick).
        }
        
        //Make all these save to log or something, maybe just do nothing.
        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) 
        { //Don't care.
            //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) 
        { //Log these, means server can't deliver that many tweets.
        	MainManager.logMessage("#TwitterManager: Got track limitation notice: " + numberOfLimitedStatuses + "missed statuses");
            //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) 
        { //Ignore, 14 day old posts get geo data scrubbed which is this warning.
            //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
        }

        @Override
        public void onStallWarning(StallWarning warning) 
        { //Log these ones, means falling behind in queue of message stream.
        	MainManager.logMessage("#TwitterManager: Got stall warning: " + warning.getPercentFull() + "%");
            //System.out.println("Got stall warning:" + warning);
        }

        @Override
        public void onException(Exception ex) 
        { //Should really log and deal with this in some way was opposed to ignoring it.
        	MainManager.logMessage("#TwitterManager: Threw exception: " + ex.toString());
            //ex.printStackTrace();
        }
    };
	
    
    twitterStream.addListener(listener); //Sets up the callbacks
    twitterStream.filter(query); //Starts the stream with the required query.
    
    MainManager.logMessage("#TwitterManager: Stream started");
    
    while(!shutdown) //Waits until shutdown
    try
    {
    	synchronized(this)
    	{
    		this.wait(5000);
    	}
    }
    catch(InterruptedException e){}
    
    MainManager.logMessage("#TwitterManager: Shutting down stream");
    twitterStream.shutdown(); //Shuts down the stream nicely.
}
*/
