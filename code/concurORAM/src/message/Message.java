package message;

import java.io.Serializable;

public abstract class Message implements Serializable {

	public enum MessageType {Ping, GetMetadata, GetBlocks, GetNodes, WriteBlock, WritePath, GetResultLog, GetStash};
	protected int MessageID;
	protected int clientID;
	
	Message(int MessageID, int ClientID){
		this.MessageID = MessageID;
		this.clientID = ClientID;
	}
	
	public abstract MessageType getMessageType(); 

}
