package Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import server.*;
import client.*;

public class ORAMTest {
	

public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
		
		final int N = 8;
		final int server_portnum = 20000;
		final int eviction_freq = 4;
		final int bucket_size = 32;
		final int num_dummy_blocks = 8;
		final Random rn;
		rn = new Random();
		rn.setSeed(12345678);
		
		Thread server_thread = new Thread(){
			public void run(){
				Server server = null;
				try {
					server = new Server(N,bucket_size,num_dummy_blocks,server_portnum,eviction_freq);
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
				client = new Client(server_portnum,"127.0.0.1",1,N);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Client is up");
			try {
				client.clientAccessRingORAM();
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
