package com.usim.ulib.notmine.alg;

import java.util.*;

/**
 * Created by lluc on 2/06/17.
 * This class encapsulates the metaheuristic VNS
 */
public class VNS {

    private static final int P_MIN = 1 ;
    private static final int P_MAX = 100 ;
    private static final int P_STEP = 2 ;
    private Test aTest;
    private Inputs inputs;
    private Random rng;
    private double bestAlpha;
    private Solution initialSolution;
    private Solution bestSolution;
    private static final double EPSILON = 10E-6;
    RouteCache  routecache = new RouteCache();

    VNS(Test myTest, Inputs myInputs, Random myrng)
    {
        aTest = myTest;
        inputs = myInputs;
        rng = myrng;
    }

    
    
    
    
    /**
     * It applies VNS metaheuristic to solve the problem specified at attribute inputs, using test instance class aTest
     * @return The best solution found by VNS
     */
    public Solution solveDet(){
    	
        long start = ElapsedTime.systemTime();
        double elapsed = 0.0;
        
        double T = 100;
        double alph = 0.99;
        inputs.setMaxMin();
        
    	//Creo solucion inicial
        createInitialSolution();
        System.out.println(initialSolution);
      
        Solution newSol = new Solution();
        bestSolution = new Solution(initialSolution);
        Solution baseSolution = new Solution(initialSolution);
        
        //VNS
        while(elapsed < aTest.getMaxTime()){
            int p = P_MIN;
            T = 100;
            while(p <= P_MAX){
            	newSol = new Solution(shake(baseSolution,p));
            	newSol = new Solution(routecache.improve(newSol));
            	newSol = new Solution(LocalSearch2(newSol));
            	newSol = new Solution(LocalSearch4(newSol));

                if(hasImproved(newSol)){
                    bestSolution = new Solution(newSol);
                    baseSolution = new Solution(newSol);
                    bestSolution.setTime(elapsed);
                    System.out.println("I improved " + bestSolution.getTotalScore()+ " "+bestSolution.getTotalCosts());
                    p = P_MIN;
                }
                else{ 
                	if(SAN(baseSolution,newSol,T)) {
                		//System.out.println("Accepted p= "+ p+ " Temp= "+T);
                		baseSolution=new Solution(newSol);
                		p = P_MIN;
                	}
                	else {
                		p += P_STEP;
                	}   
                }
                T=alph*T; 
            }

            elapsed = ElapsedTime.calcElapsed(start, ElapsedTime.systemTime());
        }
        
        
        /*** STEP 2*********   */
        bestSolution = new Solution(Stochastic.simulate(bestSolution,aTest.getLongSim(), aTest.getRandomStream(),aTest,inputs,-1,-1));
        
        System.out.println(bestSolution);
        return bestSolution;
    }
    
    
    
    
    
    
    
