package wep;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

public class Solver {
	
	public Integer N = 160; 
	public Integer L;
	public Integer rounds;
	public Integer p;
	public Integer S[];
	public Integer K[];
	public Integer freq[];
	public static Integer freq_lec[];
	public Integer R;
	public String file = new String();
	Integer threshold = 3;
	
	public Integer ksa()
	{
	  Integer i;
	  Integer j;

	  for (i=0; i<N; i++) S[i]=i;
	  j=0;

	  for (i=0; i<p; i++)
	   {
		 Integer temp;
	     j = ( j + S[i] + K [ i % L ] )% N;
	     temp = S[j]; S[j] = S[i]; S[i] = temp; 
	   }
	  
	  return j;
	}	
	
	public Integer ksa_lec()
	{
	  Integer i;
	  Integer j;

	  for (i=0; i<N; i++) S[i]=i;
	  j=0;

	  for (i=0; i<p; i++)
	   {
		 Integer X=S[1], Y=S[X];
		 if( i>=1 && i>=X && i>=Math.floorMod(X+Y, N) && X+Y==p) {
			  Integer jp = Arrays.asList(S).indexOf(R);
			  Integer kp = Math.floorMod((jp - j - S[p]), N);
			  //System.out.println("Weak IV " + kp);
			  if ( kp>=0 && kp<90 )	freq_lec[kp]++;
		 }
		 
		 Integer temp;
	     j = ( j + S[i] + K [ i % L ] )% N;
	     temp = S[j]; S[j] = S[i]; S[i] = temp; 
	   }
	  
	  Integer X=S[1], Y=S[X];
	  if( i>=1 && i>=X && i>=Math.floorMod(X+Y, N) && X+Y==p) {
		  Integer jp = Arrays.asList(S).indexOf(R);
		  Integer kp = Math.floorMod((jp - j - S[p]), N);
		  //System.out.println("Weak IV " + kp);
		  if ( kp>=0 && kp<90 )	freq_lec[kp]++;
	  }
	  return j;
	}
	
	public Boolean ksa_check(Integer K_check[], Integer o1)
	{
	  Integer i;
	  Integer j;

	  for (i=0; i<N; i++) S[i]=i;
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
	  //System.out.println("Checking IVs " + K_check[0] + " " + K_check[1] + " " + K_check[2] + ", o1 = " + o1 + ", output = " + output);
	  if( output.equals(o1) ) return true;
	  else return false;
	}
	
	private Integer indexof(Integer val) {
		Integer index = -1;
		for ( Integer i=0 ; i<N ; i++) {
			if( S[i] == val ) {
				return i;
			}
		}
		return index;
	}
	
	public Integer find(Integer[] array, Integer value) {
		Integer index = -1;
	    for(Integer i=0; i<array.length; i++) 
	         if(array[i] == value)
	             return i;
		return index;
	}
	
	public Integer getL(FileInputStream in) throws NumberFormatException, IOException {
		DecimalFormat formatter = new DecimalFormat("00");
		String L_read[] = new String[4];
		String L_whole = new String();
		for( int i=0; i<4 ; i++) L_read[i] =  Integer.toHexString((( Integer.valueOf(formatter.format(in.read()) )))) ;
		for( int i=3; i>=0 ; i--) L_whole += L_read[i];
		L = Integer.valueOf(L_whole, 16);
		//System.out.println(L);
		
		return L;
	}

	public Integer getRounds(FileInputStream in) throws NumberFormatException, IOException {
		DecimalFormat formatter = new DecimalFormat("00");
		
		String rounds_read[] = new String[4];
		String rounds_whole = new String();
		for( int i=0; i<4 ; i++) rounds_read[i] =  Integer.toHexString((( Integer.valueOf(formatter.format(in.read()) )))) ;
		for( int i=3; i>=0 ; i--) rounds_whole += rounds_read[i];
		rounds = Integer.valueOf(rounds_whole, 16);
		
		return rounds;
	}
	
