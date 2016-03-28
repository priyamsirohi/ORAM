package ringoram;

public class DataResultLog {

	private DataBlock[] blocks;
	private int head;

    public DataResultLog(int num_of_access){
    	blocks = new DataBlock[num_of_access];
    	head = 0;
    }
    

    public DataBlock[] getDataResultLog(){
    	return blocks;
    }
    
    public void setandincDataResultLog(DataBlock block){
    	blocks[head++] = block;
      }
    
    public void clearDataResultLog(){
    	head = 0;
    }
}