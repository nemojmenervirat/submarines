package com.nemojmenervirat.submarines.front.home;

import com.nemojmenervirat.submarines.back.Game.GameType;
import com.nemojmenervirat.submarines.back.User;
import com.nemojmenervirat.submarines.front.common.DialogBuilder;
import com.vaadin.flow.component.dialog.Dialog;

public class ChallengedWindow {

  private Dialog dialog;

  public ChallengedWindow(User challenger, GameType gameType, Runnable onAccept,
      Runnable onCancel) {

    String content =
        (gameType == GameType.SHORT ? "Short game." : "Long game.") + "Do you accept challenge?";

    dialog =
        DialogBuilder.from("Challenged by " + challenger.getUsername(), content).withOk(onAccept)
            .okText("Accept").withCancel(onCancel).cancelText("Decline").modal().open();
  }

  public boolean isOpened() {
    return dialog.isOpened();
  }

  public void close() {
    dialog.close();
  }

}
