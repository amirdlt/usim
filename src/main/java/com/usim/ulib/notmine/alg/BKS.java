package com.usim.ulib.notmine.alg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BKS{
	private static Map<String, Integer> BestKnownSolutions = new HashMap<String, Integer>();
	
	public static void readBKS(String path){
		Scanner scanner;
		try {
			scanner = new Scanner(new File(path));
		    while(scanner.hasNextLine()){
		    		String line = scanner.nextLine();
		    		String[] sLine = line.split(",");
		    		String instance = sLine[0];
		    		Integer profit = Integer.decode(sLine[1]);
		    		BestKnownSolutions.put(instance, profit);
		    }
		    scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		
	}
	
	public static Integer bestSolution(String instance_name){
		return BestKnownSolutions.get(instance_name);
	}
}
