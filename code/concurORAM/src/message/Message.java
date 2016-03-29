package message;

import java.io.Serializable;

public abstract class Message implements Serializable {

	public enum MessageType {Ping, GetMetadata, GetBlocksFromPath, GetPath, WriteBlock, WritePath, GetResultLogs, GetStash, WriteStash, ClearLogs, GetAccessCounter};
	public int MessageID;
	public int clientID;
	
	Message(int MessageID, int ClientID){
		this.MessageID = MessageID;
		this.clientID = ClientID;
	}
	
	public abstract MessageType getMessageType(); 
		

}
