package message;

import message.Message.MessageType;
import pdoram.PDOrambucket;

public class PDOram_WriteBucket extends Message {

	PDOrambucket bucket;
	int level_num;
	int bucket_num;
	
	public PDOram_WriteBucket(int ClientID, int MessageID, PDOrambucket bucket, int level_num, int bucket_num) {
		super(ClientID, MessageID);
		this.bucket = bucket;
		this.level_num = level_num;
		this.bucket_num = bucket_num;
	}

	
	public MessageType getMessageType(){return MessageType.PDoram_WriteBucket;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}

	public PDOrambucket getBucket(){
		return this.bucket;
	}
	
	public int getLevel_Num(){
		return this.level_num;
	}
	
	public int getBucket_Num(){
		return this.bucket_num;
	}
}
