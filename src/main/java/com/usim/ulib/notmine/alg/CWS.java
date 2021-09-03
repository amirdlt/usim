package com.usim.ulib.notmine.alg;

import java.util.*;

/**
 * @author lluc
 *
 */
public class CWS{
	
	public static Solution solve(Inputs inputs, Test aTest, Random rng, int useRandom ){
		/* 1. RESET VARIABLES */
		Solution currentSol = new Solution();
		// dummySol resets isInterior and inRoute in nodes
		List<Node> nodes = Arrays.asList(inputs.getNodes());
		currentSol = generateDummySol(nodes,inputs);
		Node depot = nodes.get(0);
		int index;

		LinkedList<Edge> savings = new LinkedList<Edge>(inputs.getSavings());
		/* 3. PERFORM THE EDGE-SELECTION & ROUTING-MERGING ITERATIVE PROCESS */
		//System.out.println(savings);
		
		while (!savings.isEmpty()) {
			// 3.1. Select the next edge from the list (either at random or not)
			
			//Deter
			if (useRandom == 0) // classical Clarke & Wright solution
				index = 0; // greedy behavior
			else // suffle the savingsList
				index = getRandomPositionalpha(aTest.getBeta1(), rng, savings.size());
			Edge ijEdge = savings.get(index);
			savings.remove(ijEdge); // remove edge from list

			// 3.2. Determine the nodes i < j that define the edge
			Node iNode = ijEdge.getOrigin();
			Node jNode = ijEdge.getEnd();

			// 3.3. Determine the routes associated with each node
			Route iR = iNode.getInRoute();
			Route jR = jNode.getInRoute();
			
			if(iR != null && jR != null){
				int iSize = iR.getEdges().size();
				
				// 3.4. If all necessary conditions are satisfied, merge
				boolean isMergePossible;
				isMergePossible = checkMergingConditions(inputs, iR, jR, ijEdge);
				if (isMergePossible) { // 3.4.1. Get an edge iE in iR							// containing nodes i and 0
					Edge iE = iR.getEdges().get(iSize - 1);
					// 3.4.2. Remove edge iE from iR route and update costs
					iR.getEdges().remove(iE);
					iR.setCosts(iR.getCosts() - iE.getCosts());
					iR.setScore(iR.getScore()+jR.getScore());
					// 3.4.3. If there are more than one edge then i will be
					// interior
					iNode.setIsEndAdjacent(false);
					if (iR.getEdges().size() > 1){
						iNode.setIsInterior(true);
						iNode.setIsOriginAdjacent(false);
					}
					// 3.4.4. If new route iR does not start at 0 it must be
					// reversed
					//if (iR.getEdges().get(0).getOrigin().getId() != depot.getId() )
					//	iR.reverse();
					// 3.4.5. Get an edge jE in jR containing nodes j and 0
					Edge jE = jR.getEdges().get(0); // jE is either (0,j) or
					// 3.4.6. Remove edge jE from jR route
					jR.getEdges().remove(jE);
					jR.setCosts(jR.getCosts() - jE.getCosts());
					// 3.4.7. If there are more than one edge then j will be
					// interior
					jNode.setIsOriginAdjacent(false);
					if (jR.getEdges().size() > 1){
						jNode.setIsInterior(true);
						jNode.setIsEndAdjacent(false);
					}
					// 3.4.8. If new route jR starts at 0 it must be reversed
					//if (jR.getEdges().get(0).getOrigin().getId()  == depot.getId() )
					//	jR.reverse(); // reverseRoute(inputs, jR);
					// 3.4.9. Add ijEdge = (i, j) to new route iR
					iR.getEdges().add(ijEdge);
					iR.setCosts(iR.getCosts() + ijEdge.getCosts());
					//iR.setDemand(iR.getDemand() + ijEdge.getEnd().getUnitsToServe());
					jNode.setInRoute(iR);
					// 3.4.10. Add route jR to new route iR
					for (Edge e : jR.getEdges()) {
						iR.getEdges().add(e);
						//iR.setDemand(iR.getDemand() + e.getEnd().getUnitsToServe());
						iR.setCosts(iR.getCosts() + e.getCosts());
						e.getEnd().setInRoute(iR);
					}
					// 3.4.11. Delete route jR from currentSolution
					currentSol.setRoutingCosts(currentSol.getRoutingCosts() - ijEdge.getClassicSavings());
					currentSol.getRoutes().remove(jR);
				}
			}
	
		}
		/* 4. RETURN THE SOLUTION */

		/* Keep the top solutions and set costs */
		currentSol.sliceSolutionAndSetCost(inputs);
		
		return currentSol;
	}




