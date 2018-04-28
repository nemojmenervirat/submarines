package com.nemojmenervirat.submarines.logic;

import java.awt.Point;
import java.awt.Rectangle;

import com.nemojmenervirat.submarines.logic.Game.Direction;

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
	private int totalAttacks;
	private int currentAttacks;
	private int totalSonarTurns;
	private int currentSonarTurns;
	private Projectile projectile;

	public int getTotalSonarTurns() {
		return totalSonarTurns;
	}

	public void setTotalSonarTurns(int totalSonarTurns) {
		this.totalSonarTurns = totalSonarTurns;
	}

	public int getCurrentSonarTurns() {
		return currentSonarTurns;
	}

	public void setCurrentSonarTurns(int currentSonarTurns) {
		this.currentSonarTurns = currentSonarTurns;
	}

	public int getSonarRange() {
		return sonarRange;
	}

	public void setSonarRange(int sonarRange) {
		this.sonarRange = sonarRange;
	}

	public Projectile getProjectile() {
		return projectile;
	}

	public void setProjectile(Projectile projectile) {
		this.projectile = projectile;
	}

	public Point getProjectileStartPosition() {
		float x = getStartX() + getX() + (getDirection() == Direction.LTR ? getWidth() : -getProjectile().getWidth());
		float y = getStartY() + getY() + getHeight() / 2 - getProjectile().getHeight() / 2;
		Point position = new Point((int) x, (int) y);
		return position;
	}

	public Rectangle getRectangle() {
		return new Rectangle((int) (getStartX() + getX()), (int) (getStartY() + getY()), (int) getWidth(),
				(int) getHeight());
	}

	public int getTotalAttacks() {
		return totalAttacks;
	}

	public void setTotalAttacks(int maxAttacks) {
		this.totalAttacks = maxAttacks;
	}

	public int getCurrentAttacks() {
		return currentAttacks;
	}

	public void setCurrentAttacks(int currentAttacks) {
		this.currentAttacks = currentAttacks;
	}

	public float getStartX() {
		return startX;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public float getStartY() {
		return startY;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}

	public void attack() {
		currentAttacks--;
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

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public int getTotalHp() {
		return totalHp;
	}

	public void setTotalHp(int totalHp) {
		this.totalHp = totalHp;
	}

	public int getCurrentHp() {
		return currentHp;
	}

	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}

	public int getTotalMovesPerTurn() {
		return totalMovesPerTurn;
	}

	public void setTotalMovesPerTurn(int totalMovesPerTurn) {
		this.totalMovesPerTurn = totalMovesPerTurn;
	}

	public int getCurrentMovesPerTurn() {
		return currentMovesPerTurn;
	}

	public void setCurrentMovesPerTurn(int currentMovesPerTurn) {
		this.currentMovesPerTurn = currentMovesPerTurn;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getMoveLength() {
		return moveLength;
	}

	public void setMoveLength(int moveLength) {
		this.moveLength = moveLength;
	}
}
