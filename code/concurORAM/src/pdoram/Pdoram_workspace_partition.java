package pdoram;

import java.io.Serializable;

public class Pdoram_workspace_partition implements Serializable {

	private int level_num;
	private Pdoram_entry[] work_area;
	
		
	public Pdoram_workspace_partition(int num_entries, int level_num) {
		this.level_num = level_num;
		work_area = new Pdoram_entry[num_entries];
	}

	public Pdoram_entry getEntry(int entry_num) {
		return this.work_area[entry_num];
		} 
	
	public void setEntry(int index, Pdoram_entry entry){
		work_area[index] = entry;
	}
	
	
}
