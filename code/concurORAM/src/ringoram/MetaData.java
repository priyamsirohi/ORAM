package ringoram;

import java.io.Serializable;

public class MetaData implements Serializable {

	
	public int[] dummy_pos;					// dummy positions in the bucket
	boolean[] dummy;					// to check if a block in a bucket is a dummy block
	public int [] log_bucket_pos_map;
	public int [] phy_bucket_pos_map;
	public int bucket_access_counter;
	public int[] next_free;
	public int num_free;
	public int next_free_counter;
	
	MetaData(int bucket_size, int num_dummy_blocks){
		dummy_pos = new int[num_dummy_blocks];
		dummy = new boolean[bucket_size];
		log_bucket_pos_map = new int[bucket_size];
		phy_bucket_pos_map = new int[bucket_size];
		bucket_access_counter = 0;
		next_free = new int[bucket_size];
		num_free = bucket_size - num_dummy_blocks;
		next_free_counter = 0;
	}
	
	
	
}
