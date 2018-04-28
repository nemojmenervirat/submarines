package com.nemojmenervirat.submarines.ui;

import java.util.List;

import org.vaadin.jouni.dom.Dom;

import com.nemojmenervirat.submarines.logic.Submarine;
import com.nemojmenervirat.submarines.logic.Game.Move;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

public class ControlLayout extends Panel {

	private SubmarinesUI ui;
	private Image selectedImage;
	private ProgressBar progresBarMoves;
	private Label lblMovesPerTurn;
	private Label lblMoveLength;
	private Label lblMovesLeft;
	private Label lblProgresBarMoves;
	private Label lblTotalHp;
	private Label lblCurrentHp;
	private ProgressBar progresBarHp;
	private Label lblProgresBarHp;
	private Label lblDamage;
	private Label lblRange;
	private Label lblSonar;
	private Button goright;
	private Button goleft;
	private Button goup;
	private Button godown;
	private Button fire;
	private Button sonar;

	private PlayGround playGround;

	private static final String MOVE_LEN = "Move length: ";
	private static final String MOVE_PER_TURN = "Moves per turn: ";
	private static final String MOVE_LEFT = "Moves left in this turn: ";
	private static final String HP_TOTAL = "Total hit points: ";
	private static final String HP_CURRENT = "Current hit points: ";
	private static final String PROGRES_LBL = "0/0";
	private static final String ATT_DMG = "Attack damage: ";
	private static final String ATT_RNG = "Attack range: ";
	private static final String ATT_SON = "Sonar range: ";

	public void setSelectedImage(Image image) {
		if (selectedImage != null) {
			new Dom(selectedImage).getStyle().setProperty("border", "none");
		}
		selectedImage = image;
		if (selectedImage != null) {
			new Dom(selectedImage).getStyle().setProperty("border", "2px solid green");
			lblMoveLength.setValue(MOVE_LEN + selectedSubmarine().getMoveLength());
			lblMovesPerTurn.setValue(MOVE_PER_TURN + selectedSubmarine().getTotalMovesPerTurn());
			lblMovesLeft.setValue(MOVE_LEFT + selectedSubmarine().getCurrentMovesPerTurn());
			progresBarMoves.setValue((float) selectedSubmarine().getCurrentMovesPerTurn() / (float) selectedSubmarine().getTotalMovesPerTurn());
			lblProgresBarMoves.setValue(selectedSubmarine().getCurrentMovesPerTurn() + "/" + selectedSubmarine().getTotalMovesPerTurn());

			lblTotalHp.setValue(HP_TOTAL + selectedSubmarine().getTotalHp());
			lblCurrentHp.setValue(HP_CURRENT + selectedSubmarine().getCurrentHp());
			progresBarHp.setValue((float) selectedSubmarine().getCurrentHp() / (float) selectedSubmarine().getTotalHp());
			lblProgresBarHp.setValue(selectedSubmarine().getCurrentHp() + "/" + selectedSubmarine().getTotalHp());

			lblDamage.setValue(ATT_DMG + selectedSubmarine().getDamage());
			lblRange.setValue(ATT_RNG + selectedSubmarine().getRange());
			lblSonar.setValue(ATT_SON + selectedSubmarine().getSonarRange());

			updateButtons();
		} else {
			lblMoveLength.setValue(MOVE_LEN);
			lblMovesPerTurn.setValue(MOVE_PER_TURN);
			lblMovesLeft.setValue(MOVE_LEFT);
			progresBarMoves.setValue(0);
			lblProgresBarMoves.setValue(PROGRES_LBL);

			lblTotalHp.setValue(HP_TOTAL);
			lblCurrentHp.setValue(HP_CURRENT);
			progresBarHp.setValue(0);
			lblProgresBarHp.setValue(0 + "/" + 0);

			lblDamage.setValue(ATT_DMG);
			lblRange.setValue(ATT_RNG);
			lblSonar.setValue(ATT_SON);
		}
		setEnabled(selectedImage != null && ui.getCurrentGame().getCurrentTurnUser().equals(ui.getCurrentUser()));
	}

	public void reselect() {
		if (selectedSubmarine() != null && selectedSubmarine().getCurrentHp() > 0) {
			setSelectedImage(selectedImage);
		} else {
			setSelectedImage(null);
		}
	}

	private Submarine selectedSubmarine() {
		if (selectedImage == null) {
			return null;
		}
		return (Submarine) selectedImage.getData();
	}

