package com.group19.hypochondriapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class UtilityManager 
{
	public static void unZip(String zipFile, String outputDir)
	{
		byte[] buffer = new byte[1024];
		 
	     try
	     {
	    	//create output directory is not exists
	    	File folder = new File(outputDir);
	    	if(!folder.exists())
	    	{
	    		folder.mkdir();
	    	}
	 
	    	//get the zip file content
	    	ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	 
	    	while(ze!=null)
	    	{
	 
	    	   String fileName = ze.getName();
	           File newFile = new File(outputDir + File.separator + fileName);
	 
	            //create all non exists folders
	            //else you will hit FileNotFoundException for compressed folder
	            new File(newFile.getParent()).mkdirs();
	 
	            FileOutputStream fos = new FileOutputStream(newFile);             
	 
	            int len;
	            while ((len = zis.read(buffer)) > 0) 
	            {
	            	fos.write(buffer, 0, len);
	            }
	 
	            fos.close();   
	            ze = zis.getNextEntry();
	    	}
	 
	        zis.closeEntry();
	    	zis.close();
	 
	    	MainManager.logMessage("#UtilityManager: Successfully extracted ZIP file contents");
	     }
	     catch(IOException ex)
	     {
	    	 MainManager.logMessage("#UtilityManager: Could not extract zip \"" + zipFile + "\"");
	    	 ex.printStackTrace(); 
	     }
	} 
	
	public static void excelToCSV(String file, PrintStream output)
	{
		ExcelConverter converter = null;
		
		try
		{
			converter = new ExcelConverter(new POIFSFileSystem(new FileInputStream(file)), output, -1);
			converter.process();
		}
		catch(IOException e)
		{
			MainManager.logMessage("#UtilityManager: IOException occured with file \"" + file + "\" while converting");
			e.printStackTrace();
		}
		
		
	}
}
