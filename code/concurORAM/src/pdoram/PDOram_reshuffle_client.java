package pdoram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import message.Message;
import message.Message.MessageType;
import message.PDoram_getBucketsForReshuffle;
import message.WriteWSpacePart;

public class PDOram_reshuffle_client implements Runnable {

	private Socket clientListener;
    private int clientID;
    private int messageID;
     private int portNum;
    private String hostname;
    private int levels;
    private int[] next_bucket;
    private int bucket_size;
    
	public PDOram_reshuffle_client(int portnum, String host_name, int levels, int bucket_size){
		this.portNum = portnum;
		this.clientID = 0;
		this.messageID = 0;
		this.hostname = host_name;
		this.levels = levels;
		this.next_bucket = new int[levels];
		for (int i = 0 ; i < this.levels;i++){
			this.next_bucket[i] = 0;
		}
		this.bucket_size = bucket_size;
	}
	
		
	public void run(){
	
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
		
    
    	while (true){
    		
    		PDoram_getBucketsForReshuffle getbucket_res = new PDoram_getBucketsForReshuffle(clientID, messageID++, this.levels);
    		getbucket_res.setBucket_Nums(this.next_bucket);
    		for (int i = 0; i<this.levels; i++)
    			this.next_bucket[i] = this.next_bucket[i] % ((int) Math.pow(2, i));
    		
    		Message ms = null;
			try {
				ms = (Message) is.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		while (ms.getMessageType().compareTo(MessageType.PDoram_getBucketsForReshuffle) != 0){
    			Thread.yield();
    		}
    		
    		PDoram_getBucketsForReshuffle getbucket_res_new = (PDoram_getBucketsForReshuffle) ms;
    		
    		WriteWSpacePart write_wspace_part = new WriteWSpacePart(clientID,messageID++,this.levels);
    		
    		for (int i = 0; i<this.levels;i++){
    			int counter = 0;
    			Pdoram_entry[] work_area_temp = new Pdoram_entry[this.bucket_size];
    			for (int j = 0; j < this.bucket_size; j++){
    				if(getbucket_res_new.getBuckets()[i].getEntryIndex(j).getLogID() != -1)
    					work_area_temp[counter++] = getbucket_res_new.getBuckets()[i].getEntryIndex(j);
    				
    			}
    			Pdoram_workspace_partition wspace_part = new Pdoram_workspace_partition(counter,i);
    			for (int k = 0 ; k < counter; k++){
    				wspace_part.setEntry(k, work_area_temp[k]);
    			}
    			
    			write_wspace_part.setWSpacePartIndex(i, wspace_part);
    			
    				
    		}
    		
    		try {
				os.writeObject(write_wspace_part);
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
    	
    	
	}
	
	
}
