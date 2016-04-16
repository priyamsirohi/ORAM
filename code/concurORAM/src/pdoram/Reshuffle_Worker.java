package pdoram;

import pdoram.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Operations.*;
import message.Message;
import message.Message.MessageType;
import message.PDoram_getBucketsForReshuffle;
import message.WriteWSpacePart;


public class Reshuffle_Worker extends Thread{

	
	private PDOramServer pdoramserver;
	ServerSocket ss;
	ObjectInputStream is;
	ObjectOutputStream os;
	

	Reshuffle_Worker(ServerSocket ss, PDOramServer pdoramserver ){
		this.pdoramserver = pdoramserver;
		this.ss = ss;
		
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
		
		
		while (true){
			
			Message ms = null;
			
			
			try {
				ms = (Message) is.readObject();
				
			} catch (ClassNotFoundException | IOException e2) {
				continue;
			}
			
			if(ms.getMessageType().compareTo(MessageType.PDoram_getBucketsForReshuffle) == 0){
				PDoram_getBucketsForReshuffle getbucket_res = (PDoram_getBucketsForReshuffle) ms;
				PDOrambucket[] pdoram_bucket = new PDOrambucket[this.pdoramserver.levels];
				for (int i = 0; i < this.pdoramserver.levels; i++){
					try {
						pdoram_bucket[i] = this.pdoramserver.PDoramRead(i, getbucket_res.getBucket_Nums()[i]);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				
				getbucket_res.setBuckets(pdoram_bucket);
				try {
					os.writeObject(getbucket_res);
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
			
		
			if(ms.getMessageType().compareTo(MessageType.WriteWSpacePart)==0){
				WriteWSpacePart write_wspace_part = (WriteWSpacePart) ms; 
				for (int i = 0; i < this.pdoramserver.levels;i++){
					// TODO: COMPLETE THIS
				}
			}
		}
		
	}
}

