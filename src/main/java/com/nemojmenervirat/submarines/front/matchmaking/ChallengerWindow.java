package com.nemojmenervirat.submarines.front.matchmaking;

import com.nemojmenervirat.submarines.back.Game.GameType;
import com.nemojmenervirat.submarines.back.User;
import com.nemojmenervirat.submarines.front.common.DialogBuilder;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ChallengerWindow {

  private Dialog dialog;

  public ChallengerWindow(User challenged, GameType gameType, Runnable onCancel) {

    Image image = new Image("images/loading.svg", "loading");
    image.setWidth(70, Unit.PIXELS);
    image.setHeight(70, Unit.PIXELS);

    Label labelWait = new Label(
        (gameType == GameType.SHORT ? "Short game." : "Long game.") + "Waiting for response...");

    VerticalLayout content = new VerticalLayout(image, labelWait);


    dialog = DialogBuilder.from("Challenging " + challenged.getUsername(), content).modal()
        .withCancel(onCancel).open();
  }

  public boolean isOpened() {
    return dialog.isOpened();
  }

  public void close() {
    dialog.close();
  }
}
