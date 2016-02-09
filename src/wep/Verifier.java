package wep;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Verifier {

	public static Integer K[]; 
	public static Integer N = 160; 
	public static Integer S[];
	public static int L, rounds;
	
	public static void ksa_check(Integer K_check[], Integer o1)
	{
	  Integer i;
	  Integer j;

	  for (i=0; i<N; i++) { S[i]=i; }
	  j=0;

	  for (i=0; i<N; i++)
	   {
		 Integer temp;
	     j = ( j + S[i] + K_check [ i % L ] )% N;
	     temp = S[j]; S[j] = S[i]; S[i] = temp; 
	   }
	  
	  i = j = 0;
	  i = (i+1) % N;
	  j = (j+S[i]) % N;
	  //Integer temp = S[j]; S[j] = S[i]; S[i] = temp;
	  
	  Integer output = S[ Math.floorMod( (S[i] + S[j]) , N) ];
	  
	  if( output.equals(o1) ) 
	  {
		  System.out.println("VERIFIED");
	  }
	  else 
	  { 
		  System.out.print("WRONG ");
		  System.out.println("Checking IVs " + K_check[0] + " " + K_check[1] + " " + K_check[2] + ", o1 = " + o1 + ", output = " + output);
	  } 
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Scanner sc = new Scanner(System.in);
		
		int fileno = sc.nextInt();
		
		String prepend = new String("C:\\Users\\user\\Desktop\\Sem 3\\CS4236\\Project\\A");
		DecimalFormat formatter = new DecimalFormat("00");
		String filenum = formatter.format(fileno);
		String filename = new String(prepend+filenum+".data"); 
		FileInputStream in = null;
		//System.out.println(filename);
		
		int IV0, IV1, IV2, o1;
		in = new FileInputStream(filename);
		int temp,t;
		
		
		String L_read[] = new String[4];
		String L_whole = new String();
		for( int i=0; i<4 ; i++) L_read[i] =  Integer.toHexString((( Integer.valueOf(formatter.format(in.read()) )))) ;
		for( int i=3; i>=0 ; i--) L_whole += L_read[i];
		L = Integer.valueOf(L_whole, 16);
		//System.out.println(L);
		
		String rounds_read[] = new String[4];
		String rounds_whole = new String();
		for( int i=0; i<4 ; i++) rounds_read[i] =  Integer.toHexString((( Integer.valueOf(formatter.format(in.read()) )))) ;
		for( int i=3; i>=0 ; i--) rounds_whole += rounds_read[i];
		rounds = Integer.valueOf(rounds_whole, 16);
		//System.out.println(rounds);
		
		K = new Integer[L]; 
		
		for( int i=3; i<L; i++)  K[i] = sc.nextInt();
		
		Boolean isCorrect;
		
		for( int i=1; i<=100 ; i++)
		{
			K[0] = in.read();
			K[1] = in.read();
			K[2] = in.read();
			o1 = in.read();
			S = new Integer[N];
			
			ksa_check( K, o1 );
					
		}
		
				
			
	} 
	
}



/*
 
 		Integer a[] = { 1, 2, 3, 4, 5 };
		System.out.println(a.length);
		
		for (int i : a) System.out.print(i);
		System.out.println();
				
		Arrays.sort(a, Collections.reverseOrder() );		
		for (int i : a) System.out.print(i);
		System.out.println();
		
		System.out.println(Collections.max(Arrays.asList(a)));
		System.out.println(Collections.min(Arrays.asList(a)));
		
		System.out.println(Arrays.asList(a).indexOf(4));
		
 
 */

