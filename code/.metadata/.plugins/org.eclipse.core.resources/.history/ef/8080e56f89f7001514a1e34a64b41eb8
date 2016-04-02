package message;

import ringoram.DataBlock;

public class WriteBlock extends Message{

	private DataBlock blk;
		
		public WriteBlock(int MessageID, int ClientID, DataBlock blk) { super(MessageID,ClientID); this.blk = blk;} 
		
		
		@Override
		public MessageType getMessageType(){return MessageType.WriteBlock;}
		public int getMessageID (){ return super.MessageID;}
		public int getClientID() {return super.clientID;}
		public DataBlock getBlk() {return this.blk;}
	
	
}
