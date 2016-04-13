package pdoram;

import java.io.Serializable;

public class HashFunctions implements Serializable {

	int level_num;
	int hash_a;
	int hash_b;
	
	HashFunctions(int level_num, int a, int b){
		this.level_num = level_num;
		this.hash_a = a;
		this.hash_b = b;
	}
	
	public int getHash_a() {return this.hash_a;}
	public int getHash_b() {return this.hash_b;}
	
}
