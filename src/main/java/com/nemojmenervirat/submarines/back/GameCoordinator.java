package com.nemojmenervirat.submarines.back;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import com.nemojmenervirat.submarines.back.Game.GameType;

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
    if (game.isLeft()) {
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

  public static Game findGame(UUID id) {
    return games.stream().filter(e -> e.getId().equals(id)).findAny().orElse(null);
  }

}
