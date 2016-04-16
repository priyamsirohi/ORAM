package message;

import message.Message.MessageType;
import pdoram.PDOrambucket;

public class PDoram_getBucketsForReshuffle  extends Message{

	private int[] bucket_nums;
	private PDOrambucket[] buckets;
	
	public PDoram_getBucketsForReshuffle(int clientID, int messageID, int num_levels){
		super(clientID,messageID); 
		this.bucket_nums = new int[num_levels];
		this.buckets = new PDOrambucket[num_levels];
	}
	
	public MessageType getMessageType(){return MessageType.PDoram_getBucketsForReshuffle;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	
	public void setBucket_Nums(int[] bucket_nums){ this.bucket_nums = bucket_nums;}
	public int[] getBucket_Nums(){ return this.bucket_nums;}
	public void setBuckets(PDOrambucket[] buckets){this.buckets = buckets;}
	public PDOrambucket[] getBuckets(){return this.buckets;}
	
	}
	

