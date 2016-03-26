package ringoram;

import java.io.Serializable;
import java.util.*;

public class bucket implements Serializable {

	public int num_dummy_blocks;			// number of dummy blocks in this bucket
	public int bucket_access_counter;		// number of times this bucket has been accessed
	public int[] dummy_pos;					// dummy positions in the bucket
	boolean[] dummy;					// to check if a block in a bucket is a dummy block
	public int bucket_size;					
	
	public Random rn; 
	private DataBlock[] blocks;
	private int [] bucket_pos_map;
	
	
	bucket(int bucket_size, int num_dummy_blocks){
		
		this.bucket_size = bucket_size;
		this.num_dummy_blocks = num_dummy_blocks;
		this.bucket_access_counter = 0;
		this.blocks = new DataBlock[bucket_size];
		this.bucket_pos_map = new int[bucket_size];
		
		for (int i = 0;i<bucket_size;i++)
			blocks[i] = new DataBlock(i);
		
		/* Dummy block assignment */
		
		rn = new Random();
		rn.setSeed(12345678);
		
		this.dummy_pos = new int[num_dummy_blocks];
		this.dummy = new boolean[bucket_size];
		
	
		this.num_dummy_blocks = num_dummy_blocks;
		int j;
		
		for (int i = 0; i< num_dummy_blocks;i++) {
			while ((this.dummy[j = rn.nextInt(num_dummy_blocks+1)]));
				this.dummy_pos[i] = j;
				this.dummy[j] = true;
		}
						
	}
	
	public void setbucket(DataBlock[] blocks){
		for (int i=0; i<this.bucket_size;i++){
			if (!(this.dummy[i]))
				this.blocks[i] = blocks[i];
		}
	}

	
	public DataBlock[] getDataBlocks(){
		return blocks;
	}
	
	
	public int getBucketAccessCounter(){
		return bucket_access_counter;
	}
	
	public void setBucketAccessCounter(int count){
		bucket_access_counter = count;
	}
	
	public void incrementBucketAccessCounter(){
		bucket_access_counter++;
	}
	
	
	public int[] getDummyPos(){
		return dummy_pos;
	}
	
	public int[] getBucketPosMap(){
		return bucket_pos_map;
	}
	
	public void setBucketPosMap(int[] arr){
		bucket_pos_map = arr;
	}
	
	//TODO: Implement Bucket Reshuffle
	
}
