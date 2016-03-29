package message;


public class ClearLogs extends Message{

	
	
	public ClearLogs(int clientID, int messageID){super(clientID,messageID);}
	
	public MessageType getMessageType(){return MessageType.ClearLogs;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	
}
