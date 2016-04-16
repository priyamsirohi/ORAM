/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdoram;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author priyam.sirohi
 */
public class reshuffle {
    int s = 0, a, b, no_of_buckets;
    Queue<Integer> q1, q2;
    
    reshuffle(int[] A, int[] B){
        
        a = 1;
        b = 3;
        no_of_buckets = 5;
        
        
        s = (int)Math.sqrt(A.length) + 1;
        q1 = new LinkedList<Integer>();
        q2 = new LinkedList<Integer>();
        oblivious_merge_sort(A, B, 0, A.length -1);
        
       
    }
    
    private void oblivious_merge_sort(int[] A, int[] B, int start, int end){
            
        int n = end-start+1;
        if(n<=1) return;
       
        
        oblivious_merge_sort(A, B, start, (end+start)/2 );
        oblivious_merge_sort(A, B, (end+start)/2 +1, end);
        
        
        int b_index = start;
        
        int start1 = start;
        int start2 = (end+start)/2 +1;
        int end1 = (end+start)/2;
        int end2 = end;
        
        for(int i =0;i< s/2;i++){
            if(start1<=end1)q1.add(A[start1++]);
            if(start2<=end2)q2.add(A[start2++]);
        }
        
        while((!q1.isEmpty())||(!q2.isEmpty())){
            
                if(start1<=end1)q1.add(A[start1++]);
                if(start2<=end2)q2.add(A[start2++]);
            
            
            for(int j = 0;j<2;j++){
                int temp = -1;
                
                if(!q1.isEmpty()&&!q2.isEmpty()){
                    int temp1 = q1.peek();
                    int temp2 = q2.peek();

                    if(temp1 == compareHash(temp1, temp2))
                        temp = q1.remove();
                    else 
                        temp = q2.remove();
       
                }
                
                else {
                    if(!q1.isEmpty())
                        temp = q1.remove();
                    else if(!q2.isEmpty())
                        temp = q2.remove();                    
                }
                
                if(temp!=-1) B[b_index++] = temp;                
            }
        }
        
        
        //copy function to save momory.
        for(int i = start; i<= end; i++)
            A[i] = B[i];
        
    }
    
    private int compareHash(int temp1, int temp2){
       
       int x1 = (a*temp1 + b)% no_of_buckets;
       int x2 = (a*temp2 + b)% no_of_buckets;
       
       if(x1<x2) return temp1;
       else return temp2;
    }
}