	private void afterMoveEvents() {
		playGround.animateMove(selectedSubmarine());
		lblMovesLeft.setValue(MOVE_LEFT + selectedSubmarine().getCurrentMovesPerTurn());
		progresBarMoves.setValue((float) selectedSubmarine().getCurrentMovesPerTurn() / (float) selectedSubmarine().getTotalMovesPerTurn());
		lblProgresBarMoves.setValue(selectedSubmarine().getCurrentMovesPerTurn() + "/" + selectedSubmarine().getTotalMovesPerTurn());
		List<Submarine> collidingSubmarines = ui.getCurrentGame().collidingSubmarines(ui.getCurrentUser(), selectedSubmarine());
		if (collidingSubmarines.size() > 0) {
			ui.getCurrentGame().destroySubmarine(selectedSubmarine());
			playGround.animateDeath(selectedSubmarine(), 0);
			for (Submarine submarine : collidingSubmarines) {
				ui.getCurrentGame().destroySubmarine(submarine);
				playGround.animateDeath(submarine, 100);
			}
			setSelectedImage(null);
			ui.getCurrentGame().checkStatus();
		} else {
			updateButtons();
		}
	}

	private void updateButtons() {
		goup.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
		godown.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
		goleft.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
		goright.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
		fire.setEnabled(selectedSubmarine().getCurrentAttacks() > 0);
		sonar.setEnabled(selectedSubmarine().getCurrentSonarTurns() == selectedSubmarine().getTotalSonarTurns());
	}

