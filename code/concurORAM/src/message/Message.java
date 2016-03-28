package message;

import java.io.Serializable;

public abstract class Message implements Serializable {

	public enum MessageType {Ping, GetMetadata, GetBlocksFromPath, GetPath, WriteBlock, WritePath, GetResultLog, GetStash};
	public int MessageID;
	public int clientID;
	
	Message(int MessageID, int ClientID){
		this.MessageID = MessageID;
		this.clientID = ClientID;
	}
	
	public abstract MessageType getMessageType(); 
		

}
