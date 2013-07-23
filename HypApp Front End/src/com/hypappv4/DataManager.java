package com.hypappv4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.group19.hypochondriapp.*;


@SuppressLint("HandlerLeak")
public class DataManager {
	
	/*
	NetworkReceiver netReceiver;
	public float[][] decodedIll;
	public AppDataPacket data;
	private MapV02 map;
	public Context context;
	private DataManager thisDM;
	String fileName;
	private Thread thread;
	
	public DataManager (MapV02 inmap, Context incontext){
		
		fileName = "HypAppCache";
		inmap.setLoadMessage("D");
		Log.v("DOING SHIT", "A");
		map = inmap;
		context = incontext;
		thisDM = this;
		netReceiver = new NetworkReceiver();
		
		netReceiver.update();
		
		Log.v("UPDATING....", "not done yet....");
		Log.v("going into the loop", "wait up a little");
		
		while(!netReceiver.isDone()){
			if(netReceiver.IOexcep || netReceiver.CNFexcep || netReceiver.UHexcep){
				break;
				
			}
		}
		
		inmap.setLoadMessage("I");
		
		Log.v("FOUND!", "yayayayayay");
		data = netReceiver.getData();
		
		decodedIll = data.decodeIll();
		
		//thread = new Thread(new SaveData());
		//thread.start();
		
		
		//inmap.setLoadMessage("D");
		Log.v("Decoded...", "butts");
		
		//map.drawOverlay(this);
		
		inmap.setLoadMessage("");
		
		//handler.sendEmptyMessageDelayed(1, 2000);
	}
	
	/*@SuppressLint("HandlerLeak")
	private Handler handler = new Handler () {
		
		@Override
		public void handleMessage(Message msg){
			Log.v("checking..", "going again....");
			
			if(netReceiver.isDone()){
				Log.v("FOUND!", "yayayayayay");
				data = netReceiver.getData();
				
				decodedIll = data.decodeIll();
				
				thread = new Thread(new SaveData());
				thread.start();
				
				map.drawOverlay(thisDM);
			}else{
				try{
					this.sendEmptyMessageDelayed(1, 2000);
				} catch (Exception e){
					Log.e("YO", e.getLocalizedMessage());
				}
			}
			
		}
	};*/
	
	
	/*private class SaveData implements Runnable{
		@Override
		public void run (){
			FileOutputStream fos;
			try {
				fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);
				os.writeObject(data);
				os.close();
				Log.v("File Written!", " ");
			} catch (Exception e) {
				Log.e("FILE WRITING ERROR", ":(");
				Log.e("Error message:", e.getMessage());
			}
		}
	}*/
	
	/*private class LoadData implements Runnable{
		@Override
		public void run (){
			try{
				FileInputStream fis = context.openFileInput(fileName);
				ObjectInputStream is = new ObjectInputStream(fis);
				AppDataPacket readData = (AppDataPacket) is.readObject();
				is.close();
				
				Log.v("File Read!", " ");
				
			}catch (Exception e){
				Log.e("FILE READING ERROR", ":(");
				Log.e("Error message:", e.getMessage());
			}
		}
	}*/
	
	
	
}
