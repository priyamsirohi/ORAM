/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdoram;

import java.io.IOException;
import java.util.ArrayList;


/**
 *
 * @author priyam.sirohi
 */
public class PDOram {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        PDOramFunctionality oram = new PDOramFunctionality(5,5);
        ArrayList build_argument = new ArrayList();
        
        for(int i=1;i<6;i++){
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.add(i);
            temp.add(i+5);
            build_argument.add(temp);
        }
        //size of this arraylist is assumed to be same as N.
        oram.build(build_argument);
        
    }
   
}
