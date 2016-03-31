package message;


public class AccessComplete extends Message{

	public AccessComplete(int clientID, int messageID){super(clientID,messageID);}
		
	public MessageType getMessageType(){return MessageType.AccessComplete;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	
}
