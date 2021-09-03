package com.usim.ulib.notmine.alg;

public class PairBestProfit implements Comparable<PairBestProfit> {
	private static int serial = 0;
	private final int id = serial++;

	private double profit;
	private Node node;

	public PairBestProfit(double profit, Node node) {
		this.profit = profit;
		this.node = node;
	}

	public double getkey() {
		return profit;
	}

	public Node getvalue() {
		return node;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		PairBestProfit other = (PairBestProfit) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(PairBestProfit o) {
		if (o.getkey() == this.getkey()) {
			return 0;
		} else if (o.getkey() > this.getkey()) {
			return 1;
		} else {
			return -1;
		}
	}

}
