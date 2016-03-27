package client;

import ringoram.*;
import java.net.Socket;

public class Client {

	private Socket clientListener;
    private String clientName;
    private String result="";
    private int num;
    private int portNum;
    private String hostname;
  
    
    private PositionMap pm;
    
    public Client(int portnum, String host){
        this.portNum=portnum;
        this.hostname=host;
        pm = new PositionMap(8);
    }
	
    
     
    public void writeToSocket(){
        try{
                
                clientListener = new Socket(hostname,portNum);
                
		 DataOutputStream output=new DataOutputStream(clientListener.getOutputStream());
                 BufferedReader input = new BufferedReader(new InputStreamReader(clientListener.getInputStream()));
                 //System.out.println("Client thread writing");
                 debug.print_debug(1,"Client thread writing");
                 String sendStream = clientName+";"+num+'\n';
                 //System.out.println(sendStream);
                 debug.print_debug(1,sendStream);
		 output.writeBytes(sendStream);
                 output.flush();
                 result=result+input.readLine()+"\n";
		
	 }catch(IOException E){System.err.println("Client listener opening:"+E.toString());debug.print_debug(1,"Client listener opening:"+E.toString());System.exit(1);}
	 finally{try{
                    clientListener.close();
                }
                catch(IOException E){
                    System.err.println("Client listener closing:"+E.toString());
                    debug.print_debug(1,"Client listener closing:"+E.toString());
                    System.exit(1);
                }
        }
    }
	
}
