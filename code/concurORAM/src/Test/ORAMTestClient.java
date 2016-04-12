package Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import server.*;
import client.*;

public class ORAMTestClient{
	

public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
		
		final int N = 32;
		final int server_portnum = 20000;
		final int eviction_freq = 8;
		final int bucket_size = 32;
		final int num_dummy_blocks = 16;
		final int num_clients = 8;
		final Random rn;
		final String serverID = "127.0.0.1";
		final boolean concurrent = false;
		rn = new Random();
		rn.setSeed(12345678);
	
		
		Server server = new Server(N,bucket_size,num_dummy_blocks,server_portnum,eviction_freq,concurrent);
		server.run(num_clients);
		
		SuperClient sup_client = new SuperClient(N,server_portnum,serverID);
		sup_client.clientSetup();
		sup_client.start_clients(num_clients,concurrent);
		

		System.out.println("Successful");
		
	}
}
