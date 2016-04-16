/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdoram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


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
      /*  PDOramFunctionality oram = new PDOramFunctionality(5,5);
        ArrayList build_argument = new ArrayList();
        
        for(int i=1;i<6;i++){
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.add(i);
            temp.add(i+5);
            build_argument.add(temp);
        }
        //size of this arraylist is assumed to be same as N.
        oram.build(build_argument);
        System.out.println("\n"+oram.PDOramRead(3));*/
      
      int[] A = new int[12];
      
      A[0] = 4;
      A[1] = 5;
      A[2] = 9;
      A[3] = 7;
      A[4] = 3;
      A[5] = 8;
      A[6] = 2;
      A[7] = 6;
      A[8] = 0;
      A[9] = 1;
      A[10] =11;
      A[11] =10;
      
      
     int[] B = A.clone();
    reshuffle ref = new reshuffle(A,B);
       for(int i:A) System.out.println(i);
     /* Queue<Integer> list = new LinkedList<Integer>();
      list.add(1);
      list.add(2);
      list.add(3);
      list.remove();
        System.out.println(list.peek());
        System.out.println(list);*/
    }
   
}
