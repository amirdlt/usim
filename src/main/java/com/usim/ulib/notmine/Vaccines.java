package com.usim.ulib.notmine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Vaccines {
	
	public static class Country {
		private int ID;
		private int vaccine_threshold;
		private int vaccine_to_receive;
		private final ArrayList<Integer> allies_ID;
		private final ArrayList<Integer> allies_vaccine;
		private final ArrayList<Integer> allies_ID0 = new ArrayList<>();
		private final ArrayList<Integer> allies_vaccine0 = new ArrayList<>();
		private boolean stopped = false;
		public Country() {
			this.allies_ID = new ArrayList<>();
			this.allies_vaccine = new ArrayList<>();
			this.vaccine_threshold = 0;
			this.vaccine_to_receive = 0;
		}
		public int get_ID() {
			return this.ID;
		}
		public int get_vaccine_threshold() {
			return this.vaccine_threshold;
		}
		public ArrayList<Integer> get_all_allies_ID() {
			return allies_ID;
		}
		public ArrayList<Integer> get_all_allies_vaccine() {
			return allies_vaccine;
		}
		public int get_allies_ID(int index) {
			return allies_ID.get(index);
		}
		public int get_allies_vaccine(int index) {
			return allies_vaccine.get(index);
		}
		public int get_num_allies() {
			return allies_ID.size();
		}
		public int get_vaccines_to_receive() {
			return vaccine_to_receive;
		}
		public void set_allies_ID(int value) {
			allies_ID.add(value);
		}
		public void set_allies_vaccine(int value) {
			allies_vaccine.add(value);
		}
		public void set_ID(int value) {
			this.ID = value;
		}
		public void set_vaccine_threshold(int value) {
			this.vaccine_threshold = value;
		}
		public void set_vaccines_to_receive(int value) {
			this.vaccine_to_receive = value;
		}
	}
	
	public int vaccines(Country[] graph) {
		dfs(graph, 0);
		return (int) Arrays.stream(graph).filter(e -> e.vaccine_to_receive >= e.vaccine_threshold).count();
	}

	private void handleAllies(Country[] graph, int index) {
		graph[index].stopped = graph[index].vaccine_to_receive < graph[index].vaccine_threshold | index == 0;
		if (!graph[index].stopped)
			return;
		for (int i = 0; i < graph[index].allies_ID0.size(); i++) {
			var ally = graph[graph[index].allies_ID0.get(i) - 1];
			ally.vaccine_to_receive -= graph[index].allies_vaccine0.get(i);
			var old = ally.stopped;
			ally.stopped = ally.vaccine_to_receive < ally.vaccine_threshold;
			if (!old & ally.stopped)
				bfs(graph, index);
		}
	}

	public void test(String filename) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(filename));
		int num_countries = sc.nextInt();
		Country[] graph = new Country[num_countries];
		Arrays.setAll(graph, i -> new Country());
		for (int i=0; i<num_countries; i++) {
			if (!sc.hasNext()) {
                sc.close();
                sc = new Scanner(new File(filename + ".2"));
            }
			int amount_vaccine = sc.nextInt();
			graph[i].set_ID(i+1);
			graph[i].set_vaccine_threshold(amount_vaccine);
			int other_countries = sc.nextInt();
			for (int j =0; j<other_countries; j++) {
				int neighbor = sc.nextInt();
				int vaccine = sc.nextInt();
				graph[neighbor -1].set_allies_ID(i+1);
				graph[neighbor -1].set_allies_vaccine(vaccine);
				graph[i].allies_ID0.add(neighbor);
				graph[i].allies_vaccine0.add(vaccine);
				graph[i].vaccine_to_receive += vaccine;
			}
		}
		sc.close();
		System.out.println(vaccines(graph));
	}

	private void bfs(Country[] graph, int index) {
		boolean[] visited = new boolean[graph.length];
		var queue = new LinkedList<Integer>();
		visited[index] = true;
		queue.add(index);
		while (queue.size() != 0) {
			index = queue.poll();
			for (int i = 0; i < graph[index].allies_ID0.size(); i++) {
				var ally = graph[graph[index].allies_ID0.get(i) - 1];
				ally.vaccine_to_receive -= graph[index].allies_vaccine0.get(i);
				if (!ally.stopped && ally.vaccine_to_receive < ally.vaccine_threshold)
					ally.stopped = true;
			}
			for (var n : graph[index].allies_ID0)
				if (!visited[--n]) {
					queue.add(n);
					visited[n] = true;
				}
		}
	}

	private void dfs(Country[] graph, int index) {
		// Initially mark all vertices as not visited
//		Vector<Boolean> visited = new Vector<>(V);
//		for (int i = 0; i < V; i++)
//			visited.add(false);
		var visited = new boolean[graph.length];

		// Create a stack for DFS
		Stack<Integer> stack = new Stack<>();

		// Push the current source node
		stack.push(index);

		while (!stack.empty()) {
			// Pop a vertex from stack and print it
			index = stack.peek();
			stack.pop();

			handleAllies(graph, index);
			// Stack may contain same vertex twice. So
			// we need to print the popped item only
			// if it is not visited.
			visited[index] = true;

			// Get all adjacent vertices of the popped vertex s
			// If a adjacent has not been visited, then puah it
			// to the stack.

			for (var i : graph[index].allies_ID0)
				if (!visited[--i])
					stack.push(i);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Vaccines vaccines = new Vaccines();
		vaccines.test(args[0]);
	}
}