	public ControlLayout(PlayGround playGround, SubmarinesUI ui) {

		this.ui = ui;
		this.playGround = playGround;
		playGround.addLayoutClickListener((e) -> {
			if (selectedSubmarine() != null && !(selectedSubmarine().getStartX() + selectedSubmarine().getX() < e.getRelativeX()
					&& selectedSubmarine().getStartY() + selectedSubmarine().getY() < e.getRelativeY()
					&& selectedSubmarine().getStartX() + selectedSubmarine().getX() + selectedSubmarine().getWidth() > e.getRelativeX()
					&& selectedSubmarine().getStartY() + selectedSubmarine().getY() + selectedSubmarine().getHeight() > e.getRelativeY())) {
				setSelectedImage(null);
				System.out.println("SELECTED = NULL");
			}
		});

		goright = new Button();
		goright.addClickListener((e) -> {
			if (selectedSubmarine() != null && ui.getCurrentGame().canMove(ui.getCurrentUser(), selectedSubmarine(), Move.RIGHT)) {
				selectedSubmarine().moveForward();
				afterMoveEvents();
			}
		});
		goleft = new Button();
		goleft.addClickListener((e) -> {
			if (selectedSubmarine() != null && ui.getCurrentGame().canMove(ui.getCurrentUser(), selectedSubmarine(), Move.LEFT)) {
				selectedSubmarine().moveBackward();
				afterMoveEvents();
			}
		});
		goup = new Button();
		goup.addClickListener((e) -> {
			if (selectedSubmarine() != null && ui.getCurrentGame().canMove(ui.getCurrentUser(), selectedSubmarine(), Move.UP)) {
				selectedSubmarine().moveUp();
				afterMoveEvents();
			}
		});
		godown = new Button();
		godown.addClickListener((e) -> {
			if (selectedSubmarine() != null && ui.getCurrentGame().canMove(ui.getCurrentUser(), selectedSubmarine(), Move.DOWN)) {
				selectedSubmarine().moveDown();
				afterMoveEvents();
			}
		});
		fire = new Button();
		fire.addClickListener((e) -> {
			selectedSubmarine().attack();
			Submarine target = ui.getCurrentGame().getHitTarget(ui.getCurrentUser(), selectedSubmarine());
			if (target != null) {
				ui.getCurrentGame().submarineHit(selectedSubmarine(), target);
				if (target.getCurrentHp() <= 0) {
					ui.getCurrentGame().destroySubmarine(target);
				}
			}
			playGround.animateProjectile(selectedSubmarine(), target);
			updateButtons();
			ui.getCurrentGame().checkStatus();
		});
		fire.setDescription("Attack once per turn per submarine.");
		sonar = new Button();
		sonar.addClickListener((e) -> {
			selectedSubmarine().sonar();
			List<Submarine> sonarTargets = ui.getCurrentGame().getSonarTargets(ui.getCurrentUser(), selectedSubmarine());
			playGround.animateSonar(selectedSubmarine(), sonarTargets);
			updateButtons();
		});
		sonar.setDescription("Explore battleground once every 3 turns.");

		lblMovesPerTurn = new Label(MOVE_PER_TURN);
		lblMoveLength = new Label(MOVE_LEN);
		lblMovesLeft = new Label(MOVE_LEFT);
		progresBarMoves = new ProgressBar(0);
		lblProgresBarMoves = new Label(PROGRES_LBL);

		AbsoluteLayout layoutMovement = new AbsoluteLayout();
		layoutMovement.setWidth("250px");
		layoutMovement.setHeight("200px");
		layoutMovement.addComponent(lblMoveLength, "top:10px; left:10px;");
		layoutMovement.addComponent(lblMovesPerTurn, "top:30px; left:10px;");
		layoutMovement.addComponent(lblMovesLeft, "top:50px; left:10px;");
		layoutMovement.addComponent(progresBarMoves, "top:78px; left:10px;");
		progresBarMoves.setWidth("180px");
		layoutMovement.addComponent(lblProgresBarMoves, "top:70px; left:200px;");
		layoutMovement.addComponent(goup, "top:100px; left:100px");
		goup.setIcon(new ThemeResource("img/goup.png"));
		goup.setWidth("40px");
		goup.setHeight("40px");
		new Dom(goup).getStyle().setProperty("padding", "0px");
		layoutMovement.addComponent(godown, "top:150px; left:100px");
		godown.setIcon(new ThemeResource("img/godown.png"));
		godown.setWidth("40px");
		godown.setHeight("40px");
		new Dom(godown).getStyle().setProperty("padding", "0px");
		layoutMovement.addComponent(goleft, "top:125px; left:50px");
		goleft.setIcon(new ThemeResource("img/goleft.png"));
		goleft.setWidth("40px");
		goleft.setHeight("40px");
		new Dom(goleft).getStyle().setProperty("padding", "0px");
		layoutMovement.addComponent(goright, "top:125px; left:150px;");
		goright.setIcon(new ThemeResource("img/goright.png"));
		goright.setWidth("40px");
		goright.setHeight("40px");
		new Dom(goright).getStyle().setProperty("padding", "0px");
		Panel panelMovement = new Panel("Movement", layoutMovement);

		lblTotalHp = new Label(HP_TOTAL);
		lblCurrentHp = new Label(HP_CURRENT);
		progresBarHp = new ProgressBar(0);
		lblProgresBarHp = new Label(PROGRES_LBL);

		AbsoluteLayout layoutHp = new AbsoluteLayout();
		layoutHp.setWidth("250px");
		layoutHp.setHeight("78px");
		layoutHp.addComponent(lblTotalHp, "top:10px; left:10px;");
		layoutHp.addComponent(lblCurrentHp, "top:30px; left:10px;");
		layoutHp.addComponent(progresBarHp, "top:58px; left:10px;");
		progresBarHp.setWidth("180px");
		layoutHp.addComponent(lblProgresBarHp, "top:50px; left:200px;");
		Panel panelHp = new Panel("Hit points", layoutHp);

		lblDamage = new Label(ATT_DMG);
		lblRange = new Label(ATT_RNG);
		lblSonar = new Label(ATT_SON);

		AbsoluteLayout layoutAttack = new AbsoluteLayout();
		layoutAttack.setWidth("250px");
		layoutAttack.setHeight("130px");
		layoutAttack.addComponent(lblDamage, "top:10px; left:10px");
		layoutAttack.addComponent(lblRange, "top:30px; left:10px");
		layoutAttack.addComponent(lblSonar, "top:50px; left:10px");
		layoutAttack.addComponent(fire, "top:78px; left:10px");
		fire.setIcon(new ThemeResource("img/attack.png"));
		fire.setWidth("40px");
		fire.setHeight("40px");
		new Dom(fire).getStyle().setProperty("padding", "0px");
		layoutAttack.addComponent(sonar, "top:78px; left:58px");
		sonar.setIcon(new ThemeResource("img/sonar.png"));
		sonar.setWidth("40px");
		sonar.setHeight("40px");
		new Dom(sonar).getStyle().setProperty("padding", "0px");
		Panel panelAttack = new Panel("Attack", layoutAttack);

		VerticalLayout vl = new VerticalLayout(panelMovement, panelHp, panelAttack);
		setContent(vl);
		setEnabled(false);
		setWidth("300px");
	}
}
