package assignment_mazeworld;

import java.util.ArrayList;
import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.SimpleMazeProblem.SimpleMazeNode;

public class MultiRobotProblem extends InformedSearchProblem {

	// Five possible actions (modified Maze.java)
	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST, Maze.PASS};

	private int xStart, yStart, xGoal, yGoal, numBots;

	private Maze maze;

	public MultiRobotProblem(Maze m, int sx, int sy, int gx, int gy, int k) {
		startNode = new MultiRobotNode(sx, sy, 0, 0);
		xStart = sx;
		yStart = sy;
		xGoal = gx;
		yGoal = gy;
		numBots = k;

		maze = m;		
	}

	// Node class (for multi robots) used by searches
	public class MultiRobotNode implements SearchNode {

		// Keep track of parent for backchaining
		private SearchNode parent;

		// State. Keeps track of location of all robots in maze and whose turn it is
		protected int[][] state;

		// Which robot has the turn to move
		private int turn;

		// How far current node is from start
		private double cost;

		// Constructor for initial start node
		public MultiRobotNode(double c, int t) {
			cost = c;

			// Initial state: bots all at start node
			state = new int[numBots][2];
			for (int i = 0; i < numBots; i++) {
				this.state[i][0] = xStart;
				this.state[i][1] = yStart;
			}

			// And first bot's turn to move (bot 0)
			turn = t;
		}

		// Constructor for all other successor nodes
		public MultiRobotNode(int x, int y, double c, int t, int[][] predState) {
			cost = c;

			// Copy state from predecessor node
			this.state = copyState(predState);

			// Modify the state so the bot that just moved has its location updated
			int predBot = (t-1) % numBots;
			this.state[predBot][0] = x;
			this.state[predBot][1] = y;

			// Turn is updated
			turn = t;
		}

		// Get the valid successors of the current node
		public ArrayList<SearchNode> getSuccessors() {

			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();
						
			// Run through every bot, starting with the one to move
			for (int i = turn; i < numBots; i = (i+1) % numBots) {
				
				// Run through all possible actions for current bot
				for (int[] action: actions) {
					int xNew = state[i][0] + action[0];
					int yNew = state[i][1] + action[1]; 

					//System.out.println("testing successor " + xNew + " " + yNew);

					if(maze.isLegal(xNew, yNew)) {
						//System.out.println("legal successor found " + " " + xNew + " " + yNew);
						
					}

				}
			}

			return successors;

		}

		// Helper method to copy predecessor node's state array
		private int[][] copyState(int[][] predState) {
			// Copy to be returned
			int[][] newArray = new int[numBots][3];

			for (int i = 0; i < predState.length; i++) {
				for (int j = 0; j < predState[j].length; j++) {
					newArray[i][j] = predState[i][j];
				}
			}

			return newArray;
		}

		// Get and set the parent node for backchaining
		public SearchNode getParent() {
			return this.parent;
		}

		public void setParent(SearchNode parent) {
			this.parent = parent;
		}
	}
}