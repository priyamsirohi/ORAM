package message;


public class Ping extends Message {

	public Ping(int MessageID, int ClientID) { super(MessageID,ClientID);} 
	
	@Override
	public MessageType getMessageType(){return MessageType.Ping;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
}
