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
		System.out.println("push"+head);
		System.out.println("push"+tail);
		q[head++] = client_id;
		if (head == num_clients)
			head =0;
	}
	
	public int pop(){
		System.out.println("pop"+head);
		System.out.println("pop"+tail);

		
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
	
	public int[] getQ(){
		return q;
	}
	
	public void reset(){
		head = 0;
		tail = 0;
	}
	
	public int getNumOfElements(){
		if (head >= tail)
			return (head - tail);
		else 
			return (head + num_clients - tail);
	}
}
