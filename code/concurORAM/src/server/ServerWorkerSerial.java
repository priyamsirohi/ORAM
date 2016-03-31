package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ringoram.DataBlock;
import ringoram.DataResultLog;
import ringoram.MetaData;
import ringoram.Node;
import ringoram.Stash;
import ringoram.TreeORAM;

import message.GetAccessCounter;
import message.GetBlocksFromPath;
import message.GetMetadata;
import message.GetPath;
import message.GetResultLogs;
import message.Message;
import message.Ping;
import message.WriteBlock;
import message.WritePath;
import message.WriteStash;
import message.Message.MessageType;

public class ServerWorkerSerial implements Runnable{

	private static final LockObject lock = new LockObject();
	ServerSocket ss;
	ObjectInputStream is;
	ObjectOutputStream os;
	Stash stash;
	TreeORAM tree;
	DataResultLog drs;
	int accessCounter;
	int eviction_rate;
	int path_counter;
	ClientQueue queue;
	
	ServerWorkerSerial(ServerSocket ss, TreeORAM tree, Stash stash, DataResultLog drs, 
			int accessCounter, int eviction_rate, int path_counter, ClientQueue queue) 
			throws IOException {
		this.ss = ss;
		this.stash = stash;
		this.tree = tree;
		this.accessCounter = accessCounter;
		this.drs = drs;
		this.path_counter = path_counter;
		this.eviction_rate = eviction_rate;
		this.queue = queue;
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
			e2.printStackTrace();
		}
		
		if (ms.getMessageType().compareTo(MessageType.Ping) == 0){
				
			synchronized(this.lock){ 
				queue.push(ms.clientID);
			}
			
			while (ms.clientID != queue.getTop()){
				
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		
			
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
			drs.setandincDataResultLog(wb.getBlk());
			accessCounter++;
			
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
			gac.access_counter = this.accessCounter;
			gac.clientID = 0;
			gac.eviction_rate = this.eviction_rate;
			gac.path_counter = this.path_counter++;
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
			accessCounter = 0;
		}
		
		if (ms.getMessageType().compareTo(MessageType.AccessComplete)==0){
			this.queue.pop();
			}
		
					
	
	}	
	}	
	}

	
