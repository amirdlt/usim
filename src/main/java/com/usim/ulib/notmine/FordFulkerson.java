package com.usim.ulib.notmine;

import java.util.*;

public class FordFulkerson {
	public static ArrayList<Integer> pathDFS(int source, int destination, WGraph g) {
		var path = new LinkedList<Integer>();
		var parentMap = new HashMap<Integer, Edge>();
		var frontier = new Stack<Integer>();
		parentMap.put(source, null);
		frontier.add(source);
		label: while (!frontier.isEmpty()) {
			var newFrontier = new Stack<Integer>();
			for (int node : frontier) {
				var set = new HashSet<Edge>();
				g.getNeighbors().get(node).forEach(e -> set.add(g.getEdge(node, e)));
				for (var e : set)
					if (e.nodes[0] == node && !parentMap.containsKey(e.nodes[1]) && e.flow < e.weight) {
						parentMap.put(e.nodes[1], e);
						if (e.nodes[1] == destination)
							break label;
						newFrontier.add(e.nodes[1]);
					} else if (e.nodes[1] == node && !parentMap.containsKey(e.nodes[0]) && e.flow > 0) {
						parentMap.put(e.nodes[0], e);
						if (e.nodes[0] == destination)
							break label;
						newFrontier.add(e.nodes[0]);
					}
			}
			frontier = newFrontier;
		}
		if (frontier.isEmpty())
			return null;
		var node = destination;
		path.add(destination);
		while (node != source) {
			var e = parentMap.get(node);
			path.addFirst(e.nodes[0]);
			if (e.nodes[0] == node) {
				node = e.nodes[1];
			} else {
				node = e.nodes[0];
			}
		}
		return new ArrayList<>(path);
	}

	public static String fordfulkerson(WGraph g) {
		List<Integer> pathNode;
		while ((pathNode = pathDFS(g.getSource(), g.getDestination(), g)) != null) {
			var path = new ArrayList<Edge>();
			for (int i = 0; i < pathNode.size() - 1; i++)
				path.add(g.getEdge(pathNode.get(i), pathNode.get(i+1)));
			int minCf = Integer.MAX_VALUE;
			int lastNode = g.getSource();
			for (var edge : path) {
				int capacity;
				if (edge.nodes[0] == lastNode) {
					capacity = edge.weight - edge.flow;
					lastNode = edge.nodes[1];
				} else {
					capacity = edge.flow;
					lastNode = edge.nodes[0];
				}
				if (capacity < minCf)
					minCf = capacity;
			}
			lastNode = g.getSource();
			for (var edge : path)
				if (edge.nodes[0] == lastNode) {
					edge.flow += minCf;
					lastNode = edge.nodes[1];
				} else {
					edge.flow -= minCf;
					lastNode = edge.nodes[0];
				}
		}
		int maxFlow = 0;
		var set = new HashSet<Edge>();
		g.getNeighbors().get(g.getSource()).forEach(e -> set.add(g.getEdge(g.getSource(), e)));
		for (var edge : set)
			maxFlow += edge.flow;
		return maxFlow + "\n" + g.toString();
	}

	public static void main(String[] args) {
	    System.out.println(fordfulkerson(new WGraph("2.txt")));
	}
}

