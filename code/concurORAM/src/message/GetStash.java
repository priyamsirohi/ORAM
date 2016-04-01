package message;

import ringoram.Stash;


public class GetStash extends Message {

	public Stash stash;
	
	GetStash(int ClientID, int MessageID) { super(ClientID,MessageID);} 
	
	
	public MessageType getMessageType(){return MessageType.GetStash;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}

	
}
