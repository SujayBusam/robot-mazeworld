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

		SearchNode currentNode;

		// Implementation of algorithm
		while (!fringe.isEmpty()) {
			incrementNodeCount();
			updateMemory(fringe.size() + visited.size());


			// Pop node from queue and continue, but only if its priority is lower than what this node's priority is if it was visited before
			// or if it hasn't been visited at all before
			currentNode = fringe.peek();
			
			if ((visited.containsKey(currentNode) && currentNode.priority() < visited.get(currentNode)) || !visited.containsKey(currentNode)) {
				currentNode = fringe.poll();

				// Test if goal found
				if (currentNode.goalTest()) {
					// Backchain
					return this.astarBackchain(currentNode);
				}

				// Add node to explored map
				visited.put(currentNode, currentNode.priority());


				// For each successor
				for (SearchNode successor: currentNode.getSuccessors()) {

					// If successor not explored, add to fringe and visited
					if (!visited.containsKey(successor)) {
						successor.setParent(currentNode); // Set parent
						fringe.add(successor);
					}

					// If node was visited but now has less expensive path cost, add node
					else if (visited.containsKey(successor) && successor.priority() < visited.get(successor)) {
						successor.setParent(currentNode); // Set parent
						fringe.add(successor);
					}

					// Otherwise, disregard node
				}
			}
			
			// Otherwise, disregard current node
			else {
				fringe.remove();
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