    /**
     * It applies SimVNS metaheuristic to solve the problem specified at attribute inputs, using test instance class aTest
     * @return The best solution found by VNS
     */
    public Solution solveSto(){
    	
        long start = ElapsedTime.systemTime();
        double elapsed = 0.0;
        
        double T = 100;
        double alph = 0.99;
        inputs.setMaxMin();
        
        BestSolutionsDiff listBestSols = new BestSolutionsDiff(); //Best solutions customers violate  
        
    	//Creo solucion inicial
        createInitialSolution();
        System.out.println(initialSolution);
      
        //Simulacion rapida de la solución inicial
         initialSolution = Stochastic.simulate(initialSolution,aTest.getLongSim(), aTest.getRandomStream(),aTest,inputs,-1,-1);

        
		PairBestDist solToAdd = new PairBestDist(initialSolution, initialSolution.getPercentTimesViolated());
		listBestSols.addSolution(solToAdd);
        
        Solution newSol = new Solution();
        bestSolution = new Solution(initialSolution);
        Solution baseSolution = new Solution(initialSolution);
        
        //VNS
        while(elapsed < aTest.getMaxTime()){
            int p = P_MIN;
            T = 100;
            while(p <= P_MAX){
            	newSol = new Solution(shake(baseSolution,p));
            	newSol = new Solution(routecache.improve(newSol));
            	newSol = new Solution(LocalSearch2(newSol));
            	newSol = new Solution(LocalSearch4(newSol));
                if(hasImprovedBaseSol(newSol, baseSolution)){
                	baseSolution = new Solution(newSol);
                	newSol =  Stochastic.simulate(baseSolution,aTest.getShortSim(), aTest.getRandomStream(),aTest,inputs,0,1);
                	if(hasImprovedSto(newSol)){
                		 bestSolution = new Solution(newSol);
                         bestSolution.setTime(elapsed);
                         System.out.println("I improved " + bestSolution.getStochScore());
                   		 solToAdd = new PairBestDist(bestSolution,bestSolution.getStochScore());
                   		 listBestSols.addSolution(solToAdd);
                         p = P_MIN;                		 
                	}
                }else{ 
                	if(SAN(baseSolution,newSol,T)) {
                		//System.out.println("Accepted p= "+ p+ " Temp= "+T);
                		baseSolution=new Solution(newSol);
                		p = P_MIN;
                	}
                	else {
                		p += P_STEP;
                	}   
                }
                T=alph*T; 
            }

            elapsed = ElapsedTime.calcElapsed(start, ElapsedTime.systemTime());
        }
        
        
        /*** STEP 2********* */
       /* Deep simulation of best Sols**** */
        
        
       //BEst solutions violated distance
      BestSolutionsDiff auxListBestSolsDist = new BestSolutionsDiff();
      auxListBestSolsDist = listBestSols;
      listBestSols  = new BestSolutionsDiff();
      Iterator iterator;
      iterator = auxListBestSolsDist.getSolutions().iterator();
      listBestSols  = new BestSolutionsDiff();
      while(iterator.hasNext()){            	
     	  PairBestDist pairIter = (PairBestDist) iterator.next();
       	  Solution sol = pairIter.getkey();
          sol = new Solution(Stochastic.simulate(sol,aTest.getLongSim(), aTest.getRandomStream(),aTest,inputs,-1,0));
       	  PairBestDist newPairIter = new PairBestDist(sol,sol.getStochScore());
          listBestSols.addSolution(newPairIter);
         }

        
        bestSolution = new Solution(listBestSols.getSolutions().first().getkey());
        
        System.out.println(bestSolution);
        return bestSolution;
    }
    
    
    
    
    
    
    //Simulated annealing
    private boolean SAN(Solution baseSolution, Solution newSol,double T) 
    {
    	double delta=baseSolution.getTotalScore()-newSol.getTotalScore();
    	if(Math.abs(delta) > EPSILON) {    		 
             double r = delta / T;
             double a = Math.exp(-r);
             double u = rng.nextDouble();
             
             if(u < a) return true;            
    	 }
            
    	return false;
    }
    
    

    /**
     * It creates the initial solution for the VNS metaheuristic, using CWS heuristic, and
     * determining the best value of alpha to generate the savings list
     */
    private void createInitialSolution(){
        InputsManager.generateDepotEdges(inputs);
        Solution best = new Solution();
        best.setTotalCosts(Double.MAX_VALUE);


        bestAlpha = 0.0;
        for (double alpha = 0; alpha <= 1; alpha+=0.1){
            InputsManager.generateSavingsList(inputs,alpha);
            Solution detSol = new Solution(CWS.solve(inputs,aTest,this.rng,0));
            Collections.sort(detSol.getRoutes());
            if(detSol.getTotalScore() >= best.getTotalScore() ||
                    (detSol.getTotalScore() == best.getTotalScore()
                            && detSol.getTotalCosts() < best.getTotalCosts())){
                best = new Solution(detSol);
                bestAlpha = alpha;
           
            }

        }
        System.out.println("BestAplha: " + bestAlpha);
        initialSolution = best;
    }

    
    
    
    
    /**
     * indicates if baseSolution is better than bestSolution
     * @param baseSolution The solution to compare with BestSolution
     * @return  True if baseSolution is better than BestSolution, false otherwise
     */
    private boolean hasImproved(Solution baseSolution){
    	double gap =   baseSolution.getTotalCosts() - bestSolution.getTotalCosts();
    	    	
        return ( (baseSolution.getTotalScore() > bestSolution.getTotalScore()) || 
        		 ((baseSolution.getTotalScore() == bestSolution.getTotalScore()) && (gap  < -0.01)))  ;
    }
    
    

