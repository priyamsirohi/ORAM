package Operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import ringoram.*;

public class WriteNode extends Operations {

	public WriteNode(String k){ super(k);}
	
	@Override
	public OperationType getType() { return OperationType.WriteNode; }
	
	public void write_to_file(Node node) throws IOException{
		
		String fp = key;
		File f = new File(fp);
		FileOutputStream fs = new FileOutputStream(f);
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(node);
	}
		
	
}
