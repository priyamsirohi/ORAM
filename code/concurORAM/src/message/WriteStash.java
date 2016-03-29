package message;

import ringoram.Stash;
public class WriteStash extends Message{

	public Stash stash;
	public WriteStash(int clientID, int messageID, Stash stash) {super(clientID,messageID); this.stash = stash;}

	
	public MessageType getMessageType(){return MessageType.WriteStash;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}

}
