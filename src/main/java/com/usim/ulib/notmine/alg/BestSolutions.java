package com.usim.ulib.notmine.alg;

import java.util.TreeSet;

public class BestSolutions {
	
	private TreeSet<PairBestCust> arbol; 
	private static final int MAX_SOLS = 5;
	
    public BestSolutions(){ 
    	arbol = new TreeSet<>(); 
    }
	
	public void addSolution(PairBestCust sol){
		arbol.add(sol); 
		if(arbol.size() > MAX_SOLS) 
			arbol.remove(arbol.last()); 
	}
	
	
	public TreeSet<PairBestCust> getSolutions(){
		return arbol;	
	}
	
	
	public int getSize(){
		return arbol.size();	
	}
}
