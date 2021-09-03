package com.usim.ulib.notmine.alg;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Outputs
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private Solution bestInitSol;
    private Solution bestSol;
    private String instanceName;
    private float lambda;
    private float variance;
    private float runningTime;
    private double alpha; 
    private double beta;
    private double maxTravel;
    private int seed;
    private boolean exe;
	private ArrayList<Outputs> list = null;
    private ArrayList<Solution> finalsol = null;




	public void setList(){
    	list.add(this);
    }
    
   
    public Outputs()
    {   bestInitSol = null;
        bestSol = null;
        instanceName = null;
        lambda = 0;
        variance = 0;
        runningTime = 0;
        finalsol = new ArrayList<Solution>();
    }

    /* SET METHODS */
    public void setBestInitSol(Solution aSol){bestInitSol = aSol;}
    public void setOBSol(Solution obSol){bestSol = obSol;}
    public void setInstanceName(String name){instanceName = name;}
    public void setLambda(float lam){lambda = lam;}
    public void setK(float k){variance = k;}
    public void setRunningT(float t){runningTime = t;}
	public void setAlpha(double alpha) {this.alpha = alpha;}
	public void setBeta(double beta) {this.beta = beta;}
	public void setMaxTravel(double maxTravel) {this.maxTravel = maxTravel;}
	public void setExe(boolean exe) {this.exe = exe;}
	public void setseed(int seed) {this.seed = seed;}
    public void addSoltoArray (Solution solList){finalsol.add(solList); }
	
	
    /* GET METHODS */
    public Solution getBestInitSol(){return bestInitSol;}
    public Solution getOBSol(){return bestSol;}
    public String getInstanceName(){return instanceName;}
    public float getLambda(){return lambda;}
    public float getK(){return variance;}
    public float getRunningT(){return runningTime;}
	public double getAlpha() {return alpha;}
	public double getBeta() {return beta;}
	public double getSeed() {return seed;}
	public double getMaxTravel() {return maxTravel;}
	public boolean isExe() {return exe;}
    public ArrayList<Solution> getFinalsol() {return finalsol;}


	public static void printSol(ArrayList<Outputs> list){
		try 
		{   
			PrintWriter out = new PrintWriter("GlobalOutput.txt");
			for (int i = 0; i < list.size(); i++){
				Solution singleList = list.get(i).getOBSol(); //Solution
                double coste = 0.0;
                double profit = 0.0;
				out.printf("%s\n", list.get(i).getInstanceName());

				for (int j = 0; j < singleList.getRoutes().size(); j++){
					Route r = singleList.getRoutes().get(j); //Obtengo la ruta
					out.printf("Ruta: %d Coste ruta: %f\n", j, r.getCosts());
					out.printf("        Profit ruta: %f\n", r.getScore());
					Node last = null;
					for(Edge e: r.getEdges()){ //obtengo edges
						out.printf("%d -- ", e.getOrigin().getId());	
						last = e.getEnd();
					}
					coste += r.getCosts();
					out.printf("\n");
				} 
				out.printf("Coste total Solution: %f \n",coste);
				out.printf("Profit total Solution: %f \n",singleList.getTotalScore());
				out.print("\n\n");
			}//end for
			out.close();
		} 
		catch (IOException exception) 
		{   System.out.println("Error processing output file: " + exception);
		}
		
		/*
		 * This will compare the best known solutions with our solutions
		 */
		try 
		{   
			PrintWriter out = new PrintWriter("SolutionComparison.txt");
			out.println("Instance, Cost, Profit, BKS");
			for(Outputs o : list){
				Solution sol = o.getOBSol();
				int profit = (int) sol.getTotalScore();
				double distance = sol.getTotalCosts();
				Integer bestProfit = BKS.bestSolution(o.instanceName);
				if(bestProfit == null) bestProfit = -1;
				out.printf(o.instanceName + ", %f, %d, %d", distance,profit,bestProfit);
				out.println();
			}
			out.close();
		}
		catch(IOException exception){
			System.out.println("Error processing output file: " + exception);
		}
	}//end method
    
  
	public static void printSolST(ArrayList<Outputs> list, Test aTest){
		try 
		{   
			PrintWriter out = new PrintWriter("ResumeSols.txt");
			out.printf("TypeExec	Instance	Seed	Profit StoProfit	Reliability	RunTime");
			for(Outputs o : list){
				Solution sol = o.getOBSol();
				out.println();
				//out.printf("%s	%d	%d	%d	%f	%f\n", o.getInstanceName(),o.getSeed(),sol.getTotalScore(),sol.getStochClientsNoServed(),sol.getPercentTimesViolated(),sol.getTime());
				//out.printf("%s	%d\n", o.getInstanceName(),o.getSeed());
				//out.printf("Stochastic	"+o.getInstanceName()+"		"+(int)o.getSeed()+"	"+sol.getTotalScore()+"		"+(double)sol.getPercentTimesViolated()*100+"	"+(double)(1-sol.getPercentTimesViolated())*100+"	"+sol.getDistanceViolated()+"	"+sol.getTime());
				if(aTest.isModeExe() == true){
					out.printf("Det	%s",o.getInstanceName());	
				}else{
					out.printf("Sto	%s",o.getInstanceName());					
				}	
				out.printf("	"+(int)o.getSeed());
				out.printf("	%.2f",sol.getTotalScore());
				out.printf("	%.2f", sol.getStochScore());
				out.printf("	%.2f",sol.getReliability());
				out.printf("	%.2f",sol.getTime());
				
			}
			out.close();
		} 
		catch (IOException exception) 
		{   System.out.println("Error processing output file: " + exception);
		}
	}//end method
}
