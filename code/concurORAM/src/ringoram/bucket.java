package ringoram;

import java.io.Serializable;
import java.util.*;

public class bucket implements Serializable {

	
	protected int num_dummy_blocks;			// number of dummy blocks in this bucket
	protected int bucket_size;					
	protected Random rn; 
	private DataBlock[] blocks;
	protected MetaData md;
	
	
	bucket(int bucket_size, int num_dummy_blocks){
		this.bucket_size = bucket_size;
		this.num_dummy_blocks = num_dummy_blocks;
		this.blocks = new DataBlock[bucket_size];
		this.md = new MetaData(bucket_size,num_dummy_blocks);
		
		for (int i = 0;i<bucket_size;i++)
			blocks[i] = new DataBlock(i);
		
		/* Dummy block assignment */
		
		rn = new Random();
		rn.setSeed(12345678);
					
	
		this.num_dummy_blocks = num_dummy_blocks;
		int j;
		
		for (int i = 0; i< num_dummy_blocks;i++) {
			while ((this.md.dummy[j = rn.nextInt(num_dummy_blocks)]));
				this.md.dummy_pos[i] = j;
				this.md.dummy[j] = true;
			}
	
	
		int counter = 0;
		for (int i=0; i<this.bucket_size;i++){
			if (!(this.md.dummy[i])){
				this.md.next_free[counter] = i;
				counter++;
			}
			
			}
	}
	
	
	
	
	public DataBlock[] getDataBlocks(){return blocks;}
	public void setDataBlock(int index, DataBlock block) {blocks[index] = block;}
	
	public MetaData getMetaData(){return this.md;}
	
	
	
	
}
