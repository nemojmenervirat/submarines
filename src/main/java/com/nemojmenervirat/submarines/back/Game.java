package com.nemojmenervirat.submarines.back;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.nemojmenervirat.submarines.back.SubmarineFactory.Type;
import lombok.Getter;

public class Game {

  static ExecutorService executorService = Executors.newSingleThreadExecutor();

  @Getter
  private UUID id = UUID.randomUUID();
  private User leftUser;
  private User rightUser;
  private List<Submarine> leftUserSubmarines = new LinkedList<>();
  private List<Submarine> rightUserSubmarines = new LinkedList<>();
  private GameBroadcastListener leftListener;
  private GameBroadcastListener rightListener;
  private int turn;
  @Getter
  private int playgroundWidth;
  @Getter
  private int playgroundHeight;

  public interface GameBroadcastListener {
    public void receiveTurnEndBroadcast(User attacker);

    public void receiveSubmarineHitBroadcast(Submarine attacked);

    public void receiveGameFinishedBroadcast();
  }

  public Game(User leftUser, User rightUser, GameType gameType) {
    this.leftUser = leftUser;
    this.rightUser = rightUser;

    playgroundWidth = 1000;
    playgroundHeight = 700;

    if (gameType == GameType.SHORT) {
      playgroundWidth = 600;
      playgroundHeight = 500;
    }

    int leftLine = 100;
    int rightLine = playgroundWidth - 100;

    Random random = new Random(System.currentTimeMillis());
    final int RandomYRange = playgroundHeight / 12;
    final int RandomXRange = playgroundWidth / 7;

    {

      int randomY = random.nextInt(RandomYRange);
      int randomX = random.nextInt(RandomXRange);
      Submarine sub1 =
          SubmarineFactory.createNew(Type.T1, Direction.LTR, leftLine - RandomXRange / 2 + randomX,
              playgroundHeight / 6 * 1 - RandomYRange / 2 + randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub2 =
          SubmarineFactory.createNew(Type.T2, Direction.LTR, leftLine - RandomXRange / 2 + randomX,
              playgroundHeight / 6 * 2 - RandomYRange / 2 + randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub3 =
          SubmarineFactory.createNew(Type.T3, Direction.LTR, leftLine - RandomXRange / 2 + randomX,
              playgroundHeight / 6 * 3 - RandomYRange / 2 + randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub4 =
          SubmarineFactory.createNew(Type.T4, Direction.LTR, leftLine - RandomXRange / 2 + randomX,
              playgroundHeight / 6 * 4 - RandomYRange / 2 + randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub5 =
          SubmarineFactory.createNew(Type.T5, Direction.LTR, leftLine - RandomXRange / 2 + randomX,
              playgroundHeight / 6 * 5 - RandomYRange / 2 + randomY);

      leftUserSubmarines.add(sub1);
      leftUserSubmarines.add(sub2);
      leftUserSubmarines.add(sub3);
      leftUserSubmarines.add(sub4);
      leftUserSubmarines.add(sub5);
    }
    {
      int randomY = random.nextInt(RandomYRange);
      int randomX = random.nextInt(RandomXRange);
      Submarine sub1 =
          SubmarineFactory.createNew(Type.T1, Direction.RTL, rightLine + RandomXRange / 2 - randomX,
              playgroundHeight / 6 * 1 + RandomYRange / 2 - randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub2 =
          SubmarineFactory.createNew(Type.T2, Direction.RTL, rightLine + RandomXRange / 2 - randomX,
              playgroundHeight / 6 * 2 + RandomYRange / 2 - randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub3 =
          SubmarineFactory.createNew(Type.T3, Direction.RTL, rightLine + RandomXRange / 2 - randomX,
              playgroundHeight / 6 * 3 + RandomYRange / 2 - randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub4 =
          SubmarineFactory.createNew(Type.T4, Direction.RTL, rightLine + RandomXRange / 2 - randomX,
              playgroundHeight / 6 * 4 + RandomYRange / 2 - randomY);
      randomY = random.nextInt(RandomYRange);
      randomX = random.nextInt(RandomXRange);
      Submarine sub5 =
          SubmarineFactory.createNew(Type.T5, Direction.RTL, rightLine + RandomXRange / 2 - randomX,
              playgroundHeight / 6 * 5 + RandomYRange / 2 - randomY);

      rightUserSubmarines.add(sub1);
      rightUserSubmarines.add(sub2);
      rightUserSubmarines.add(sub3);
      rightUserSubmarines.add(sub4);
      rightUserSubmarines.add(sub5);
    }

    turn = 1;

  }

  public synchronized void join(User user, GameBroadcastListener listener) {
    if (user.equals(leftUser)) {
      leftListener = listener;
    }
    if (user.equals(rightUser)) {
      rightListener = listener;
    }
  }

  public int getCurrentTurn() {
    return turn;
  }

  public User getCurrentTurnUser() {
    return turn % 2 == 0 ? rightUser : leftUser;
  }

  public synchronized void goNextMove() {
    User current = getCurrentTurnUser();
    turn++;
    if (current.equals(leftUser)) {
      resetSubmarines(rightUserSubmarines);
      executorService.execute(() -> rightListener.receiveTurnEndBroadcast(leftUser));
    }
    if (current.equals(rightUser)) {
      resetSubmarines(leftUserSubmarines);
      executorService.execute(() -> leftListener.receiveTurnEndBroadcast(rightUser));
    }
  }

  private void resetSubmarines(List<Submarine> submarines) {
    for (Submarine submarine : submarines) {
      submarine.setCurrentMovesPerTurn(submarine.getTotalMovesPerTurn());
      submarine.setCurrentAttacksPerTurn(submarine.getTotalAttacksPerTurn());
      int currentSonarTurns = submarine.getCurrentSonarTurns();
      int totalSonarTurns = submarine.getTotalSonarTurns();
      if (currentSonarTurns < totalSonarTurns) {
        submarine.setCurrentSonarTurns(currentSonarTurns + 1);
      }
    }
  }


  public List<Submarine> getSubmarines(User user) {
    return leftUser.equals(user) ? leftUserSubmarines : rightUserSubmarines;
  }

  public Direction getDirection(User user) {
    return leftUser.equals(user) ? Direction.LTR : Direction.RTL;
  }

  public boolean isUserIn(User user) {
    return leftUser != null && leftUser.equals(user) || rightUser != null && rightUser.equals(user);
  }

  public boolean canMove(User user, Submarine submarine, Move move) {
    Direction direction = submarine.getDirection();
    Rectangle playgroundBounds = new Rectangle(direction == Direction.LTR ? 0 : playgroundWidth / 2,
        0, playgroundWidth / 2, playgroundHeight);
    int xMove = 0;
    int yMove = 0;
    switch (move) {
      case LEFT:
        xMove -= submarine.getMoveLength();
        break;
      case RIGHT:
        xMove += submarine.getMoveLength();
        break;
      case UP:
        yMove -= submarine.getMoveLength();
        break;
      case DOWN:
        yMove += submarine.getMoveLength();
        break;
    }
    Rectangle submarineBounds =
        new Rectangle((int) (submarine.getStartX() + submarine.getX()) + xMove,
            (int) (submarine.getStartY() + submarine.getY()) + yMove, (int) submarine.getWidth(),
            (int) submarine.getHeight());
    return playgroundBounds.contains(submarineBounds);
  }

  public List<Submarine> collidingSubmarines(User user, Submarine submarine) {
    List<Submarine> submarines = user.equals(leftUser) ? leftUserSubmarines : rightUserSubmarines;
    List<Submarine> result = new LinkedList<>();
    for (Submarine sub : submarines) {
      if (!sub.equals(submarine)) {
        if (submarine.getRectangle().intersects(sub.getRectangle())) {
          result.add(sub);
        }
      }
    }
    return result;
  }

  public synchronized void destroySubmarine(Submarine submarine) {
    submarine.setCurrentHp(0);
    leftUserSubmarines.remove(submarine);
    rightUserSubmarines.remove(submarine);
  }

  public boolean isFinished() {
    return leftUserSubmarines.isEmpty() || rightUserSubmarines.isEmpty();
  }

  public User getWinner() {
    if (!isFinished()) {
      return null;
    }
    return leftUserSubmarines.isEmpty() ? rightUser : leftUser;
  }

  public Submarine getHitTarget(User user, Submarine attacker) {
    Submarine target = null;
    List<Submarine> possibleTargets =
        user.equals(leftUser) ? rightUserSubmarines : leftUserSubmarines;
    possibleTargets.sort((s1, s2) -> {
      return attacker.getDirection() == Direction.LTR
          ? Float.compare(s1.getStartX() + s1.getX(), s2.getStartX() + s2.getX())
          : Float.compare(s2.getStartX() + s2.getX(), s1.getStartX() + s1.getX());
    });
    Rectangle projectileRectangle = new Rectangle(attacker.getProjectileStartPosition().x,
        attacker.getProjectileStartPosition().y, (int) attacker.getProjectile().getWidth(),
        (int) attacker.getProjectile().getHeight());
    Rectangle projectileTrajectory = new Rectangle(
        attacker.getDirection() == Direction.LTR ? (int) projectileRectangle.getX()
            : (int) (projectileRectangle.getX() - attacker.getRange()),
        (int) projectileRectangle.getY(),
        (int) projectileRectangle.getWidth() + attacker.getRange(),
        (int) projectileRectangle.getHeight());
    for (Submarine submarine : possibleTargets) {
      Rectangle intersection = submarine.getRectangle().intersection(projectileTrajectory);
      if (intersection.getWidth() >= projectileRectangle.getWidth() / 2
          && intersection.getHeight() >= projectileRectangle.getHeight() / 2) {
        target = submarine;
        break;
      }
    }
    return target;
  }

  private static double distance(Point p1, Point p2) {
    return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
        + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
  }

  public List<Submarine> getSonarTargets(User user, Submarine submarine) {
    List<Submarine> sonarTargets = new LinkedList<>();
    List<Submarine> possibleTargets =
        user.equals(leftUser) ? rightUserSubmarines : leftUserSubmarines;
    Rectangle submarineRectangle = submarine.getRectangle();
    Point submarinePoint = new Point(submarineRectangle.x, submarineRectangle.y);
    for (Submarine possibleTarget : possibleTargets) {
      Rectangle possibleTargetRectangle = possibleTarget.getRectangle();
      Point possibleTargetPoint = new Point(possibleTargetRectangle.x, possibleTargetRectangle.y);
      if (distance(submarinePoint, possibleTargetPoint) <= submarine.getSonarRange()) {
        sonarTargets.add(possibleTarget);
      }
    }
    return sonarTargets;
  }

  public enum Direction {
    LTR, RTL
  }

  public enum Move {
    UP, DOWN, LEFT, RIGHT
  }

  public enum GameType {
    SHORT, LONG
  }

  public synchronized void submarineHit(Submarine attacker, Submarine target) {
    target.setCurrentHp(target.getCurrentHp() - attacker.getDamage());
    User targetOwner = rightUserSubmarines.contains(target) ? rightUser : leftUser;
    if (targetOwner.equals(rightUser)) {
      executorService.execute(() -> rightListener.receiveSubmarineHitBroadcast(target));
    }
    if (targetOwner.equals(leftUser)) {
      executorService.execute(() -> leftListener.receiveSubmarineHitBroadcast(target));
    }
  }

  public synchronized void checkStatus() {
    if (isFinished()) {
      executorService.execute(() -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
        rightListener.receiveGameFinishedBroadcast();
        leftListener.receiveGameFinishedBroadcast();
      });
    }
  }

  public void leave(User currentUser) {
    if (leftUser != null && leftUser.equals(currentUser)) {
      leftUser = null;
    }
    if (rightUser != null && rightUser.equals(currentUser)) {
      rightUser = null;
    }
  }

  public boolean isLeft() {
    return leftUser == null && rightUser == null;
  }

}
