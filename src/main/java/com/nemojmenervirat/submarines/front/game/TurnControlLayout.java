package com.nemojmenervirat.submarines.front.game;

import com.nemojmenervirat.submarines.back.Game;
import com.nemojmenervirat.submarines.back.Submarine;
import com.nemojmenervirat.submarines.back.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TurnControlLayout extends Div {
  private UI ui;
  private Game game;
  private User currentUser;

  private Label labelTurn;
  private Label labelUser;

  private static final String CUR_TUR = "Current turn: ";
  private static final String CUR_USER = "Current turn user: ";

  public TurnControlLayout(ControlLayout controlLayout, PlayGround playGround, UI ui, Game game,
      User currentUser) {
    this.ui = ui;
    this.game = game;
    this.currentUser = currentUser;
    labelTurn = new Label(CUR_TUR);
    labelUser = new Label(CUR_USER);

    Button turnFinish = new Button("End turn");
    turnFinish.addClickListener((e) -> {
      game.goNextMove();
      updateCurrentTurn();
      controlLayout.reselect();
    });

    Button surrender = new Button("Surrender");
    surrender.addClickListener((e) -> {
      int i = 1;
      for (Submarine submarine : game.getSubmarines(currentUser)) {
        playGround.animateDeath(submarine, 500 + 100 * i);
      }
      game.getSubmarines(currentUser).clear();
      game.checkStatus();
    });

    VerticalLayout layout = new VerticalLayout(labelTurn, labelUser, turnFinish, surrender);
    add(layout);
    setWidthFull();

    setEnabled(false);
  }

  public void updateCurrentTurn() {
    labelTurn.setText(CUR_TUR + game.getCurrentTurn());
    labelUser.setText(CUR_USER + game.getCurrentTurnUser().getUsername());
    setEnabled(game.getCurrentTurnUser().equals(currentUser));
  }
}
