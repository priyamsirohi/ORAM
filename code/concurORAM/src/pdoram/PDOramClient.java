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


	
	    String PDHashFunctions = "/home/nsac/anrin/ORAM/priyam/PDHashFunctions";
	   // String PDoramDB = "/home/nsac/anrin/ORAM/priyam/PDoramDB/";    
	    int levels, PDoramDBSize, M;
	    ObjectInputStream is;
	    ObjectOutputStream os;
	    PDOrambucket[] buckets;
	    int N;
	    
	    /********
	     * @param N, m
	     * @throws FileNotFoundException
	     * @throws IOException 
	     * Input : Number of entries in the mapping(N), size of each bucket(m)
	     * Output: initializes PDHashFunctions file with random values
	     * and PDoramDB file to contain all values as -1 indicating invalid entry.
	     ********/
	  
	    public PDOramClient(int N, int m) throws FileNotFoundException, IOException{

	      levels = (int)Math.ceil(Math.log(N) / Math.log(2)) + 1;
	      PDoramDBSize = N*m*2; 
	      M=m;
	      int temp;
	      buckets = new PDOrambucket[N];  
	      this.N = N;
	      Random randomGenerator = new Random();
	      File f = new File(PDHashFunctions);
	      FileOutputStream out = new FileOutputStream(f);
	      ObjectOutputStream oos = new ObjectOutputStream(out);	      
	      
	      Hash hash = new Hash(levels);
	      
	    
	      for(int i=0;i<levels;i++){
	    	  HashFunctions function = new HashFunctions(i, Math.abs(randomGenerator.nextInt()), Math.abs(randomGenerator.nextInt()));
	          hash.setHash(function, i);
	      }
	      oos.writeObject(hash);	
	      oos.close();
	      out.close();
	     }
	    
	
	    /************
	     * build(ArrayList list)
	     * All the data is populated in the last level for efficiency.
	     * No collisions are assumed (collisions inside bucket are allowed).
	    ************/
	    
	    
	  
	    
	   public void build(ArrayList list, ObjectInputStream is, ObjectOutputStream os) throws FileNotFoundException, IOException, ClassNotFoundException{
	       
	       
 	        File f = new File(PDHashFunctions);
 	        FileInputStream inhash = new FileInputStream(f);
	        ObjectInputStream ois = new ObjectInputStream(inhash);
 	       
	        Hash hash_arr = (Hash) ois.readObject();
	       
	        int entry_counter = 1;
		        
	        
	        for (int i = 1; i<levels;i++){
	        	int hash_a = hash_arr.getHash(i).hash_a;
	        	int hash_b = hash_arr.getHash(i).hash_b;
	 	       while(entry_counter < N){
	        	for (int j = 0; j< Math.pow(2, i);j++){
	        		int hash = Math.abs(((hash_a * entry_counter) + hash_b)%((int)Math.pow(2,i)));
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
	        	
	        }
	        }  
	        ois.close();
	        inhash.close();
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
	

