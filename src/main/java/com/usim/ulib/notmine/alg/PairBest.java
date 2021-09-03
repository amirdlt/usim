package com.usim.ulib.notmine.alg;

public class PairBest implements Comparable<PairBest> {
	private static int serial = 0;
	private final int id = serial++;

	private double distance;
	private Node node;

	public PairBest(double distance, Node node) {
		this.distance = distance;
		this.node = node;
	}

	public double getkey() {
		return distance;
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
		PairBest other = (PairBest) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(PairBest o) {
		if (o.getkey() == this.getkey()) {
			return 0;
		} else if (o.getkey() < this.getkey()) {
			return 1;
		} else {
			return -1;
		}
	}

}
