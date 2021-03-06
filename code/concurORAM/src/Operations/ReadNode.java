package Operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import ringoram.Node;


public class ReadNode extends Operations {

public ReadNode(String k){ super(k);}
	
	@Override
	public OperationType getType() { return OperationType.ReadNode; }
	
	public Node read_from_file() throws IOException, ClassNotFoundException{
		
		
		File f = new File(super.key);
		FileInputStream fs = new FileInputStream(f);
		ObjectInputStream is = new ObjectInputStream(fs);
		Node node = (Node) is.readObject();
		is.close();
		return node;
		
	}
}
