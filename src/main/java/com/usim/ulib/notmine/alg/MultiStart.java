package com.usim.ulib.notmine.alg;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import umontreal.iro.lecuyer.probdist.Distribution;
import umontreal.iro.lecuyer.probdist.LognormalDist;
import umontreal.iro.lecuyer.randvar.LognormalGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.LFSR113;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.rng.RandomStreamBase;

/**
 * Iteratively calls the RandCWS and saves the best solution.
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 13810
 */
public class MultiStart 
{
    /* 0. Instance fields and class constructor */
    private Test aTest;
    private Inputs inputs; // contains savingsList too
    private Random rng;
    private Solution[] initialSols = new Solution[6]; // (0):0% (1):25% ... (5):decent.
    private Solution initialSol = null;
    private Solution bestSol = null;
    private Solution bestSolDist = null;
    private Solution bestSolVio = null;
    private Solution newSol = null;
    private Solution baseSol = null;
    private Outputs outputs = new Outputs();
    RandomStreamBase stream = new LFSR113(); // L'Ecuyer stream
    Node[] auxTempNodes; 
    List<Double> inventoryCost= new ArrayList<>();
    List<Double> routingCost= new ArrayList<>();
    ArrayList<Solution> outList = new ArrayList<Solution>();
    
    MultiStart(Test myTest, Inputs myInputs, Random myRng)
    {
        aTest = myTest;
        inputs = myInputs;
        rng = myRng;
    }
    
    
    

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%% Deterministic %%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public Solution multiCW(Solution sol, double alpha){
    	Solution newsol = null;
        long start = ElapsedTime.systemTime();
        double elapsed = 0.0;
        boolean firstime = true;
        
  	    initialSol = new Solution(sol); //Lo llamo 1 vez fuera del bucle para generar una solución inicial
  	    System.out.println("Solucion inicial: " + initialSol.getTotalScore());
  	    baseSol = initialSol;
        bestSol = initialSol;  
        InputsManager.generateSavingsList(inputs,alpha);

   
        while( elapsed < aTest.getMaxTime() )
        {     
    	 newSol = new Solution(CWS.solve(inputs,aTest,rng,1));  //APlico C&W bias
    	 Collections.sort(newSol.getRoutes());  //Me quedo con las N mejores rutas (igual al numero de vehiculos)
		 double totalCost = 0.0;
		 double totalProfit = 0.0;
		 int used_veh = Math.min(inputs.getVehNumber(),newSol.getRoutes().size());
		 newSol.sliceSolution(used_veh);
		 for(int i = 0; i < used_veh; i++){
				Route r = newSol.getRoutes().get(i);
				totalCost += r.getCosts();
				totalProfit += r.getScore();
		}	
		 newSol.setTotalCosts(totalCost);
		 newSol.setTotalScore(totalProfit);
 
    	 if(newSol.getTotalScore() > baseSol.getTotalScore()){
    		 baseSol = newSol;
    		 
    		 if(newSol.getTotalScore() > bestSol.getTotalScore()){
    			 bestSol = newSol;
    			 bestSol.setTime(elapsed);
    			 System.out.println("Mejoro SOL : " + bestSol.getTotalScore());
    		 }
    		 
    	 }

          elapsed = ElapsedTime.calcElapsed(start, ElapsedTime.systemTime());
        }
    	    	 
    	 return bestSol;
    }
    
    
    
    
    
    
    
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%% Estocastica %%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public Object[] multiSto(){
    	Solution newsol = null;
    	
    	Object[] BestSols = new Object[2];
    	
    	BestSolutions listBestSols = new BestSolutions(); //Best solutions customers violated
    	BestSolutionsDiff listBestSolsDist = new BestSolutionsDiff(); //Best solutions distance violated
    	

        long start = ElapsedTime.systemTime();
        double elapsed = 0.0;
        boolean firstime = true;
        
  	    initialSol = new Solution(solve()); //Lo llamo 1 vez fuera del bucle para generar una solución inicial
  	    System.out.println("Solucion inicial: " + initialSol.getTotalScore());
  	    initialSol = Stochastic.simulate(initialSol,aTest.getShortSim(), aTest.getRandomStream(),aTest,inputs,-1,0); //Fast simulation
  	   
  	    baseSol = initialSol;
        bestSol = initialSol;
        bestSolDist = initialSol;
        bestSolVio = initialSol;
        
		 PairBestCust solToAdd = new PairBestCust(initialSol,initialSol.getPercentTimesViolated());
		 listBestSols.addSolution(solToAdd);
		 
		 PairBestDist solToAddDist = new PairBestDist(initialSol,initialSol.getDistanceViolated());
		 listBestSolsDist.addSolution(solToAddDist);
        
                
       
        while( elapsed < aTest.getMaxTime() )
        {     
    	 newSol = new Solution(solve()); //Resuelvo 

    	 if(newSol.getTotalScore() > bestSol.getTotalScore()){ //Si el escore es mayor lo tomo como solución prometedora y simulo
    		 bestSol = new Solution(newSol);
             newSol = Stochastic.simulate(newSol, aTest.getShortSim(), aTest.getRandomStream(), aTest, inputs,-1,0); //Fast simulation
    			 
    			 //Percetange customers violated
    			 if(newSol.getPercentTimesViolated() <= bestSolVio.getPercentTimesViolated()){
    				 bestSolVio = new Solution(newSol);
    				 bestSolVio.setTime(elapsed);
        			 solToAdd = new PairBestCust(bestSolVio,newSol.getPercentTimesViolated());
        			 listBestSols.addSolution(solToAdd);
        			 System.out.println("Mejoro SOL : " + bestSolVio.getTotalScore());
        		 }
    			 
    			 
    			 //Percetange distance violated
    			 if(newSol.getPercentTimesViolated() <= bestSolDist.getPercentTimesViolated()){ //Percetange customers violated
    				 bestSolDist = new Solution(newSol);
    				 bestSolDist.setTime(elapsed);
    				 solToAddDist = new PairBestDist(bestSolDist,newSol.getDistanceViolated());
        			 System.out.println("Mejoro SOL : " + bestSolDist.getDistanceViolated());
        			 listBestSolsDist.addSolution(solToAddDist);
        		 } 
    	 }

          elapsed = ElapsedTime.calcElapsed(start, ElapsedTime.systemTime());
        }
    	    	 
        //Final simulation (long simulation)
        //BEst solutions violated customers
        Iterator iterator;
        iterator = listBestSols.getSolutions().iterator();
        int c = 0;
        while(iterator.hasNext()){            	
        	  PairBestCust pairIter = (PairBestCust) iterator.next();
        	  Solution sol = pairIter.getkey();
        	  sol = Stochastic.simulate(sol,aTest.getLongSim(), aTest.getRandomStream(),aTest,inputs,1,c);
        	  c++;
          }
         
          
           //BEst solutions violated distance
         BestSolutionsDiff auxListBestSolsDist = new BestSolutionsDiff();
         auxListBestSolsDist = listBestSolsDist;
         listBestSolsDist  = new BestSolutionsDiff();
         
         iterator = auxListBestSolsDist.getSolutions().iterator();
         c = 0;
          while(iterator.hasNext()){            	
        	  PairBestDist pairIter = (PairBestDist) iterator.next();
          	  Solution sol = pairIter.getkey();
              sol = new Solution(Stochastic.simulate(sol,aTest.getLongSim(), aTest.getRandomStream(),aTest,inputs,2,c));
          	  PairBestDist newPairIter = new PairBestDist(sol,sol.getDistanceViolated());
          	  listBestSolsDist.addSolution(newPairIter);
            }
          
          ////test
          iterator = listBestSolsDist.getSolutions().iterator();
          while(iterator.hasNext()){            	
        	  PairBestDist pairIter = (PairBestDist) iterator.next();
          	  Solution sol = pairIter.getkey();
          	  System.out.println("Solucion distancia " + sol.getDistanceViolated());
            }
          
          ///fin test
          
         
      	BestSols[0] = listBestSols;
        BestSols[1] = listBestSolsDist;
         
    	 return BestSols;
    }
    
    
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%% Heuristica inserccion local %%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public Solution solve()
    {
        // 1. Obtain the number of vechicles to define the routes
    	Solution sol = new Solution();
    	sol.setTotalCosts(0.0);
    	int vehicles = inputs.getVehNumber();
    	InputsManager.generateDepotEdges(inputs);
    	ArrayList<Node> availableNodes = new  ArrayList<Node>();
        
    	for(int n = 1; n < inputs.getNodes().length-1; n++){ //Avoid first (init depot)
    		availableNodes.add(inputs.getNodes()[n]);
    	}
    	
    	Node initDepot = inputs.getNodes()[0];
    	Node endDepot = inputs.getNodes()[inputs.getNodes().length-1];
    	
    	for( int i = 0; i < vehicles; i++ ){   
    	  Route route = new Route();  //Create a route
    	  boolean timeExceed = false; 
    	  int iterations = 0;
         
    	  while((timeExceed == false) && (iterations < 100000)){
        	  if(route.getEdges().isEmpty() == true){ //if route is empty, select a node to go from the depot
        		  int initCustomer = rng.nextInt(availableNodes.size()-1);
        		  Node initNode = availableNodes.get(initCustomer);
        		  Edge idEdge = new Edge(initDepot,initNode);
        		  double initCost = idEdge.calcCosts(initDepot,initNode);
        		  
      
        		  double costToFinalNode = idEdge.calcCosts(initNode,endDepot); //cal cost firstnode - end node
        		  																//Depot - First Node - End Depot
        		  if(initCost + costToFinalNode <= inputs.gettMax() ){ //Exccess max time???
        			  availableNodes.remove(initCustomer); //Borro el nodo de available nodes
        			  idEdge.setCosts((idEdge.calcCosts(initDepot,initNode)));
        			  route.getEdges().add(idEdge);
            		  route.setCosts(idEdge.getCosts() + 0); //Coste de viaje + coste de servicio (0 cambiar por service time si se cambian de instancias)
            		  route.setScore(idEdge.getEnd().getProfit());
        		  }
        		  
        	  }else{ //Route is not empty
        		  
        		  //Select and edge with bias
        		  int index = getRandomPosition(aTest.getBeta1(), rng, route.getEdges().size()); //Bias randomization
        		  Edge edge = route.getEdges().get(index);
        		  
        		  //calculate function evaluation for all the availables nodes for the selected edge
        		  //caculate distance and order between nodes(j's) and Edge(p,q)  return list in increasing order
        		  TreeSet<PairBest> vectDist = calEvaluationFunction(availableNodes,edge);
        		  if(availableNodes.size() == 0){
        			  break; // there aren't free nodes (all in use)
        		  }
        		  
        		  index = getRandomPosition(aTest.getBeta1(),  rng, vectDist.size()); //select node using Bias randomization
        		  //System.out.println("Node: " + index);
        		
        		  PairBest b = obtainDistanceNode(index,vectDist);	  
        		  
        		  //Coste de ir del ultimo node de la ruta al depot
        		  Node lastNodeRoute = route.getEdges().get(route.getEdges().size()-1).getEnd();
        		  double costTravelEndDepot = edge.calcCosts(lastNodeRoute, endDepot);
        		  
        		  if(merge(route , edge,  b.getvalue(), aTest, inputs, costTravelEndDepot)){ //It is possible make a merge?
        			  int  position = route.getEdges().indexOf(edge); //Posicion del edge en la routa
        			  route.getEdges().remove(edge); //Borro el egde de la ruta
        			  
        			  //P -> J
        			  Edge pjEdge = new Edge(edge.getOrigin(), b.getvalue());
        			  pjEdge.setCosts(pjEdge.calcCosts(edge.getOrigin(), b.getvalue()));
        			  route.getEdges().add(position, pjEdge);
        			 
        			 // System.out.println(b.getvalue().getProfit() + " " + route.getScore());
        			  route.setScore(b.getvalue().getProfit() + route.getScore()); //Sumo score
        			  
        			  //J -> Q
        			  Edge jqEdge = new Edge(b.getvalue(), edge.getEnd());
        			  jqEdge.setCosts(jqEdge.calcCosts(b.getvalue(), edge.getEnd()));
        			  route.getEdges().add(position + 1, jqEdge);
        			  availableNodes.remove(b.getvalue());
        		  } else{ //Excedo el tiempo de viaje
        			  timeExceed = true;
        				  Edge lastEdge = route.getEdges().get(route.getEdges().size()-1); //ultimo edge
        				  Node startLastNode = lastEdge.getEnd(); //Nodo end del ultimo edge
        				  Edge lastNewEdge = new Edge(startLastNode,endDepot); //ultimo edge al final depot
        				  lastNewEdge.setCosts(lastNewEdge.calcCosts(startLastNode,endDepot));
        				  route.setCosts(lastNewEdge.getCosts() + route.getCosts());//Coste ultimo nodo al final depot
        			  	  route.getEdges().add(lastNewEdge);

        		  }
        	  }
        	  
          iterations++;  
          }	//End while
          
          if(route.getEdges().size() != 0){
        	  route.setClientsServed(route.getEdges().size()-1); //Customers visitados en la ruta
        	  sol.setClientsServed(route.getClientsServed() + sol.getClientsServed()); // añado Customers visitados en la ruta a sol
        	  sol.setTotalScore(route.getScore() + sol.getTotalScore()); //Actualizo score de la sol
        	  sol.setTotalCosts(sol.getTotalCosts() + route.getCosts()); //Actualizo cost de la sol (viaje + tiempo de servicio nodo)
        	  sol.addRoute(route);
        	 // System.out.println("SOL SCORE: " + sol.getTotalScore());
          }
         
         // System.out.println("Multistart: " + sol.getTotalScore()); 

          
        }
    	
        return sol;
    }
    
    
    

    
    private static boolean merge(Route route, Edge edge, Node node, Test aTest, Inputs inputs, double costLastNode) {
		
    	boolean ismerge = false;
    	
		Node p = edge.getOrigin();
		Node q = edge.getEnd();
		
		
		double cost = (edge.calcCosts(p, node) + edge.calcCosts(node, q) - edge.calcCosts(p, q) + 0);
		if( (route.getCosts() + cost + costLastNode ) <= inputs.gettMax()){
			ismerge = true;
			route.setCosts((route.getCosts() + cost));
		}

		return ismerge;
	}
  
    
    
