package com.group19.hypochondriapp;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestClass extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private Color[][] cells;
	private BufferedImage mapImage;

	public TestClass()
	{
		setRandomGrid();
	}
	
	public void setRandomGrid()
	{
		cells = new Color[40][40];
		
		for(int i = 0; i < 40; i++)
		{
			for(int j = 0; j < 40; j++)
			{
				cells[i][j] = new Color(1.0f, 0.0f, 0.0f, (float)(i)/40.0f);
			}
		}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		this.setBackground(Color.WHITE);
		
		File mapFile = new File("./res/map.png");
		
		mapImage = null;
		try 
		{
		    //mapImage = ImageIO.read(new File(".res/map.png"));
			mapImage = ImageIO.read(mapFile);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		g.drawImage(mapImage, 0, 0, this);
		
		for(int i = 0; i < 40; i++)
		{
			for(int j = 0; j < 40; j++)
			{
				g.setColor(cells[i][j]);
				g.fillRect(i*15, j*15, 15, 15);
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
	{
		
		/*
		Socket socket = new Socket(InetAddress.getByName("localhost"), 2002);
		
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		
		String message = (String) input.readObject();
		
		System.out.println(message);
		*/
		
		JFrame window = new JFrame("Hypochondriapp");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		TestClass panel = new TestClass();
		panel.setSize(600, 600);
		window.add(panel);
		window.setSize(606,628);
		window.setVisible(true);
		
		
		
		
	}
}