	private static Solution generateDummySol(List<Node> nodes, Inputs inputs) {
		HashSet<Node> nodeSet = new HashSet<>(nodes);
		nodeSet.remove(nodes.get(0));
		nodeSet.remove(nodes.get(nodes.size()-1));
		Solution sol = new Solution(nodeSet);
		for (int i = 1; i < nodes.size() - 1; i++) // i = 0 is the origin, i = n-1 is the end
		{
			Node iNode = nodes.get(i);

			// Get diEdge and idEdge
			Edge diEdge = iNode.getDiEdge();
			Edge idEdge = iNode.getIdEdge();
			// Create didRoute (and set corresponding total costs and
			// demand)
			Route didRoute = new Route();
			didRoute.getEdges().add(diEdge);
			//didRoute.setDemand(didRoute.getDemand() + diEdge.getEnd().getUnitsToServe());
			didRoute.setCosts(didRoute.getCosts() + diEdge.getCosts());
			didRoute.getEdges().add(idEdge);
			didRoute.setCosts(didRoute.getCosts() + idEdge.getCosts());
			didRoute.setScore(iNode.getProfit());
			if(didRoute.getCosts() <= inputs.gettMax()){
				// Update iNode properties (inRoute and isInterior)
				iNode.setInRoute(didRoute); // save route to which node belongs
				iNode.setIsInterior(false); // node is directly connected to
											// depot
				// Add didRoute to current solution
				iNode.setIsOriginAdjacent(true);
				iNode.setIsEndAdjacent(true);
				sol.addRoute(didRoute);
				sol.setRoutingCosts(sol.getRoutingCosts() + didRoute.getCosts());
			}else{
				nodeSet.remove(nodes.get(i));
			}
		}
		return sol;
	}
	
	
	private static boolean checkMergingConditions(Inputs inputs,Route iR, Route jR, Edge ijEdge) {
		// Condition 1: iR and jR are not the same route
		if (iR == jR)
			return false;
		// Condition 2: both nodes are exterior nodes in their respective routes
		Node iNode = ijEdge.getOrigin();
		Node jNode = ijEdge.getEnd();
		if (iNode.getIsInterior() || jNode.getIsInterior())
			return false;
		// Condition 3: A node of the edge has to be adjacent to the origin, the other to the end.
		if (!iNode.getIsEndAdjacent() || !jNode.getIsOriginAdjacent())
			return false;
		// Condition 4: total costs (distance) after merging are feasible
		double maxCost = inputs.gettMax();
		double newCost = iR.getCosts() + jR.getCosts() - ijEdge.getClassicSavings();
		if(newCost > maxCost) return false;
		
		//float maxRoute = 0;//aTest.getMaxRouteCosts();
		//float serviceCosts = 0;//aTest.getServiceCosts();
		//int nodesInIR = iR.getEdges().size();
		//int nodesInJR = jR.getEdges().size();
		//double newCost = iR.getCosts() + jR.getCosts() - ijEdge.getSavings();
		//if (newCost > maxRoute - serviceCosts * (nodesInIR + nodesInJR - 2))
			//return false;

		return true;
	}
	
	
	private static int getRandomPositionalpha(double beta, Random r, int size) {
		int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - beta));
		index = index % size;
		return index;
	}
	
	
    
    public static int getRandomPosition(double alpha, double beta, Random r,int size) {
		 double randomValue = alpha + (beta - alpha) * r.nextDouble();
		 int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - randomValue));
		 index = index % size;
		 return index;
	}
	
}
