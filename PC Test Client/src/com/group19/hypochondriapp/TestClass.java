package com.group19.hypochondriapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClass 
{
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
	{
		Socket socket = new Socket(InetAddress.getByName("localhost"), 2002);
		
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		
		String message = (String) input.readObject();
		
		System.out.println(message);
	}
}
