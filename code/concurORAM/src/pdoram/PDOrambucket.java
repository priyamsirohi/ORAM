package pdoram;

import java.io.Serializable;
import java.util.ArrayList;

public class PDOrambucket implements Serializable {
	
	Pdoram_entry[] entries;
	
	public PDOrambucket(int bucket_size) {entries = new Pdoram_entry[bucket_size];}
	

	public Pdoram_entry[] getEntries(){
		return this.entries;
	}
	
	public void setEntries(Pdoram_entry[] entries){
		this.entries = entries;
	}
	
	
		
	
	public void setEntryIndex (int index, int log_id, int leaf_id){
		entries[index].setLogID(log_id);
		entries[index].setLeafID(leaf_id);
	}
	
	public Pdoram_entry getEntryIndex(int index){
		return this.entries[index];
	}
}
	

