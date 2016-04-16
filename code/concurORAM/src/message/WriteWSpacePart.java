package message;

import message.Message;
import pdoram.Pdoram_workspace_partition;

public class WriteWSpacePart extends Message{

	Pdoram_workspace_partition[] wspace_part;
	
		
	public WriteWSpacePart(int ClientID, int MessageID, int size) {
		super(ClientID, MessageID);
		this.wspace_part = new Pdoram_workspace_partition[size];
		
	}

	public Pdoram_workspace_partition[] getWSpacePart(){
		return this.wspace_part;
	}
	
	public void setWSpacePart(Pdoram_workspace_partition[] wspace_part){
		this.wspace_part = wspace_part;
	}
	
	public void setWSpacePartIndex(int index, Pdoram_workspace_partition wspace_part) { 
		this.wspace_part[index] = wspace_part;
	}

	public Pdoram_workspace_partition getWSpacePartIndex(int index) { 
		return this.wspace_part[index];
	}
	@Override
	public MessageType getMessageType() { return MessageType.WriteWSpacePart;}
	}
	

