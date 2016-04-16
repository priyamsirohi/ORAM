package Operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Operations.Operations.OperationType;
import pdoram.Pdoram_workspace_partition;

public class WriteWSpace extends Operations {

	public WriteWSpace(String k) {
		super(k);
	}

	@Override
	public OperationType getType() {
		return OperationType.WriteWSpace;
	}

	
	public void write_space(Pdoram_workspace_partition wspace) throws IOException, ClassNotFoundException{
		
		File f = new File(super.key);
		FileOutputStream fs = new FileOutputStream(f);
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(wspace);
		os.close();
		
	}
}