    public static int getRandomPosition(double alpha, double beta, Random r,int size) {
		 double randomValue = alpha + (beta - alpha) * r.nextDouble();
		 int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - randomValue));
		 index = index % size;
		 return index;
	}
    
    
    
    
	public static int getRandomPosition( double beta, Random r, int size) {
		 int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - beta));
         index = index % size;
     return index;
	}
    
	
	
	public static TreeSet<PairBest> calEvaluationFunction(ArrayList<Node> nodes,Edge edge) {
		
		TreeSet<PairBest> distanceNodes = new TreeSet<PairBest>();
		
		Node p = edge.getOrigin();
		Node q = edge.getEnd();
		for(Node inode: nodes){
			double cost = (edge.calcCosts(p, inode) + edge.calcCosts(inode, q) - edge.calcCosts(p, q) + 0) / inode.getProfit();
			PairBest nodeDist = new PairBest(cost,inode);
			distanceNodes.add(nodeDist);
		}

		return distanceNodes;
	}
    
	
	
	
public static TreeSet<PairBest> calEvaluationFunction(Set<Node> nodes,Edge edge, Route r, Inputs inputs) {
		
		TreeSet<PairBest> distanceNodes = new TreeSet<PairBest>();
		
		Node p = edge.getOrigin();
		Node q = edge.getEnd();
		for(Node inode: nodes){
			double cost = (edge.calcCosts(p, inode) + edge.calcCosts(inode, q) - edge.calcCosts(p, q) + 0) / inode.getProfit();
			PairBest nodeDist = new PairBest(cost,inode);
			distanceNodes.add(nodeDist);
		}

		return distanceNodes;
	}
	
	

