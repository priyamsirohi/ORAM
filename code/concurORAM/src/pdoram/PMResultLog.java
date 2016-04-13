package pdoram;

import java.util.ArrayList;

public class PMResultLog {

	int[] map;
	int[] pm_entry;
	int head;
	
	public PMResultLog(int size){
		map = new int[size];
		pm_entry = new int[size];
		head = 0;
	}
	
	
	public int[] getMap() {return this.map;}
	public void push(int entry, int val) {this.map[head] = entry; this.pm_entry[head++] = val;}
	public void clear(){
		head = 0;
	}
		
}
