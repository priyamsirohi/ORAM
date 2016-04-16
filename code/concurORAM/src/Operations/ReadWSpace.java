package Operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import ringoram.Node;
import pdoram.*;

public class ReadWSpace extends Operations{

	public ReadWSpace(String k) {
		super(k);
	}

	@Override
	public OperationType getType() {
		return OperationType.ReadWSpace;
	}

	
	public Pdoram_workspace_partition read_space() throws IOException, ClassNotFoundException{
		
		File f = new File(super.key);
		FileInputStream fs = new FileInputStream(f);
		ObjectInputStream is = new ObjectInputStream(fs);
		Pdoram_workspace_partition wspace = (Pdoram_workspace_partition) is.readObject();
		is.close();
		return wspace;
	}
	
}
