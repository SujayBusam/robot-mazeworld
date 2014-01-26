package assignment_mazeworld;

import java.util.ArrayList;
import java.util.Arrays;
import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.SimpleMazeProblem.SimpleMazeNode;

public class MultiRobotProblem extends InformedSearchProblem {

	// Five possible actions (modified Maze.java)
	private static int actions[][] = {Maze.PASS, Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST};

	private int numBots;

	private int[][] goalState;

	private Maze maze;

	public MultiRobotProblem(Maze m, int[][] start, int[][] goal) {

		startNode = new MultiRobotNode(start, 0, 0);
		goalState = goal;
		numBots = start.length;

		maze = m;		
	}

	// Node class (for multi robots) used by searches
	public class MultiRobotNode implements SearchNode {

		// Keep track of parent for backchaining
		private SearchNode parent;

		// State. Keeps track of location of all robots in maze
		protected int[][] currentState;

		// Which robot has the turn to move
		private int turn;

		// How far current node is from start
		private double cost;

		// Constructor
		public MultiRobotNode(int[][] start, double c, int t) {
			cost = c;
			turn = t;

			// Initialize state
			currentState = start;
		}

		// Get the valid successors of the current node
		public ArrayList<SearchNode> getSuccessors() {

			ArrayList<SearchNode> successorsList = new ArrayList<SearchNode>();


			// Run through all possible actions for bot whose turn it is to move
			for (int[] action: actions) {
				int xNew = currentState[turn][0] + action[0];
				int yNew = currentState[turn][1] + action[1]; 

				//System.out.println("testing successor " + xNew + " " + yNew);

				if(maze.isLegal(xNew, yNew) && !isOccupied(xNew, yNew)) {
					//System.out.println("legal successor found " + " " + xNew + " " + yNew);

					// Make a copy of the current state
					int[][] newState = copyState(this.currentState);

					// Modify new state appropriately
					newState[turn][0] = xNew;
					newState[turn][1] = yNew;

					// Cost incurred regardless of movement
					double newCost = getCost() + 1.0;

					// Create successor node with this new state
					MultiRobotNode successor = new MultiRobotNode(newState,
							newCost, (turn + 1) % numBots);

					// Add successor to list
					successorsList.add(successor);
				}
			}
			return successorsList;
		}

		// Helper method to see if x,y space is already occupied by a bot
		private boolean isOccupied(int x, int y) {

			// Run through x,y of all bots (excluding current one) to check for collisions
			for (int i = (turn + 1) % numBots; i != (turn % numBots); i = ((i + 1) % numBots)) {

				// Check if this bot's position matches with the checked bot's position
				int currentBotX = currentState[i][0];
				int currentBotY = currentState[i][1];

				if (x == currentBotX && y == currentBotY) {
					return true;
				}
			}

			// No collisions with other bots if for loop exited
			return false;
		}

		// Helper method to copy predecessor node's state array
		private int[][] copyState(int[][] oldState) {
			// Copy to be returned
			int[][] newState = new int[numBots][2];

			for (int i = 0; i < oldState.length; i++) {
				for (int j = 0; j < 2; j++) {
					newState[i][j] = oldState[i][j];
				}
			}

			return newState;
		}

		// Get and set the parent node for backchaining
		public SearchNode getParent() {
			return this.parent;
		}

		public void setParent(SearchNode parent) {
			this.parent = parent;
		}


		@Override
		public boolean goalTest() {
			return (Arrays.deepEquals(currentState, goalState));
		}


		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			//
			//			for (int i = 0; i < numBots; i++) {
			//				if (!Arrays.equals(this.currentState[i], ((MultiRobotNode) other).currentState[i])) {
			//					return false;
			//				}
			//			}
			//
			//			// Dropped out of loop. All arrays are equal
			//			return true;

			return (Arrays.deepEquals(this.currentState, ((MultiRobotNode) other).currentState));
		}

		@Override
		public int hashCode() {
			int hashCode = 0;

			// Upper bound integer
			int upperBound = Math.min(maze.width, maze.height);

			int exponent = 2 * numBots - 1;

			for (int i = 0; i < numBots; i++) {

				hashCode = (int) (hashCode + (this.currentState[i][0] * Math.pow(upperBound, exponent) 
						+ (Math.pow(this.turn, upperBound))));
				exponent--;

				hashCode = (int) (hashCode + (this.currentState[i][1] * Math.pow(upperBound, exponent) 
						+ (Math.pow(this.turn, upperBound))));
				exponent--;
			}

			return hashCode;
		}

		//		@Override
		//		public String toString() {
		//			return new String("Maze state " + state[0] + ", " + state[1] + " "
		//					+ " depth " + getCost());
		//		}

		@Override
		public double getCost() {
			return cost;
		}

		// Return the state
		public int[][] getState() {
			return this.currentState;
		}


		@Override
		public double heuristic() {
			//			// Manhattan distance taking into account all robots
			//
			//			// Get sum of all x values of robots in current state
			//			double totalCurrentX = 0;
			//			for (int i = 0; i < numBots; i++) {
			//				totalCurrentX += this.currentState[i][0];
			//			}
			//
			//			// Get sum of all y values of robots in current state
			//			double totalCurrentY = 0;
			//			for (int i = 0; i < numBots; i++) {
			//				totalCurrentY += this.currentState[i][1];
			//			}
			//
			//			// Get sum of all x values of robots in goal state
			//			double totalGoalX = 0;
			//			for (int i = 0; i < numBots; i++) {
			//				totalGoalX += goalState[i][0];
			//			}
			//
			//			// Get sum of all y values of robots in goal state
			//			double totalGoalY = 0;
			//			for (int i = 0; i < numBots; i++) {
			//				totalGoalY += goalState[i][1];
			//			}
			//
			//			// Get the difference and return
			//			double dx = totalGoalX - totalCurrentX;
			//			double dy = totalGoalY - totalCurrentY;
			//			return Math.abs(dx) + Math.abs(dy);

			// Straight line distance heuristic
			double heuristic = 0;

			for (int i = 0; i < numBots; i++) {
				heuristic += (Math.sqrt(Math.pow((goalState[i][0] - currentState[i][0]), 
						2.0) + Math.pow((goalState[i][1] - currentState[i][1]), 2.0)));
			}

			return heuristic;
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}

		@Override
		public double priority() {
			return heuristic() + getCost();
		}
	}
}