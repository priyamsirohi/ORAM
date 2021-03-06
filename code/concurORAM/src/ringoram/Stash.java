package ringoram;

import java.io.Serializable;

public class Stash implements Serializable {

	
	public DataBlock[] blocks;
	public int[] phy_pos_map;
	public int num_of_elements;
	
	 public Stash(){
	    	blocks = new DataBlock[200];
	    	
	    	phy_pos_map = new int[200];
	    	num_of_elements = 0;
	 }
	 
	 public DataBlock[] getStash(){
	    	return blocks;
	    }
	    
    public void setStash(DataBlock[] blocks){
	    	this.blocks = blocks;
      }

	
	public int[] getPhyPosMap() {
		return phy_pos_map;
	}

	public void setPhyPosMap(int[] phy_pos_map) {
		this.phy_pos_map = phy_pos_map;
	}
    

}
