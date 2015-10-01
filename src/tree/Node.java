package tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import controller.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Node {
	private Game game;
	//private List<Node> children = new ArrayList<Node>();
	private HashMap<MOVE, Node> children;
	private int totalValue;
	private int visits;
	private Node parent;
	private MOVE move;
	private static Random rng = new Random();
	private int expansions;

	public Node(Game g, Node p, MOVE m) {
		game = g;
		parent = p;
		move = m;
		children = new HashMap<MOVE, Node>();
	}

	public boolean isTerminal() {
		return game.gameOver();
	}

	public MOVE getMove(){
		return move;
	}

	public MOVE[] getMoves() {
		MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		if (move == null) {//root node
			return possibleMoves;
		}
		else {
			MOVE[] rMoves = new MOVE[possibleMoves.length-1];
			int rPointer = 0;
			for (MOVE m : possibleMoves) {
				if (m != move.opposite()) {
					rMoves[rPointer] = m;
					rPointer++;
				}
			}
			
			/*
			for (int i = 0; i < possibleMoves.length; i++) {
				if (possibleMoves[i] != move.opposite()) {
					rMoves[rPointer] = possibleMoves[i];
					rPointer++;
				}
			}*/
			return rMoves;
		}
	}

	public Game getState() {
		return game;
	}

	public HashMap<MOVE, Node> getChildren() {
		return children;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public double getVisits() {
		return visits;
	}

	public Node visit(int score) {
		totalValue += score;
		visits++;
		return parent;
	}
	
	public Node expand(){
		MOVE[] moves = getMoves();
		
		if (expansions == 0) {
			for (MOVE m : moves) {
				Game newGame = game.copy();
				MOVE currentDirection = m;
				do {
					ArrayList<MOVE> tempMoves = new ArrayList<MOVE>(Arrays.asList(newGame.getPossibleMoves(newGame.getPacmanCurrentNodeIndex())));
					//MOVE[]g.getPossibleMoves(pacmanNode)
					if (!tempMoves.contains(currentDirection)) {
						tempMoves.remove(currentDirection.opposite());
						currentDirection = tempMoves.get(0);
					}
					
					newGame.advanceGame(currentDirection, Controller.getGhostController().getMove(newGame, 0));
					//newGame.advanceGame(m, Controller.getGhostController().getMove(newGame, 0));
				} while (!newGame.isJunction(newGame.getPacmanCurrentNodeIndex()) || newGame.gameOver());
				Node child = new Node(newGame, this, m);
				addChild(child);
			}
		}
		
		//Return untried children
		expansions++;
		int i = rng.nextInt(moves.length);
		while (children.get(moves[i]).getVisits() != 0) {
			i = rng.nextInt(moves.length);
		}
		return children.get(moves[i]);
	}

	public boolean getExpanded() {
		return expansions == children.size() && expansions != 0;
	}

	public void addChild(Node c) {
		children.put(c.getMove(), c);
	}
}
