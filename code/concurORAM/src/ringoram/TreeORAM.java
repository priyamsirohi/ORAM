package ringoram;

import java.io.IOException;

import Operations.*;

public class TreeORAM {

	protected int bucket_size;
	protected int num_dummy_blocks;
	private int N;
	
	Node root;
	private int depth; 
	private int counter;
	
	public TreeORAM (int N, int bucket_size, int num_dummy_blocks){
		this.N = N;
		this.bucket_size = bucket_size;
		this.depth = (int) (Math.log10(N)/Math.log10(2));
		this.counter = 1;
		this.num_dummy_blocks = num_dummy_blocks;
		this.root = new Node(0,bucket_size,num_dummy_blocks);
		this.root.setNode_depth(0);
		this.root.setNode_id(1);
	}
	
	
	protected void build_tree(Node root) throws IOException{
		
		String key = "Node#" + root.getNode_id();
		WriteNode wn = new WriteNode(key);
		wn.write_to_file(root);
	
	
		if (root.getNode_depth() == this.depth)
			return;
		
		else{
			root.left_id = ++counter;
			root.right_id = ++counter;			
			Node left_child = new Node(root.left_id, this.bucket_size,this.num_dummy_blocks);
			left_child.setNode_depth(root.getNode_depth()+1);
			Node right_child = new Node(root.right_id, this.bucket_size,this.num_dummy_blocks);
			right_child.setNode_depth(root.getNode_depth()+1);
			
			build_tree(left_child);
			build_tree(right_child);
		}
	}
	
	
	/* 
	 * Takes as input a leaf id and 
	 * reads path from root to leaf 
	 * */
	
	protected Node[] read_path(int leaf_id) throws ClassNotFoundException, IOException{
		
		Node[] path;
		path = new Node[depth+1];
		
		path[0] = this.root;
		ReadNode rn;
		String key;
		
		for (int i = 1;i<=depth;i++){
			if (leaf_id <= N/2)
				key = "Node#" + 2*path[i-1].getNode_id();
				
			else
				key = "Node#" + (2*(path[i-1].getNode_id())+1);
		
			rn = new ReadNode(key);
			path[i] = rn.read_from_file();
			N = N/2;
			leaf_id = (int) (leaf_id%(Math.pow(2,depth-i)+1));
			
		}
		
		return path;
	}
	
	
	/* 
	 * Takes as input a path from root to 
	 * leaf and inserts the nodes in the tree 
	 * */
		
	protected void write_path(int leaf_id, Node[] path) throws ClassNotFoundException, IOException{
		
			WriteNode wn;
			String key;
			
			for (int i = 1;i<=depth;i++){
				if (leaf_id <= N/2)
					key = "Node#" + 2*path[i-1].getNode_id();
					
				else
					key = "Node#" + (2*(path[i-1].getNode_id())+1);
			
				wn = new WriteNode(key);
				wn.write_to_file(path[i]);
							
				
				N = N/2;
				leaf_id = (int) (leaf_id%(Math.pow(2,depth-i)+1));
				System.out.println(key);
			}
			
	}
	
	
	
}
