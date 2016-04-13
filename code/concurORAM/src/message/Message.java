package message;

import java.io.Serializable;


public abstract class Message implements Serializable {

	public enum MessageType{Ping, GetMetadata, GetBlocksFromPath, GetPath, WriteBlock, WritePath, GetResultLogs, GetStash, WriteStash, ClearLogs, GetAccessCounter, AccessComplete, GetQLog, PDoram_GetBucket, PDoram_WriteBucket, SetPMLog};
	public int MessageID;
	public int clientID;
	
	Message(int ClientID, int MessageID){
		this.MessageID = MessageID;
		this.clientID = ClientID;
	}
	
	public abstract MessageType getMessageType(); 
		

}
