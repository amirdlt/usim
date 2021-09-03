package com.usim.ulib.notmine.alg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PairBestCust implements Comparable<PairBestCust> {
	private static int serial = 0;
	private final int id = serial++;

	private Solution sol;
	private double totalCost;

	public PairBestCust(Solution sol, double TotalCost) {
		this.sol = sol;
		this.totalCost = TotalCost;
	}

	public Solution getkey() {
		return sol;
	}
	
	public double getValue() {
		return totalCost;
	}

	

	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((sol == null) ? 0 : sol.hashCode());
		long temp;
		temp = Double.doubleToLongBits(totalCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		PairBestCust other = (PairBestCust) obj;
		if (id != other.id)
			return false;
		if (sol == null) {
			if (other.sol != null)
				return false;
		} else if (!sol.equals(other.sol))
			return false;
		if (Double.doubleToLongBits(totalCost) != Double.doubleToLongBits(other.totalCost))
			return false;
		return true;
	}

	@Override
	public int compareTo(PairBestCust o) {
		if (o.getValue() == this.getValue()) {
			return 0;
		} else if (o.getValue() < this.getValue()) {
			return 1;
		} else {
			return -1;
		}
	}

}
