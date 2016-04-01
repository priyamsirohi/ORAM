package message;

public class GetAccessCounter extends Message{

	public int access_counter;
	public int path_counter;
	public int eviction_rate;
	
	public GetAccessCounter(int ClientID, int MessageID) { super(ClientID,MessageID);} 
		
	
	public MessageType getMessageType(){return MessageType.GetAccessCounter;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}

	
}
