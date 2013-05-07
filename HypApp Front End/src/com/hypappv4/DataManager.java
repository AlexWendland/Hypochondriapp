package com.hypappv4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import com.group19.hypochondriapp.*;

public class DataManager {
	
	NetworkReceiver netReceiver;
	public float[][] decodedIll;
	public AppDataPacket data;
	
	public DataManager (Context context){
		
		netReceiver = new NetworkReceiver();
		
		netReceiver.update();
		
		Log.v("UPDATING....", "not done yet....");
		Log.v("going into the loop", "wait up a little");
		
		while(!netReceiver.isDone()){
			
		}
		
		Log.v("FOUND!", "yayayayayay");
		data = netReceiver.getData();
		
		String fileName = "HypAppCache";
		
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(data);
			os.close();
			Log.v("File Written!", null);
			Log.v("File Hash:", ""+data.hashCode());
			Log.v("To String:", ""+data.toString());
		} catch (Exception e) {
			Log.e("FILE WRITING ERROR", ":(");
			Log.e("Error message:", e.getMessage());
		}
		
		
		try{
			FileInputStream fis = context.openFileInput(fileName);
			ObjectInputStream is = new ObjectInputStream(fis);
			AppDataPacket readData = (AppDataPacket) is.readObject();
			is.close();
			
			Log.v("File Read!", null);
			Log.v("File Written hash:", ""+readData.hashCode());
			Log.v("To String:", ""+readData.toString());
			
		}catch (Exception e){
			Log.e("FILE READING ERROR", ":(");
			Log.e("Error message:", e.getMessage());
		}
		
		decodedIll = data.decodeIll();
	}
}
