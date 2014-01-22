package assignment_mazeworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class InformedSearchProblem extends SearchProblem {

	public List<SearchNode> astarSearch() {

		resetStats();

		// Initialize fringe / frontier
		PriorityQueue<SearchNode> fringe = new PriorityQueue<SearchNode>();

		// Initialize explored hashmap. Maps node to its priority
		HashMap<SearchNode, Double> visited = new HashMap<SearchNode, Double>();

		// Add start node to fringe
		startNode.setParent(null);
		fringe.add(startNode);

		// Implementation of algorithm
		while (!fringe.isEmpty()) {
			incrementNodeCount();
			updateMemory(fringe.size() + visited.size());
			
			
			// Get the node with lowest priority
			SearchNode currentNode = fringe.poll();

			// Test if goal found
			if (currentNode.goalTest()) {
				// Backchain
				return this.astarBackchain(currentNode);
			}

			// Add node to explored map
			if (!visited.containsKey(currentNode)) {
				visited.put(currentNode, currentNode.priority());
			}

			// For each successor
			for (SearchNode successor: currentNode.getSuccessors()) {

				// If successor not explored, add to fringe and visited
				if (!visited.containsKey(successor)) {
					successor.setParent(currentNode); // Set parent
					fringe.add(successor);
					visited.put(successor, successor.priority());
				}

				// If node was visted but now has less expensive path cost, add node
				else if (visited.containsKey(successor) && successor.priority() < visited.get(successor)) {
					fringe.add(successor);
				}

				// Otherwise, disregard node
			}
		}
		
		// Path to goal not found
		return null;
	}

	public List<SearchNode> astarBackchain(SearchNode node) {
		SearchNode currentNode = node;
		List<SearchNode> path = new LinkedList<SearchNode>();
		
		while (currentNode != null) {
			((LinkedList<SearchNode>) path).addFirst(currentNode);
			currentNode = currentNode.getParent();
		}
		
		return path;
	}

}
