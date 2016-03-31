package Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import server.*;
import client.*;

public class ORAMTest{
	

public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
		
		final int N = 8;
		final int server_portnum = 20000;
		final int eviction_freq = 4;
		final int bucket_size = 32;
		final int num_dummy_blocks = 16;
		final int num_clients = 3;
		final Random rn;
		rn = new Random();
		rn.setSeed(12345678);
		
		Server server = new Server(N,bucket_size,num_dummy_blocks,server_portnum,eviction_freq);
		server.run(num_clients);
		
		SuperClient sup_client = new SuperClient(N,server_portnum);
		sup_client.clientSetup();
		sup_client.start_clients(num_clients);
		

		System.out.println("Successful");
		
	}
}
