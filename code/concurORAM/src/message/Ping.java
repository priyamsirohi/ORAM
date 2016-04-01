package message;
import server.QueryLog;

public class Ping extends Message {

	
	public Ping(int ClientID, int MessageID) { super(ClientID,MessageID);} 
	
	@Override
	public MessageType getMessageType(){return MessageType.Ping;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
}
