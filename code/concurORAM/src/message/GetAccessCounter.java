package message;

public class GetAccessCounter extends Message{

	public int access_counter;
	public int path_counter;
	public int eviction_rate;
	
	public GetAccessCounter(int MessageID, int ClientID) { super(MessageID,ClientID);} 
		
	
	public MessageType getMessageType(){return MessageType.GetAccessCounter;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}

	
}
