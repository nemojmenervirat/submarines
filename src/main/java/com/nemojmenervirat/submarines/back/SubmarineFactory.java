package com.nemojmenervirat.submarines.back;

import com.nemojmenervirat.submarines.back.Game.Direction;

public class SubmarineFactory {

  public enum Type {
    T1, T2, T3, T4, T5
  }

  private static String getPath(String path, Direction direction) {
    return path + (direction == Direction.LTR ? "_l.png" : "_r.png");
  }

  public static Submarine createNew(Type type, Direction direction, float x, float y) {
    Submarine submarine = new Submarine();
    submarine.setDirection(direction);
    submarine.setMoveLength(10);
    submarine.setX(0);
    submarine.setY(0);
    submarine.setProjectile(new Projectile());
    submarine.getProjectile().setWidth(20);
    submarine.getProjectile().setHeight(5);
    submarine.getProjectile().setImagePath(getPath("images/projectile1", direction));
    submarine.setTotalAttacksPerTurn(1);
    submarine.setTotalSonarTurns(3);

    switch (type) {
      case T1:
        submarine.setImagePath(getPath("images/submarine1", direction));
        submarine.setHeight(42);
        submarine.setWidth(120);
        submarine.setTotalMovesPerTurn(3);
        submarine.setTotalHp(8);
        submarine.setDamage(3);
        submarine.setRange(500);
        submarine.setSonarRange(500);
        break;
      case T2:
        submarine.setImagePath(getPath("images/submarine2", direction));
        submarine.setHeight(30);
        submarine.setWidth(100);
        submarine.setTotalMovesPerTurn(4);
        submarine.setTotalHp(5);
        submarine.setDamage(2);
        submarine.setRange(400);
        submarine.setSonarRange(250);
        break;
      case T3:
        submarine.setImagePath(getPath("images/submarine3", direction));
        submarine.setHeight(22);
        submarine.setWidth(72);
        submarine.setTotalMovesPerTurn(8);
        submarine.setTotalHp(3);
        submarine.setDamage(1);
        submarine.setRange(400);
        submarine.setSonarRange(250);
        break;
      case T4:
        submarine.setImagePath(getPath("images/submarine4", direction));
        submarine.setHeight(36);
        submarine.setWidth(122);
        submarine.setTotalMovesPerTurn(3);
        submarine.setTotalAttacksPerTurn(10);
        submarine.setTotalHp(7);
        submarine.setDamage(2);
        submarine.setRange(450);
        submarine.setSonarRange(500);
        break;
      case T5:
        submarine.setImagePath(getPath("images/submarine5", direction));
        submarine.setHeight(32);
        submarine.setWidth(120);
        submarine.setTotalMovesPerTurn(2);
        submarine.setTotalHp(12);
        submarine.setDamage(1);
        submarine.setRange(500);
        submarine.setSonarRange(500);
        break;
    }

    switch (direction) {
      case LTR:
        submarine.setStartX(x);
        submarine.setStartY(y);
        break;
      case RTL:
        submarine.setStartX(x - submarine.getWidth());
        submarine.setStartY(y);
        break;
    }
    submarine.setCurrentMovesPerTurn(submarine.getTotalMovesPerTurn());
    submarine.setCurrentHp(submarine.getTotalHp());
    submarine.setCurrentAttacksPerTurn(submarine.getTotalAttacksPerTurn());
    submarine.setCurrentSonarTurns(submarine.getTotalSonarTurns());

    return submarine;
  }

}
