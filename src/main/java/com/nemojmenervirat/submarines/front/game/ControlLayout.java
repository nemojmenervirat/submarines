package com.nemojmenervirat.submarines.front.game;

import java.util.List;
import com.nemojmenervirat.submarines.back.Game;
import com.nemojmenervirat.submarines.back.Game.Move;
import com.nemojmenervirat.submarines.back.Submarine;
import com.nemojmenervirat.submarines.back.User;
import com.nemojmenervirat.submarines.front.common.AbsoluteLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;

public class ControlLayout extends AbsoluteLayout {

  private final Game game;
  private final User currentUser;
  private final PlayGround playGround;

  private SubmarineImage selectedImage;

  private GameProgressBar progressBarMoves =
      new GameProgressBar("Moves", ProgressBarVariant.LUMO_CONTRAST);
  private GameProgressBar progressBarHp =
      new GameProgressBar("HP", ProgressBarVariant.LUMO_SUCCESS);
  private GameProgressBar progressBarAttacks =
      new GameProgressBar("Attacks", ProgressBarVariant.LUMO_CONTRAST);
  private GameProgressBar progressBarSonars =
      new GameProgressBar("Sonar charged", true, ProgressBarVariant.LUMO_CONTRAST);
  private GameLabel lblDamage = new GameLabel("Attack damage:");
  private GameLabel lblRange = new GameLabel("Attack range:");
  private GameLabel lblSonar = new GameLabel("Sonar range:");
  private GameLabel lblMoveLength = new GameLabel("Move length:");
  private GameButton buttonRight = new GameButton(VaadinIcon.ANGLE_DOUBLE_RIGHT, Key.ARROW_RIGHT);
  private GameButton buttonLeft = new GameButton(VaadinIcon.ANGLE_DOUBLE_LEFT, Key.ARROW_LEFT);
  private GameButton buttonUp = new GameButton(VaadinIcon.ANGLE_DOUBLE_UP, Key.ARROW_UP);
  private GameButton buttonDown = new GameButton(VaadinIcon.ANGLE_DOUBLE_DOWN, Key.ARROW_DOWN);
  private GameButton buttonFire = new GameButton(VaadinIcon.SWORD, Key.KEY_F);
  private GameButton buttonSonar = new GameButton(VaadinIcon.SIGNAL, Key.KEY_S);


  public ControlLayout(PlayGround playGround, Game game, User currentUser) {
    this.playGround = playGround;
    this.game = game;
    this.currentUser = currentUser;
    playGround.addClickListener((e) -> {
      if (selectedSubmarine() != null
          && !(selectedSubmarine().getStartX() + selectedSubmarine().getX() < e.getClientX()
              && selectedSubmarine().getStartY() + selectedSubmarine().getY() < e.getClientY()
              && selectedSubmarine().getStartX() + selectedSubmarine().getX()
                  + selectedSubmarine().getWidth() > e.getClientX()
              && selectedSubmarine().getStartY() + selectedSubmarine().getY()
                  + selectedSubmarine().getHeight() > e.getClientY())) {
        // setSelectedImage(null);
        System.out.println("SELECTED = NULL");
      }
    });

    buttonRight.addClickListener((e) -> {
      if (selectedSubmarine() != null
          && game.canMove(currentUser, selectedSubmarine(), Move.RIGHT)) {
        selectedSubmarine().moveForward();
        afterMoveEvents();
      }
    });
    buttonLeft.addClickListener((e) -> {
      if (selectedSubmarine() != null
          && game.canMove(currentUser, selectedSubmarine(), Move.LEFT)) {
        selectedSubmarine().moveBackward();
        afterMoveEvents();
      }
    });
    buttonUp.addClickListener((e) -> {
      if (selectedSubmarine() != null && game.canMove(currentUser, selectedSubmarine(), Move.UP)) {
        selectedSubmarine().moveUp();
        afterMoveEvents();
      }
    });
    buttonDown.addClickListener((e) -> {
      if (selectedSubmarine() != null
          && game.canMove(currentUser, selectedSubmarine(), Move.DOWN)) {
        selectedSubmarine().moveDown();
        afterMoveEvents();
      }
    });
    buttonFire.addClickListener((e) -> {
      selectedSubmarine().attack();
      progressBarAttacks.setValue(selectedSubmarine().getCurrentAttacksPerTurn(),
          selectedSubmarine().getTotalAttacksPerTurn());
      Submarine target = game.getHitTarget(currentUser, selectedSubmarine());
      if (target != null) {
        game.submarineHit(selectedSubmarine(), target);
        if (target.getCurrentHp() <= 0) {
          game.destroySubmarine(target);
        }
      }
      playGround.animateProjectile(selectedSubmarine(), target);
      updateButtons();
      game.checkStatus();
    });
    buttonFire.getElement().setProperty("title", "Attack once per turn per submarine.");

    buttonSonar.addClickListener((e) -> {
      selectedSubmarine().sonar();
      progressBarSonars.setValue(selectedSubmarine().getCurrentSonarTurns(),
          selectedSubmarine().getTotalSonarTurns());
      List<Submarine> sonarTargets = game.getSonarTargets(currentUser, selectedSubmarine());
      playGround.animateSonar(selectedSubmarine(), sonarTargets);
      updateButtons();
    });
    buttonSonar.getElement().setProperty("title", "Explore battleground once every 3 turns.");

    progressBarHp.setWidth("280px");
    progressBarMoves.setWidth("280px");
    progressBarAttacks.setWidth("280px");
    progressBarSonars.setWidth("280px");

    add(progressBarHp, 10, 10);
    add(lblDamage, 30, 10);
    add(lblRange, 50, 10);
    add(lblSonar, 70, 10);
    add(lblMoveLength, 90, 10);

    add(progressBarMoves, 130, 10);
    add(progressBarAttacks, 150, 10);
    add(progressBarSonars, 170, 10);
    add(buttonUp, 200, 60);
    add(buttonDown, 250, 60);
    add(buttonLeft, 225, 10);
    add(buttonRight, 225, 110);
    add(buttonFire, 200, 200);
    add(buttonSonar, 250, 200);

    setEnabled(false);
    setWidth("300px");
    setHeight("300px");
  }

