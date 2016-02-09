package wep;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

public class Reader {

	public static int convert(String string) {
		  return Integer.valueOf(String.valueOf(string), 16);
	}
	
	public static int hex(int x){
	    int y=0;
	    int i=0;

	    while (x>0){
	        y+=(x%10)*Math.pow(16,i);
	        x/=10;
	        i++;
	    }
	    return y;
	}
	
	public static String byteArrayToHex(byte[] a) {
		   StringBuilder sb = new StringBuilder(a.length * 2);
		   for(byte b: a)
		      sb.append(String.format("%02x", b & 0xff));
		   return sb.toString();
		}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String prepend = new String("C:\\Users\\user\\Desktop\\Sem 3\\CS4236\\Project\\A");
		
		FileInputStream in = null;
		
				int IV0, IV1, IV2, o1;
				in = new FileInputStream("C:\\Users\\user\\Desktop\\Sem 3\\CS4236\\Project\\A24.data");
				int temp,t;
				int L, rounds;
				
				DecimalFormat formatter = new DecimalFormat("00");
				String L_read[] = new String[4];
				String L_whole = new String();
				for( int i=0; i<4 ; i++) L_read[i] =  Integer.toHexString((( Integer.valueOf(formatter.format(in.read()) )))) ;
				for( int i=3; i>=0 ; i--) L_whole += L_read[i];
				L = Integer.valueOf(L_whole, 16);
				System.out.println(L);
				
				String rounds_read[] = new String[4];
				String rounds_whole = new String();
				for( int i=0; i<4 ; i++) rounds_read[i] =  Integer.toHexString((( Integer.valueOf(formatter.format(in.read()) )))) ;
				for( int i=3; i>=0 ; i--) rounds_whole += rounds_read[i];
				rounds = Integer.valueOf(rounds_whole, 16);
				System.out.println(rounds);
				
				for( int i=1; i<=8 ; i++)
				{
					IV0 = in.read();
					IV1 = in.read();
					IV2 = in.read();
					o1 = in.read();
					System.out.println(IV0 + " " + IV1 + " " + IV2 + " " + o1);
					
				}
				System.out.println("Completed");
				
			
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

