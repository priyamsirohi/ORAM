/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdoram;

import java.io.IOException;


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
        PDOramFunctionality oram = new PDOramFunctionality(20);
       oram.PDOramRead(4);
    }
   
}
