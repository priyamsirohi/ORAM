package pdoram;

import java.io.Serializable;

public class Hash implements Serializable {
	
	HashFunctions[] functions;
	
	public Hash(int num_levels){
		functions = new HashFunctions[num_levels];
	}
	
	
	public HashFunctions getHash(int level_num) {return this.functions[level_num];}
	public void setHash(HashFunctions function, int level_num) {this.functions[level_num] = function;}
}

