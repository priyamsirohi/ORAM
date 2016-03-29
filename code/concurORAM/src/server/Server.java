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

public class Server {

	private Stash stash;
	private TreeORAM tree;
	private ServerSocket serverListener;
	private Socket server;
	private int portnum;
	private DataResultLog drs;
	private int accessCounter;
	private int eviction_rate;
	private int path_counter;
	
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
	
	public void run() throws IOException, ClassNotFoundException, InterruptedException{
		this.server = serverListener.accept();
		ObjectInputStream is = new ObjectInputStream(server.getInputStream());
		ObjectOutputStream os = new ObjectOutputStream(server.getOutputStream());
		while(true){
		
		Message ms = (Message) is.readObject();
	
		
		if (ms.getMessageType().compareTo(MessageType.Ping) == 0){
			Ping ping = new Ping(0,0);
			os.writeObject(ping);
			os.flush();
		}
		
		if (ms.getMessageType().compareTo(MessageType.GetMetadata)==0){
			GetMetadata gm = (GetMetadata) ms;
			gm.clientID = 0;
			Node [] node;
			node = tree.read_path(gm.getLeaf_ID());		
			gm.metadata = new MetaData[tree.getDepth()];
			for (int i = 0;i<tree.getDepth();i++){
				gm.metadata[i] = node[i].getBucket().getMetaData();
				node[i].getBucket().getMetaData().bucket_access_counter++;
			}
			os.writeObject(gm);
			os.flush();
		}
		
		
		if (ms.getMessageType().compareTo(MessageType.GetBlocksFromPath)==0){
			GetBlocksFromPath gbp = (GetBlocksFromPath) ms;
			gbp.clientID = 0;
			Node [] node;
			node = tree.read_path(gbp.getLeaf_ID());
			gbp.blocks = new DataBlock[tree.getDepth()];
			for (int i = 0;i<tree.getDepth();i++){
				gbp.blocks[i] = node[i].getBucket().getDataBlocks()[gbp.blk_num[i]];
			}
			
			gbp.stash = this.stash;
			os.writeObject(gbp);
			os.flush();
		}
		
		
		
		if (ms.getMessageType().compareTo(MessageType.WriteBlock)==0){
			WriteBlock wb = (WriteBlock) ms;
			drs.setandincDataResultLog(wb.getBlk());
			accessCounter++;
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.GetPath)==0){
			GetPath gp = (GetPath) ms;
			gp.clientID = 0;
			gp.path = tree.read_path(gp.getLeaf_ID());
			os.writeObject(gp);
			os.flush();
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.WritePath)==0){
			WritePath wp = (WritePath) ms;
			tree.write_path(wp.getLeaf_ID(), wp.getNodes());
					
		}
		
		if (ms.getMessageType().compareTo(MessageType.GetResultLogs)==0){
			GetResultLogs grl = (GetResultLogs) ms;
			grl.clientID = 0;
			grl.drs = this.drs;
			os.writeObject(grl);
			os.flush();
		}
		
		if (ms.getMessageType().compareTo(MessageType.WriteStash)==0){
			WriteStash ws = (WriteStash) ms;
			this.stash = ws.stash;
		}
		
		if (ms.getMessageType().compareTo(MessageType.ClearLogs)==0){
			this.drs.clearDataResultLog();
		}
		
		if (ms.getMessageType().compareTo(MessageType.GetAccessCounter)==0){
			GetAccessCounter gac = (GetAccessCounter) ms;
			gac.access_counter = this.accessCounter;
			gac.clientID = 0;
			gac.eviction_rate = this.eviction_rate;
			gac.path_counter = this.path_counter++;
			os.writeObject(gac);
			os.flush();
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.ClearLogs)==0){
			this.drs.clearDataResultLog();
			accessCounter = 0;
		}
		
		
	}
}
}
