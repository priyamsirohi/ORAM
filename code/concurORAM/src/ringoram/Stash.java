package ringoram;

public class Stash {

	private DataBlock[] blocks;
	
	 Stash(){
	    	blocks = new DataBlock[200];
	 }
	 
	 public DataBlock[] getStash(){
	    	return blocks;
	    }
	    
    public void setStash(DataBlock[] blocks){
	    	this.blocks = blocks;
      }

}
