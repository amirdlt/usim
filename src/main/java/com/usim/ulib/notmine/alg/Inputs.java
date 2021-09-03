package com.usim.ulib.notmine.alg;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Inputs implements Serializable
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private Node[] nodes; // List of all nodes in the problem/sub-problem
    private int vehicles = 0; // Vehicle capacity (homogeneous fleet)
    private LinkedList<Edge> savings = null; 
    private LinkedList<Edge> depotDistance = null; //Distance depot with customers 
    private float[] vrpCenter; // (x-bar, y-bar) is a geometric VRP center
    private double alpha = 0.0;
    private double beta = 0.0;
    private double tMax; //Maximum time of travel
    private double maxprofit;
    private double minprofit;




	public Inputs(int n) {
		nodes = new Node[n]; // n nodes, including the depot
        vrpCenter = new float[2];
    }

    public Inputs(Inputs i, Node[] nodes){
	    this.nodes = nodes;
	    vehicles = i.vehicles;
	    tMax = i.tMax;
    }

    /* GET METHODS */
    public Node[] getNodes(){return nodes;}
    public LinkedList<Edge> getSavings(){return savings;}
    public int getVehNumber(){return vehicles;}
    public float[] getVrpCenter(){return vrpCenter;}
    public double gettMax() {return tMax;}
    public double getMaxprofit() {return maxprofit;}
    public double getMinprofit() {return minprofit;}
    public LinkedList<Edge> getdistanceDepot(){return depotDistance;}
    
    /* SET METHODS */
    public void setVrpCenter(float[] center){vrpCenter = center;}
    public void setVehNumber(int c){vehicles = c;}
    public void setNodes(Node[] nodes){this.nodes = nodes;}
    public void setList(LinkedList<Edge> sList){savings = sList;}
	public void settMax(double tMax) {this.tMax = tMax;}	
	public void setdistanceDepot(LinkedList<Edge> sList){depotDistance = sList;}
    
	public void setMaxMin()
	{
	    maxprofit=0;
	    minprofit=10^6;
	    for(int j=1; j< nodes.length-1; j++) 
	    {
	    	if(nodes[j].getProfit() > maxprofit) 
	    	{
	    		maxprofit=nodes[j].getProfit();
	    	}
	    	if(nodes[j].getProfit() < minprofit) 
	    	{
	    		minprofit=nodes[j].getProfit();
	    	}	
	    }
	}
	public void remove(int index){
		Node[] nNode=new Node[getNodes().length-1];
		int j=0;
		for(int i=0;i<getNodes().length-1;i++){
			if(i!=index) {
				nNode[i]=new Node(nodes[j]);
				j++;
			}
			else {
				nNode[i]=new Node(nodes[i+1]);
				j++;
				j++;
			}
		}
		nodes = new Node[nNode.length];
		for(int i=0;i<nNode.length;i++){
			nodes[i]=new Node(nNode[i]);
		}
	}
	
}
