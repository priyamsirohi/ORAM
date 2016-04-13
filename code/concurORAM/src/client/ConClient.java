package client;

import ringoram.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

import message.Message.MessageType;
import message.*;


import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import pdoram.*;

public class ConClient extends Thread{

	private Socket clientListener;
    private int clientID;
    private int messageID;
     private int portNum;
    private String hostname;
     private Logger clientLog;
     /*
     private FileHandler fh;
     private SimpleFormatter formatter;
     */
    private PositionMap pm;
    private int N;
    private int num_runs;
    PDOramClient pdoram;
    
    public ConClient(int portnum, String host,int clientID, int N, PDOramClient pdoram, PositionMap pm, Logger clientLog, int num_runs) throws UnknownHostException, IOException{
        this.portNum=portnum;
        this.hostname=host;
        this.pm = pm; 
        this.messageID = 0;
        this.clientID = clientID;
        this.num_runs = num_runs;
        /*
        String fname = "Logs/Client#" + clientID+ ".log";
        clientLog = Logger.getLogger(fname);
        fh = new FileHandler(fname);
        clientLog.addHandler(fh);
        formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        */
        this.pdoram = pdoram;
        this.clientLog = clientLog;
        this.N = N;
   }
	
   
    
	/*
	 * Helper Functions
	 */
    

	public int getPM(int blk_id,ObjectInputStream is, ObjectOutputStream os) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		if (pm.getMap(blk_id)!= PDOramRead(blk_id,is,os))
    	   	return pm.getMap(blk_id);
    	
