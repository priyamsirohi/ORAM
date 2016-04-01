package message;

import ringoram.Node;
import ringoram.Stash;


public class GetPath extends Message{

	private int leaf_id;
	public Node[] path;
	
	
	public GetPath(int ClientID, int MessageID, int leaf_id) { super(ClientID,MessageID); this.leaf_id = leaf_id;} 
	
	
	@Override
	public MessageType getMessageType(){return MessageType.GetPath;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public int getLeaf_ID() {return this.leaf_id;}
	
}
