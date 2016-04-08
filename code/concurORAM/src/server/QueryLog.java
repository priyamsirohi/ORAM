package server;

import java.io.Serializable;

public class QueryLog implements Serializable {

	private int[] qlog;
	private int[] client_num;
	private int head;
	boolean [] complete;
	
	public QueryLog(int num_client){
		qlog = new int[num_client];
		complete = new boolean[num_client];
		client_num = new int[num_client];
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
	
	
	public int getIndex(int client_id){
		int index = 0;
		for (int i = 0;i<client_num.length;i++){
			if (client_num[i] == client_id)
				index = i;
		}
			return index;
	}
	
	public boolean isComplete(int index){
		
		for (int i = 0; i < index; i++){
			if (!(complete[i]))
					return false;
		}
		return true;
	}
	
	public void setComplete(int client_ID){
		int index = getIndex(client_ID);
		if (!complete[index])
			complete[index] = true;
	}
}