    /**
     * indicates if baseSolution is better than bestSolution
     * @param baseSolution The solution to compare with BestSolution
     * @return  True if baseSolution is better than BestSolution, false otherwise
     */
    private boolean hasImprovedBaseSol(Solution newSol, Solution baseSol){
    	double gap =  baseSol.getTotalCosts() - newSol.getTotalCosts();
    	
        return ( (newSol.getTotalScore() > baseSol.getTotalScore()) || 
        		 ((newSol.getTotalScore() == baseSol.getTotalScore()) && (gap > 0.01) )) ;
    }
    


  
    private boolean hasImprovedSto(Solution newSol){
    	double gap =  bestSolution.getTotalCosts() - newSol.getTotalCosts();
    	
        return (newSol.getStochScore() > bestSolution.getStochScore());
    }
    	
    	
    
    
    /**
     * It destroys a percentage of routes of base Solution and it
     * generates them again using biased C&W constructive heuristic
     * @param base  Base solution to shake
     * @param p percentage of routes of base Solution to delete
     */
    private Solution shake(Solution base, int p){

    	Solution ShakeSol = new Solution(base);
    	Solution ShakeSolAux = new Solution(base);
    	
        /*We destroy a percantage of routes*/
        LinkedList<Route> routes = ShakeSol.getRoutes();
        int nRoutesDestroy = (int)  ((routes.size()*p)/100); 
       
        HashSet<Node> nodesSet = destroyNroutes(routes,nRoutesDestroy);

        /* The nodes destroyed now are not used in the base solution*/
        Set<Node> baseNode = ShakeSol.getNotUsedNodes();
        baseNode.addAll(nodesSet);

        /* We get all nodes not used in the base solution*/
        Node[] nodes = new Node[baseNode.size() + 2];
        nodes[0] = inputs.getNodes()[0];
        nodes[nodes.length - 1] = inputs.getNodes()[inputs.getNodes().length - 1];
        int i = 1;
        for(Node n: baseNode){
            nodes[i] = n;
            ++i;
        }

        
        /* Create a subproblem with previous nodes and solve it with CWS */
        Inputs subInput = new Inputs(inputs,nodes);
        InputsManager.generateDepotEdges(subInput);
        
        InputsManager.generateSavingsList(subInput,bestAlpha);
        Solution subSol = CWS.solve(subInput,aTest,this.rng,1);
        routes.addAll(subSol.getRoutes());
        mergeNotUsedNodes(ShakeSol,subSol);

        /* Merge baseSolution and subSolution and slice routes not used*/

        ShakeSol.sliceSolutionAndSetCost(inputs);
        
        return ShakeSol;
    }


    /**
     * It updates the NotUsedNodes set of baseSol using subSol. Every node
     * used on subSol is removed from the baseSol NotUsedNodes set.
     * @param baseSol The original solution
     * @param subSol The sub-solution using nodes not used on baseSol and nodes of the deleted routes
     */
    private void mergeNotUsedNodes(Solution baseSol, Solution subSol){
        for(Route r: subSol.getRoutes()){
            int i = 0;
            for(Edge e: r.getEdges()){
                if(i != r.getEdges().size())
                    baseSol.getNotUsedNodes().remove(e.getEnd());
                ++i;
            }
        }
    }

    /**
     * It destroys n random routes from list routes and it returns a node array
     * with origin and end positions null and the other positions will be the nodes
     * that the deleted routes had.
     * @param routes A list with the total routes
     * @param n The number of routes to delete
     * @return the nodes that contained the deleted routes
     */
    private HashSet<Node> destroyNroutes(List<Route> routes, int n){
        ArrayList<Route> routesDestroy = new ArrayList<>();
        if(!routes.isEmpty()) {
            for (int i = 0; i < n; ++i) {
                int index = this.rng.nextInt(routes.size());
                Route r = routes.get(index);
                routesDestroy.add(r);
            }
        }
        
        HashSet<Node> nodesD = new HashSet<>();
        for(Route r : routesDestroy){
            int i = 0;
            for(Edge e : r.getEdges()){
                if(i != r.getEdges().size() - 1){
                    nodesD.add(e.getEnd());
                }
                ++i;
            }
            routes.remove(r);

        }

        return nodesD;
    }
    



