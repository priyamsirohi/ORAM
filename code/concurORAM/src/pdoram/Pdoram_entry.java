package pdoram;

import java.io.Serializable;

public class Pdoram_entry implements Serializable {

	private int logical_id;
	private int leaf_id;

	public Pdoram_entry(int log_id, int leaf_id) {this.logical_id = log_id; this.leaf_id = leaf_id;}
	
	public int getLogID() {return this.logical_id;}
	public void setLogID(int log_id) {this.logical_id = log_id;}
	public int getLeafID() {return this.leaf_id;}
	public void setLeafID(int leaf_id) {this.leaf_id = leaf_id;}
}



