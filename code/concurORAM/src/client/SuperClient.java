package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import message.AccessComplete;
import message.GetPath;
import message.Message;
import message.Ping;
import message.WritePath;
import message.ClearLogs;
import message.Message.MessageType;
import ringoram.DataBlock;
import ringoram.PositionMap;
import pdoram.*;

public class SuperClient extends Thread {
	
	PositionMap pm;
	PDOramClient pdoram;
	int clientID = 1;
	int messageID = 0;
	String serverID;
	int server_portnum;
	int N;
	private static Logger clientLog;
	private FileHandler fh;
	private SimpleFormatter formatter;
	
	public SuperClient(int N, int portnum, String serverID) throws SecurityException, IOException{
		this.N = N;
		this.serverID = serverID;
		this.server_portnum = portnum;
		this.pm = new PositionMap(N);
		int  bucket_size = 32; //((int) Math.log(N));
		this.pdoram = new PDOramClient(N,bucket_size);
		String fname = "Logs/Client.log";
	    clientLog = Logger.getLogger(fname);
	    fh = new FileHandler(fname);
	    clientLog.addHandler(fh);
	    formatter = new SimpleFormatter();
	    fh.setFormatter(formatter);
	}
	
	 public void clientSetup() throws IOException, ClassNotFoundException, InterruptedException{

		
		 Socket s = new Socket(this.serverID,++server_portnum);
		 ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
		 ObjectInputStream is = new ObjectInputStream(s.getInputStream()); 
		
		 
	    	Random rn;
	    	
	    	rn = new Random();
	    	rn.setSeed(12345678);
	    	Ping ping = new Ping(clientID,messageID++);
			os.writeUnshared(ping);
			os.flush();
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(0);
			list.set(0,0);
			
			Message ms = (Message) is.readUnshared();
		
			  while (ms.getMessageType().compareTo(MessageType.Ping) != 0){
			    	Thread.sleep(5000); 	
			    	
					}
			  DataBlock block = new DataBlock();
			  
	    	for(int i = 1;i<=N;i++){
	    		
	    		try {
					
							  
						int map = rn.nextInt(N);				
						pm.setMap(i, map); 			// Testing with linear mapping
						list.add(i);
						list.set(i, map);
						GetPath gp = new GetPath(clientID,messageID++,i);
				
						os.writeUnshared(gp);
						os.flush();
						
						
						block.set_id(i);
						ms = (Message) is.readUnshared();
						 
					    while (ms.getMessageType().compareTo(MessageType.GetPath) != 0){
					    	Thread.sleep(5000); 	
							}
					    gp = (GetPath) ms;
					
					    
					    
					    for (int j = gp.path.length-1; j>=0;j--){
					    	if (gp.path[j].getBucket().getMetaData().num_free == 0)
					    		continue;
					    	else{ 
					    		int next_free = gp.path[j].getBucket().getMetaData().next_free_counter;
					    		next_free = gp.path[j].getBucket().getMetaData().next_free[next_free];
					    		gp.path[j].getBucket().setDataBlock(next_free,block);
					    		gp.path[j].getBucket().getDataBlocks()[next_free].set_id(i);
					    		gp.path[j].getBucket().getMetaData().log_bucket_pos_map[next_free] = i;
					    		gp.path[j].getBucket().getMetaData().phy_bucket_pos_map[next_free] = map;
					    		gp.path[j].getBucket().getMetaData().num_free--;
					    		gp.path[j].getBucket().getMetaData().next_free_counter++;
					    						    		
					    	}
					    }
					    WritePath wp = new WritePath(clientID,messageID++,i,gp.path);
					    os.writeUnshared(wp);
					    os.reset();
					    os.flush();
					   
					    
				    	wp = null;	
				    	gp = null;
				    
				    	gp = null;
						
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		
	    		
	    		
	    		
	    	}
	    	System.out.println("Client Setup complete");
	    	ClearLogs cl = new ClearLogs(clientID,messageID++);
		    os.writeUnshared(cl);
		    os.flush();
    		
		    pdoram.build(list,is,os);
		    AccessComplete ac = new AccessComplete(clientID,messageID++);
		    os.writeUnshared(ac);
		    os.flush();
		    
		    ac = null;
		    cl = null;
		 //   block = null;
		    
	 }
	 
	public void start_clients(int num_clients,boolean concurrent) throws UnknownHostException, IOException{
	
		Random rn;
		rn = new Random();
		if(concurrent){
		for (int i = 0;i<num_clients;i++){
			ConClient client= new ConClient(++server_portnum,this.serverID,this.clientID+1+i,this.N,this.pdoram, this.pm,this.clientLog,20);
			Thread thread = new Thread(client);
			thread.start();
			
		}
	}	else {
		
		for (int i = 0;i<num_clients;i++){
			Client client= new Client(++server_portnum,this.serverID,this.clientID+1+i,this.N,this.pdoram,this.pm,this.clientLog,1);
			Thread thread = new Thread(client);
			thread.start();
			
		}
	}
		
		System.out.println("All client threads started");
		
	}
}

