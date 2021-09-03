package com.usim.ulib.notmine.alg;

import java.util.TreeSet;

public class BestSolutionsDiff {
	
	private TreeSet<PairBestDist> arbol; 
	private static final int MAX_SOLS = 5;
	
    public BestSolutionsDiff(){ 
    	arbol = new TreeSet<>(); 
    }
	
	public void addSolution(PairBestDist sol){
		arbol.add(sol); 
		if(arbol.size() > MAX_SOLS) 
			arbol.remove(arbol.last()); 
	}
	
	
	public TreeSet<PairBestDist> getSolutions(){
		return arbol;	
	}
	
	
	public int getSize(){
		return arbol.size();	
	}
	

}