    private  boolean merge(Route route, Edge edge, Node node) {

    	boolean ismerge = false;

    	Node p = edge.getOrigin();
    	Node q = edge.getEnd();

    	double cost = (edge.calcCosts(p, node) + edge.calcCosts(node, q) - edge.calcCosts(p, q) );
    	if( (route.getCosts() + cost ) <= inputs.gettMax()){
    		ismerge = true;
    		route.setCosts((route.getCosts() + cost));
    	}

    	return ismerge;
    }


    
    
    
    /**
     * It creates the initial solution for the VNS metaheuristic, using CWS heuristic, and
     * determining the best value of alpha to generate the savings list
     */
    private Solution LocalSearch2(Solution Sol){
    	//Solution LsSolution = new Solution(Sol);
    	Boolean isMerge = true;	
    	Solution AuxSolLS = new Solution(Sol);
    	Solution LsSolution = new Solution(AuxSolLS);
    	double min = 0.0;	
    	Boolean delRandom = false;


    	int nodesToDelete = (int) (inputs.getNodes().length * 0.05);
    	boolean stopped = false;
    	int ndeleted = 0;
    	int thershold = 12;// 
    	int del = this.rng.nextInt(3);
    	
    	if(del==0){
    		 min = inputs.getMinprofit();
    	}else{
    		if(del == 1){
    			min = inputs.getMaxprofit()  - thershold;
    		}else{
    			min = -1.0;
    			delRandom = true;
    		}
    		
    	}
    	
    	for(Route r : LsSolution.getRoutes()){
    		
    		if(r.getEdges().size() >= 4){
    			for(int i = 0;i<r.getEdges().size()-1;i++){
	    			int index = i;
	    			if (delRandom == true){
	    				index = this.rng.nextInt( (int) Math.round(r.getEdges().size() -2));
	    			}
	    			
	    			Edge edgeIni = r.getEdges().get(index);
	    			Edge edgeEnd = r.getEdges().get(index+1);
	
	    			if( ( (edgeIni.getEnd().getProfit() >= min) && (edgeIni.getEnd().getProfit() <= min + thershold)) || delRandom == true){
	    				ndeleted++;
	   
	    				double routeCost = r.getCosts();
	    				double saveCost = 0;
	
	    				int  position = r.getEdges().indexOf(edgeIni); //Posicion del edge n la routa
	    				r.getEdges().remove(edgeIni); //Borro el egde de la ruta
	    				saveCost = edgeIni.getCosts();
	
	    				int  positionEnd = r.getEdges().indexOf(edgeEnd); //Posicion del edge en la routa
	    				r.getEdges().remove(edgeEnd); //Borro el egde de la ruta  				
	    				saveCost +=  edgeEnd.getCosts();
	
	    				Edge newEdge = new Edge(edgeIni.getOrigin(), edgeEnd.getEnd());
	
	    				newEdge.setCosts(newEdge.calcCosts(edgeIni.getOrigin(), edgeEnd.getEnd()));
	    				r.getEdges().add(position, newEdge);
	
	    				double lastScoreRoute = r.getScore();
	    				r.setScore(r.getScore() - edgeIni.getEnd().getProfit()); //resto score
	    				r.setCosts(r.getCosts() - saveCost + newEdge.getCosts());
	
	    				LsSolution.getNotUsedNodes().add(edgeIni.getEnd());	
	    				r.setClientsServed(r.getEdges().size()-1); //Customers visitados en la ruta
	
	    				LsSolution.setTotalScore(r.getScore() + LsSolution.getTotalScore() - lastScoreRoute); //Actualizo score de la sol
	    				LsSolution.setTotalCosts(LsSolution.getTotalCosts() - routeCost  + r.getCosts()); //Actualizo cost de la sol (viaje + tiempo de servicio nodo)				
	    				
	    				if(ndeleted == nodesToDelete){
	    					stopped = true;
	    				}
	    			}
	    		}
	    		if (stopped == true){
	    			break;
	    		}
    		}
    	}

         AuxSolLS = new Solution(routecache.improve(LsSolution)); 
    	return AuxSolLS;
    }
    
    
    
    
    
