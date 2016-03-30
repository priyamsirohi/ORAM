package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import ringoram.*;
import message.*;
import message.Message.MessageType;

public class Server extends Thread{

	protected Stash stash;
	protected TreeORAM tree;
	protected ServerSocket serverListener;
	
	protected int portnum;
	protected DataResultLog drs;
	protected int accessCounter;
	protected int eviction_rate;
	protected int path_counter;
	protected ObjectInputStream is;
	protected ObjectOutputStream os;
	
	public Server(int N, int bucket_size, int num_dummy_blocks, int portnum, int eviction_rate) throws UnknownHostException, IOException{
		this.stash = new Stash();
		this.tree = new TreeORAM(N,bucket_size,num_dummy_blocks);
		this.portnum = portnum;
		this.serverListener = new ServerSocket(portnum);
		this.drs = new DataResultLog(eviction_rate);
		this.accessCounter = 0;
		this.path_counter = 0;
		this.eviction_rate = eviction_rate;
		
	}
	
	
	
	
	public void run(int num_clients) throws IOException, ClassNotFoundException, InterruptedException{
		
			for(int i = 0;i<num_clients;i++){
				ServerSocket ss = new ServerSocket(++portnum);
				ServerWorkerSerial worker = new ServerWorkerSerial(ss, this.tree, this.stash,this.drs,
						this.accessCounter, this.eviction_rate,this.path_counter);
				Thread thread = new Thread(worker);
				thread.start();
				System.out.println("The server is up");
			}
			
			
		}
	
			
	}

