package Operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import ringoram.Node;


public class ReadNode extends Operations {

public ReadNode(String k){ super(k);}
	
	@Override
	public OperationType getType() { return OperationType.WriteNode; }
	
	public Node read_from_file() throws IOException, ClassNotFoundException{
		
		String fp = key;
		File f = new File(fp);
		FileInputStream fs = new FileInputStream(f);
		ObjectInputStream is = new ObjectInputStream(fs);
		return (Node) is.readObject();
	}
}
