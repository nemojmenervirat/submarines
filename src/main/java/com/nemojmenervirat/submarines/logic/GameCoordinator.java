package com.nemojmenervirat.submarines.logic;

import java.util.LinkedList;
import java.util.List;

import com.nemojmenervirat.submarines.logic.Game.GameType;

public class GameCoordinator {

	static List<Game> games = new LinkedList<>();

	public synchronized static Game reserveGame(final User u1, final User u2, GameType gameType) {
		for (Game game : games) {
			if (!game.isFinished() && game.isUserIn(u1) && game.isUserIn(u2)) {
				return game;
			}
		}
		Game game = new Game(u1, u2, gameType);
		games.add(game);
		return game;
	}

	public synchronized static void gameFinished(Game game) {
		if (game.isFinished()) {
			games.remove(game);
		}
	}

	public static boolean isUserInGame(User user) {
		for (Game game : games) {
			if (game.isUserIn(user)) {
				return true;
			}
		}
		return false;
	}

}
