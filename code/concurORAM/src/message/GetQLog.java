package message;

import server.QueryLog;

public class GetQLog extends Message{
	
	QueryLog qlog;
	public int access_counter;
	public int eviction_rate;
	public int path_counter;
	public int blk_id;
	public int your_access;
	
	public GetQLog(int ClientID,int MessageID, int blk_id) {super(ClientID,MessageID); this.blk_id = blk_id;}
	public GetQLog(int ClientID,int MessageID,QueryLog qlog) {super(ClientID,MessageID);this.qlog = new QueryLog(qlog.getQLog().length);}
	
	public MessageType getMessageType(){return MessageType.GetQLog;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public QueryLog getQLog() {return qlog;}

}
