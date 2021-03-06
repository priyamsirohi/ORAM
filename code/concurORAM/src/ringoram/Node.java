package ringoram;

import java.io.Serializable;

public class Node implements Serializable{
	

	protected int left_id;
	protected int right_id;
	
	private int node_id;
	private bucket bkt;
	private int depth;
	
	
	Node(int node_id, int bucket_size, int num_dummy_blocks){
		this.setNode_id(node_id);
		bkt = new bucket(bucket_size,num_dummy_blocks);
	}


	public int getNode_id() {
		return node_id;
	}


	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}



	public int getNode_depth() {
		return depth;
	}


	public void setNode_depth(int depth) {
		this.depth = depth;
	}
	
	public bucket getBucket(){
		return bkt;
	}

	
}
