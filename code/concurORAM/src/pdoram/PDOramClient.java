package pdoram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import message.*;
import message.Message.MessageType;
import message.*;
/**
*
* @author priyam.sirohi
*/


public class PDOramClient  {


	
	    String PDHashFunctions = "/home/anrin/ORAM/priyam/PDHashFunctions";
	   // String PDoramDB = "/home/nsac/anrin/ORAM/priyam/PDoramDB/";    
	    int levels, PDoramDBSize, M;
	    ObjectInputStream is;
	    ObjectOutputStream os;
	    PDOrambucket[] buckets;
	    
	    /********
	     * @param N, m
	     * @throws FileNotFoundException
	     * @throws IOException 
	     * Input : Number of entries in the mapping(N), size of each bucket(m)
	     * Output: initializes PDHashFunctions file with random values
	     * and PDoramDB file to contain all values as -1 indicating invalid entry.
	     ********/
	  
	    public PDOramClient(int N, int m) throws FileNotFoundException, IOException{

	      levels = (int)Math.ceil(Math.log(N) / Math.log(2));
	      PDoramDBSize = N*m*2; 
	      M=m;
	      int temp;
	      buckets = new PDOrambucket[N];  
	      
	      Random randomGenerator = new Random();
	      FileOutputStream out = new FileOutputStream(PDHashFunctions);
	      byte data[] =  new byte[4];
	      for(int i=0;i<2*levels;i++){
	          temp=Math.abs(randomGenerator.nextInt());

	              data[0] = (byte) (temp >> 24);
	              data[1] = (byte) (temp >> 16);
	              data[2] = (byte) (temp >> 8);
	              data[3] = (byte) (temp);
	       
	          out.write(data);
	        }
	    
	}
	    /************
	     * build(ArrayList list)
	     * All the data is populated in the last level for efficiency.
	     * No collisions are assumed (collisions inside bucket are allowed).
	    ************/
	    
	    
	    
	   public int getBucketOffset(int level_num, int bucket_num){
		   
		   int offset = 0;
		   for (int i = 0; i< level_num-1;i++){
			   offset += Math.pow(2, level_num)*M*8;
		   }
		   
		   return (offset+(bucket_num*M*8));
		   
	   }
	    
	  
	    
	    public int getBucketNum(int level_num, int bucket_num){
	    	int offset = 0;
	    	for (int i = 0; i < level_num; i++){
	    		offset += Math.pow(2,i);
	    	}
	    	
	    	offset += bucket_num;
	    	
	    	return offset;
	    }
	    
	    
	    
	   public void build(ArrayList list, ObjectInputStream is, ObjectOutputStream os) throws FileNotFoundException, IOException, ClassNotFoundException{
	        int a=-1,b=-1, temp;
	        byte data[] =  new byte[4];
	        
	        
	       
	        
	        
	        int entry_counter = 1;
		        
	        System.out.println("levels =" + this.levels);
	        for (int i = 0; i<levels-1;i++){
	        	FileInputStream inhash = new FileInputStream(PDHashFunctions);
	        	inhash.skip(8*(i));
	 	        for(int j=0;j<2;j++){
	 	            temp=0;
	 	             for (int k = 0; k < 4; k++) {
	 	                int shift = (4 - 1 - k) * 8;
	 	                temp += (inhash.read() & 0x000000FF) << shift;
	 	            }
	 	            if(a==-1) a = temp;
	 	            else b = temp;
	 	        }
	        	       
	 	        
	        	for (int j = 0; j< Math.pow(2, i);j++){
	        		//int hash = Math.abs((a*entry_counter + b)%((int)Math.pow(2,i)));
	        		//System.out.println(hash);
	        		int hash = entry_counter % ((int)Math.pow(2,i));
	        		int val = (int) list.get(entry_counter);
	        		PDoram_getBucket pdgb = new PDoram_getBucket(0,0,i,hash);
	        		os.writeObject(pdgb);
	        		os.flush();
	        		
	        		Message ms = (Message) is.readObject();
	        		while(ms.getMessageType().compareTo(MessageType.PDoram_GetBucket)!= 0){
    	    			Thread.yield();
    	    		}
    	       
    	    		PDoram_getBucket new_pgdb = (PDoram_getBucket) ms;    	   
    	    	   	PDOrambucket bucket = new_pgdb.getBucket();
    	    		
    	    	   	for (int k = 0; k < M; k++){
	        			if (bucket.getMap()[k] == -1){
	        				bucket.setMapIndex(k, entry_counter++);
	        				bucket.setBucketIndex(k, val);
	        				break;
	        			}
    	    	   	}
	        			PDOram_WriteBucket pdwb = new PDOram_WriteBucket(0,0, bucket,i,hash);
	        			os.writeObject(pdwb);
	        			os.flush();
	        	}
	        	inhash.close();
	        }
	             	
	   }       	
	        	
	        		        
	/*   	    
	    
	    public  int PDOramRead(int id,int clientID,int messageID) throws FileNotFoundException, IOException, ClassNotFoundException{
	        
	    int val =-1;
	    RandomAccessFile PDHash = new RandomAccessFile(PDHashFunctions, "rw");
	    //RandomAccessFile PDDB = new RandomAccessFile(PDoramDB, "rw");
	    
	    int a, b, skipBefore, skipAfter, hash;
	    Random rn;
	    rn = new Random();
	    
	    
	    boolean found = false;
	    for(int i=0; i< levels; i++){
	    	if(!found){
	    		a = PDHash.readInt();
	    		b = PDHash.readInt();
	      
	    		hash = Math.abs((a*id + b)%(1<<i));
	       
	    		PDoram_getBucket pdgb = new PDoram_getBucket(clientID,messageID,i,hash);
	       
	    		os.writeObject(pdgb);
	    		os.flush();
	    		os.reset();
	    		Message ms = (Message) is.readObject();
	       
	       
	    		while(ms.getMessageType().compareTo(MessageType.PDoram_GetBucket)!= 0){
	    			Thread.yield();
	    		}
	       
	    		PDoram_getBucket new_pgdb = (PDoram_getBucket) ms;    	   
	    	   
	        	    	
	        
		      for(int j =0;j<M;j++){
		    	  if(new_pgdb.getBucket().getMap()[j] == id){
		    		  found = true;
		    		 val = new_pgdb.getBucket().getBucket().get(j);
		    		 break;
		    	  }
		    		  
		      }
	    	}  
		      else
		      {
		    	  PDoram_getBucket pdgb = new PDoram_getBucket(clientID,messageID,i,rn.nextInt((int) (Math.pow(2,i))));
		    	  
		    	  os.writeObject(pdgb);
		    		os.flush();
		    		os.reset();
		    		Message ms = (Message) is.readObject();
		       
		       
		    		while(ms.getMessageType().compareTo(MessageType.PDoram_GetBucket)!= 0){
		    			Thread.yield();
		    		}
		      continue;
		    		
		      }
	    } 
	    	  
	    	  
	    	  
	  if (!found){
		  System.out.println("COULD NOT FIND MAP, THE WHOLE WORLD IS FINISHED :(");
		  System.exit(1);
	  }
	return val;
	    }
	  
	 */
	   
	 public int getLevels(){
		 return this.levels;
	 }
	 
	 public String getHashFunc(){
		 return this.PDHashFunctions;
	 }
	 
	 public int getBucketSize(){
		 return this.M;
	 }
}
	

