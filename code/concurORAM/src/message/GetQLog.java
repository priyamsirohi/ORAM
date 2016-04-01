package message;

import server.QueryLog;

public class GetQLog extends Message{
	
	QueryLog qlog;
	public int access_counter;
	public int eviction_rate;
	public int path_counter;
	
	public GetQLog(int ClientID,int MessageID,QueryLog qlog) {super(ClientID,MessageID);this.qlog = new QueryLog(qlog.getQLog().length);}
	
	public MessageType getMessageType(){return MessageType.GetQLog;}
	public int getMessageID (){ return super.MessageID;}
	public int getClientID() {return super.clientID;}
	public QueryLog getQLog() {return qlog;}

}