    /**
     * It creates the initial solution for the VNS metaheuristic, using CWS heuristic, and
     * determining the best value of alpha to generate the savings list
     */
    private Solution LocalSearch4(Solution Sol){
    	//Solution LsSolution = new Solution(Sol);
    	Boolean isMerge = true;	
    	Solution AuxSolLS = new Solution(Sol);
    	Solution LsSolution = new Solution(AuxSolLS);
    	Solution BaseSol = new Solution(AuxSolLS);
    	Boolean improve = true;

    	while(improve == true){
    		for(Route r : LsSolution.getRoutes()){
    			for(int i = 0;i<r.getEdges().size();i++){
    				
    				int index = i;
    				Edge edge = r.getEdges().get(index);

    				//calculate function evaluation for all the availables nodes for the selected edge
    				//caculate distance and order between nodes(j's) and Edge(p,q)  return list in increasing order
    				if(LsSolution.getNotUsedNodes().size() == 0){
    					isMerge = false;
    					break; // there aren't free nodes (all in use)
    				}
    				TreeSet<PairBest> vectDist = MultiStart.calEvaluationFunction(LsSolution.getNotUsedNodes(),edge,r,inputs);
    				 
    				index = MultiStart.getRandomPosition(0.30, rng, vectDist.size()); //select node using Bias randomization

    				PairBest b = MultiStart.obtainDistanceNode(index,vectDist);	 

    				if(merge(r,edge,b.getvalue())){

    					int  position = r.getEdges().indexOf(edge); //Posicion del edge en la routa
    					r.getEdges().remove(edge); //Borro el egde de la ruta
    					//System.out.println("Mejoraaaa LS");

    					//P -> J
    					Edge pjEdge = new Edge(edge.getOrigin(), b.getvalue());
    					pjEdge.setCosts(pjEdge.calcCosts(edge.getOrigin(), b.getvalue()));
    					r.getEdges().add(position, pjEdge);

    					// System.out.println(b.getvalue().getProfit() + " " + route.getScore());
    					double lastScoreRoute = r.getScore();
    					r.setScore(b.getvalue().getProfit() + r.getScore()); //Sumo score

    					//J -> Q
    					Edge jqEdge = new Edge(b.getvalue(), edge.getEnd());
    					jqEdge.setCosts(jqEdge.calcCosts(b.getvalue(), edge.getEnd()));
    					r.getEdges().add(position + 1, jqEdge);

    					r.setClientsServed(r.getEdges().size()-1); //Customers visitados en la ruta

    					LsSolution.setTotalScore(r.getScore() + LsSolution.getTotalScore() - lastScoreRoute); //Actualizo score de la sol
    					LsSolution.setTotalCosts(LsSolution.getTotalCosts() + r.getCosts()); //Actualizo cost de la sol (viaje + tiempo de servicio nodo)

    					LsSolution.getNotUsedNodes().remove(b.getvalue());	
    					
    					if( (LsSolution.getTotalScore() > AuxSolLS.getTotalScore()) ||
    			    			( (LsSolution.getTotalScore() == AuxSolLS.getTotalScore()) && (LsSolution.getTotalCosts() < AuxSolLS.getTotalCosts()))){
    			    			  AuxSolLS = new Solution(routecache.improve(LsSolution)); 
    			    	 	}
    				}	
    		 } 
    		}
    		
    		if ((AuxSolLS.getTotalScore() > BaseSol.getTotalScore()) ||
			( (AuxSolLS.getTotalScore() == BaseSol.getTotalScore()) && (AuxSolLS.getTotalCosts() < BaseSol.getTotalCosts()))){
    			LsSolution = new Solution(routecache.improve(AuxSolLS)); 
    			BaseSol = new Solution(routecache.improve(LsSolution)); 
			}else{improve = false;}
    	  }
    	return AuxSolLS;
    }

       

}
