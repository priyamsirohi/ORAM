package message;

import ringoram.DataResultLog;


public class GetResultLog extends Message{
	
	public DataResultLog rs; 
	
	GetResultLog(int MessageID, int ClientID) { super(MessageID,ClientID);} 
	
	
	public MessageType getMessageType(){return MessageType.GetResultLog;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	
}
