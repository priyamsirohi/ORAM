package Operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import pdoram.PDOrambucket;
import Operations.Operations.OperationType;

public class ReadPDBucket extends Operations {

	
public ReadPDBucket(String k){ super(k);}
	
	@Override
	public OperationType getType() { return OperationType.ReadPDBucket; }
	
	public PDOrambucket read_from_file() throws IOException, ClassNotFoundException{
		
		
		File f = new File(super.key);
		FileInputStream fs = new FileInputStream(f);
		ObjectInputStream is = new ObjectInputStream(fs);
		PDOrambucket bucket = (PDOrambucket) is.readObject();
		is.close();
				return bucket;
	}
	
}
