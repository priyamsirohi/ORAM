package message;

import message.Message.MessageType;
import ringoram.DataBlock;

public class SetPMLog extends Message{
	 
	int entry;
	int val;
	
	public SetPMLog(int clientID, int messageID, int entry, int val){ super(clientID,messageID); this.entry = entry; this.val = val;}   

	
	public MessageType getMessageType(){return MessageType.SetPMLog;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public int getEntry() {return this.entry;}
	public int getVal() {return this.val;}
	
}
