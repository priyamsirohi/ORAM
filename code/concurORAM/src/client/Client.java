package client;

import ringoram.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import message.*;
import message.Message.MessageType;


public class Client {

	private Socket clientListener;
    private int clientID;
    private int messageID;
     private int portNum;
    private String hostname;
     
    private PositionMap pm;
    
    public Client(int portnum, String host,int clientID) throws UnknownHostException, IOException{
        this.portNum=portnum;
        this.hostname=host;
        pm = new PositionMap(8);
        this.messageID = 0;
        this.clientID = clientID;
        clientListener = new Socket(hostname,portNum);
    }
	
    
   
    public void writeToSocket() throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException{
    	ObjectOutputStream os = new ObjectOutputStream(clientListener.getOutputStream());		
    	ObjectInputStream is = new ObjectInputStream(clientListener.getInputStream());
    	while(true){
    	 		System.out.println("CLIENT: Writing to socket");               
                
                               	
                Ping ping = new Ping(clientID, messageID++);
                      
                os.writeObject(ping);
                os.flush();
                System.out.println("CLIENT: Writing done");
                Message ms = (Message) is.readObject();
                if (ms.getMessageType().compareTo(MessageType.Ping) != 0)
                	Thread.sleep(5000); 	
    			}
	 }
        
    }
	

