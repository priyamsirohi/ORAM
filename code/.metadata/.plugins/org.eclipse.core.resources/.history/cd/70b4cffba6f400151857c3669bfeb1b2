package ringoram;

import java.io.Serializable;

public class Stash implements Serializable {

	
	private DataBlock[] blocks;
	private int[] log_pos_map;
	private int[] phy_pos_map;
	
	 public Stash(){
	    	blocks = new DataBlock[200];
	    	log_pos_map = new int[200];
	    	phy_pos_map = new int[200];
	 }
	 
	 public DataBlock[] getStash(){
	    	return blocks;
	    }
	    
    public void setStash(DataBlock[] blocks){
	    	this.blocks = blocks;
      }

	public int[] getLogPosMap() {
		return log_pos_map;
	}

	public void setLogPosMap(int[] log_pos_map) {
		this.log_pos_map = log_pos_map;
	}

	public int[] getPhyPosMap() {
		return phy_pos_map;
	}

	public void setPhyPosMap(int[] phy_pos_map) {
		this.phy_pos_map = phy_pos_map;
	}
    

}
