package com.nemojmenervirat.submarines.front.matchmaking;

import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import com.nemojmenervirat.submarines.back.CurrentUser;
import com.nemojmenervirat.submarines.back.Game;
import com.nemojmenervirat.submarines.back.Game.GameType;
import com.nemojmenervirat.submarines.back.GameCoordinator;
import com.nemojmenervirat.submarines.back.User;
import com.nemojmenervirat.submarines.back.UserBroadcaster;
import com.nemojmenervirat.submarines.back.UserBroadcaster.UserBroadcastListener;
import com.nemojmenervirat.submarines.back.UserCoordinator;
import com.nemojmenervirat.submarines.front.game.GameView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@PageTitle("Matchmaking | Submarines")
@Route("matchmaking")
@RouteAlias("")
@RequiredArgsConstructor
@Log4j2
@UIScope
public class MatchmakingView extends HorizontalLayout
    implements UserBroadcastListener, AfterNavigationObserver, BeforeLeaveObserver {

  private final UserCoordinator userCoordinator;
  private final CurrentUser currentUser;
  private final Binder<CurrentUser> binderCurrentUser = new Binder<>();

  private RadioButtonGroup<User> radioButtonGroupUsers = new RadioButtonGroup<>();
  private UI ui;
  private ChallengedWindow cdw;
  private ChallengerWindow crw;

  private Collection<User> getOtherUsers() {
    return userCoordinator.getActiveUsers().stream().filter((u) -> !u.equals(getUser()))
        .collect(Collectors.toList());
  }

  @PostConstruct
  void init() {

    getStyle().set("background-image", "url('images/background.jpg')")
        .set("background-size", "cover").set("background-position", "center")
        .set("background-repeat", "no-repeat");

    TextField textFieldUsername = new TextField("My name");
    textFieldUsername.setValue(currentUser.getUsername());
    textFieldUsername.setValueChangeMode(ValueChangeMode.LAZY);
    textFieldUsername.setWidthFull();
    binderCurrentUser.forField(textFieldUsername).asRequired()
        .withValidator(e -> userCoordinator.validateUsername(currentUser, e),
            "Username already taken")
        .bind(User::getUsername, User::setUsername);
    binderCurrentUser.addValueChangeListener(e -> UserBroadcaster.broadcastUpdate(currentUser));

    setSizeFull();
    radioButtonGroupUsers.setLabel("Other players (" + getOtherUsers().size() + ")");
    radioButtonGroupUsers.setItemLabelGenerator(User::getUsername);
    radioButtonGroupUsers.setWidthFull();
    radioButtonGroupUsers.setItems(getOtherUsers());

    RadioButtonGroup<GameType> radioButtonGroupGameType = new RadioButtonGroup<>();
    radioButtonGroupGameType.setItems(GameType.SHORT, GameType.LONG);
    radioButtonGroupGameType.setValue(GameType.SHORT);
    radioButtonGroupGameType.setWidthFull();

    Button buttonChallenge = new Button();
    buttonChallenge.setEnabled(false);
    buttonChallenge.setText("Challenge player");
    buttonChallenge.setWidthFull();

    radioButtonGroupUsers
        .addValueChangeListener(e -> buttonChallenge.setEnabled(e.getValue() != null));

    buttonChallenge.addClickListener((e) -> {
      if (radioButtonGroupUsers.getValue() != null) {
        User challenged = radioButtonGroupUsers.getValue();
        if (GameCoordinator.isUserInGame(challenged)) {
          Notification.show("User already in game");
          return;
        }
        User challenger = currentUser;
        if (!challenged.equals(challenger)) {
          UserBroadcaster.broadcastChallenge(challenger, challenged,
              radioButtonGroupGameType.getValue());
          crw = new ChallengerWindow(challenged, radioButtonGroupGameType.getValue(), () -> {
            UserBroadcaster.broadcastCancelChallenge(challenger, challenged);
          });
        }
      }
    });

    VerticalLayout verticalLayout = new VerticalLayout(textFieldUsername, radioButtonGroupUsers,
        radioButtonGroupGameType, buttonChallenge);
    verticalLayout.setWidth("300px");
    verticalLayout.setFlexGrow(1, radioButtonGroupUsers);
    add(verticalLayout);
    verticalLayout.getStyle().set("background-color", "var(--lumo-base-color)");

    UserBroadcaster.register(this);
    binderCurrentUser.setBean(currentUser);
  }


  @Override
  public User getUser() {
    return currentUser;
  }

  @Override
  public void receiveActiveUsersChange() {
    log.debug("Received active users change for " + currentUser);
    ui.access(() -> {
      radioButtonGroupUsers.setItems(getOtherUsers());
      radioButtonGroupUsers.setLabel("Other players (" + getOtherUsers().size() + ")");
    });
  }


  @Override
  public void receiveChallengeBroadcast(User challenger, User challenged, GameType gameType) {
    ui.access(() -> {
      log.debug("Received challenge user broadcast from " + challenger + " to " + challenged
          + " at " + currentUser);
      cdw = new ChallengedWindow(challenger, gameType, () -> {
        UserBroadcaster.broadcastAcceptChallenge(challenger, challenged, gameType);
        Game game = GameCoordinator.reserveGame(challenger, challenged, gameType);
        ui.navigate(GameView.class, game.getId().toString());
      }, () -> {
        UserBroadcaster.broadcastDeclineChallenge(challenger, challenged);
      });
    });
  }

  @Override
  public void receiveCancelChallengeBroadcast(User challenger, User challenged) {
    ui.access(() -> {
      log.debug(
          "Received cancel challenge user broadcast from " + challenger + " at " + currentUser);
      cdw.close();
    });
  }

  @Override
  public void receiveDeclineChallengeBroadcast(User challenger, User challenged) {
    ui.access(() -> {
      log.debug(
          "Received cancel challenge user broadcast from " + challenged + " at " + currentUser);
      crw.close();
    });
  }

  @Override
  public void receiveAcceptChallengeBroadcast(User challenger, User challenged, GameType gameType) {
    ui.access(() -> {
      log.debug(
          "Received accept challenge user broadcast from " + challenged + " at " + currentUser);
      crw.close();
      Game game = GameCoordinator.reserveGame(challenger, challenged, gameType);
      ui.navigate(GameView.class, game.getId().toString());
    });
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    ui = UI.getCurrent();
    userCoordinator.register(currentUser);
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    userCoordinator.unregister(currentUser);
  }
}