    	return PDOramRead(blk_id,is,os);
    	
    }
    
	
    public MetaData[] getMetadata(int leaf_id,ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    	
    	//TODO: check for message authenticity
    
    	GetMetadata gm = new GetMetadata(clientID,messageID++,leaf_id);
    	
    	os.writeObject(gm);
    	os.flush();
    	
    	Message ms = (Message) is.readObject();
        while (ms.getMessageType().compareTo(MessageType.GetMetadata) != 0);
        
        gm = (GetMetadata) ms;
        return gm.metadata;
        
     }	
    
    public DataBlock[] getBlocks(GetBlocksFromPath gbp, ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    	
    	os.writeObject(gbp);
    	os.flush();
    	
    	Message ms = (Message) is.readObject();
        while (ms.getMessageType().compareTo(MessageType.GetBlocksFromPath) != 0);
        
        gbp = (GetBlocksFromPath) ms;
        
        return gbp.blocks;
    	
    }
    
  
    public void WriteBackBlock(WriteBlock wb, ObjectOutputStream os) throws IOException{
    	
    	os.writeObject(wb);
    	os.flush();
    	return;
    	
    }
    	
    	
    public GetQLog getQLog(ObjectInputStream is,ObjectOutputStream os,int blk_id) throws IOException, ClassNotFoundException, InterruptedException{

    	GetQLog gql = new GetQLog(clientID, messageID++,blk_id);
    	
    	os.writeObject(gql);
    	os.flush();
    	
    	 Message ms = (Message) is.readObject();
         while (ms.getMessageType().compareTo(MessageType.GetQLog) != 0);
         
         GetQLog gql_temp;
         gql_temp = (GetQLog) ms;
         
         return gql_temp;
    }
    
    public void ConnTest(ObjectInputStream is,ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{

    	Ping ping = new Ping(clientID, messageID++);
    	
    	os.writeObject(ping);
    	os.flush();
    	
    	 Message ms = (Message) is.readObject();
         while (ms.getMessageType().compareTo(MessageType.Ping) != 0);
         
         return;
    }
    
    
    
    public GetResultLogs getResultLog(GetResultLogs grl, ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    	
    	os.writeObject(grl);
    	os.flush();
    	
    	 Message ms = (Message) is.readObject();
         while (ms.getMessageType().compareTo(MessageType.GetResultLogs) != 0);
         
         grl = (GetResultLogs) ms;
         return grl;
    }
    
    public GetAccessCounter getAccessCounter(GetAccessCounter gac, ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    
    	os.writeObject(gac);
    	os.flush();
    	

   	 Message ms = (Message) is.readObject();
        while (ms.getMessageType().compareTo(MessageType.GetAccessCounter) != 0);
        
        gac = (GetAccessCounter) ms;
        return gac;
    	
    	
    }
    
    
       
    public void do_evict(int leaf_id, GetResultLogs grs, GetBlocksFromPath gbp, ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException, InterruptedException{
    	
    	Node[] path;
    	GetPath gp = new GetPath(clientID,messageID++,leaf_id);
    	os.writeObject(gp);
    	os.flush();
    	
    	Message ms = (Message) is.readObject();
	    while (ms.getMessageType().compareTo(MessageType.GetPath) != 0);
	    gp = (GetPath) ms;
    	
	    path = gp.path;
    	Random rn;
    	rn = new Random();
    	rn.setSeed(12345678);
    	
    	
    	DataBlock [] stash_log_comb_temp;
    	stash_log_comb_temp = new DataBlock[grs.drs.getHead() + gbp.stash.num_of_elements];
    	
    	int drs_size = 0;
      	
    	for (int i = 0; i<grs.drs.getHead();i++){
    		int flag = 0;
    		for (int j = i;j<grs.drs.getHead();j++){
    			if (grs.drs.getDataResultLog()[i] == grs.drs.getDataResultLog()[j])
    				flag = 1;
    		}
    		if (flag == 0)
    			stash_log_comb_temp[drs_size++] = grs.drs.getDataResultLog()[i];
    	}
    	
    	int k;
       	for (k = 0;k<gbp.stash.num_of_elements;k++) { stash_log_comb_temp[drs_size+k] = gbp.stash.getStash()[k];}

       
       	DataBlock [] stash_log_comb;
       	stash_log_comb = new DataBlock[drs_size+k];
       	
       	for (int l = 0; l<drs_size+k;l++)
       		stash_log_comb[l] = stash_log_comb_temp[l];
       	
    	Stash new_stash;
    	new_stash = new Stash();
    	int stash_head = 0;
   
    	for (int i = 0; i<stash_log_comb.length;i++){
    		int map = rn.nextInt(this.N);
    		int counter = 2;
    		int lca = LCA(map,leaf_id,path.length,this.N,counter);
    		boolean mapped = false;
	    	for (int j = path.length-1; j>=0;j--){
	    		if (lca <= j){
	    			
	    			if (path[j].getBucket().getMetaData().num_free == 0)
			    		continue;
			    	else{ 
			    		int next_free = path[j].getBucket().getMetaData().next_free_counter;
			    		next_free = path[j].getBucket().getMetaData().next_free[next_free];
			    		path[j].getBucket().setDataBlock(next_free,stash_log_comb[i]);
			    		path[j].getBucket().getMetaData().log_bucket_pos_map[next_free] = i;
			    		path[j].getBucket().getMetaData().phy_bucket_pos_map[next_free] = map;
			    		path[j].getBucket().getMetaData().num_free--;
			    		path[j].getBucket().getMetaData().next_free_counter++;
			    		mapped = true;
			    	}	
	    		
	    		if (!mapped)
	    			new_stash.getStash()[stash_head++] = stash_log_comb[i];
	    		else{
	    			
	    		 	this.pm.setMap((int) stash_log_comb[i].get_id(), map);
	    		}
	    	}
		    	
	    }
	         	
    	}
    	
    	WritePath wp = new WritePath(clientID,messageID++,leaf_id,path);
	    WriteStash ws = new WriteStash(clientID,messageID++,new_stash);
	   
	    os.writeObject(wp);
    	os.flush();
    	
    	os.writeObject(ws);
    	os.flush();
    }
    	
    	
    	public int LCA(int map,int leaf_id,int depth,int N,int counter){
    		int val = leaf_id - map;
    		try{
    		
    		if (Math.abs(val) > N/counter)
    			return depth;
       		
    		else{
    			counter = counter*2;
    			return LCA(map,leaf_id,depth-1,N,counter);
    		}
    		}
    	
    	catch(Exception e){
    		System.out.println("Division error");
    		return 0;
    	}
    	}

    	
  /* 
   * Loop function
   */
    
    
    
  
    public void run(){
    	
    	Random rn;
    	rn = new Random();
    	ObjectOutputStream os = null;
    	ObjectInputStream is = null;
    	try {
			clientListener = new Socket(hostname,portNum);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
			os = new ObjectOutputStream(clientListener.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
    	try {
			is = new ObjectInputStream(clientListener.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	int counter = 0;
    	while(counter < this.num_runs){
    	
    	try {
    		Thread.sleep(rn.nextInt(1000));
			clientAccessRingORAM(rn.nextInt(N)+1,os,is);
			counter++;
		} catch (ClassNotFoundException | IOException
				| InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
    
    
    /*
     * Main access function
     */
     
    public void clientAccessRingORAM(int blk_id, ObjectOutputStream os, ObjectInputStream is) throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException{
    	
    	
    	 Random rn;
    	 rn = new Random();
    	
    	
    	 ConnTest(is,os);
    	 GetQLog gql = getQLog(is,os,blk_id);
       	
      	
    	boolean fake = false;
    	int[] qlog = gql.getQLog().getQLog();
    	int path_counter = gql.path_counter;
    	int eviction_rate = gql.eviction_rate;
    	int access_counter = gql.access_counter;
    	
    	
    	
    	for (int i = 0;i<gql.getQLog().getHead();i++){
    		if (qlog[i] == blk_id && i!=gql.your_access){
    			fake = true;
    	}
    	}
    	
    
    	GetBlocksFromPath gbp = null;
    	DataBlock req_block = null;
    	if (fake){
    		getPM(-1,is,os);
    		SetPMLog spml = new SetPMLog(clientID,messageID,-1,-1);
        	
        	os.writeObject(spml);
        	os.flush();
        	int leaf_id = rn.nextInt(N);
        	
    		/* Fake access to PD-ORAM */
    		
    		MetaData[] md = getMetadata(leaf_id,is,os);
    		gbp = new GetBlocksFromPath(clientID,messageID++,leaf_id,md.length);
    		
    		for (int i = 0 ;i<md.length;i++){
    			if(md[i].bucket_access_counter >= md[i].dummy_pos.length)
					md[i].bucket_access_counter = 0;								// TODO: Implement Reshuffle
				gbp.blk_num[i] = md[i].dummy_pos[md[i].bucket_access_counter++];
		  		
			}
    		
    		DataBlock[] blocks;
        	blocks = getBlocks(gbp,is,os);
    		
    	}
   	
    	
    	else{
    		int leaf_id = getPM(blk_id,is,os);
    		SetPMLog spml = new SetPMLog(clientID,messageID,blk_id,leaf_id);
        	
        	os.writeObject(spml);
        	os.flush();
    	MetaData[] md = getMetadata(leaf_id,is,os);
      	  	    			
    	
    	gbp = new GetBlocksFromPath(clientID,messageID++,leaf_id,md.length);
       	int req_index_in_path = -1;
    	int req_index_in_stash = -1;
    	boolean unlikely = true;
    	
    	for (int i = 0 ;i<md.length;i++){
    		for(int j = 0;j<md[i].log_bucket_pos_map.length;j++){
    			if (md[i].log_bucket_pos_map[j] == blk_id){
    				gbp.blk_num[i] = j; 	
    				req_index_in_path = i;
    			}
    		}
    			if (req_index_in_path == -1){
    				if(md[i].bucket_access_counter >= md[i].dummy_pos.length)
    					md[i].bucket_access_counter = 0;								// TODO: Implement Reshuffle
    				gbp.blk_num[i] = md[i].dummy_pos[md[i].bucket_access_counter++];
    		  		
    			}
    	}

    	
    	
    	
    	DataBlock[] blocks;
    	blocks = getBlocks(gbp,is,os);
    	
          	
  
    	
    	for (int i=0;i<gbp.stash.num_of_elements;i++){
    		if (gbp.stash.getStash()[i].get_id() == blk_id){
    			req_index_in_stash = i;
    		}
    			
    	}
    	
       
    	
    	if (req_index_in_path != -1){
    		req_block = blocks[req_index_in_path];
    		unlikely = false;
    		
    	}
    	else if (req_index_in_stash!=-1){
    		req_block = gbp.stash.getStash()[req_index_in_stash];
    		unlikely = false;
    		
    	}
    }
    		
    	    	
    		
    	GetResultLogs grl = new GetResultLogs(clientID,messageID++);
        grl = getResultLog(grl,is,os);
    	
        if (fake){
    		for (int i = 0;i<grl.drs.getHead();i++){
    			if (grl.drs.getDataResultLog()[i].get_id() == blk_id){
    				req_block = grl.drs.getDataResultLog()[i];
    				}
    		}
    	
    	
    	if (req_block != null){
    		WriteBlock wb = new WriteBlock(clientID,messageID++,req_block);
    		WriteBackBlock(wb,os);
    		clientLog.severe("Failure in retrieving block");
    	}
    	
  	}
    	
    	    	
    
    
    	if(access_counter >= eviction_rate){
    		/* EVICTION */
    		do_evict(path_counter,grl,gbp,is,os);
    		ClearLogs cl = new ClearLogs(clientID,messageID++);
    		os.writeObject(cl);
    		os.flush();
    		   		
    	}
    	
    	AccessComplete ac = new AccessComplete(clientID,messageID++);
    	os.writeObject(ac);
    	os.flush();
    	clientLog.info(clientID+"-Access Complete for"+ blk_id);   	
    	messageID = 0;
    	
    	return;
    } 	
    


	public  int PDOramRead(int id,ObjectInputStream is, ObjectOutputStream os) throws FileNotFoundException, IOException, ClassNotFoundException{
        
	    int val =-1;
	    FileInputStream fis = new FileInputStream(this.pdoram.getHashFunc());
	    ObjectInputStream ois = new ObjectInputStream(fis);
	    Hash hash_arr = (Hash) ois.readObject();
	    
	  //  RandomAccessFile PDHash = new RandomAccessFile(this.pdoram.getHashFunc(), "rw");
	       	    
	    int a, b, skipBefore, skipAfter, hash;
	    Random rn;
	    rn = new Random();
	   
	  
	    
	    boolean found = false;
	    if(id == -1)
	    	found = true;									// HACK: Setting found to true always forces the loop to the else condition and selects random buckets
	    
	    for(int i=1; i< (this.pdoram.getLevels()); i++){
	    	if(!found){
	    		a = hash_arr.getHash(i).getHash_a();
	    		b = hash_arr.getHash(i).getHash_b();
	      
	    		//hash = Math.abs((a*id + b)%(1<<i));
	    		hash = Math.abs((a*id + b)%((int)Math.pow(2,i)));
	    		
	    		
	    		PDoram_getBucket pdgb = new PDoram_getBucket(clientID,messageID,i,hash);
	       
	    		os.writeObject(pdgb);
	    		os.flush();
	    		os.reset();
	    		Message ms = (Message) is.readObject();
	       
	       
	    		while(ms.getMessageType().compareTo(MessageType.PDoram_GetBucket)!= 0){
	    			Thread.yield();
	    		}
	       
	    		PDoram_getBucket new_pgdb = (PDoram_getBucket) ms;    	   
	    	   
	        	    	
	        
		      for(int j =0;j<this.pdoram.getBucketSize();j++){
		    	  if(new_pgdb.getBucket().getMap()[j] == id){
		    		  found = true;
		    		 val = new_pgdb.getBucket().getBucket().get(j);
		    		 break;
		    	  }
		    		  
		      }
	    	}  
		      else
		      {
		    	  PDoram_getBucket pdgb = new PDoram_getBucket(clientID,messageID++,i,rn.nextInt((int) (Math.pow(2,i))));
		    	  
		    	  	os.writeObject(pdgb);
		    		os.flush();
		    		os.reset();
		    		Message ms = (Message) is.readObject();
		       
		       
		    		while(ms.getMessageType().compareTo(MessageType.PDoram_GetBucket)!= 0){
		    			Thread.yield();
		    		}
		      
		    		
		      }
	    } 
	    	  
	    	  
	    	  
	  if (!found && id != -1){
		  System.out.println("COULD NOT FIND MAP, THE WHOLE WORLD IS FINISHED :(");
		  System.exit(1);
	  }
	  ois.close();
	  fis.close();
	return val;
	    }


    
}
    	
  