package server;

public class ClientQueue {

	private int head;
	private int tail;
	private int[] q;
	private int num_clients;
	
	ClientQueue(int num_clients){
		q = new int[num_clients];
		head = 0;
		tail = 0;
		this.num_clients = num_clients;
	}
	
	public void push(int client_id){
		q[head++] = client_id;
		if (head == num_clients)
			head =0;
	}
	
	public int pop(){
		if (tail+1 == num_clients){
			int temp = tail;
			tail = 0;
			return q[temp];
		}
		else 
			return q[tail++];
	}
	
	public int getTop(){
		return q[tail];
	}
}
