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
	
	public Server(int N, int bucket_size, int num_dummy_blocks, int portnum) throws UnknownHostException, IOException{
		this.stash = new Stash();
		this.tree = new TreeORAM(N,bucket_size,num_dummy_blocks);
		this.portnum = portnum;
		this.serverListener = new ServerSocket(portnum);
		this.drs = new DataResultLog(32);
		
	}
	
	public void run() throws IOException, ClassNotFoundException, InterruptedException{
		this.server = serverListener.accept();
		ObjectInputStream is = new ObjectInputStream(server.getInputStream());
		ObjectOutputStream os = new ObjectOutputStream(server.getOutputStream());
		while(true){
		
		Message ms = (Message) is.readObject();
		System.out.println(ms.getMessageType());
		
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
			for (int i = 0;i<tree.getDepth();i++){
				gm.metadata[i] = node[i].getBucket().getMetaData();
			}
			os.writeObject(gm);
			os.flush();
		}
		
		
		if (ms.getMessageType().compareTo(MessageType.GetBlocksFromPath)==0){
			GetBlocksFromPath gbp = (GetBlocksFromPath) ms;
			gbp.clientID = 0;
			Node [] node;
			node = tree.read_path(gbp.getLeaf_ID());			
			for (int i = 0;i<tree.getDepth();i++){
				gbp.blocks[i] = node[i].getBucket().getDataBlocks()[gbp.blk_num[i]];
			}
			gbp.stash = stash;
			os.writeObject(gbp);
			os.flush();
		}
		
		
		if (ms.getMessageType().compareTo(MessageType.WriteBlock)==0){
			WriteBlock wb = (WriteBlock) ms;
			drs.setandincDataResultLog(wb.getBlk());
			
		}
		
		if (ms.getMessageType().compareTo(MessageType.WriteBlock)==0){
			WriteBlock wb = (WriteBlock) ms;
			drs.setandincDataResultLog(wb.getBlk());
			
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
			wp.clientID = 0;
			tree.write_path(wp.getLeaf_ID(), wp.getNodes());
					
		}
		
	}
}
}