public static TreeSet<PairBestProfit> calEvaluationFunctionProfit(Set<Node> nodes,Edge edge, Route r, Inputs inputs) {
	
	TreeSet<PairBestProfit> distanceNodes = new TreeSet<PairBestProfit>();
	
	Node p = edge.getOrigin();
	Node q = edge.getEnd();
	for(Node inode: nodes){
		//double cost = (edge.calcCosts(p, inode) + edge.calcCosts(inode, q) - edge.calcCosts(p, q) + 0) / inode.getProfit();
		double cost = inode.getProfit();
		PairBestProfit nodeDist = new PairBestProfit(cost,inode);
		distanceNodes.add(nodeDist);
	}

	return distanceNodes;
}


   
    
	public static PairBest obtainDistanceNode(int position, TreeSet<PairBest> tree) {
		
		Iterator it = tree.iterator();
        int k = 0;
        PairBest p = null;
        
		while(it.hasNext()) {
            p = (PairBest) it.next();
            if(position == k){
            	return p;
            }
        k++;
		}    
		return p;
	}
	
	
	
	public static PairBestProfit obtainProfitNode(int position, TreeSet<PairBestProfit> tree) {
		
		Iterator it = tree.iterator();
        int k = 0;
        PairBestProfit p = null;
        
		while(it.hasNext()) {
            p = (PairBestProfit) it.next();
            if(position == k){
            	return p;
            }
        k++;
		}    
		return p;
	}
	
	
	
	
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%% Heuristica C&W local %%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public Solution solveCWS(){
		InputsManager.generateDepotEdges(inputs);
		Solution best = new Solution();
		best.setTotalCosts(Double.MAX_VALUE);
		
		
		///1- Aplicaciones heurísticaC&W
		double bAlpha = 0.0;
		for (double alpha = 0; alpha <= 1; alpha+=0.1){
			InputsManager.generateSavingsList(inputs,alpha);
			Solution detSol = CWS.solve(inputs,aTest,rng,0);
			Collections.sort(detSol.getRoutes());
			double totalCost = 0.0;
			double totalProfit = 0.0;
			int used_veh = Math.min(inputs.getVehNumber(),detSol.getRoutes().size());

			detSol.sliceSolution(used_veh);

			for(int i = 0; i < used_veh; i++){
				Route r = detSol.getRoutes().get(i);
				totalCost += r.getCosts();
				totalProfit += r.getScore();
			}
			
			detSol.setTotalCosts(totalCost);
			detSol.setTotalScore(totalProfit);
			
			if(totalProfit > best.getTotalScore() || 
			   (totalProfit == best.getTotalScore() && totalCost < best.getTotalCosts())){
				best = detSol;
				bAlpha = alpha;
			}
			
		}
		
		
		
		///2- Aplicaciones heurísticaC&W proceso constructivo para la mejor alpha
		best = multiCW(best,bAlpha); //Ejecución determinista			
		return best;
	}
	

    
    
  
}










