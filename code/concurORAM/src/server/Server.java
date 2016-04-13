package server;

import java.io.IOException;
import java.util.concurrent.atomic.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import ringoram.*;
import message.*;
import message.Message.MessageType;
import pdoram.*;

public class Server extends Thread{

	protected Stash stash;
	protected TreeORAM tree;
	protected ServerSocket serverListener;
	protected int[] client_queue;
	protected int portnum;
	protected DataResultLog drs;
	protected PMResultLog pmreslog;
	protected AtomicInteger accessCounter;
	protected int eviction_rate;
	protected AtomicInteger path_counter;
	protected static ClientQueue queue;
	protected QueryLog qlog;
	protected boolean concurrent;
	protected PDOramServer pdserver;
	
	public Server(int N, int bucket_size, int num_dummy_blocks, int portnum, int eviction_rate, boolean concurrent) throws UnknownHostException, IOException{
		this.stash = new Stash();
		this.tree = new TreeORAM(N,bucket_size,num_dummy_blocks);
		this.portnum = portnum;
		this.serverListener = new ServerSocket(portnum);
		this.drs = new DataResultLog(eviction_rate);
		this.pmreslog = new PMResultLog(eviction_rate);
		this.eviction_rate = eviction_rate;
		accessCounter = new AtomicInteger(0);
		path_counter = new AtomicInteger(0);
		this.concurrent = concurrent;
		pdserver = new PDOramServer(N, 32);//(int) Math.log(N));
		
	}
	
	
	
	
	public void run(int num_clients) throws IOException, ClassNotFoundException, InterruptedException{
			
			
			ServerSocket setup_socket = new ServerSocket(++portnum);
			queue = ClientQueue.getInstance(num_clients);
			qlog = new QueryLog(num_clients);
			
			ServerWorkerSerial setup_worker = new ServerWorkerSerial(setup_socket, this.tree, this.stash,this.drs,
					this.accessCounter, this.eviction_rate,this.path_counter, this.queue,this.pdserver,this.pmreslog);
			Thread setup_thread = new Thread(setup_worker);
			setup_thread.start();
					
			
			for(int i = 0;i<num_clients;i++){
				ServerSocket ss = new ServerSocket(++portnum);
				if(concurrent){
				ServerWorkerConc worker = new ServerWorkerConc(ss, this.tree, this.stash,this.drs,
						this.accessCounter, this.eviction_rate,this.path_counter, this.queue,this.qlog,this.pdserver);
						Thread thread = new Thread(worker);
						thread.start();
				}
				else {
					ServerWorkerSerial worker = new ServerWorkerSerial(ss, this.tree, this.stash,this.drs,
							this.accessCounter, this.eviction_rate,this.path_counter, this.queue,this.pdserver,this.pmreslog);
						Thread thread = new Thread(worker);
						thread.start();
				}
				
								
			}
			
			System.out.println("The server is up");
			this.queue.reset();
		}
	
			
	}

