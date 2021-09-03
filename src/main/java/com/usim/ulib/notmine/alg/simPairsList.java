package com.usim.ulib.notmine.alg;

public class simPairsList {
    int bestSolNr;    
	int simRun;
    double stockCosts;
    double stockOutCosts;
    
    

    public simPairsList(int sol, int simRun, double costs, double stockOut)
    {
        this.bestSolNr = sol;
        this.simRun = simRun;
        this.stockCosts = costs;
        this.stockOutCosts = stockOut;
    }  
    
    public int getBestSolNr() {
		return bestSolNr;
	}

	public void setBestSolNr(int sol) {
		this.bestSolNr = sol;
	}

	public int getSimRun() {
		return simRun;
	}

	public void setSimRun(int simRun) {
		this.simRun = simRun;
	}

	public double getsStockCosts() {
		return stockCosts;
	}

	public void setStockCosts(double costs) {
		this.stockCosts = costs;
	}


	public double getStockOut() {
		return stockOutCosts;
	}

	public void setStockOut(double stockOut) {
		this.stockOutCosts = stockOut;
	}
}
