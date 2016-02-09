package wep;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

public class Solve {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		int listOfFiles[] = { 12, 15, 16, 17, 18, 19,
							  20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 
							  31, 32, 33, 35, 36, 37, 38, 39, 
							  41, 42, 43, 44, 46, 47, 48, 49, 50,
							  51, 52, 55, 56, 58, 59, 60
							}; 
		
		System.out.println("Running");
		
		String prepend = new String("C:\\Users\\user\\Desktop\\Sem 3\\CS4236\\Project\\A");
		
		for( int i=0; i<listOfFiles.length; i++ ) {
			
			DecimalFormat formatter = new DecimalFormat("00");
			
			String filenum = formatter.format(listOfFiles[i]);
		
			String filename = new String(prepend+filenum+".data"); 
			System.out.println(filename);
			
			Solver solver = new Solver();
			solver.solve1(filename);
			solver.solve2(filename);
			solver.solve3(filename);
			solver.solve4(filename);
			solver.solve5(filename);
			solver.solve6(filename);
			solver.solve7(filename);
			solver.solve8(filename);
			solver.lecture(filename);
			solver.solveAll(filename);
			if (solver.bruteForce(filename)) continue;
			
			System.out.println("bruteComboLecture");
			if (solver.bruteComboLecture(filename)) continue;
			System.out.println("bruteComboSolver1");
			if (solver.bruteComboSolver1(filename)) continue;
			System.out.println("bruteComboSolver2");
			if (solver.bruteComboSolver2(filename)) continue;
			System.out.println("bruteComboSolver3");
			if (solver.bruteComboSolver3(filename)) continue;
			System.out.println("bruteComboSolver4");
			if (solver.bruteComboSolver4(filename)) continue;
			System.out.println("bruteComboSolver5");
			if (solver.bruteComboSolver5(filename)) continue;
			System.out.println("bruteComboSolver6");
			if (solver.bruteComboSolver6(filename)) continue;
			System.out.println("bruteComboSolver7");
			if (solver.bruteComboSolver7(filename)) continue;
			System.out.println("bruteComboSolver8");
			if (solver.bruteComboSolver8(filename)) continue;
			
			System.out.println("backtrackComboLecture");
			if (solver.backtrackComboLecture(filename)) continue;
			
			for ( int attack = 1; attack<=8; attack++) {
				System.out.println("backtrackComboSolver" + attack);
				if (solver.backtrackComboSolver(filename, attack)) break;
			}
			
		}
		System.out.println("\nCompleted program");
			
	}

}