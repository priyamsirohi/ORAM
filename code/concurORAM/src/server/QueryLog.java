package server;

import java.io.Serializable;

public class QueryLog implements Serializable {

	private int[] qlog;
	private int head;
	
	public QueryLog(int num_client){
		qlog = new int[num_client];
	}
	
	public int[] getQLog(){
		return qlog;
	}
	
	public void setQLog(int[] qlog){
				this.qlog = qlog;
	}
	
	public void setEntry(int value){
		qlog[head++] = value;
	}
	
	public void clearQLog(){
		head = 0;
	}
	
	public int getHead(){
		return head;
	}
}