  public void setSelectedImage(SubmarineImage image) {
    if (selectedImage != null) {
      selectedImage.getStyle().set("border", "none");
    }
    selectedImage = image;
    if (selectedImage != null) {
      selectedImage.getStyle().set("border", "2px solid green");
      progressBarMoves.setValue(selectedSubmarine().getCurrentMovesPerTurn(),
          selectedSubmarine().getTotalMovesPerTurn());
      progressBarHp.setValue(selectedSubmarine().getCurrentHp(), selectedSubmarine().getTotalHp());
      progressBarAttacks.setValue(selectedSubmarine().getCurrentAttacksPerTurn(),
          selectedSubmarine().getTotalAttacksPerTurn());
      progressBarSonars.setValue(selectedSubmarine().getCurrentSonarTurns(),
          selectedSubmarine().getTotalSonarTurns());

      lblDamage.setValue(selectedSubmarine().getDamage());
      lblRange.setValue(selectedSubmarine().getRange());
      lblSonar.setValue(selectedSubmarine().getSonarRange());
      lblMoveLength.setValue(selectedSubmarine().getMoveLength());

      updateButtons();
    } else {
      progressBarMoves.setValue(0, 0);
      progressBarHp.setValue(0, 0);
      progressBarAttacks.setValue(0, 0);
      progressBarSonars.setValue(0, 0);
      lblDamage.clear();
      lblRange.clear();
      lblSonar.clear();
      lblMoveLength.clear();
    }
    setEnabled(selectedImage != null && game.getCurrentTurnUser().equals(currentUser));
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
    return (Submarine) selectedImage.getSubmarine();
  }

  private void afterMoveEvents() {
    playGround.animateMove(selectedSubmarine());
    progressBarMoves.setValue(selectedSubmarine().getCurrentMovesPerTurn(),
        selectedSubmarine().getTotalMovesPerTurn());
    List<Submarine> collidingSubmarines =
        game.collidingSubmarines(currentUser, selectedSubmarine());
    if (collidingSubmarines.size() > 0) {
      game.destroySubmarine(selectedSubmarine());
      playGround.animateDeath(selectedSubmarine(), 0);
      for (Submarine submarine : collidingSubmarines) {
        game.destroySubmarine(submarine);
        playGround.animateDeath(submarine, 100);
      }
      setSelectedImage(null);
      game.checkStatus();
    } else {
      updateButtons();
    }
  }

  private void updateButtons() {
    buttonUp.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
    buttonDown.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
    buttonLeft.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
    buttonRight.setEnabled(selectedSubmarine().getCurrentMovesPerTurn() > 0);
    buttonFire.setEnabled(selectedSubmarine().getCurrentAttacksPerTurn() > 0);
    buttonSonar.setEnabled(
        selectedSubmarine().getCurrentSonarTurns() == selectedSubmarine().getTotalSonarTurns());
  }

}
