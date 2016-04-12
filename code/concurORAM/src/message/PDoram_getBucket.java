package message;

import message.Message.MessageType;
import pdoram.*;

public class PDoram_getBucket extends Message{

	PDOrambucket bucket;
	int level_num;
	int bucket_num;
	
	public PDoram_getBucket(int clientID, int messageID, int level_num, int bucket_num){super(clientID,messageID); this.level_num = level_num; this.bucket_num = bucket_num;}
		
		
	public void setBucket(PDOrambucket bucket){ this.bucket = bucket; }
	public PDOrambucket getBucket(){ return this.bucket;}
	public int getLevel_num() {return this.level_num;}
	public void setLevel_num(int level_num) {this.level_num = level_num;}
	public int getBucket_num() {return this.bucket_num;}
	public void setBucket_num(int bucket_num) {this.bucket_num = bucket_num;}
	
	public MessageType getMessageType(){return MessageType.PDoram_GetBucket;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	
	
}
