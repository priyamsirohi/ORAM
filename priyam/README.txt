

1. You need to have 2 files on system: 
	i.  PDoramDB
	ii. PDHashFunctions
	
	
	
	
	
	
	
	
	The Initialisation functions used: (can be used in the PDOramFunctionality constructor)
	 /******CREATE THE FILES********** 
        try {
    		 
	      File file = new File("C:\\Users\\priyam.sirohi\\Documents\\NetBeansProjects\\PDOram\\PDHashFunctions");
	     
              
	      if (file.createNewFile()){
	        System.out.println("File is created!");
	      }else{
	        System.out.println("File already exists.");
	      }
	      
              
              
    	} catch (IOException e) {
	      e.printStackTrace();
    } ********************************/
    
   /*****INIT PDHASHFUNCTIONS FILE******
  Random randomGenerator = new Random();
  FileOutputStream out = new FileOutputStream(PDHashFunctions);
  byte data[] =  new byte[4];
  int temp;
  
  for(int i=0;i<2*levels;i++){
    temp=randomGenerator.nextInt();
    
        data[0] = (byte) (temp >> 24);
        data[1] = (byte) (temp >> 16);
        data[2] = (byte) (temp >> 8);
        data[3] = (byte) (temp);

        System.out.print(temp +" ");
    out.write(data);
  }
    out.close();

        System.out.println();
    
    
    
    FileInputStream in = new FileInputStream(PDHashFunctions);
    
    for(int j=0;j<2*levels;j++){
        temp=0;
         for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            temp += (in.read() & 0x000000FF) << shift;
        }
         System.out.print(temp +" ");
    } 
       
    *************************************/
  /********INIT PDORAMDB FILE******************  
  Random randomGenerator = new Random();
  FileOutputStream out = new FileOutputStream(PDoramDB);
  byte data[] =  new byte[4];
  int temp;
  
  for(int i=0;i<(1<<levels) -1;i++){
    temp=randomGenerator.nextInt();
    
        data[0] = (byte) (temp >> 24);
        data[1] = (byte) (temp >> 16);
        data[2] = (byte) (temp >> 8);
        data[3] = (byte) (temp);

        System.out.print(temp +" ");
    out.write(data);
  }
    out.close();

        System.out.println();
    
    
    
    FileInputStream in = new FileInputStream(PDoramDB);
    
    for(int j=0;j<(1<<levels) -1;j++){
        temp=0;
         for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            temp += (in.read() & 0x000000FF) << shift;
        }
         System.out.print(temp +" ");
    } **********************************************/
  