package controller;

import java.util.EnumMap;

import pacman.Executor;
import pacman.controllers.examples.PassiveGhost;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Controller {
	static MCTS controller = new MCTS();
	private static pacman.controllers.Controller<EnumMap<GHOST,MOVE>> ghostController = new StarterGhosts();
	//private static pacman.controllers.Controller<EnumMap<GHOST,MOVE>> ghostController = new PassiveGhost();
	
	public static void main(String[] args) {
		
		Executor exec = new Executor();
		exec.runGame(controller, ghostController, true, 40);
	}
	
	public static pacman.controllers.Controller<EnumMap<GHOST,MOVE>> getGhostController(){
		return ghostController;
	}
}
