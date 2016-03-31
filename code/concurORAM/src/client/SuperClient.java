package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import message.AccessComplete;
import message.GetPath;
import message.Message;
import message.Ping;
import message.WritePath;
import message.Message.MessageType;
import ringoram.DataBlock;
import ringoram.PositionMap;

public class SuperClient extends Thread {
	
	PositionMap pm;
	int clientID = 1;
	int messageID = 0;
	int server_portnum;
	int N;
	
	
	public SuperClient(int N, int portnum){
		this.N = N;
		this.server_portnum = portnum;
		this.pm = new PositionMap(N);
	}
	
	 public void clientSetup() throws IOException, ClassNotFoundException, InterruptedException{

		
		 Socket s = new Socket("127.0.0.1",++server_portnum);
		 ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
		 ObjectInputStream is = new ObjectInputStream(s.getInputStream()); 
		
		 
	    	Random rn;
	    	
	    	rn = new Random();
	    	rn.setSeed(12345678);
	    	for(int i = 0;i<N;i++){
	    		
	    		try {
					
						Ping ping = new Ping(clientID,messageID++);
						os.writeObject(ping);
						os.flush();
						
						Message ms = (Message) is.readObject();
					
						  while (ms.getMessageType().compareTo(MessageType.Ping) != 0){
						    	Thread.sleep(5000); 	
						    	
								}
						  
						int map = rn.nextInt(N);				
						pm.setMap(i, map); 			// Testing with linear mapping
						GetPath gp = new GetPath(clientID,messageID++,i);
					
						os.writeObject(gp);
						os.flush();
						
						DataBlock block;
						block = new DataBlock(i);
						ms = (Message) is.readObject();
						 
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
					    		gp.path[j].getBucket().getMetaData().log_bucket_pos_map[next_free] = i;
					    		gp.path[j].getBucket().getMetaData().phy_bucket_pos_map[next_free] = map;
					    		gp.path[j].getBucket().getMetaData().num_free--;
					    		gp.path[j].getBucket().getMetaData().next_free_counter++;
					    						    		
					    	}
					    }
					    WritePath wp = new WritePath(clientID,messageID++,i,gp.path);
					    os.writeObject(wp);
				    	os.flush();
					  
					    
						
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		
	    		
	    		
	    		
	    	}
	    	System.out.println("Client Setup complete");
	    	AccessComplete ac = new AccessComplete(clientID,messageID++);
	    	os.writeObject(ac);
    		os.flush();
	 }
	 
	public void start_clients(int num_clients) throws UnknownHostException, IOException{
		System.out.println("All client threads started");
		for (int i = 0;i<num_clients;i++){
			Client client= new Client(++server_portnum,"127.0.0.1",this.clientID+1+i,this.N,this.pm);
			Thread thread = new Thread(client);
			thread.start();
			
		}
		
		
	}
}

