package Operations;

public abstract class Operations
{
	public enum OperationType {ReadNode, WriteNode, GetData, PutData, WritePDBucket, ReadPDBucket, ReadWSpace, WriteWSpace };
	
		
	protected String key = null;
	
	 public Operations(String k)
	{
		key = k;
	}
	
	public String getKey() { return key; }
	
	public abstract OperationType getType();
}