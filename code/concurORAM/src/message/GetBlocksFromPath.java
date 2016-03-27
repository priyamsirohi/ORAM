package message;

import ringoram.DataBlock;

public class GetBlocksFromPath extends Message{

private int leaf_id;
private int[] blk_num;
public DataBlock[] blocks; 	//Return message from Server

	GetBlocksFromPath(int MessageID, int ClientID, int leaf_id, int depth) { super(MessageID,ClientID); this.leaf_id = leaf_id; this.blk_num = new int[depth];} 
	
	
	@Override
	public MessageType getMessageType(){return MessageType.GetBlocks;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public int getLeaf_ID() {return this.leaf_id;}
	public int[] getBlkNum() {return this.blk_num;}
	
}
