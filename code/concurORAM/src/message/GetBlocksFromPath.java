package message;

import ringoram.DataBlock;
import ringoram.Stash;

public class GetBlocksFromPath extends Message{

public int leaf_id;
public int[] blk_num;
public DataBlock[] blocks; 	//Return message from Server
public Stash stash;

	public GetBlocksFromPath(int ClientID, int MessageID, int leaf_id, int depth) { super(ClientID,MessageID); 
	this.leaf_id = leaf_id; this.blk_num = new int[depth];stash = new Stash();} 
	
	
	@Override
	public MessageType getMessageType(){return MessageType.GetBlocksFromPath;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public int getLeaf_ID() {return this.leaf_id;}
	public int[] getBlkNum() {return this.blk_num;}
	
}
