package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import ringoram.*;
import message.*;
import message.Message.MessageType;

public class Server {

	private Stash stash;
	private TreeORAM tree;
	private ServerSocket serverListener;
	private Socket server;
	private int portnum;
	
	public Server(int N, int bucket_size, int num_dummy_blocks, int portnum) throws UnknownHostException, IOException{
		this.stash = new Stash();
		this.tree = new TreeORAM(N,bucket_size,num_dummy_blocks);
		this.portnum = portnum;
		this.serverListener = new ServerSocket(portnum);
		
	}
	
	public void run() throws IOException, ClassNotFoundException, InterruptedException{
		this.server = serverListener.accept();
		ObjectInputStream is = new ObjectInputStream(server.getInputStream());
		ObjectOutputStream os = new ObjectOutputStream(server.getOutputStream());
		while(true){
		System.out.println(is);
		Message ms = (Message) is.readObject();
		System.out.println(ms.getMessageType());
		if (ms.getMessageType().compareTo(MessageType.Ping) == 0){
			Ping ping = new Ping(0,0);
			os.writeObject(ping);
			os.flush();
		}
			
			
		
	}
}
}
