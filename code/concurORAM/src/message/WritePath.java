package message;

import ringoram.Node;

public class WritePath extends Message{

	private int leaf_id;
	private Node[] nodes;
	
	public WritePath(int MessageID, int ClientID, int leaf_id, Node[] nodes) { super(MessageID,ClientID); this.leaf_id = leaf_id; this.nodes = nodes;} 
	
	
	@Override
	public MessageType getMessageType(){return MessageType.WritePath;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public int getLeaf_ID() {return this.leaf_id;}
	public Node[] getNodes() {return this.nodes;}
		

}