package com.usim.ulib.notmine.alg;
import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Edge implements Serializable
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private Node origin; // origin node
    private Node end; // end node
    private double costs = 0.0; // edge costs
    private double savings = 0.0; // edge savings (Clarke & Wright)
    private double classicSavings = 0.0;
    private Route inRoute = null; // route containing this edge (0 if no route assigned)
    private Edge inverseEdge = null; // edge with inverse direction
    private double stoCosts = 0.0; // edge costs
            
    public Edge(Node originNode, Node endNode) 
    {   origin = originNode;
        end = endNode;
    }

    public Edge(Edge e){   
    	this.origin = e.origin;
    	this.end = e.end; 
    	this.costs = e.costs;
    	this.savings = e.savings; 
    	this.classicSavings = e.classicSavings;
    	if(e.inRoute !=null){
         this.inRoute = new Route (e.inRoute);
    	}else{
    		this.inRoute = null;	
    	}
        this.stoCosts = 0.0; 
    }

	

	/* SET METHODS */
    public void setCosts(double c){costs = c;}
    public void setSavings(double s){savings = s;}
    public void setInRoute(Route r){inRoute = r;}
    public void setInverse(Edge e){inverseEdge = e;}
    public void setStoCosts(double stoCosts) {this.stoCosts = stoCosts;}

    /* GET METHODS */
    public Node getOrigin(){return origin;}
    public Node getEnd(){return end;}
    public double getCosts(){return costs;}
    public double getSavings(){return savings;}
    public Route getInRoute(){return inRoute;}
    public Edge getInverseEdge(){return inverseEdge;}
    public double getStoCosts() {return stoCosts;}
    public double getClassicSavings() {return classicSavings;}
    
    
    /* AUXILIARY METHODS */
    
    public double calcCosts()
    {   double X1 = origin.getX();
        double Y1 = origin.getY();
        double X2 = end.getX();
        double Y2 = end.getY();
        double d = Math.sqrt((X2 - X1) * (X2 - X1) + (Y2 - Y1) * (Y2 - Y1));
        return d;
    }
    
    public static double calcCosts(Node origin, Node end)
    {   double X1 = origin.getX();
        double Y1 = origin.getY();
        double X2 = end.getX();
        double Y2 = end.getY();
        double d = Math.sqrt((X2 - X1) * (X2 - X1) + (Y2 - Y1) * (Y2 - Y1));
        return d;
    }

    public double calcSavings(Node origin, Node end)
    {
        // Costs of origin depot to end node
        double Coj = calcCosts(origin,this.end);
        // Costs of origin node to end depot
        double Cie = calcCosts(this.origin,end);
        // Costs of originNode to endNode
        double Cij = costs;
        
        //Return cost depot to savings
        return Coj + Cie - Cij;
    }
    

    public double calcSavings(Node origin, Node end, double alpha)
    {
        // Costs of origin depot to end node
        double Coj = calcCosts(origin,this.end);
        // Costs of origin node to end depot
        double Cie = calcCosts(this.origin,end);
        // Costs of originNode to endNode
        double Cij = costs;
        
        //Return cost depot to savings
        double Sij = Coj + Cie - Cij;
        classicSavings = Sij;
        return alpha*Sij + (1-alpha)*(this.origin.getProfit() + this.end.getProfit());
    }
    
	static final Comparator<Edge> minDistance = (a1, a2) -> Double.compare(a2.costs, a1.costs);
    
	static final Comparator<Edge> savingsComp = (a1, a2) -> Double.compare(a2.savings, a1.savings);

    @Override
    public String toString() 
    { 
    	String s = "";
        s = s.concat("\nEdge origin: " + this.getOrigin());
        s = s.concat("\nEdge end: " + this.getEnd());
        s = s.concat("\nEdge costs: " + (this.getCosts()));
        s = s.concat("\nEdge savings: " + (this.getSavings()));
        return s;
    }

}
