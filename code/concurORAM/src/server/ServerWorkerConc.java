package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import pdoram.PDOramServer;
import pdoram.PDOrambucket;

import ringoram.DataBlock;
import ringoram.DataResultLog;
import ringoram.MetaData;
import ringoram.Node;
import ringoram.PositionMap;
import ringoram.Stash;
import ringoram.TreeORAM;


import message.GetAccessCounter;
import message.GetBlocksFromPath;
import message.GetMetadata;
import message.GetPath;
import message.GetResultLogs;
import message.Message;
import message.PDOram_WriteBucket;
import message.PDoram_getBucket;
import message.Ping;
import message.WriteBlock;
import message.WritePath;
import message.WriteStash;
import message.Message.MessageType;
import message.GetQLog;


public class ServerWorkerConc implements Runnable{

	private static LockObject lock = LockObject.getInstance();
	ServerSocket ss;
	ObjectInputStream is;
	ObjectOutputStream os;
	Stash stash;
	TreeORAM tree;
	DataResultLog drs;
	AtomicInteger accessCounter;
	int eviction_rate;
	AtomicInteger path_counter;
	ClientQueue queue;
	private Logger ServerLog;
    private FileHandler fh;
    private SimpleFormatter formatter;
    private QueryLog qlog; 
	private PDOramServer pdserver;
    
