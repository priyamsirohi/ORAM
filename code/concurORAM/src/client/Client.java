package client;

import ringoram.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import message.*;
import message.Message.MessageType;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Client {

	private Socket clientListener;
    private int clientID;
    private int messageID;
     private int portNum;
    private String hostname;
     private Logger clientLog;
     private FileHandler fh;
     private SimpleFormatter formatter;
    private PositionMap pm;
    private int N;
    
    public Client(int portnum, String host,int clientID, int N) throws UnknownHostException, IOException{
        this.portNum=portnum;
        this.hostname=host;
        pm = new PositionMap(N);
        this.messageID = 0;
        this.clientID = clientID;
        clientListener = new Socket(hostname,portNum);
        String fname = "Client#" + clientID;
        clientLog = Logger.getLogger(fname);
        fh = new FileHandler(fname);
        formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        this.N = N;
       
    }
	
    public void clientSetup(ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    	
    	for(int i = 0;i<N;i++)
			try {
				{
					pm.setMap(i, i); 			// Testing with linear mapping
					GetPath gp = new GetPath(clientID,messageID++,i);
					os.writeObject(gp);
					os.flush();
					DataBlock block;
					block = new DataBlock(i);
					Message ms = (Message) is.readObject();
				    while (ms.getMessageType().compareTo(MessageType.GetPath) != 0){
				    	Thread.sleep(5000); 	
						}
				    gp = (GetPath) ms;
				    Node[] path;
				    
				    
				    for (int j = gp.path.length-1; j>=0;j--){
				    	if (gp.path[j].getBucket().getMetaData().num_free == 0)
				    		continue;
				    	else{ 
				    		int next_free = gp.path[j].getBucket().getMetaData().next_free_counter;
				    		next_free = gp.path[j].getBucket().getMetaData().next_free[next_free];
				    		gp.path[j].getBucket().setDataBlock(next_free,block);
				    		gp.path[j].getBucket().getMetaData().log_bucket_pos_map[next_free] = i;
				    		gp.path[j].getBucket().getMetaData().phy_bucket_pos_map[next_free] = i;
				    		gp.path[j].getBucket().getMetaData().num_free--;
				    		gp.path[j].getBucket().getMetaData().next_free_counter++;
				    						    		
				    	}
				    }
				    WritePath wp = new WritePath(clientID,messageID++,i,gp.path);
				    os.writeObject(wp);
			    	os.flush();
				    
				    
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
  
     
    public void clientAccessRingORAM(int blk_id) throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException{
    	ObjectOutputStream os = new ObjectOutputStream(clientListener.getOutputStream());		
    	ObjectInputStream is = new ObjectInputStream(clientListener.getInputStream());
    	
    	
    	/* Testing Server Response */
    	clientLog.info("Starting access for block ID"+blk_id);
     	clientLog.info("Pinging Server");
    	ConnTest(is,os);
    	clientLog.info("Response from Server received");
    	
    	/* Client Setup ( if initialization) */
    	
    	clientSetup(is, os);
    	
    	/* Getting PM entry */
    	clientLog.info("Get PM entry");
    	int leaf_id = getPM(blk_id);
    	clientLog.info("Retrieved PM entry"); 
    
    	/* Getting MetaData */
    	clientLog.info("Get MetaData");
    	MetaData[] md = getMetadata(leaf_id,is,os);
    	clientLog.info("Retrieved MetaData");
      	  	    			
    	/* Getting Path and Stash */
    	GetBlocksFromPath gbp = new GetBlocksFromPath(clientID,messageID++,leaf_id,md.length);
    	int req_index_in_path = -1;
    	int req_index_in_stash = -1;
    	boolean unlikely = true;
    	
    	for (int i = 0 ;i<md.length;i++){
    		for(int j = 0;j<md[i].log_bucket_pos_map.length;j++){
    			if (md[i].log_bucket_pos_map[j] == blk_id){
    				gbp.blk_num[i] = j; 	
    				req_index_in_path = i;
    			}
    			else
    				gbp.blk_num[i] = md[i].dummy_pos[md[i].bucket_access_counter++];
    		}
   		
    	}

    	
    	clientLog.info("Get Blocks and stash");
    	DataBlock[] blocks;
    	blocks = getBlocks(gbp,is,os);
    	
    	Stash stash;
    	stash = gbp.stash;
    	
    	clientLog.info("Blocks and stash Retrieved");
    	
    	for (int i=0;i<stash.getLogPosMap().length;i++){
    		if (stash.getLogPosMap()[i] == blk_id){
    			req_index_in_stash = i;
    		}
    			
    	}
    	
    	DataBlock req_block = null;
    	    	
    	
    	if (req_index_in_path != -1){
    		req_block = blocks[req_index_in_path];
    		unlikely = false;
    	}
    	else if (req_index_in_stash!=-1){
    		req_block = blocks[req_index_in_stash];
    		unlikely = false;
    	}
    	
    	if(!(unlikely))
    		clientLog.info("Required item found");
    	    	
    	Thread.sleep(10000);
    	
    	/* Write back item */
    	clientLog.info("Writing back block");
    	
    	WriteBlock wb = new WriteBlock(clientID,messageID++,req_block);
    	WriteBackBlock(wb,os);
    	clientLog.info("Access Complete");
    	    	    	
    } 	
    	
    	
    public int getPM(int blk_id){
    	
    	return pm.getMap(blk_id);
    }
    	
    public MetaData[] getMetadata(int leaf_id,ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    	
    	//TODO: check for message authenticity
    
    	GetMetadata gm = new GetMetadata(clientID,messageID++,leaf_id);
    	
    	os.writeObject(gm);
    	os.flush();
    	
    	Message ms = (Message) is.readObject();
        while (ms.getMessageType().compareTo(MessageType.GetMetadata) != 0){
        	Thread.sleep(5000); 	
			}
        
        gm = (GetMetadata) ms;
        return gm.metadata;
        
     }	
    
    public DataBlock[] getBlocks(GetBlocksFromPath gbp, ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    	
    	os.writeObject(gbp);
    	os.flush();
    	
    	Message ms = (Message) is.readObject();
        while (ms.getMessageType().compareTo(MessageType.GetBlocksFromPath) != 0){
        	Thread.sleep(5000); 	
			}
        
        gbp = (GetBlocksFromPath) ms;
        
        return gbp.blocks;
    	
    }
    
  
    public void WriteBackBlock(WriteBlock wb, ObjectOutputStream os) throws IOException{
    	
    	os.writeObject(wb);
    	os.flush();
    	return;
    	
    }
    	
    	
    public void ConnTest(ObjectInputStream is,ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{

    	Ping ping = new Ping(clientID, messageID++);
    	
    	os.writeObject(ping);
    	os.flush();
    	
    	 Message ms = (Message) is.readObject();
         while (ms.getMessageType().compareTo(MessageType.Ping) != 0){
         	Thread.sleep(5000); 	
			}
         
         return;
    }
    
    
}
    