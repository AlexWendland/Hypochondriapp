package com.group19.hypochondriapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class BorWriter {
	
	private int[] Bor;
	
	public BorWriter()
	{
		
	}
	
	public void FilSqu(int Star, int Hig, int Len, int Val)
	{
		
		int Num = Star;
		
		for (int i = 0; i < Hig; i++)
		{
		
			for (int j = 0; j < Len; j++)
			{
				
				Bor[Num] = Val;
				
				Num ++;
				
			}
		
			Num += 40 - Len;
		
		}
		
	}
	
	//Hard coding Boroughs.
	
	public void SetBor()
	{
		
		Bor = new int[1600];
		
		//Boroughs.
		
		//City of London.
		
		FilSqu(864, 2, 2, 1);
		
		//Barking and Dagenham.
		
		FilSqu(869, 6, 5, 2);
		
		FilSqu(1112, 6, 2, 2);
				
		//Barnet.
		
		FilSqu(1172, 11, 5, 3);
		
		//Bexley.
		
		FilSqu(631, 6, 9, 4);
		
		//Brent.
		
		FilSqu((24*40 + 10), 2, 5, 5);
		
		FilSqu((26*40 + 12), 3, 3, 5);
		
		//Bromley.
		
		FilSqu(24, 15, 16, 6);
		
		//Camden.
		
		FilSqu((24*40 + 15), 5, 9, 7);
		
		FilSqu((22 + 20*40), 4, 2, 7);
		
		//Crydon.
		
		FilSqu(18, 15, 6, 8);  
		
		//Ealing.
		
		FilSqu((19*40), 1, 5, 9);
		
		FilSqu((19*40 + 5), 7, 5, 9);
		
		FilSqu((19*40+ 10), 5, 1, 9);
		
		//Enfield.
		
		FilSqu((31*40 + 17), 9, 6, 10);
		
		//Greenwich.
		
		FilSqu((18*40 + 27), 3, 4, 11);
		
		FilSqu((15*40 + 29), 3, 2, 11);
		
		//Hackney.
		
		FilSqu((23*40 + 25), 3, 1, 12);
		
		FilSqu((26*40 + 25), 3, 2, 12);
		
		//Hammersmith and Fulham.
		
		FilSqu((16*40 + 11), 8, 3, 13);
		
		//Haringey.
		
		FilSqu((29*40 + 17), 2, 6, 14);
		
		//Harrow.
		
		FilSqu((26*40 + 5), 14, 7, 15);
		
		//Havering.
		
		FilSqu((21*40 + 34), 19, 6, 16);
		
		//Hillingdon.
		
		FilSqu((20*40), 20, 5, 17);
		
		//Hounslow.
		
		FilSqu((13*40), 6, 11, 18);
		
		//Islington.
		
		FilSqu((23*40 + 24), 6, 1, 19);
		
		//Kensington and Chelsea.
		
		FilSqu((16*40 + 14), 5, 4, 20);
		
		FilSqu((21*40 + 14), 3, 2, 20);
		
		//Kingston upon Thames.
		
		FilSqu(0, 10, 11, 21);
		
		//Lambeth.
		
		FilSqu((15*40 + 18), 5, 6, 22);
		
		//Lewisham.
		
		FilSqu((15*40 + 25), 3, 4, 23);
		
		FilSqu((18*40 + 25), 3, 2, 23);
		
		//Merton.
		
		FilSqu((8*40 + 11), 6, 7, 24);
		
		//Newham.
		
		FilSqu((21*40 + 27), 6, 2, 25);
		
		//Redbridge.
		
		FilSqu((27*40 + 27), 6, 5, 26);
		
		FilSqu((33*40 + 27), 7, 7, 26);
		
		//Richmond upon Thames.
		
		FilSqu((10*40), 3, 11, 27);
		
		//Southwark.
		
		FilSqu((15*40 + 24), 6, 1, 28);
		
		//Sutton
		
		FilSqu(11, 8, 7, 29);
		
		//Tower Hamlets.
		
		FilSqu((21*40 + 26), 5, 1, 30);
		
		//Waltham Forest.
		
		FilSqu((29*40 + 23), 11, 4, 31);
		
		//Wandsworth.
		
		FilSqu((14*40 + 11), 2, 7, 32);
		
		//Westminster.
		
		FilSqu((20*40 + 18), 4, 4, 33);
		
		FilSqu((21*40 + 16), 3, 2, 33);
		
	}
	
	//Only reason main exists is to write to file.
	
	public static void main(String[] args) {
			
		String content = new String();
		
		BorWriter bor = new BorWriter();
		
		bor.SetBor();
		
		System.out.println("Start!");
		
		for(int i = 0; i < 1600; i++)
		{
			
			if(bor.Bor[i] == 0)
			{
				
				System.out.println((i%40) + " " + (int)(i/40));
				
			}

			content += bor.Bor[i] + " ";
			
		}
			
		try {
				 
	 
			File file = new File("res/BoroughPlace.txt");
				
			if (!file.exists()) {
				file.createNewFile();
			}
	 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
	 
			System.out.println("Done!");
	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
