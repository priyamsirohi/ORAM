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
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author priyam.sirohi
 */
public class PDOramFunctionality {
    String PDHashFunctions = "C:\\Users\\priyam.sirohi\\Documents\\NetBeansProjects\\PDOram\\PDHashFunctions";
    String PDoramDB = "C:\\Users\\priyam.sirohi\\Documents\\NetBeansProjects\\PDOram\\PDoramDB";
    int levels = 6;
    
    PDOramFunctionality() throws FileNotFoundException, IOException{}
    
    
    /********
     * @param lev
     * @throws FileNotFoundException
     * @throws IOException 
     * Takes input as number of levels in the PDORAM, writes PDHashFunctions and PDoramDB files
     * with random values, created for testing purposes.
     */
    PDOramFunctionality(int lev) throws FileNotFoundException, IOException{
      levels = lev;
      Random randomGenerator = new Random();
      FileOutputStream out = new FileOutputStream(PDHashFunctions);
      byte data[] =  new byte[4];
      int temp;
      
    for(int i=0;i<2*levels;i++){
        temp=Math.abs(randomGenerator.nextInt());

            data[0] = (byte) (temp >> 24);
            data[1] = (byte) (temp >> 16);
            data[2] = (byte) (temp >> 8);
            data[3] = (byte) (temp);

           
        out.write(data);
      }
    
     FileOutputStream out1 = new FileOutputStream(PDoramDB);
     
     for(int i=0;i<(1<<levels) -1;i++){
        temp=Math.abs(randomGenerator.nextInt());
    
        data[0] = (byte) (temp >> 24);
        data[1] = (byte) (temp >> 16);
        data[2] = (byte) (temp >> 8);
        data[3] = (byte) (temp);

        out1.write(data);
    
  }
    out1.close();
}
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
            value += (inHash.read() & 0x000000FF) << shift;
        }
       ret.add(value);
       inDB.skip(skipAfter);
       
    }
    
    inHash.close();
    inDB.close();
    
        System.out.println(ret);
    
    return ret;
    }
}