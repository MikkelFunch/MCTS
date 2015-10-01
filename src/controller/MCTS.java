package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import tree.Node;


public class MCTS extends Controller<MOVE> {
	private Random rng = new Random();
	
	private final double COEFFICIENT = Math.sqrt(2);
	private final int DEFAULT_TIME = 40;
	private final int depth = 103;
	private MOVE currentDirection;
	private boolean moved = true;
	
	@Override
	public MOVE getMove(Game g, long t) {
		if (g.gameOver()) {
			currentDirection = null;
		}
		else if (!moved) {
			moved = true;
			return currentDirection;
		}
		else if (!g.isJunction(g.getPacmanCurrentNodeIndex()) && currentDirection != null) {
			ArrayList<MOVE> moves = new ArrayList<MOVE>(Arrays.asList(g.getPossibleMoves(g.getPacmanCurrentNodeIndex())));
			//MOVE[]g.getPossibleMoves(pacmanNode)
			if (moves.contains(currentDirection)) {
				return currentDirection;
			} else {
				moves.remove(currentDirection.opposite());
				currentDirection = moves.get(0);
				return moves.get(0);
			}
		}
		
		
		
		if (0 < t) {
			return run(g, t);
		} else {
			//TESTING
			MOVE m = run(g);
			return m;
			//
			
			//return run(g);
		}
	}
	
	public MOVE run(Game g){
		return run(g, DEFAULT_TIME);
	}
	
	/**
	 * The main loop of the algorithm
	 * @param g
	 * @param t
	 * @return
	 */
	public MOVE run(Game g, long t){
		long start = System.currentTimeMillis();
		Node root = new Node(g, null, null);
		int i = -1;
		do {
			Node next = treePolicy(root);
			int score = defaultPolicy(next);
			backup(next, score);
			i++;
			
		} //while(t-5 > System.currentTimeMillis()-start);
			while(i < 400);
		System.out.println(i);
		MOVE m = bestChild(root).getMove();
		currentDirection = m;
		moved = false;
		return m;
	}
	
	/**
	 * Method for finding the best child to explore
	 * @param n
	 * @return
	 */
	private Node bestChild(Node n) {
		Node best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Node c : n.getChildren().values()) {
			double uctValue = (c.getTotalValue() / c.getVisits()) + COEFFICIENT * Math.sqrt((2 * Math.log(n.getVisits())) / (c.getVisits()));
        	
			uctValue += rng.nextDouble() * 1e-6; //In case of same uctValue, this random will remove bias towards first checked child
			if (bestValue < uctValue) {
				bestValue = uctValue;
				best = c; 
			}
		}
        //TESTING
        if (best == null) {
        	System.out.println("BUGS");
		}
        //
		return best;
	}
	
	/**
	 * Backup method
	 * @param next
	 * @param score
	 */
	private void backup(Node next, int score) {
		Node current = next;
		while(current != null){
			current = current.visit(score);
		}
	}

	/**
	 * Method for figureing out if a node is fully expanded yet or terminal
	 * @param n
	 * @return
	 */
	public Node treePolicy(Node n){
		//Node current = n;
		while (!n.isTerminal()) { //Non terminal
			if (!n.getExpanded()) { //Not fully expanded
				return n.expand();
			} else {
				n = bestChild(n);
			}
		}
		return n;
	}
	
	/**
	 * Method which simulates the game from a given node/state
	 * @param n
	 * @return
	 */
	private int defaultPolicy(Node n){
		Game currentState = n.getState().copy();
		int d = 0;
		
		while(!isStateTerminal(currentState) && d != depth){
			MOVE localCurrentDirection = null;
			if (d == 0 || currentState.isJunction(currentState.getPacmanCurrentNodeIndex())) { //is in a junction
				MOVE[] moves = currentState.getPossibleMoves(currentState.getPacmanCurrentNodeIndex());
				int i = rng.nextInt(moves.length);
				localCurrentDirection = moves[i];
				while (moves[i] == localCurrentDirection.opposite()) {
					i = rng.nextInt(moves.length);
				}
				localCurrentDirection = moves[i];
			}
			
			currentState.advanceGame(localCurrentDirection, controller.Controller.getGhostController().getMove(currentState, 0));
			d++;
		}
		
		if (isStateTerminal(currentState)) {
			return -1;
		}
		return currentState.getScore();
	}
	
	private boolean isStateTerminal(Game g){
		return g.gameOver();
	}
}
