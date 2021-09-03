package com.usim.ulib.notmine.alg;

import java.io.IOException;
import java.io.PrintWriter;

import umontreal.iro.lecuyer.randvar.LognormalGen;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.rng.RandomStream;
/***
 * 
 * Simulation 
 * -Edge cost as mean. 
 * -Log-normal distribution.
 * -Simulation on edge-costs.
 * 
 * @author emartinezmasip
 * @version 20160418
 */
public class Stochastic {

	
	public static double std(double[] vector, double mean) {
            double var = 0;
            for(int i = 0; i < vector.length; i++) {
                    var += Math.pow(vector[i] - mean, 2);
            }
            return var/vector.length;
	}
	
	
	
	
	public static Solution simulate(Solution sol, int simulations, RandomStream rs, Test t, Inputs inputs,int type, int NumBestSol) {
            		double[] sims = new double[simulations];
            		double[] simDistance = new double[simulations];
            		int CustoNoServe = 0;

            
            		//Obtain average cost of routing
   		    		double meanCostRot = 0;
   		    		int totEgdes = 0;
   		    		double reliabilityRoutes [] = new double[sol.getRoutes().size()];
   		    		double expProfitRoutes [] = new double[sol.getRoutes().size()];
   		    		double totProfit = 0;
   		    		int nRoute = 0;
                	sol.setReliability(1);
                	sol.setStochScore(0);
                	
                    //foreach route in sol                  
                    for(Route r: sol.getRoutes()) {
       		    		CustoNoServe = 0;
       		    		totProfit = 0;
                        for(int i = 0; i < simulations; i++) {         	
	                    		double rCost = 0; //cost stoch route -> 0  
	                    		double profit = 0;
	                            	//foreach edge in route
	                            	for(Edge e: r.getEdges()) {
	                            		double mean = 0;
	                            		mean = e.getCosts();
	                            		if (mean>0) {
	                            			double newArc =	getStochasticValue(rs,mean,t.getVariance());
	                            			rCost += newArc;
	                            			if(rCost > inputs.gettMax() ){ //Me paso del tiempo máximo permitdo
	                            				profit = 0;
	                            				CustoNoServe++;
	                            				break;	
	                            			}
	                            			profit += e.getEnd().getProfit(); 
	                            		}                                        
	                               }//End edges
	                               totProfit += profit;
                        }//END simulations
                    	reliabilityRoutes[nRoute]  = (double) (simulations - CustoNoServe)/simulations;
                    	expProfitRoutes[nRoute] = totProfit/simulations;
                    	
                    	
                    	//Accumulate reli and profit
                    	sol.setReliability(sol.getReliability() * reliabilityRoutes[nRoute]);
                    	sol.setStochScore(sol.getStochScore() + expProfitRoutes[nRoute]);
                    	nRoute++;
                    }// End Routes
                    
  
          
           
                    if(type != -1){ //Only long sims
                    	printSim(sims, simDistance,t,type,NumBestSol); //Imprimir los valores de las simulaciones
                    }
            return sol;
	}
	
	
	
	
	
   public static double getStochasticValue(RandomStream stream, double mean, float variance) {

       double squareSigma = Math.log(1 + (variance / Math.pow(mean, 2)));
       double mu = Math.log(mean) - squareSigma / 2;
       double sigma = Math.sqrt(squareSigma);
       return LognormalGen.nextDouble(stream, mu, sigma);

   }
      
   
   public static void printSim(double[] custo, double[] dist, Test t, int type, int num){
	   try{
		PrintWriter out = null;
		String filename = "";
		if(type == 0){
			 filename = "DetSim_" + t.getInstanceName() + ".txt";
		}else if(type == 1){
			 filename = "CustSim_"+num +"_"+ t.getInstanceName() + ".txt";
		}else{
			 filename = "DistSim_" + num +"_"+ t.getInstanceName() + ".txt";
		}
		    
		    out = new PrintWriter(filename);

		   for (int i = 0; i < custo.length; i++){
					out.printf("%.4f \t %.4f\n",custo[i],dist[i]);
			}//end for
		    out.close();
	   }
	catch (IOException exception) 
	{   
		System.out.println("Error processing output file: " + exception);
	}
	   
   }
	
	
	
	
	
	
}      
