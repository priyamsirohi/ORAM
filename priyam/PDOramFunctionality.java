/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdoram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author priyam.sirohi
 */
public class PDOramFunctionality {
    String PDHashFunctions = "C:\\Users\\priyam.sirohi\\Documents\\NetBeansProjects\\PDOram\\PDHashFunctions";
    String PDoramDB = "C:\\Users\\priyam.sirohi\\Documents\\NetBeansProjects\\PDOram\\PDoramDB";    
    int levels, PDoramDBSize, M;
    /********
     * @param N, m
     * @throws FileNotFoundException
     * @throws IOException 
     * Input : Number of entries in the mapping(N), size of each bucket(m)
     * Output: initializes PDHashFunctions file with random values
     * and PDoramDB file to contain all values as -1 indicating invalid entry.
     ********/
    PDOramFunctionality(int N, int m) throws FileNotFoundException, IOException{

      levels = (int)(Math.log(N) / Math.log(2));
      PDoramDBSize = ((int)(Math.pow(2,levels)-1)*m*2); 
      M=m;
      int temp;
        
      Random randomGenerator = new Random();
      FileOutputStream out = new FileOutputStream(PDHashFunctions);
      FileOutputStream out1 = new FileOutputStream(PDoramDB);
      byte data[] =  new byte[4];
      
      temp = -1;
     for(int i=0;i<PDoramDBSize;i++){
            
        data[0] = (byte) (temp >> 24);
        data[1] = (byte) (temp >> 16);
        data[2] = (byte) (temp >> 8);
        data[3] = (byte) (temp);

        out1.write(data);
        }
     out1.close();
        
  
    for(int i=0;i<2*levels;i++){
        temp=Math.abs(randomGenerator.nextInt());

            data[0] = (byte) (temp >> 24);
            data[1] = (byte) (temp >> 16);
            data[2] = (byte) (temp >> 8);
            data[3] = (byte) (temp);
     
        out.write(data);
      }
    
    
}
    /************
     * build(ArrayList list)
     * All the data is populated in the last level for efficiency.
     * No collisions are assumed (collisions inside bucket are allowed).
    ************/
    
    void build(ArrayList list) throws FileNotFoundException, IOException{
        int a=-1,b=-1, temp;
        byte data[] =  new byte[4];
        
        FileInputStream inhash = new FileInputStream(PDHashFunctions);
        inhash.skip(8*(levels-1));
        for(int j=0;j<2;j++){
            temp=0;
             for (int i = 0; i < 4; i++) {
                int shift = (4 - 1 - i) * 8;
                temp += (inhash.read() & 0x000000FF) << shift;
            }
            if(a==-1) a = temp;
            else b = temp;
        }
        
        
         
        
        for(int i=0; i<list.size();i++){
         int writeOffset = 0;
         RandomAccessFile in = new RandomAccessFile(PDoramDB, "rw");
         in.skipBytes((int)(Math.pow(2,levels-1)-1)*M*4); //now I am at the starting of the last level
         
         
         ArrayList<Integer> list1 = (ArrayList<Integer>) list.get(i);
         int id = list1.get(0);
         int val = list1.get(1);
         
         int hash = (a*id + b)%((int)Math.pow(2,levels-1)); // we have to put the data into hashth bucket.
         
         // go to the starting of hashth bucket
         in.skipBytes(hash*M*8);
         
         
         for(int j=0;j<M;j++){
             temp=0;
             for (int k = 0; k < 4; k++) {
                int shift = (4 - 1 - k) * 8;
                temp += (in.read() & 0x000000FF) << shift;
            }
             if(temp == -1) break;  
             else {
                 in.skipBytes(4);
             }
         }
         writeOffset = (int) (in.getFilePointer()-4);
         in.close();
         
          RandomAccessFile out = new RandomAccessFile(PDoramDB, "rw");
          out.skipBytes(writeOffset);
          
         data[0] = (byte) (id >> 24);
            data[1] = (byte) (id >> 16);
            data[2] = (byte) (id >> 8);
            data[3] = (byte) (id);
     
        out.write(data);
        
        data[0] = (byte) (val >> 24);
            data[1] = (byte) (val >> 16);
            data[2] = (byte) (val >> 8);
            data[3] = (byte) (val);
     
        out.write(data);
         out.close();
        }
        FileInputStream in = new FileInputStream(PDoramDB);
    
    for(int j=0;j<PDoramDBSize;j++){
        temp=0;
         for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            temp += (in.read() & 0x000000FF) << shift;
        }
         System.out.print(temp +" ");
    }
    in.close();
        
    }
    /*
    ArrayList<Integer> PDOramRead(int id) throws FileNotFoundException, IOException{
        
    ArrayList<Integer> ret = new ArrayList<Integer>();
    FileInputStream inHash = new FileInputStream(PDHashFunctions);
    FileInputStream inDB = new FileInputStream(PDoramDB);
    int a, b, skipBefore, skipAfter, hash, value;
    
    for(int i=0; i< levels; i++){
       a = 0;
       b = 0;
       for (int k = 0; k < 4; k++) {
            int shift = (4 - 1 - k) * 8;
            a += (inHash.read() & 0x000000FF) << shift;
        }
       for (int k = 0; k < 4; k++) {
            int shift = (4 - 1 - k) * 8;
            b += (inHash.read() & 0x000000FF) << shift;
        }
       
       hash = Math.abs((a*id + b)%(1<<i));
       skipBefore = 4*hash;
       skipAfter = 4*((1<<i) -(hash+1));
       inDB.skip(skipBefore);
       value = 0;
       
       for (int k = 0; k < 4; k++) {
            int shift = (4 - 1 - k) * 8;
            value += (inDB.read() & 0x000000FF) << shift;
        }
       
       ret.add(value);
       inDB.skip(skipAfter);
    }
    
    inHash.close();
    inDB.close();
    
    return ret;
    }*/
}