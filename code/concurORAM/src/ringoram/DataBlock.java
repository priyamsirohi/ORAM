package ringoram;

import java.io.Serializable;

public class DataBlock implements Serializable {

		private long Id;
		private char[] payload;
		
		public DataBlock(){
			this.Id = 0;
			this.payload = new char[4096];
		}
		
		public DataBlock(int ID) {
			this.Id = ID;
			payload = new char[4096];
				
		}
		
		
		
		public long get_id(){
			return this.Id;
		}
		
		public void set_id(long Id){
			this.Id = Id;
		}
		
		public char[] get_payload(){
			return this.payload;
		}
		
		public void set_payload(char[] payload){
			this.payload = payload;
		}
	}


