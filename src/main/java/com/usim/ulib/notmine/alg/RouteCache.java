package com.usim.ulib.notmine.alg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/********************
 * Simple Route Cache
 * 
 * @author emartinezmasip
 * 
 */

public class RouteCache {
	private final HashMap<String, Route> routes;

	public RouteCache() {
		this.routes = new HashMap<String, Route>();
	}

	public boolean isCached(Route route) {
		return isCached(key(route));
	}

	public void put(Route keyRoute, Route route) {
		put(key(keyRoute), route);
	}

	private void put(String key, Route route) {
		routes.put(key, route);
	}

	public boolean isCached(String key) {
		return routes.containsKey(key);
	}

	public Route get(Route route) {
		return get(key(route));
	}

	public Route get(String key) {
		return routes.get(key);
	}

	public double getCachedCost(Route r) {
		String k = key(r);
		if (isCached(k)) {
			return get(k).getCosts();
		}
		return Double.MAX_VALUE;
	}
	public static String key(Route r) {

		int[] v = new int[r.getEdges().size()];
		int i = 0;
		for (Edge e : r.getEdges()) {
			v[i++] = e.getEnd().getId();
		}
		Arrays.sort(v);
		return Arrays.toString(v);
	}
	
	
	

	public static Solution improveWithCache(Solution newSol, RouteCache cache) {		
		int n = newSol.getRoutes().size(); 
	
		Solution newRoutingSol = new Solution(newSol);

		for (int i = 0; i < n; i++) {
			Route route = new Route(); 
			route = newRoutingSol.getRoutes().get(i);
			route = improveNodesOrder(route);
			String skey = key(route);
			
			if (!cache.isCached(skey)) 
			{
				cache.put(skey, route);
			} 
			else 
			{
				Route rCached = cache.get(skey);
				if ( route.getCosts() < rCached.getCosts()) 
				{		
					 cache.put(skey, route);
					 //System.out.println("Ruta: "+i+" key: " +skey+ " Mejora coste: " + route.getCosts() + " origin: " +rCached.getCosts());
					
				} 
				else 
				{
					newRoutingSol.getRoutes().remove(i);
					newRoutingSol.getRoutes().add(rCached);
				}
			}
		}		
		return new Solution(newRoutingSol);		
	}

	public Solution improve(Solution newSol) {
		return improveWithCache(newSol, this);
	}
	

	
	
	/*******************************************************************************
	    * PRIVATE METHOD improveNodesOrder()
	    * Given aRoute, this method tries to sort its nodes in a more efficient way.
	    *  (e.g. by eliminating possible knots in the current route)
	    *******************************************************************************/
	private static Route improveNodesOrder(Route aRoute)
	{
		List<Edge> edges =  aRoute.getEdges();
		// Edges in aRoute must be directed

		if( edges.size() >= 4 ) // if size <= 3 there aren't knots
		{   
			for ( int i = 0; i <= edges.size() - 3; i++ )
			{   // Get the current way of sorting the 3 next edges
				Edge e1 = edges.get(i);
				Edge e2 = edges.get(i + 1);
				Edge e3 = edges.get(i + 2);
				double currentCosts = e1.getCosts() + e2.getCosts() + e3.getCosts();
				// Construct the alternative way
				Node originE1 = e1.getOrigin();
				Node originE2 = e2.getOrigin();
				Node endE2 = e2.getEnd();
				Node endE3 = e3.getEnd();
				Edge e1b = new Edge(originE1, endE2);
				e1b.setCosts(e1b.calcCosts(originE1, endE2));
				Edge e2b = new Edge(endE2, originE2);
				e2b.setCosts(e2b.calcCosts(endE2, originE2));
				Edge e3b = new Edge(originE2, endE3);
				e3b.setCosts(e3b.calcCosts(originE2, endE3));

				double alterCosts = e1b.getCosts() + e2b.getCosts() + e3b.getCosts();
				// Compare both ways and, if appropriate, update route
				if( alterCosts < currentCosts )
				{  
					aRoute.removeEdge(e1);
					aRoute.substractCosts(e1);
					aRoute.addEdge(i, e1b);
					aRoute.addCosts(e1b);
					aRoute.removeEdge(e2);
					aRoute.substractCosts(e2);
					aRoute.addEdge(i + 1, e2b);
					aRoute.addCosts(e2b);
					aRoute.removeEdge(e3);
					aRoute.substractCosts(e3);
					aRoute.addEdge(i + 2, e3b);
					aRoute.addCosts(e3b);
				}
			}
		}
		return aRoute;
	}

}
