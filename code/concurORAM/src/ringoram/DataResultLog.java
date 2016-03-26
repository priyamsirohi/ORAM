package ringoram;

public class DataResultLog {

	private DataBlock[] blocks;
	private int head;

    DataResultLog(){
    	blocks = new DataBlock[32];
    	head = 0;
    }
    

    public DataBlock[] getDataResultLog(){
    	return blocks;
    }
    
    public void setandincDataResultLog(DataBlock node){
    	blocks[head++] = node;
      }
    
    public void clearDataResultLog(){
    	head = 0;
    }
}