	public void solve1(String filename) throws IOException {
		
		file = filename;
		FileInputStream in = null;
		try {
			
			in = new FileInputStream(file);
			Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
			
			L = getL(in);
			rounds = getRounds(in);
			in.close();
			
			K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
			
			for ( p=3; p<L; p++) {
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;
				
				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
				
				//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
				//System.out.println();
				
				for( Integer i=1; i<=rounds ; i++)
				{
					IV[0] = in.read();
					IV[1] = in.read();
					IV[2] = in.read();
					o1 = in.read();
					//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
					
					if(i==1) {
						iv0_check = IV[0];
						iv1_check = IV[1];
						iv2_check = IV[2];
						o1_check = o1;
					}
					S = new Integer[N];
					
					K[0] = IV[0];
					K[1] = IV[1];
					K[2] = IV[2];		
					
					j_p_1 = ksa();
					if( S[1] < p ) {
						if( Math.floorMod( (S[1] + S[S[1]]), N) == p ) {
							if( (S[o1] != 1) && (S[o1] != S[S[1]]) ) {
								//System.out.println("Korek A_s5_1 at i = " + i);
								//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
								Integer index = Arrays.asList(S).indexOf(o1);
								if (index>=0) {
									Integer kp = Math.floorMod( (index - S[p] - j_p_1), N);
									if ( kp>=0 && kp<90 )	freq[kp]++;
									//System.out.println("K[" + p + "] = " + kp + ".");
									//break;
								}
								
							}
						}
					}
					
					
					//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
				}
			
			
				Integer freq_copy[] = freq.clone();
				Arrays.sort(freq_copy, Collections.reverseOrder());
				K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
			
				//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
				//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
				
			}
			
			if ( ksa_check( K, R) ) { 
				K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
				System.out.println("\nCompleted solver1 " + filename + " L:" + L + " rounds:" + rounds);
				System.out.print("K[] : ");
				for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
				System.out.println();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void solve2(String filename) throws IOException {
	
	file = filename;
	FileInputStream in = null;
	try {
		
		in = new FileInputStream(file);
		Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
		
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
		
		for ( p=3; p<L; p++) {
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			//System.out.println();
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if( S[1]== p ) // || (S[1] == 0 && S[p] == p)
				{
					if( o1 == p ) 
					{
						//System.out.println("Korek A_s13 at i = " + i);
						//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
						Integer index = Arrays.asList(S).indexOf(0);
						if (index>=0) {
							Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
							//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1);
							if ( kp>=0 && kp<N )	freq[kp]++;							
						}
					}
				}
				
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
			//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
			
		}
		
		if ( ksa_check( K, R) ) { 
			K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
			System.out.println("\nCompleted solver2 " + filename + " L:" + L + " rounds:" + rounds);
			System.out.print("K[] : ");
			for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
			System.out.println();
		}
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

	public void solve3(String filename) throws IOException {
	
	file = filename;
	FileInputStream in = null;
	try {
		
		in = new FileInputStream(file);
		Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
		
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
		
		for ( p=3; p<L; p++) {
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			//System.out.println();
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if( S[1] == p ) {
					if( o1 == Math.floorMod((1-p),N) ) {
						//System.out.println("Korek A_u13_1 at i = " + i);
						//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
						Integer index = Arrays.asList(S).indexOf(o1);
						if (index>=0) {
							Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
							//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1 + ", o1 = " + o1);
							if ( kp>=0 && kp<N )	freq[kp]++;
						}
					}
				}
				
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
			//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
			
		}
		
		if ( ksa_check( K, R) ) { 
			K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
			System.out.println("\nCompleted solver3 " + filename + " L:" + L + " rounds:" + rounds);
			System.out.print("K[] : ");
			for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
			System.out.println();
		}
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

	public void solve4(String filename) throws IOException {
	
	file = filename;
	FileInputStream in = null;
	try {
		
		in = new FileInputStream(file);
		Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
		
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
		
		for ( p=3; p<L; p++) {
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			//System.out.println();
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				
				j_p_1 = ksa();
				if( S[1] == p ) {
					if( o1!=p && o1!=Math.floorMod((1-p),N) ) {
						if( (Arrays.asList(S).indexOf(o1)<p) && 
								( Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N)) !=1 ) 
							) {
							//System.out.println("Korek A_u5_1 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							
							Integer jp = Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N));
							Integer kp = Math.floorMod((jp - S[p] - j_p_1), N);
							
							if ( kp>=0 && kp<N )	freq[kp]++;
							//System.out.println("K[" + p + "] = " + kp + ".");
							
						}
					}
				} 
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
			//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
			
		}
		
		if ( ksa_check( K, R) ) { 
			K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
			System.out.println("\nCompleted solver4 " + filename + " L:" + L + " rounds:" + rounds);
			System.out.print("K[] : ");
			for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
			System.out.println();
		}
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

	public void solve5(String filename) throws IOException {
	
	file = filename;
	FileInputStream in = null;
	try {
		
		in = new FileInputStream(file);
		Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
		
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
		
		for ( p=3; p<L; p++) {
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			//System.out.println();
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				
				j_p_1 = ksa();
				if ( S[p]==1 && Arrays.asList(S).indexOf(o1)==2 ) {
					//System.out.println("Korek A_u5_2 at i = " + i);
					//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
					Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
					if ( kp>=0 && kp<N )	freq[kp]++;
				}
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
			//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
			
		}
		
		if ( ksa_check( K, R) ) { 
			K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
			System.out.println("\nCompleted solver5 " + filename + " L:" + L + " rounds:" + rounds);
			System.out.print("K[] : ");
			for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
			System.out.println();
		}
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

	public void solve6(String filename) throws IOException {
	
	file = filename;
	FileInputStream in = null;
	try {
		
		in = new FileInputStream(file);
		Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
		
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
		
		for ( p=3; p<L; p++) {
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			//System.out.println();
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if ( S[p] == p ) {
					if ( S[1] == 0 ) {
						if ( o1 == p ) {
							//System.out.println("Korek A_u13_2 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
							if ( kp>=0 && kp<N )	freq[kp]++;
						}
					}
				}
				
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
			//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
			
		}
		
		if ( ksa_check( K, R) ) { 
			K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
			System.out.println("\nCompleted solver6 " + filename + " L:" + L + " rounds:" + rounds);
			System.out.print("K[] : ");
			for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
			System.out.println();
		}
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

	public void solve7(String filename) throws IOException {
	
	file = filename;
	FileInputStream in = null;
	try {
		
		in = new FileInputStream(file);
		Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
		
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
		
		for ( p=3; p<L; p++) {
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			//System.out.println();
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if ( S[p] == p ) {
					if ( S[1] == o1 ) {
						if ( S[1] == Math.floorMod( 1-p, N) ) {
							//System.out.println("Korek A_u13_3 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
							if ( kp>=0 && kp<N )	freq[kp]++;
						}
					}
				}
				
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
			//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
			
		}
		
		if ( ksa_check( K, R) ) { 
			K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
			System.out.println("\nCompleted solver7 " + filename + " L:" + L + " rounds:" + rounds);
			System.out.print("K[] : ");
			for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
			System.out.println();
		}
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

	public void solve8(String filename) throws IOException {
	
	file = filename;
	FileInputStream in = null;
	try {
		
		in = new FileInputStream(file);
		Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
		
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
		
		for ( p=3; p<L; p++) {
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			//System.out.println();
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if ( S[p] == p ) {
					if ( S[1] == o1 ) {
						if ( S[1] >= Math.floorMod( (-1*p), N) ) {
							if ( S[1] == Math.floorMod( (Arrays.asList(S).indexOf(o1) - p) , N) ) {
								if ( Arrays.asList(S).indexOf(o1) != 1 ) {
									//System.out.println("Korek A_u5_3 at i = " + i);
									//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
									Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
									if ( kp>=0 && kp<N )	freq[kp]++;
								}
							}
						}
					}
				}
				
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
			//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
			
		}
		
		if ( ksa_check( K, R) ) { 
			K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
			System.out.println("\nCompleted solver8 " + filename + " L:" + L + " rounds:" + rounds);
			System.out.print("K[] : ");
			for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
			System.out.println();
		}
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}

	public void solveAll(String filename) throws IOException {
		
		file = filename;
		FileInputStream in = null;
		try {
			
			in = new FileInputStream(file);
			Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
			
			L = getL(in);
			rounds = getRounds(in);
			in.close();
			
			K = new Integer[L]; for (int i=0; i<L; i++) K[i] = 0;
			
			for ( p=3; p<L; p++) {
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;
				
				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
				
				//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
				//System.out.println();
				
				for( Integer i=1; i<=rounds ; i++)
				{
					IV[0] = in.read();
					IV[1] = in.read();
					IV[2] = in.read();
					o1 = in.read();
					//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
					
					if(i==1) {
						iv0_check = IV[0];
						iv1_check = IV[1];
						iv2_check = IV[2];
						o1_check = o1;
					}
					S = new Integer[N];
					
					K[0] = IV[0];
					K[1] = IV[1];
					K[2] = IV[2];		
					
					j_p_1 = ksa();
					if( S[1] < p ) {
						if( Math.floorMod( (S[1] + S[S[1]]), N) == p ) {
							if( (S[o1] != 1) && (S[o1] != S[S[1]]) ) {
								//System.out.println("Korek A_s5_1 at i = " + i);
								//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
								Integer index = Arrays.asList(S).indexOf(o1);
								if (index>=0) {
									Integer kp = Math.floorMod( (index - S[p] - j_p_1), N);
									if ( kp>=0 && kp<90 )	freq[kp]++;
									//System.out.println("K[" + p + "] = " + kp + ".");
									//break;
								}
								
							}
						}
					}
					
					
					j_p_1 = ksa();
					if( S[1]== p ) // || (S[1] == 0 && S[p] == p)
					{
						if( o1 == p ) 
						{
							//System.out.println("Korek A_s13 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer index = Arrays.asList(S).indexOf(0);
							if (index>=0) {
								Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
								//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1);
								if ( kp>=0 && kp<N )	freq[kp]++;							
							}
						}
					}
					
					
					j_p_1 = ksa();
					if( S[1] == p ) {
						if( o1 == Math.floorMod((1-p),N) ) {
							//System.out.println("Korek A_u13_1 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer index = Arrays.asList(S).indexOf(o1);
							if (index>=0) {
								Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
								//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1 + ", o1 = " + o1);
								if ( kp>=0 && kp<N )	freq[kp]++;
							}
						}
					}
					
					
					j_p_1 = ksa();
					if( S[1] == p ) {
						if( o1!=p && o1!=Math.floorMod((1-p),N) ) {
							if( (Arrays.asList(S).indexOf(o1)<p) && 
									( Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N)) !=1 ) 
								) {
								//System.out.println("Korek A_u5_1 at i = " + i);
								//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
								
								Integer jp = Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N));
								Integer kp = Math.floorMod((jp - S[p] - j_p_1), N);
								
								if ( kp>=0 && kp<N )	freq[kp]++;
								//System.out.println("K[" + p + "] = " + kp + ".");
								
							}
						}
					} 
					
					
					j_p_1 = ksa();
					if ( S[p]==1 && Arrays.asList(S).indexOf(o1)==2 ) {
						//System.out.println("Korek A_u5_2 at i = " + i);
						//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
						Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
						if ( kp>=0 && kp<N )	freq[kp]++;
					}
					
					
					j_p_1 = ksa();
					if ( S[p] == p ) {
						if ( S[1] == 0 ) {
							if ( o1 == p ) {
								//System.out.println("Korek A_u13_2 at i = " + i);
								//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
								Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
								if ( kp>=0 && kp<N )	freq[kp]++;
							}
						}
					}
					
										
					j_p_1 = ksa();
					if ( S[p] == p ) {
						if ( S[1] == o1 ) {
							if ( S[1] == Math.floorMod( 1-p, N) ) {
								//System.out.println("Korek A_u13_3 at i = " + i);
								//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
								Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
								if ( kp>=0 && kp<N )	freq[kp]++;
							}
						}
					}
					
					
					j_p_1 = ksa();
					if ( S[p] == p ) {
						if ( S[1] == o1 ) {
							if ( S[1] >= Math.floorMod( (-1*p), N) ) {
								if ( S[1] == Math.floorMod( (Arrays.asList(S).indexOf(o1) - p) , N) ) {
									if ( Arrays.asList(S).indexOf(o1) != 1 ) {
										//System.out.println("Korek A_u5_3 at i = " + i);
										//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
										Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
										if ( kp>=0 && kp<N )	freq[kp]++;
									}
								}
							}
						}
					}
					
					
					//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
				}
			
			
				Integer freq_copy[] = freq.clone();
				Arrays.sort(freq_copy, Collections.reverseOrder());
				K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
			
				//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq[ K[p] ]
				//	+ ", and " + freq[ Arrays.asList(freq).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq).indexOf( freq_copy[1] ) );
				
			}
			
			if ( ksa_check( K, R) ) { 
				K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
				System.out.println("\nCompleted solverAll " + filename + " L:" + L + " rounds:" + rounds);
				System.out.print("K[] : ");
				for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
				System.out.println();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void lecture(String filename) throws IOException {
		
		file = filename;
		FileInputStream in = null;
		try {
			
			in = new FileInputStream(file);	
			Integer iv0_check=0, iv1_check=0, iv2_check=0, o1_check=0;
			
			L = getL(in);
			rounds = getRounds(in);
			in.close();
			
			K = new Integer[L];
			
			for ( p=3; p<L; p++) {
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;

				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq_lec = new Integer[N]; for (int i=0; i<N; i++) freq_lec[i] = 0;	
				
				//for ( t=0; t<8; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
				//System.out.println();
				
			for( Integer i=1; i<=rounds ; i++)
			{
				
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				R = o1;
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
				
				if(i==1) {
					iv0_check = IV[0];
					iv1_check = IV[1];
					iv2_check = IV[2];
					o1_check = o1;
				}
				
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];
				
				
				j_p_1 = ksa_lec();
				
				
				//if( i%1000000 == 0 ) System.out.println("Gone past i = " + i);
			}
						
			Integer freq_copy[] = freq_lec.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq_lec).indexOf( freq_copy[0] );
			
			//System.out.println("Most likely K[" + p + "]: " + K[p] + " with frequency " + freq_lec[ K[p] ]
			//	+ ", and " + freq_lec[ Arrays.asList(freq_lec).indexOf( freq_copy[1] ) ] + " with second choice " + Arrays.asList(freq_lec).indexOf( freq_copy[1] ) );
			}
			
			if ( ksa_check( K, R) ) { 
				K[0] = iv0_check; K[1] = iv1_check; K[2] = iv2_check; R = o1_check;
				System.out.println("\nCompleted lecture " + filename + " L:" + L + " rounds:" + rounds);
				System.out.print("K[] : ");
				for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
				System.out.println();
			}
		
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public Boolean bruteComboSolver1(String filename) throws IOException {
	
		Boolean solved = false;
		Boolean searched = false;
		Integer backtracked = 0;
		file = filename;
		FileInputStream in = null;		
		Integer possible[][];
		Integer startp = 3;
		Integer Kindex[];
		Integer guessnum = 3;
		
		in = new FileInputStream(file);
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; 
		Kindex = new Integer[L];
		for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
		possible  = new Integer[L][threshold];
			
		while(!searched) {
						
			for ( p=startp; p<L-3; p++) {
				
				//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;
				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
				
				for( Integer i=1; i<=rounds ; i++)
				{
					IV[0] = in.read();
					IV[1] = in.read();
					IV[2] = in.read();
					o1 = in.read();
					//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
										
					S = new Integer[N];
					
					K[0] = IV[0];
					K[1] = IV[1];
					K[2] = IV[2];		
					
					j_p_1 = ksa();
					if( S[1] < p ) {
						if( Math.floorMod( (S[1] + S[S[1]]), N) == p ) {
							if( (S[o1] != 1) && (S[o1] != S[S[1]]) ) {
								//System.out.println("Korek A_s5_1 at i = " + i);
								//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
								Integer index = Arrays.asList(S).indexOf(o1);
								if (index>=0) {
									Integer kp = Math.floorMod( (index - S[p] - j_p_1), N);
									if ( kp>=0 && kp<90 )	freq[kp]++;
									//System.out.println("K[" + p + "] = " + kp + ".");
									//break;
								}
								
							}
						}
					}
				}	
					
				Integer freq_copy[] = freq.clone();
				Arrays.sort(freq_copy, Collections.reverseOrder());
				K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
			
				for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
				//Kindex[p] = 0;
			}
			in.close();
			
			//System.out.println("Randomizing");
			for ( int a=0; a<90; a++) {
				K[L-3] = a;
				for ( int b=0; b<90; b++ ) {
					K[L-2] = b;
					for ( int c=0; c<90; c++ ) {
						K[L-1] = c;
						
						FileInputStream verify = new FileInputStream(file);
						for(int i=0; i<8; i++) verify.read();
						int found = 0;
						
						for (int k=1; k<=5; k++) {
							K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
							if ( ksa_check( K, R) )	found++;
						}
						verify.close();
						
						if( found >= 5) {
							System.out.println("\nCompleted bruteComboSolver1 " + filename + " L:" + L + " rounds:" + rounds);
							System.out.print("K[] : ");
							for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
							System.out.println(); System.out.println();
							searched = solved = true;							
							return true;
						}
						
					}
				}
			}
			
			/*System.out.print("Kindex : ");
			for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
			System.out.println();
			for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			System.out.println();*/
			
			if (!searched) {
				//System.out.println("Backtracking");
				Boolean didBack = false;
				for (int i=3; i<L-3; i++) {
					if( Kindex[i] < threshold-1 )
					{
						startp = i+1;
						Kindex[i]++;
						K[i] = possible[i][Kindex[i]];
						backtracked++;
						//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
						//System.out.println("# of times backtracked = " + backtracked);
						didBack = true;
						break;
					}
				}
				if (!didBack) backtracked++;
			}
			
			if (backtracked == (threshold-1)*(L-3-3)+1 ) {
				searched = true;
				solved = false;
				//System.out.println("Breaking out now as # of backtracked = " + backtracked);
			}
		} 
		return solved;
	}
	
	public Boolean bruteComboSolver2(String filename) throws IOException {
		
		Boolean solved = false;
		Boolean searched = false;
		Integer backtracked = 0;
		file = filename;
		FileInputStream in = null;		
		Integer possible[][];
		Integer startp = 3;
		Integer Kindex[];
		Integer guessnum = 3;
		
		in = new FileInputStream(file);
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; 
		Kindex = new Integer[L];
		for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
		possible  = new Integer[L][threshold];
			
		while(!searched) {
						
			for ( p=startp; p<L-3; p++) {
				
				//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;
				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
				
				for( Integer i=1; i<=rounds ; i++)
				{
					IV[0] = in.read();
					IV[1] = in.read();
					IV[2] = in.read();
					o1 = in.read();
					//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
										
					S = new Integer[N];
					
					K[0] = IV[0];
					K[1] = IV[1];
					K[2] = IV[2];		
					
					j_p_1 = ksa();
					if( S[1]== p ) // || (S[1] == 0 && S[p] == p)
					{
						if( o1 == p ) 
						{
							//System.out.println("Korek A_s13 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer index = Arrays.asList(S).indexOf(0);
							if (index>=0) {
								Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
								//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1);
								if ( kp>=0 && kp<N )	freq[kp]++;							
							}
						}
					}
					
				}
			
			
				Integer freq_copy[] = freq.clone();
				Arrays.sort(freq_copy, Collections.reverseOrder());
				K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
			
				for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
				//Kindex[p] = 0;
			}
			in.close();
			
			//System.out.println("Randomizing");
			for ( int a=0; a<90; a++) {
				K[L-3] = a;
				for ( int b=0; b<90; b++ ) {
					K[L-2] = b;
					for ( int c=0; c<90; c++ ) {
						K[L-1] = c;
						
						FileInputStream verify = new FileInputStream(file);
						for(int i=0; i<8; i++) verify.read();
						int found = 0;
						
						for (int k=1; k<=5; k++) {
							K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
							if ( ksa_check( K, R) )	found++;
						}
						verify.close();
						
						if( found >= 5) {
							System.out.println("\nCompleted bruteComboSolver2 " + filename + " L:" + L + " rounds:" + rounds);
							System.out.print("K[] : ");
							for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
							System.out.println(); System.out.println();
							searched = solved = true;							
							return true;
						}
						
					}
				}
			}
			
			/*System.out.print("Kindex : ");
			for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
			System.out.println();
			for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			System.out.println();*/
			
			if (!searched) {
				//System.out.println("Backtracking");
				Boolean didBack = false;
				for (int i=3; i<L-3; i++) {
					if( Kindex[i] < threshold-1 )
					{
						startp = i+1;
						Kindex[i]++;
						K[i] = possible[i][Kindex[i]];
						backtracked++;
						//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
						//System.out.println("# of times backtracked = " + backtracked);
						didBack = true;
						break;
					}
				}
				if (!didBack) backtracked++;
			}
			
			if (backtracked == (threshold-1)*(L-3-3)+1 ) {
				searched = true;
				solved = false;
				//System.out.println("Breaking out now as # of backtracked = " + backtracked);
			}
		} 
		return solved;
	}
	
	public Boolean bruteComboSolver3(String filename) throws IOException {
		
		Boolean solved = false;
		Boolean searched = false;
		Integer backtracked = 0;
		file = filename;
		FileInputStream in = null;		
		Integer possible[][];
		Integer startp = 3;
		Integer Kindex[];
		Integer guessnum = 3;
		
		in = new FileInputStream(file);
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; 
		Kindex = new Integer[L];
		for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
		possible  = new Integer[L][threshold];
			
		while(!searched) {
						
			for ( p=startp; p<L-3; p++) {
				
				//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;
				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
				
				for( Integer i=1; i<=rounds ; i++)
				{
					IV[0] = in.read();
					IV[1] = in.read();
					IV[2] = in.read();
					o1 = in.read();
					//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
										
					S = new Integer[N];
					
					K[0] = IV[0];
					K[1] = IV[1];
					K[2] = IV[2];		
					
					j_p_1 = ksa();
					if( S[1] == p ) {
						if( o1 == Math.floorMod((1-p),N) ) {
							//System.out.println("Korek A_u13_1 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer index = Arrays.asList(S).indexOf(o1);
							if (index>=0) {
								Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
								//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1 + ", o1 = " + o1);
								if ( kp>=0 && kp<N )	freq[kp]++;
							}
						}
					}
					
				}
			
			
				Integer freq_copy[] = freq.clone();
				Arrays.sort(freq_copy, Collections.reverseOrder());
				K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
			
				for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
				//Kindex[p] = 0;
			}
			in.close();
			
			//System.out.println("Randomizing");
			for ( int a=0; a<90; a++) {
				K[L-3] = a;
				for ( int b=0; b<90; b++ ) {
					K[L-2] = b;
					for ( int c=0; c<90; c++ ) {
						K[L-1] = c;
						
						FileInputStream verify = new FileInputStream(file);
						for(int i=0; i<8; i++) verify.read();
						int found = 0;
						
						for (int k=1; k<=5; k++) {
							K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
							if ( ksa_check( K, R) )	found++;
						}
						verify.close();
						
						if( found >= 5) {
							System.out.println("\nCompleted bruteComboSolver3 " + filename + " L:" + L + " rounds:" + rounds);
							System.out.print("K[] : ");
							for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
							System.out.println(); System.out.println();
							searched = solved = true;							
							return true;
						}
						
					}
				}
			}
			
			/*System.out.print("Kindex : ");
			for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
			System.out.println();
			for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			System.out.println();*/
			
			if (!searched) {
				//System.out.println("Backtracking");
				Boolean didBack = false;
				for (int i=3; i<L-3; i++) {
					if( Kindex[i] < threshold-1 )
					{
						startp = i+1;
						Kindex[i]++;
						K[i] = possible[i][Kindex[i]];
						backtracked++;
						//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
						//System.out.println("# of times backtracked = " + backtracked);
						didBack = true;
						break;
					}
				}
				if (!didBack) backtracked++;
			}
			
			if (backtracked == (threshold-1)*(L-3-3)+1 ) {
				searched = true;
				solved = false;
				//System.out.println("Breaking out now as # of backtracked = " + backtracked);
			}
		} 
		return solved;
	}
	
	public Boolean bruteComboSolver4(String filename) throws IOException {
	
	Boolean solved = false;
	Boolean searched = false;
	Integer backtracked = 0;
	file = filename;
	FileInputStream in = null;		
	Integer possible[][];
	Integer startp = 3;
	Integer Kindex[];
	Integer guessnum = 3;
	
	in = new FileInputStream(file);
	L = getL(in);
	rounds = getRounds(in);
	in.close();
	
	K = new Integer[L]; 
	Kindex = new Integer[L];
	for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
	possible  = new Integer[L][threshold];
		
	while(!searched) {
					
		for ( p=startp; p<L-3; p++) {
			
			//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
									
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if( S[1] == p ) {
					if( o1!=p && o1!=Math.floorMod((1-p),N) ) {
						if( (Arrays.asList(S).indexOf(o1)<p) && 
								( Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N)) !=1 ) 
							) {
							//System.out.println("Korek A_u5_1 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							
							Integer jp = Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N));
							Integer kp = Math.floorMod((jp - S[p] - j_p_1), N);
							
							if ( kp>=0 && kp<N )	freq[kp]++;
							//System.out.println("K[" + p + "] = " + kp + ".");
							
						}
					}
				} 
				
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
			//Kindex[p] = 0;
		}
		in.close();
		
		//System.out.println("Randomizing");
		for ( int a=0; a<90; a++) {
			K[L-3] = a;
			for ( int b=0; b<90; b++ ) {
				K[L-2] = b;
				for ( int c=0; c<90; c++ ) {
					K[L-1] = c;
					
					FileInputStream verify = new FileInputStream(file);
					for(int i=0; i<8; i++) verify.read();
					int found = 0;
					
					for (int k=1; k<=5; k++) {
						K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
						if ( ksa_check( K, R) )	found++;
					}
					verify.close();
					
					if( found >= 5) {
						System.out.println("\nCompleted bruteComboSolver4 " + filename + " L:" + L + " rounds:" + rounds);
						System.out.print("K[] : ");
						for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
						System.out.println(); System.out.println();
						searched = solved = true;							
						return true;
					}
					
				}
			}
		}
		
		/*System.out.print("Kindex : ");
		for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
		System.out.println();
		for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
		System.out.println();*/
		
		if (!searched) {
			//System.out.println("Backtracking");
			Boolean didBack = false;
			for (int i=3; i<L-3; i++) {
				if( Kindex[i] < threshold-1 )
				{
					startp = i+1;
					Kindex[i]++;
					K[i] = possible[i][Kindex[i]];
					backtracked++;
					//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
					//System.out.println("# of times backtracked = " + backtracked);
					didBack = true;
					break;
				}
			}
			if (!didBack) backtracked++;
		}
		
		if (backtracked == (threshold-1)*(L-3-3)+1 ) {
			searched = true;
			solved = false;
			//System.out.println("Breaking out now as # of backtracked = " + backtracked);
		}
	} 
	return solved;
}

	public Boolean bruteComboSolver5(String filename) throws IOException {
	
	Boolean solved = false;
	Boolean searched = false;
	Integer backtracked = 0;
	file = filename;
	FileInputStream in = null;		
	Integer possible[][];
	Integer startp = 3;
	Integer Kindex[];
	Integer guessnum = 3;
	
	in = new FileInputStream(file);
	L = getL(in);
	rounds = getRounds(in);
	in.close();
	
	K = new Integer[L]; 
	Kindex = new Integer[L];
	for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
	possible  = new Integer[L][threshold];
		
	while(!searched) {
					
		for ( p=startp; p<L-3; p++) {
			
			//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
									
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if ( S[p]==1 && Arrays.asList(S).indexOf(o1)==2 ) {
					//System.out.println("Korek A_u5_2 at i = " + i);
					//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
					Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
					if ( kp>=0 && kp<N )	freq[kp]++;
				}
				
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
			//Kindex[p] = 0;
		}
		in.close();
		
		//System.out.println("Randomizing");
		for ( int a=0; a<90; a++) {
			K[L-3] = a;
			for ( int b=0; b<90; b++ ) {
				K[L-2] = b;
				for ( int c=0; c<90; c++ ) {
					K[L-1] = c;
					
					FileInputStream verify = new FileInputStream(file);
					for(int i=0; i<8; i++) verify.read();
					int found = 0;
					
					for (int k=1; k<=5; k++) {
						K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
						if ( ksa_check( K, R) )	found++;
					}
					verify.close();
					
					if( found >= 5) {
						System.out.println("\nCompleted bruteComboSolver5 " + filename + " L:" + L + " rounds:" + rounds);
						System.out.print("K[] : ");
						for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
						System.out.println(); System.out.println();
						searched = solved = true;							
						return true;
					}
					
				}
			}
		}
		
		/*System.out.print("Kindex : ");
		for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
		System.out.println();
		for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
		System.out.println();*/
		
		if (!searched) {
			//System.out.println("Backtracking");
			Boolean didBack = false;
			for (int i=3; i<L-3; i++) {
				if( Kindex[i] < threshold-1 )
				{
					startp = i+1;
					Kindex[i]++;
					K[i] = possible[i][Kindex[i]];
					backtracked++;
					//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
					//System.out.println("# of times backtracked = " + backtracked);
					didBack = true;
					break;
				}
			}
			if (!didBack) backtracked++;
		}
		
		if (backtracked == (threshold-1)*(L-3-3)+1 ) {
			searched = true;
			solved = false;
			//System.out.println("Breaking out now as # of backtracked = " + backtracked);
		}
	} 
	return solved;
}

	public Boolean bruteComboSolver6(String filename) throws IOException {
	
	Boolean solved = false;
	Boolean searched = false;
	Integer backtracked = 0;
	file = filename;
	FileInputStream in = null;		
	Integer possible[][];
	Integer startp = 3;
	Integer Kindex[];
	Integer guessnum = 3;
	
	in = new FileInputStream(file);
	L = getL(in);
	rounds = getRounds(in);
	in.close();
	
	K = new Integer[L]; 
	Kindex = new Integer[L];
	for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
	possible  = new Integer[L][threshold];
		
	while(!searched) {
					
		for ( p=startp; p<L-3; p++) {
			
			//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
									
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if ( S[p] == p ) {
					if ( S[1] == 0 ) {
						if ( o1 == p ) {
							//System.out.println("Korek A_u13_2 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
							if ( kp>=0 && kp<N )	freq[kp]++;
						}
					}
				}
				
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
			//Kindex[p] = 0;
		}
		in.close();
		
		//System.out.println("Randomizing");
		for ( int a=0; a<90; a++) {
			K[L-3] = a;
			for ( int b=0; b<90; b++ ) {
				K[L-2] = b;
				for ( int c=0; c<90; c++ ) {
					K[L-1] = c;
					
					FileInputStream verify = new FileInputStream(file);
					for(int i=0; i<8; i++) verify.read();
					int found = 0;
					
					for (int k=1; k<=5; k++) {
						K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
						if ( ksa_check( K, R) )	found++;
					}
					verify.close();
					
					if( found >= 5) {
						System.out.println("\nCompleted bruteComboSolver6 " + filename + " L:" + L + " rounds:" + rounds);
						System.out.print("K[] : ");
						for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
						System.out.println(); System.out.println();
						searched = solved = true;							
						return true;
					}
					
				}
			}
		}
		
		/*System.out.print("Kindex : ");
		for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
		System.out.println();
		for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
		System.out.println();*/
		
		if (!searched) {
			//System.out.println("Backtracking");
			Boolean didBack = false;
			for (int i=3; i<L-3; i++) {
				if( Kindex[i] < threshold-1 )
				{
					startp = i+1;
					Kindex[i]++;
					K[i] = possible[i][Kindex[i]];
					backtracked++;
					//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
					//System.out.println("# of times backtracked = " + backtracked);
					didBack = true;
					break;
				}
			}
			if (!didBack) backtracked++;
		}
		
		if (backtracked == (threshold-1)*(L-3-3)+1 ) {
			searched = true;
			solved = false;
			//System.out.println("Breaking out now as # of backtracked = " + backtracked);
		}
	} 
	return solved;
}
	
	public Boolean bruteComboSolver7(String filename) throws IOException {
	
	Boolean solved = false;
	Boolean searched = false;
	Integer backtracked = 0;
	file = filename;
	FileInputStream in = null;		
	Integer possible[][];
	Integer startp = 3;
	Integer Kindex[];
	Integer guessnum = 3;
	
	in = new FileInputStream(file);
	L = getL(in);
	rounds = getRounds(in);
	in.close();
	
	K = new Integer[L]; 
	Kindex = new Integer[L];
	for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
	possible  = new Integer[L][threshold];
		
	while(!searched) {
					
		for ( p=startp; p<L-3; p++) {
			
			//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
									
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if ( S[p] == p ) {
					if ( S[1] == o1 ) {
						if ( S[1] == Math.floorMod( 1-p, N) ) {
							//System.out.println("Korek A_u13_3 at i = " + i);
							//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
							Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
							if ( kp>=0 && kp<N )	freq[kp]++;
						}
					}
				}
				
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
			//Kindex[p] = 0;
		}
		in.close();
		
		//System.out.println("Randomizing");
		for ( int a=0; a<90; a++) {
			K[L-3] = a;
			for ( int b=0; b<90; b++ ) {
				K[L-2] = b;
				for ( int c=0; c<90; c++ ) {
					K[L-1] = c;
					
					FileInputStream verify = new FileInputStream(file);
					for(int i=0; i<8; i++) verify.read();
					int found = 0;
					
					for (int k=1; k<=5; k++) {
						K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
						if ( ksa_check( K, R) )	found++;
					}
					verify.close();
					
					if( found >= 5) {
						System.out.println("\nCompleted bruteComboSolver7 " + filename + " L:" + L + " rounds:" + rounds);
						System.out.print("K[] : ");
						for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
						System.out.println(); System.out.println();
						searched = solved = true;							
						return true;
					}
					
				}
			}
		}
		
		/*System.out.print("Kindex : ");
		for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
		System.out.println();
		for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
		System.out.println();*/
		
		if (!searched) {
			//System.out.println("Backtracking");
			Boolean didBack = false;
			for (int i=3; i<L-3; i++) {
				if( Kindex[i] < threshold-1 )
				{
					startp = i+1;
					Kindex[i]++;
					K[i] = possible[i][Kindex[i]];
					backtracked++;
					//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
					//System.out.println("# of times backtracked = " + backtracked);
					didBack = true;
					break;
				}
			}
			if (!didBack) backtracked++;
		}
		
		if (backtracked == (threshold-1)*(L-3-3)+1 ) {
			searched = true;
			solved = false;
			//System.out.println("Breaking out now as # of backtracked = " + backtracked);
		}
	} 
	return solved;
}

	public Boolean bruteComboSolver8(String filename) throws IOException {
	
	Boolean solved = false;
	Boolean searched = false;
	Integer backtracked = 0;
	file = filename;
	FileInputStream in = null;		
	Integer possible[][];
	Integer startp = 3;
	Integer Kindex[];
	Integer guessnum = 3;
	
	in = new FileInputStream(file);
	L = getL(in);
	rounds = getRounds(in);
	in.close();
	
	K = new Integer[L]; 
	Kindex = new Integer[L];
	for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
	possible  = new Integer[L][threshold];
		
	while(!searched) {
					
		for ( p=startp; p<L-3; p++) {
			
			//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
									
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa();
				if ( S[p] == p ) {
					if ( S[1] == o1 ) {
						if ( S[1] >= Math.floorMod( (-1*p), N) ) {
							if ( S[1] == Math.floorMod( (Arrays.asList(S).indexOf(o1) - p) , N) ) {
								if ( Arrays.asList(S).indexOf(o1) != 1 ) {
									//System.out.println("Korek A_u5_3 at i = " + i);
									//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
									Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
									if ( kp>=0 && kp<N )	freq[kp]++;
								}
							}
						}
					}
				}
				
			}
		
		
			Integer freq_copy[] = freq.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
		
			for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
			//Kindex[p] = 0;
		}
		in.close();
		
		//System.out.println("Randomizing");
		for ( int a=0; a<90; a++) {
			K[L-3] = a;
			for ( int b=0; b<90; b++ ) {
				K[L-2] = b;
				for ( int c=0; c<90; c++ ) {
					K[L-1] = c;
					
					FileInputStream verify = new FileInputStream(file);
					for(int i=0; i<8; i++) verify.read();
					int found = 0;
					
					for (int k=1; k<=5; k++) {
						K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
						if ( ksa_check( K, R) )	found++;
					}
					verify.close();
					
					if( found >= 5) {
						System.out.println("\nCompleted bruteComboSolver8 " + filename + " L:" + L + " rounds:" + rounds);
						System.out.print("K[] : ");
						for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
						System.out.println(); System.out.println();
						searched = solved = true;							
						return true;
					}
					
				}
			}
		}
		
		/*System.out.print("Kindex : ");
		for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
		System.out.println();
		for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
		System.out.println();*/
		
		if (!searched) {
			//System.out.println("Backtracking");
			Boolean didBack = false;
			for (int i=3; i<L-3; i++) {
				if( Kindex[i] < threshold-1 )
				{
					startp = i+1;
					Kindex[i]++;
					K[i] = possible[i][Kindex[i]];
					backtracked++;
					//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
					//System.out.println("# of times backtracked = " + backtracked);
					didBack = true;
					break;
				}
			}
			if (!didBack) backtracked++;
		}
		
		if (backtracked == (threshold-1)*(L-3-3)+1 ) {
			searched = true;
			solved = false;
			//System.out.println("Breaking out now as # of backtracked = " + backtracked);
		}
	} 
	return solved;
}

	public Boolean bruteComboLecture(String filename) throws IOException {
	
	Boolean solved = false;
	Boolean searched = false;
	Integer backtracked = 0;
	file = filename;
	FileInputStream in = null;		
	Integer possible[][];
	Integer startp = 3;
	Integer Kindex[];
	Integer guessnum = 3;
	
	in = new FileInputStream(file);
	L = getL(in);
	rounds = getRounds(in);
	in.close();
	
	K = new Integer[L]; 
	Kindex = new Integer[L];
	for (int i=0; i<L; i++) K[i] = Kindex[i] = 0;
	possible  = new Integer[L][threshold];
		
	while(!searched) {
					
		for ( p=startp; p<L-3; p++) {
			
			//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
			
			in = new FileInputStream(file);
			for(int i=0; i<8; i++) in.read();
			
			Integer temp,t;
			Integer IV[] = new Integer[3];
			Integer o1;
			Integer j_p_1;
			freq_lec = new Integer[N]; for (int i=0; i<N; i++) freq_lec[i] = 0;	
			
			for( Integer i=1; i<=rounds ; i++)
			{
				IV[0] = in.read();
				IV[1] = in.read();
				IV[2] = in.read();
				o1 = in.read();
				//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
									
				S = new Integer[N];
				
				K[0] = IV[0];
				K[1] = IV[1];
				K[2] = IV[2];		
				
				j_p_1 = ksa_lec();
				
			}
		
		
			Integer freq_copy[] = freq_lec.clone();
			Arrays.sort(freq_copy, Collections.reverseOrder());
			K[p] = Arrays.asList(freq_lec).indexOf( freq_copy[0] );
		
			for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq_lec).indexOf( freq_copy[j] );
			//Kindex[p] = 0;
		}
		in.close();
		
		//System.out.println("Randomizing");
		for ( int a=0; a<90; a++) {
			K[L-3] = a;
			for ( int b=0; b<90; b++ ) {
				K[L-2] = b;
				for ( int c=0; c<90; c++ ) {
					K[L-1] = c;
					
					FileInputStream verify = new FileInputStream(file);
					for(int i=0; i<8; i++) verify.read();
					int found = 0;
					
					for (int k=1; k<=5; k++) {
						K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
						if ( ksa_check( K, R) )	found++;
					}
					verify.close();
					
					if( found >= 5) {
						System.out.println("\nCompleted bruteComboLecture " + filename + " L:" + L + " rounds:" + rounds);
						System.out.print("K[] : ");
						for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
						System.out.println(); System.out.println();
						searched = solved = true;							
						return true;
					}
					
				}
			}
		}
		
		/*System.out.print("Kindex : ");
		for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
		System.out.println();
		for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
		System.out.println();*/
		
		if (!searched) {
			//System.out.println("Backtracking");
			Boolean didBack = false;
			for (int i=3; i<L-3; i++) {
				if( Kindex[i] < threshold-1 )
				{
					startp = i+1;
					Kindex[i]++;
					K[i] = possible[i][Kindex[i]];
					backtracked++;
					//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
					//System.out.println("# of times backtracked = " + backtracked);
					didBack = true;
					break;
				}
			}
			if (!didBack) backtracked++;
		}
		
		if (backtracked == (threshold-1)*(L-3-3)+1 ) {
			searched = true;
			solved = false;
			//System.out.println("Breaking out now as # of backtracked = " + backtracked);
		}
	} 
	return solved;
}
	
	public Boolean bruteForce(String filename) throws IOException {
		
		Boolean solved = false;
		file = filename;
		FileInputStream in = null;
		Integer o1;
			
			in = new FileInputStream(file);	
			
			L = getL(in);
			rounds = getRounds(in);
			
			K = new Integer[L];
			S = new Integer[N];

			int tuple[][] = new int[5][4];
			
			for ( int k=0; k<5; k++ ) {	
				tuple[k][0] = in.read();;
				tuple[k][1] = in.read();;
				tuple[k][2] = in.read();;
				tuple[k][3] = in.read();;
			}
			in.close();
			
			for ( int a=0; a<90; a++ )
			{
				K[3] = a;
				for ( int b=0; b<90; b++ )
				{
					K[4] = b;
					for ( int c=0; c<90; c++ )
					{
						K[5] = c;
						for ( int d=0; d<90; d++ )
						{
							K[6] = d;
							for ( int e=0; e<90; e++ )
							{
								K[7] = e;
								
								in = new FileInputStream(file);
								for(int i=0; i<8; i++) in.read();
								int found = 0 ;
								
								
								K[0] = tuple[0][0];	K[1] = tuple[0][1];	K[2] = tuple[0][2];	o1 = tuple[0][3];
								if ( ksa_check(K,o1) ) 
								{
									K[0] = tuple[1][0];	K[1] = tuple[1][1];	K[2] = tuple[1][2];	o1 = tuple[1][3];
									if ( ksa_check(K,o1) ) 
									{
										K[0] = tuple[2][0];	K[1] = tuple[2][1];	K[2] = tuple[2][2];	o1 = tuple[2][3];
										if ( ksa_check(K,o1) ) 
										{
											K[0] = tuple[3][0];	K[1] = tuple[3][1];	K[2] = tuple[3][2];	o1 = tuple[3][3];
											if ( ksa_check(K,o1) ) 
											{
												K[0] = tuple[4][0];	K[1] = tuple[4][1];	K[2] = tuple[4][2];	o1 = tuple[4][3];
												if ( ksa_check(K,o1) ) 
												{
													System.out.println("\nCompleted bruteForce " + filename + " L:" + L + " rounds:" + rounds);
													System.out.print("K[] : ");
													for ( int it = 3; it<L; it++ ) System.out.print(K[it] + " ");
													System.out.println();
													solved = true;
													return solved;
												}
											}
										}
									}
								}
								
								/*
								for ( int k=0; k<5; k++ ) {					
									K[0] = tuple[k][0];
									K[1] = tuple[k][1];
									K[2] = tuple[k][2];
									o1 = tuple[k][3];
									
									if (ksa_check(K,o1) ) found++;
								}
								
								if ( found>=5 ) {
									System.out.println("\nCompleted bruteForce " + filename + " L:" + L + " rounds:" + rounds);
									System.out.print("K[] : ");
									for ( int it = 3; it<L; it++ ) System.out.println(K[it] + " ");
									System.out.println();
									solved = true;
									return solved;
								}*/
							}
						}
					}
				}
				System.out.println("a = " + a);
			}
			
			return solved;
	}
	
	public Boolean backtrackComboSolver(String filename, int attack) throws IOException {
		
		Boolean solved = false;
		Boolean searched = false;
		Integer backtracked = 0;
		file = filename;
		FileInputStream in = null;		
		Integer possible[][];
		Integer startp = 3;
		Integer Kindex[];
		Boolean explored[];
		Integer guessnum = 3;
		
		in = new FileInputStream(file);
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; 
		Kindex = new Integer[L];
		explored = new Boolean[L];
		for (int i=0; i<L; i++) { K[i] = Kindex[i] = 0; explored[i] = false; }
		possible  = new Integer[L][threshold];
			
		while(!searched) {
						
			for ( p=startp; p<L-3; p++) {
				
				//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;
				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq = new Integer[N]; for (int i=0; i<N; i++) freq[i] = 0;	
				
				for( Integer i=1; i<=rounds ; i++)
				{
					IV[0] = in.read();
					IV[1] = in.read();
					IV[2] = in.read();
					o1 = in.read();
					//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
										
					S = new Integer[N];
					
					K[0] = IV[0];
					K[1] = IV[1];
					K[2] = IV[2];		
					
					switch (attack) {
					
					case 1:		j_p_1 = ksa();
								if( S[1] < p ) {
									if( Math.floorMod( (S[1] + S[S[1]]), N) == p ) {
										if( (S[o1] != 1) && (S[o1] != S[S[1]]) ) {
											//System.out.println("Korek A_s5_1 at i = " + i);
											//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
											Integer index = Arrays.asList(S).indexOf(o1);
											if (index>=0) {
												Integer kp = Math.floorMod( (index - S[p] - j_p_1), N);
												if ( kp>=0 && kp<90 )	freq[kp]++;
												//System.out.println("K[" + p + "] = " + kp + ".");
												//break;
											}
											
										}
									}
								}
								break;
								
					case 2:		j_p_1 = ksa();
								if( S[1]== p ) // || (S[1] == 0 && S[p] == p)
								{
									if( o1 == p ) 
									{
										//System.out.println("Korek A_s13 at i = " + i);
										//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
										Integer index = Arrays.asList(S).indexOf(0);
										if (index>=0) {
											Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
											//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1);
											if ( kp>=0 && kp<N )	freq[kp]++;							
										}
									}
								}
								break;
								
					case 3:		j_p_1 = ksa();
								if( S[1] == p ) {
									if( o1 == Math.floorMod((1-p),N) ) {
										//System.out.println("Korek A_u13_1 at i = " + i);
										//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
										Integer index = Arrays.asList(S).indexOf(o1);
										if (index>=0) {
											Integer kp = Math.floorMod((index - S[p] - j_p_1), N);
											//System.out.println("K[" + p + "] = " + kp + ", index = " + index + ", " + S[p] + ", " + j_p_1 + ", o1 = " + o1);
											if ( kp>=0 && kp<N )	freq[kp]++;
										}
									}
								}
								break;
								
					case 4:		j_p_1 = ksa();
								if( S[1] == p ) {
									if( o1!=p && o1!=Math.floorMod((1-p),N) ) {
										if( (Arrays.asList(S).indexOf(o1)<p) && 
												( Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N)) !=1 ) 
											) {
											//System.out.println("Korek A_u5_1 at i = " + i);
											//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
											
											Integer jp = Arrays.asList(S).indexOf(Math.floorMod( (Arrays.asList(S).indexOf(o1))-p ,N));
											Integer kp = Math.floorMod((jp - S[p] - j_p_1), N);
											
											if ( kp>=0 && kp<N )	freq[kp]++;
											//System.out.println("K[" + p + "] = " + kp + ".");
											
										}
									}
								} 
								break;
								
					case 5:		j_p_1 = ksa();
								if ( S[p]==1 && Arrays.asList(S).indexOf(o1)==2 ) {
									//System.out.println("Korek A_u5_2 at i = " + i);
									//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
									Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
									if ( kp>=0 && kp<N )	freq[kp]++;
								}
								break;
								
					case 6:		j_p_1 = ksa();
								if ( S[p] == p ) {
									if ( S[1] == 0 ) {
										if ( o1 == p ) {
											//System.out.println("Korek A_u13_2 at i = " + i);
											//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
											Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
											if ( kp>=0 && kp<N )	freq[kp]++;
										}
									}
								}
								break;
								
					case 7:		j_p_1 = ksa();
								if ( S[p] == p ) {
									if ( S[1] == o1 ) {
										if ( S[1] == Math.floorMod( 1-p, N) ) {
											//System.out.println("Korek A_u13_3 at i = " + i);
											//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
											Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
											if ( kp>=0 && kp<N )	freq[kp]++;
										}
									}
								}
								break;
								
					case 8:		j_p_1 = ksa();
								if ( S[p] == p ) {
									if ( S[1] == o1 ) {
										if ( S[1] >= Math.floorMod( (-1*p), N) ) {
											if ( S[1] == Math.floorMod( (Arrays.asList(S).indexOf(o1) - p) , N) ) {
												if ( Arrays.asList(S).indexOf(o1) != 1 ) {
													//System.out.println("Korek A_u5_3 at i = " + i);
													//System.out.println("IV:" + IV[0] + " " + IV[1] + " " + IV[2] + ", o1 = " + o1);
													Integer kp = Math.floorMod( 1-S[p]-j_p_1 , N);
													if ( kp>=0 && kp<N )	freq[kp]++;
												}
											}
										}
									}
								}
						
					
					}
					
				}	
					
				Integer freq_copy[] = freq.clone();
				Arrays.sort(freq_copy, Collections.reverseOrder());
				K[p] = Arrays.asList(freq).indexOf( freq_copy[0] );
			
				for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq).indexOf( freq_copy[j] );
				//Kindex[p] = 0;
			}
			in.close();
			
			//System.out.println("Randomizing");
			for ( int a=0; a<90; a++) {
				K[L-3] = a;
				for ( int b=0; b<90; b++ ) {
					K[L-2] = b;
					for ( int c=0; c<90; c++ ) {
						K[L-1] = c;
						
						FileInputStream verify = new FileInputStream(file);
						for(int i=0; i<8; i++) verify.read();
						int found = 0;
						
						for (int k=1; k<=5; k++) {
							K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
							if ( ksa_check( K, R) )	found++;
						}
						verify.close();
						
						if( found >= 5) {
							System.out.println("\nCompleted backtrackComboSolver" + attack + " " + filename + " L:" + L + " rounds:" + rounds);
							System.out.print("K[] : ");
							for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
							System.out.println(); System.out.println();
							searched = solved = true;							
							return true;
						}
						
					}
				}
			}
			
			/*System.out.print("Kindex : ");
			for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
			System.out.println();
			for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			System.out.println();*/
			
			if (!searched) {
				//System.out.println("Backtracking");
				Boolean didBack = false;
				for (int i=3; i<L-3; i++) {
					if( (Kindex[i] <= threshold-1) && (!explored[i]) )
					{
						if ( Kindex[i] == threshold-1 ) { explored[i] = true; Kindex[i] = 0; K[i] = possible[i][0]; continue; }
						startp = i+1;
						Kindex[i]++;
						K[i] = possible[i][Kindex[i]];
						backtracked++;
						//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
						//System.out.println("# of times backtracked = " + backtracked);
						didBack = true;
						break;
					}
				}
				if (!didBack) backtracked++;
			}
			
			if (backtracked == (threshold-1)*(L-3-3)+1 ) {
				searched = true;
				solved = false;
				//System.out.println("Breaking out now as # of backtracked = " + backtracked);
			}
		} 
		return solved;
	}
	
	public Boolean backtrackComboLecture(String filename) throws IOException {
		
		Boolean solved = false;
		Boolean searched = false;
		Integer backtracked = 0;
		file = filename;
		FileInputStream in = null;		
		Integer possible[][];
		Integer startp = 3;
		Integer Kindex[];
		Boolean explored[];
		Integer guessnum = 3;
		
		in = new FileInputStream(file);
		L = getL(in);
		rounds = getRounds(in);
		in.close();
		
		K = new Integer[L]; 
		Kindex = new Integer[L];
		explored = new Boolean[L];
		for (int i=0; i<L; i++) { K[i] = Kindex[i] = 0; explored[i] = false; }
		possible  = new Integer[L][threshold];
			
		while(!searched) {
						
			for ( p=startp; p<L-3; p++) {
				
				//System.out.println("Solving for K[] index " + startp + " to " + (L-3-1));
				
				in = new FileInputStream(file);
				for(int i=0; i<8; i++) in.read();
				
				Integer temp,t;
				Integer IV[] = new Integer[3];
				Integer o1;
				Integer j_p_1;
				freq_lec = new Integer[N]; for (int i=0; i<N; i++) freq_lec[i] = 0;	
				
				for( Integer i=1; i<=rounds ; i++)
				{
					IV[0] = in.read();
					IV[1] = in.read();
					IV[2] = in.read();
					o1 = in.read();
					//System.out.println(IV[0] + " " + IV[1] + " " + IV[2] + " " + o1);
										
					S = new Integer[N];
					
					K[0] = IV[0];
					K[1] = IV[1];
					K[2] = IV[2];		
					
					j_p_1 = ksa_lec();
					
				}
			
			
				Integer freq_copy[] = freq_lec.clone();
				Arrays.sort(freq_copy, Collections.reverseOrder());
				K[p] = Arrays.asList(freq_lec).indexOf( freq_copy[0] );
			
				for (int j=0; j<threshold; j++) possible[p][j] = Arrays.asList(freq_lec).indexOf( freq_copy[j] );
				//Kindex[p] = 0;
			}
			in.close();
			
			//System.out.println("Randomizing");
			for ( int a=0; a<90; a++) {
				K[L-3] = a;
				for ( int b=0; b<90; b++ ) {
					K[L-2] = b;
					for ( int c=0; c<90; c++ ) {
						K[L-1] = c;
						
						FileInputStream verify = new FileInputStream(file);
						for(int i=0; i<8; i++) verify.read();
						int found = 0;
						
						for (int k=1; k<=5; k++) {
							K[0] = verify.read(); K[1] = verify.read(); K[2] = verify.read(); R = verify.read();
							if ( ksa_check( K, R) )	found++;
						}
						verify.close();
						
						if( found >= 5) {
							System.out.println("\nCompleted bruteComboLecture " + filename + " L:" + L + " rounds:" + rounds);
							System.out.print("K[] : ");
							for(int it = 3; it<L; it++) System.out.print(K[it] + " ");
							System.out.println(); System.out.println();
							searched = solved = true;							
							return true;
						}
						
					}
				}
			}
			
			/*System.out.print("Kindex : ");
			for ( int ki = 3; ki<L; ki++) System.out.print(Kindex[ki] + " ");
			System.out.println();
			for ( int t=3; t<L; t++ ) System.out.print("K[" + t + "]=" + K[t] + " ");
			System.out.println();*/
			
			if (!searched) {
				//System.out.println("Backtracking");
				Boolean didBack = false;
				for (int i=3; i<L-3; i++) {
					if( (Kindex[i] <= threshold-1) && (!explored[i]) )
					{
						if ( Kindex[i] == threshold-1 ) { explored[i] = true; Kindex[i] = 0; K[i] = possible[i][0]; continue; }
						startp = i+1;
						Kindex[i]++;
						K[i] = possible[i][Kindex[i]];
						backtracked++;
						//System.out.println("Updated K[" + i +"] = " + K[i] + " with Kindex = " + Kindex[i]);
						//System.out.println("# of times backtracked = " + backtracked);
						didBack = true;
						break;
					}
				}
				if (!didBack) backtracked++;
			}
			
			if (backtracked == (threshold-1)*(L-3-3)+1 ) {
				searched = true;
				solved = false;
				//System.out.println("Breaking out now as # of backtracked = " + backtracked);
			}
		} 
		return solved;
	}
		
	
}
