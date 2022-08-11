package com.nemojmenervirat.submarines.front.game;

import java.util.List;
import java.util.UUID;
import com.nemojmenervirat.submarines.back.CurrentUser;
import com.nemojmenervirat.submarines.back.Game;
import com.nemojmenervirat.submarines.back.Game.Direction;
import com.nemojmenervirat.submarines.back.Game.GameBroadcastListener;
import com.nemojmenervirat.submarines.back.Game.GameType;
import com.nemojmenervirat.submarines.back.GameCoordinator;
import com.nemojmenervirat.submarines.back.Submarine;
import com.nemojmenervirat.submarines.back.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@PageTitle("Battleground")
@Route("game")
@RequiredArgsConstructor
@Log4j2
public class GameView extends HorizontalLayout
    implements GameBroadcastListener, HasUrlParameter<String> {

  private final CurrentUser currentUser;

  private Game currentGame;
  private PlayGround playGround;
  private ControlLayout controlLayout;
  private TurnControlLayout turnControlLayout;
  private Div playGroundPanel;

  void init(UI ui, Game game) {
    setSizeFull();
    setSpacing(false);
    setMargin(false);
    // setAlignItems(Alignment.CENTER);

    getStyle().set("background-image", "url('images/background1.jpg')")
        .set("background-size", "cover").set("background-position", "center")
        .set("background-repeat", "no-repeat");

    playGround = new PlayGround(ui, game, currentUser);
    playGroundPanel = new Div(playGround);

    controlLayout = new ControlLayout(playGround, game, currentUser);
    turnControlLayout = new TurnControlLayout(controlLayout, playGround, game, currentUser);


    VerticalLayout sideLayout = new VerticalLayout(controlLayout, turnControlLayout);
    sideLayout.getStyle().set("background-color", "var(--lumo-base-color)");
    sideLayout.setMargin(false);
    sideLayout.setSpacing(false);
    sideLayout.setPadding(false);
    sideLayout.setWidth(300, Unit.PIXELS);
    sideLayout.setFlexGrow(0, turnControlLayout);
    sideLayout.setFlexGrow(1, controlLayout);
    Scroller scroller = new Scroller(sideLayout);
    if (game.getDirection(currentUser) == Direction.LTR) {
      add(scroller, playGroundPanel);
    } else {
      add(playGroundPanel, scroller);
    }
    setAlignSelf(Alignment.CENTER, playGroundPanel);
    playGround.setWidth(game.getPlaygroundWidth());
    playGround.setHeight(game.getPlaygroundHeight());
    playGround.drawMiddleBorder();

    playGroundPanel.setWidth(game.getPlaygroundWidth() + 2, Unit.PIXELS);

    playGroundPanel.setHeight(game.getPlaygroundHeight() + 2, Unit.PIXELS);

    setFlexGrow(0, sideLayout);
    setFlexGrow(1, playGroundPanel);

    game.join(currentUser, this);
    List<Submarine> submarines = game.getSubmarines(currentUser);

    for (Submarine submarine : submarines) {
      playGround.addSubmarine(controlLayout, submarine);
    }
    turnControlLayout.updateCurrentTurn();
  }

  @Override
  public void receiveTurnEndBroadcast(User attacker) {
    turnControlLayout.updateCurrentTurn();
    controlLayout.reselect();
  }

  @Override
  public void receiveSubmarineHitBroadcast(Submarine attacked) {
    if (attacked.getCurrentHp() > 0) {
      playGround.animateHit(attacked, 500);
    } else {
      playGround.animateDeath(attacked, 500);
    }
    controlLayout.reselect();
  }

  @Override
  public void receiveGameFinishedBroadcast() {
    playGround.animateWinner(currentGame.getWinner());
  }

  @Override
  public void setParameter(BeforeEvent event, String parameter) {
    UUID gameId = UUID.fromString(parameter);
    Game game = GameCoordinator.findGame(gameId);
    if (game == null) {
      game = GameCoordinator.reserveGame(currentUser, new User(), GameType.SHORT);
    }
    if (!game.isUserIn(currentUser)) {
      log.debug("User " + currentUser + " is not in game " + game);
    } else {
      currentGame = game;
      init(event.getUI(), game);
    }
  }
}
