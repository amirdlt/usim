package com.usim.ulib.notmine.alg;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Generates a list of tests to be run.
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130807
 */
public class TestsManager
{
    public static ArrayList<Test> getTestsList(String testsFilePath)
    {   ArrayList<Test> list = new ArrayList<Test>();

        try
        {   FileReader reader = new FileReader(testsFilePath);
            Scanner in = new Scanner(reader);
            // The two first lines (lines 0 and 1) of this file are like this:
            //# instance | maxTime(sec) | ...
            // A-n32-k5       30      ...
            in.useLocale(Locale.US);
            while( in.hasNextLine() )
            {  
            	if(in.hasNext()){
            		String s = in.next();
            		if (s.charAt(0) == '#') // this is a comment line
                        in.nextLine(); // skip comment lines
                    else
                    {   String instanceName = s; // e.g.: A-n32-k5
                        float maxTime = in.nextFloat(); // max computational time (in sec)
                        float beta1 = in.nextFloat(); // distribution parameter
                        int seed = in.nextInt(); // seed for the RNG
                        int shortSim = in.nextInt(); // seed for the RNG
                        int longSim = in.nextInt(); // seed for the RNG
                        float var = in.nextFloat();
                        boolean exe  = in.nextBoolean();
                        Test aTest = new Test(instanceName,maxTime, beta1, seed,shortSim,longSim,var,exe);
                        list.add(aTest);
                    }
               }
            	else in.nextLine();
                
            }
            in.close();
        }
        catch (IOException exception)
        {   System.out.println("Error processing tests file: " + exception);
        }
        return list;
    }
}
