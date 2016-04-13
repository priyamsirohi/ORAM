package pdoram;

import java.io.File;
import Operations.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import pdoram.*;

public class PDOramServer {

	 String PDoramDB = "/home/nsac/anrin/ORAM/code/concurORAM/PDoramDb/";
	 int levels;
	 int PDoramDBSize;
	 int bucket_size;
	  
	 public PDOramServer(int N, int m) throws FileNotFoundException, IOException{
	      levels = (int)(Math.log(N) / Math.log(2))+1;
	      PDoramDBSize = N*m*2; 
	      bucket_size = m;
	      PDOram_init();
	       	
	 }
	 
	
	    
	    public void PDOram_init() throws IOException{
	    
	    	String key;
	    	int[] arr = new int[this.bucket_size];
	    	for (int i = 0; i < this.bucket_size; i++)
	    		arr[i] = -1;
	    	
	    	for (int i = 0; i<this.levels;i++){
	        	for (int j = 0; j< Math.pow(2, i);j++){
	        	key = PDoramDB + "bucket#" + i+ "_" + j;	
	        	WritePDBucket write_bucket = new WritePDBucket(key);
	        	PDOrambucket bucket = new PDOrambucket(this.bucket_size);
	        	bucket.setMap(arr);
	        	write_bucket.write_to_file(bucket);	
	        	}
	    	
	    }
	}
	    
	    
	    public PDOrambucket PDoramRead(int level_num, int bucket_num) throws IOException, ClassNotFoundException{
	    	String key = PDoramDB + "bucket#" + level_num+"_"+ bucket_num;
	    	ReadPDBucket read_bucket = new ReadPDBucket(key);
	    	
	    	
	    	return read_bucket.read_from_file();
	    }
	 
	    
	    
	   public void PDOramWriteBucket(PDOrambucket bucket, int level_num, int bucket_num) throws IOException{
		   
		   String key = PDoramDB +"bucket#" + level_num+ "_" + bucket_num;	
		   WritePDBucket write_bucket = new WritePDBucket(key.trim());
		   write_bucket.write_to_file(bucket);
	   }
	   
	   public int getLevels(){
		   return this.levels;
	   }
}