package message;

import ringoram.DataResultLog;


public class GetResultLogs extends Message{
	
	public DataResultLog drs;
		
	public GetResultLogs(int ClientID, int MessageID) { super(ClientID,MessageID);} 
	
	
	public MessageType getMessageType(){return MessageType.GetResultLogs;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	
	
	

}

	