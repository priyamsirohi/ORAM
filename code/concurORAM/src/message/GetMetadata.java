package message;

import ringoram.MetaData;

public class GetMetadata extends Message{

	private int leaf_id;
	public MetaData[] metadata;
	
	
	public GetMetadata(int MessageID, int ClientID, int leaf_id) { super(MessageID,ClientID); this.leaf_id = leaf_id;} 
	
	
	@Override
	public MessageType getMessageType(){return MessageType.GetMetadata;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public int getLeaf_ID() {return this.leaf_id;}
	
}
