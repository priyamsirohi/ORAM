package Test;

import java.io.IOException;
import java.net.UnknownHostException;

import server.*;
import client.*;

public class ORAMTest {
	

public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
		
	
		Thread server_thread = new Thread(){
			public void run(){
				Server server = null;
				try {
					server = new Server(8,32,8,20000);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					server.run();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Server is UP");
			}
		};
		
		
		Thread client_thread = new Thread(){
			public void run(){
			Client client = null;
			try {
				client = new Client(20000,"127.0.0.1",1,8);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Client is up");
			try {
				client.clientAccessRingORAM(5);
			} catch (ClassNotFoundException
					| IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		};
		
		
		server_thread.start();
		Thread.sleep(5000);
		client_thread.start();
		
		System.out.println("Successful");
		
	}
}