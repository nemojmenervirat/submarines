package com.nemojmenervirat.submarines.back;

import java.awt.Point;
import java.awt.Rectangle;
import com.nemojmenervirat.submarines.back.Game.Direction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Submarine {

  private float x;
  private float y;
  private float startX;
  private float startY;
  private float width;
  private float height;
  private Direction direction;
  private String imagePath;

  private int totalHp;
  private int currentHp;
  private int totalMovesPerTurn;
  private int currentMovesPerTurn;

  private int damage;
  private int range;
  private int sonarRange;
  private int moveLength;
  private int totalAttacksPerTurn;
  private int currentAttacksPerTurn;
  private int totalSonarTurns;
  private int currentSonarTurns;
  private Projectile projectile;

  public Point getProjectileStartPosition() {
    float x = getStartX() + getX()
        + (getDirection() == Direction.LTR ? getWidth() : -getProjectile().getWidth());
    float y = getStartY() + getY() + getHeight() / 2 - getProjectile().getHeight() / 2;
    Point position = new Point((int) x, (int) y);
    return position;
  }

  public Rectangle getRectangle() {
    return new Rectangle((int) (getStartX() + getX()), (int) (getStartY() + getY()),
        (int) getWidth(), (int) getHeight());
  }


  public void attack() {
    currentAttacksPerTurn--;
  }

  public void sonar() {
    currentSonarTurns = 0;
  }

  public void moveForward() {
    currentMovesPerTurn--;
    x += moveLength;
  }

  public void moveBackward() {
    currentMovesPerTurn--;
    x -= moveLength;
  }

  public void moveUp() {
    currentMovesPerTurn--;
    y -= moveLength;
  }

  public void moveDown() {
    currentMovesPerTurn--;
    y += moveLength;
  }

}