	ServerWorkerConc(ServerSocket ss, TreeORAM tree, Stash stash, DataResultLog drs, 
			AtomicInteger accessCounter, int eviction_rate, AtomicInteger path_counter, ClientQueue queue, QueryLog qlog, PDOramServer pdserver) 
			throws IOException {
		this.ss = ss;
		this.stash = stash;
		this.tree = tree;
		this.accessCounter = accessCounter;
		this.drs = drs;
		this.path_counter = path_counter;
		this.eviction_rate = eviction_rate;
		this.queue = queue;
		this.qlog = qlog;
		this.pdserver = pdserver;
		
		 String fname = "Logs/Worker#" + ss.getLocalPort() + ".log";
	        ServerLog = Logger.getLogger(fname);
	        fh = new FileHandler(fname);
	        ServerLog.addHandler(fh);
	        formatter = new SimpleFormatter();
	        fh.setFormatter(formatter);
	}

	
	public void run(){
		
		
		Socket server = null;
		try {
			server = ss.accept();
			
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			is = new ObjectInputStream(server.getInputStream());
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			os = new ObjectOutputStream(server.getOutputStream());
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		
		
		while(true){
		Message ms = null;
		
		
		try {
			ms = (Message) is.readObject();
			
		} catch (ClassNotFoundException | IOException e2) {
			// TODO Auto-generated catch block
			//e2.printStackTrace();
			continue;
		}
		
		
		
	
		if (ms.getMessageType().compareTo(MessageType.Ping) == 0){
			Ping ping = new Ping(0,0);
			try {
				os.writeObject(ping);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		if (ms.getMessageType().compareTo(MessageType.GetQLog) == 0){
			
			GetQLog gql;
			gql = (GetQLog) ms;
			while (accessCounter.get() >= eviction_rate){								// ALL clients in a busy wait
				
				Thread.yield();
			}
			
			synchronized(this.lock){
				qlog.setEntry(gql.blk_id);
				queue.push(ms.clientID);
				this.accessCounter.getAndIncrement();
				gql = new GetQLog(0,0,qlog);
				gql.getQLog().setQLog(this.qlog.getQLog());
				gql.access_counter = accessCounter.get();
				gql.path_counter = path_counter.get();
				gql.eviction_rate = eviction_rate;
				gql.your_access = qlog.getHead()-1;
			}
			
		
		
			
			
			
				
				
			
						
						
			try {
				os.writeObject(gql);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			
			/*
			try {
				ms = (Message) is.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 while (ms.getMessageType().compareTo(MessageType.GetQLog) != 0);
			 
			 gql = (GetQLog) ms;
			 qlog.setQLog(gql.getQLog().getQLog());
		     			
			//synchronized(this.lock) {queue.pop();}
			
			*/
			
		}
			
				
	
		if (ms.getMessageType().compareTo(MessageType.PDoram_GetBucket)==0){
			PDoram_getBucket pdgb = (PDoram_getBucket) ms;
			PDOrambucket bucket = null;
			try {
				bucket = pdserver.PDoramRead(pdgb.getLevel_num(), pdgb.getBucket_num());
				pdgb.setBucket(bucket);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				os.writeObject(pdgb);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.PDoram_WriteBucket)==0){
			PDOram_WriteBucket pdwb = (PDOram_WriteBucket) ms;
			
			try {
				pdserver.PDOramWriteBucket(pdwb.getBucket(), pdwb.getLevel_Num(), pdwb.getBucket_Num());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}
		
		
		
		
		if (ms.getMessageType().compareTo(MessageType.GetMetadata)==0){
			GetMetadata gm = (GetMetadata) ms;
			gm.clientID = 0;
			Node [] node = null;
			try {
				node = tree.read_path(gm.getLeaf_ID());
				
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			gm.metadata = new MetaData[tree.getDepth()];
			for (int i = 0;i<tree.getDepth();i++){
				gm.metadata[i] = node[i].getBucket().getMetaData();
				node[i].getBucket().getMetaData().bucket_access_counter++;
			}
			try {
				os.writeObject(gm);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if (ms.getMessageType().compareTo(MessageType.GetBlocksFromPath)==0){
		
			GetBlocksFromPath gbp = (GetBlocksFromPath) ms;
			gbp.clientID = 0;
			Node [] node = null;
			try {
				
				node = tree.read_path(gbp.getLeaf_ID());
				
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gbp.blocks = new DataBlock[tree.getDepth()];
			for (int i = 0;i<tree.getDepth();i++){
				gbp.blocks[i] = node[i].getBucket().getDataBlocks()[gbp.blk_num[i]];
			}
			
			gbp.stash = this.stash;
			try {
				os.writeObject(gbp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
				
		if (ms.getMessageType().compareTo(MessageType.WriteBlock)==0){
			WriteBlock wb = (WriteBlock) ms;
			synchronized (this.lock){
			drs.setandincDataResultLog(wb.getBlk());
			qlog.setComplete(ms.clientID);
			}
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.GetPath)==0){
			GetPath gp = (GetPath) ms;
			gp.clientID = 0;
			try {
				gp.path = tree.read_path(gp.getLeaf_ID());
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.writeObject(gp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.WritePath)==0){
			WritePath wp = (WritePath) ms;
			try {
				tree.write_path(wp.getLeaf_ID(), wp.getNodes());
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		
		if (ms.getMessageType().compareTo(MessageType.GetResultLogs)==0){
			GetResultLogs grl = (GetResultLogs) ms;
			while (!(qlog.isComplete(qlog.getIndex(ms.clientID))));
			grl.clientID = 0;
			grl.drs = this.drs;
			try {
				os.writeObject(grl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (ms.getMessageType().compareTo(MessageType.WriteStash)==0){
			WriteStash ws = (WriteStash) ms;
			this.stash = ws.stash;
		}
		
		
		
		if (ms.getMessageType().compareTo(MessageType.GetAccessCounter)==0){
			GetAccessCounter gac = (GetAccessCounter) ms;
			gac.access_counter = this.accessCounter.get();
			gac.clientID = 0;
			gac.eviction_rate = this.eviction_rate;
			gac.path_counter = this.path_counter.getAndIncrement();
			try {
				os.writeObject(gac);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.ClearLogs)==0){
			this.drs.clearDataResultLog();
			this.qlog.clearQLog();
			accessCounter.set(0);
		}
		
		if (ms.getMessageType().compareTo(MessageType.AccessComplete)==0){
			
			
			
			}
		
					
	
		}	
	}	
	}

	

