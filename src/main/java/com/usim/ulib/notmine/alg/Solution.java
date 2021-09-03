package com.usim.ulib.notmine.alg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Solution implements Cloneable
{
   


	/* INSTANCE FIELDS & CONSTRUCTOR */
    private static long nInstances = 0; // number of instances
    private long id; // solution ID
    private double stockCosts = 0.0; // solution stock costs
    private double routingCosts = 0.0; // solution routing costs
    private double totalCosts = 0.0; // solution total costs
    private double refinedRoutingCosts = 0.0;
    private double refinedTotCosts = 0.0;
    private float demandToServe = 0.0f; // total demand to serve
    private float servedDemand = 0.0F; // accum. demand served so far
    private LinkedList<Route> routes; // list of routes in this solution
    private double time = 0.0; // elapsed computational time (in seconds)
    private double totalScore = 0.0;
    private double StochCost = 0.0;
    private double StochScore = 0.0;
    private double reliability = 1.0;
    private int stochClientsNoServed = 0; //Clientes no servidos 
    private int clientsServed = 0;
    private double percentTimesViolated = 0;
    private double distanceViolated = 0;
    private Set<Node> notUsedNodes; //Set of nodes that aren't used in the solution
	private static final String LINE_SEPARATOR = System.lineSeparator();


	public Solution()
    {   nInstances++;
        id = nInstances;
        routes = new LinkedList<Route>();
        notUsedNodes = new HashSet<>();
    }

    public Solution(Set<Node> notUsedNodes)
    {   nInstances++;
        id = nInstances;
        routes = new LinkedList<Route>();
        this.notUsedNodes = notUsedNodes;
    }
    
    //new constructor
    public Solution(Solution sol)
    {   nInstances++;
        id = nInstances;
        stockCosts = sol.stockCosts; 
        routingCosts = sol.routingCosts; 
        totalCosts = sol.totalCosts;
        demandToServe = sol.demandToServe; 
        servedDemand = sol.servedDemand; 
        time = sol.time;
        
        this.routes = new LinkedList<Route>();
        
        for(Route r : sol.getRoutes()){
        	
        	Route rAux = new Route(r);
        	this.routes.add(rAux);
        }
        
        this.notUsedNodes = new HashSet<>();
        for(Node n: sol.notUsedNodes){
        	//Node nAux = new Node (n);
        	this.notUsedNodes.add(n);	
        }
         
        totalScore = sol.totalScore;
        stochClientsNoServed = sol.stochClientsNoServed;
        clientsServed = sol.clientsServed;
        percentTimesViolated = sol.percentTimesViolated;
        distanceViolated = sol.distanceViolated;      
        reliability = sol.reliability;
        StochScore = sol.StochScore;
    } 
    
    
    
    

    
    
    public Solution(Solution sol, LinkedList<Route> newroutes)
    {   nInstances++;
        id = nInstances;
        
        stockCosts = sol.stockCosts; 
        routingCosts = sol.routingCosts; 
        totalCosts = sol.totalCosts;
        demandToServe = sol.demandToServe; 
        servedDemand = sol.servedDemand; 
        time = sol.time;
        reliability = sol.reliability;
        StochScore = sol.StochScore;
        
        this.routes = new LinkedList<Route>();
        for(Route r : newroutes){
        	this.routes.add(r);
        }
    } 
    
    
    
    public Solution(LinkedList<Node> nonServed)
    {   nInstances++;
    	id = nInstances;
    	routes = new LinkedList<Route>();
    } 
    
   

    public void addRoute(Route aRoute, Integer index) {
    	routes.add(index, (aRoute));
   }

    /**
     * Adds a a route to the Solution and updates the notUsedNodes set
     * @param aRoute The route to add into the solution
     */
    public void addRoute(Route aRoute) {
    	routes.add(aRoute);
    	int i = 0;
    	for(Edge e: aRoute.getEdges()){
    	    if(i != aRoute.getEdges().size() - 1)
    	        notUsedNodes.remove(e.getEnd());
    	    ++i;
        }
   }

    public void addRoutes(List<Route> l){
	    routes.addAll(l);
    }
    
    public void deleteRoute(int i) {
    	routes.remove(i);
   }


    /**
     * It sorts the routes by cost and it keeps only the better ones that can be
     * visited with the number of vehicles available
     * @param inputs Inputs instance that specifies the number of vehicles available
     */
    public void sliceSolutionAndSetCost(Inputs inputs){
        Collections.sort(routes);
        double totalCost = 0.0;
        double totalProfit = 0.0;
        int used_veh = Math.min(inputs.getVehNumber(),routes.size());

        sliceSolution(used_veh);

        for(int i = 0; i < used_veh; i++){
            Route r = routes.get(i);
            totalCost += r.getCosts();
            totalProfit += r.getScore();
        }

        setTotalCosts(totalCost);
        setTotalScore(totalProfit);
    }


    /**
     * It removes the last n - topn routes
     * @param topn Number of routes to keep
     */
    public void sliceSolution(int topn){
	   while(routes.size() != topn){
		   Route r = routes.removeLast();
           int i = 0;
           for(Edge e: r.getEdges()){
               if(i != r.getEdges().size() - 1)
                   notUsedNodes.add(e.getEnd());
               ++i;
           }
	   }
   }
    
   
    /* GET METHODS */
    public LinkedList<Route> getRoutes(){return routes;}
    public long getId(){return id;}
    public double getStockCosts(){return stockCosts;}
    public double getRoutingCosts(){return routingCosts;}
    public double getTotalCosts(){return totalCosts;}
    public float getServedDemand(){return servedDemand;}
    public double getTime(){return time;}
    public float getDemandToServe(){return demandToServe;}
    public double getTotalScore() {return totalScore;}
	public double getStochCost() {return StochCost;}
	public double getStochScore() {return StochScore;}
	public double getReliability() {return reliability;}
	public int getStochClientsNoServed() {return stochClientsNoServed;}
	public int getClientsServed() {return clientsServed;}
	public double getPercentTimesViolated() {return percentTimesViolated;}
	public double getDistanceViolated() {return distanceViolated;}
    public Set<Node> getNotUsedNodes() {
        return notUsedNodes;
    }





    /* SET METHODS */
    public void setStockCosts(double c){stockCosts = c;}
    public void setRoutingCosts(double c){routingCosts = c;}
    public void setTotalCosts(double c){totalCosts = c;}
    public void setServedDemand(float d){servedDemand = d;}
    public void setTime(double t){time = t;}
    public void setDemandToServe(float d){demandToServe = d;}
    public void setReliability(double rel){reliability = rel;}
	public void setTotalScore(double totalScore) {this.totalScore = totalScore;}
	public void setStochCost(double stochCost) {this.StochCost = stochCost;}
	public void setStochScore(double stochScore) {StochScore = stochScore;}
	public void setStochClientsNoServed(int stochClientsNoServed) {this.stochClientsNoServed = stochClientsNoServed;}
	public void setClientsServed(int clientsServed) {this.clientsServed = clientsServed;}
	public void setPercentTimesViolated(double percentTimesViolated) {this.percentTimesViolated = percentTimesViolated;}
	public void setDistanceViolated(double distanceViolated) {this.distanceViolated = distanceViolated;}
    public void setNotUsedNodes(Set<Node> notUsedNodes) {
        this.notUsedNodes = notUsedNodes;
    }



	/*  AUXILIARY METHODS */
    
    @Override
    public String toString()
    {
        Route aRoute; // auxiliary Route variable
        String s = "";
        s = s.concat(LINE_SEPARATOR);
        s = s.concat("Sol ID : " + getId() + LINE_SEPARATOR);
        s = s.concat("Sol stock costs: " + getStockCosts() + LINE_SEPARATOR);
        s = s.concat("Sol routing costs: " + getRoutingCosts() + LINE_SEPARATOR);
        s = s.concat("Sol total costs: " + getTotalCosts() + LINE_SEPARATOR);
        s = s.concat("Sol profit: " + getTotalScore() + LINE_SEPARATOR);
        s = s.concat("# of routes in sol: " + routes.size());
        s = s.concat("\r\n\r\n\r\n");
        s = s.concat("List of routes (cost and nodes): \r\n\r\n");
        for (int i = 1; i <= routes.size(); i++)
        {   aRoute = routes.get(i - 1);
            s = s.concat("Route " + i + " || ");
            s = s.concat("Total Costs = " + aRoute.getCosts() + " || ");
            s = s.concat("Demand  = " + aRoute.getDemand()+ " || ");
            s = s.concat("Score  = " + aRoute.getScore()+ " || ");
            s = s.concat(LINE_SEPARATOR);
            
            s = s.concat("\n");
                int last = -1;
            	for(Edge e: aRoute.getEdges()){ //obtengo edges
					s = s.concat(e.getOrigin().getId() + "  - ");	
					last = e.getEnd().getId();
				}
            	s = s.concat(last + "\n");
            }
            
        return s;
        }
        
        

    
    public static Object deepClone(Object object) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(object);
          ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          ObjectInputStream ois = new ObjectInputStream(bais);
          return ois.readObject();
        }
        catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }	
    
    
    
    @Override
   	public int hashCode() {
   		final int prime = 31;
   		int result = 1;
   		result = prime * result + (int) (id ^ (id >>> 32));
   		return result;
   	}

   	@Override
   	public boolean equals(Object obj) {
   		if (this == obj)
   			return true;
   		if (obj == null)
   			return false;
   		if (getClass() != obj.getClass())
   			return false;
   		Solution other = (Solution) obj;
   		if (id != other.id)
   			return false;
   		return true;
   	}
    
    
    
}

