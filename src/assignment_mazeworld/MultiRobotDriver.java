package assignment_mazeworld;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import assignment_mazeworld.MultiRobotProblem.MultiRobotNode;
import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.SimpleMazeProblem.SimpleMazeNode;

public class MultiRobotDriver extends Application {

	Maze maze;

	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 30;
	MazeView mazeView;
	List<AnimationPath> animationPathList;

	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = Maze.readFromFile("corridor2.maz");

		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MazeView(maze, PIXELS_PER_SQUARE);

	}

	// assumes maze and mazeView instance variables are already available
	private void runSearches() {

		//		int sx = 0;
		//		int sy = 0;
		//		int gx = 6;
		//		int gy = 0;
		//
		//		SimpleMazeProblem mazeProblem = new SimpleMazeProblem(maze, sx, sy, gx,
		//				gy);
		//
		//		List<SearchNode> bfsPath = mazeProblem.breadthFirstSearch();
		//		animationPathList.add(new AnimationPath(mazeView, bfsPath));
		//		System.out.println("DFS:  ");
		//		mazeProblem.printStats();
		//
		//		List<SearchNode> dfsPath = mazeProblem
		//				.depthFirstPathCheckingSearch(5000);
		//		animationPathList.add(new AnimationPath(mazeView, dfsPath));
		//		System.out.println("BFS:  ");
		//		mazeProblem.printStats();
		//
		//		List<SearchNode> astarPath = mazeProblem.astarSearch();
		//		animationPathList.add(new AnimationPath(mazeView, astarPath));
		//		System.out.println("A*:  ");
		//		mazeProblem.printStats();
		

		
		int[][] start = new int[][] {
				{0,0},
				{0,1},
				{0,2}
		};


		int[][] goal = new int[][] {
				{7,4},
				{6,4},
				{5,4}
		};
		


		MultiRobotProblem mazeProblem = new MultiRobotProblem(maze, start, goal);
		List<SearchNode> astarPath = mazeProblem.astarSearch();
		animationPathList.add(new AnimationPath(mazeView, astarPath));
		System.out.println("A*:  ");
		mazeProblem.printStats();

	}


	public static void main(String[] args) {
		launch(args);
	}

	// javafx setup of main view window for mazeworld
	@Override
	public void start(Stage primaryStage) {

		initMazeView();

		primaryStage.setTitle("CS 76 Mazeworld");

		// add everything to a root stackpane, and then to the main window
		StackPane root = new StackPane();
		root.getChildren().add(mazeView);
		primaryStage.setScene(new Scene(root));

		primaryStage.show();

		// do the real work of the driver; run search tests
		runSearches();

		// sets mazeworld's game loop (a javafx Timeline)
		Timeline timeline = new Timeline(1.0);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(.05), new GameHandler()));
		timeline.playFromStart();

	}

	// every frame, this method gets called and tries to do the next move
	//  for each animationPath.
	private class GameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			// System.out.println("timer fired");
			for (AnimationPath animationPath : animationPathList) {
				// note:  animationPath.doNextMove() does nothing if the
				//  previous animation is not complete.  If previous is complete,
				//  then a new animation of a piece is started.
				animationPath.doNextMove();
			}
		}
	}

	// each animation path needs to keep track of some information:
	// the underlying search path, the "piece" object used for animation,
	// etc.
	private class AnimationPath {
		private Node[] pieceArray;

		private List<SearchNode> searchPath;
		private int currentMove = 0;

		private int[] lastXArray;
		private int[] lastYArray;

		boolean animationDone = true;

		public AnimationPath(MazeView mazeView, List<SearchNode> path) {
			searchPath = path;
			MultiRobotNode firstNode = (MultiRobotNode) searchPath.get(0);
			System.out.println("Got first node");


			// State of first node
			int[][] startState = firstNode.getState();

			// Piece array
			pieceArray = new Node[startState.length];
			lastXArray = new int[startState.length];
			lastYArray = new int[startState.length];

			for (int i = 0; i < startState.length; i++) {
				pieceArray[i] = mazeView.addPiece(startState[i][0], startState[i][1]);
				lastXArray[i] = startState[i][0];
				lastYArray[i] = startState[i][1];
			}
			//			piece = mazeView.addPiece(firstNode.getX(), firstNode.getY());
			//			
			//			lastX = firstNode.getX();
			//			lastY = firstNode.getY();
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			if (currentMove < searchPath.size() && animationDone) {
				System.out.println("Move #" + currentMove);
				
				MultiRobotNode mazeNode = (MultiRobotNode) searchPath
						.get(currentMove);

				int[][] startState = mazeNode.getState();

				for (int i = 0; i < startState.length; i++) {
					int dx = startState[i][0] - lastXArray[i];
					int dy = startState[i][1] - lastYArray[i];
					animateMove(pieceArray[i], dx, dy);
					lastXArray[i] = startState[i][0];
					lastYArray[i] = startState[i][1];
				}
				// System.out.println("animating " + dx + " " + dy);


				currentMove++;
			}

		}

		// move the piece n by dx, dy cells
		public void animateMove(Node n, int dx, int dy) {
			animationDone = false;
			TranslateTransition tt = new TranslateTransition(
					Duration.millis(300), n);
			tt.setByX(PIXELS_PER_SQUARE * dx);
			tt.setByY(-PIXELS_PER_SQUARE * dy);
			// set a callback to trigger when animation is finished
			tt.setOnFinished(new AnimationFinished());

			tt.play();

		}

		// when the animation is finished, set an instance variable flag
		//  that is used to see if the path is ready for the next step in the
		//  animation
		private class AnimationFinished implements EventHandler<ActionEvent> {
			@Override
			public void handle(ActionEvent event) {
				animationDone = true;
			}
		}
	}
}