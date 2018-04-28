package com.nemojmenervirat.submarines.ui;

import java.util.Collection;
import java.util.stream.Collectors;

import com.nemojmenervirat.submarines.logic.Game.GameType;
import com.nemojmenervirat.submarines.logic.GameCoordinator;
import com.nemojmenervirat.submarines.logic.User;
import com.nemojmenervirat.submarines.logic.UserBroadcaster;
import com.nemojmenervirat.submarines.logic.UserBroadcaster.UserBroadcastListener;
import com.nemojmenervirat.submarines.logic.UserCoordinator;
import com.nemojmenervirat.submarines.logic.utils.DefaultLogger;
import com.nemojmenervirat.submarines.ui.language.DefaultLocale;
import com.nemojmenervirat.submarines.ui.language.Strings;
import com.nemojmenervirat.submarines.ui.utils.Styles;
import com.nemojmenervirat.submarines.ui.utils.Views;
import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

@Push
public class MainScreen extends VerticalLayout implements View, UserBroadcastListener {

	private boolean loaded;
	private ListSelect<User> userListSelect = new ListSelect<>();
	private SubmarinesUI ui;
	private ChallengedWindow cdw;
	private ChallengerWindow crw;

	public MainScreen(SubmarinesUI ui) {
		this.ui = ui;
		setStyleName(Styles.DefaultBackground);
	}

	private Collection<User> getOtherUsers() {
		return UserCoordinator.getActiveUsers().stream().filter((u) -> !u.equals(getUser())).collect(Collectors.toList());
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		if (!loaded) {
			loaded = true;

			Label currentUser = new Label();
			currentUser.setStyleName(Styles.WhiteBackground);
			currentUser.setValue(DefaultLocale.getString(Strings.textCurrentUser) + " " + UserCoordinator.getCurrent(ui).getUsername());

			Button logout = new Button();
			logout.setCaption(DefaultLocale.getString(Strings.captionLogout));
			logout.addClickListener((e) -> {

			});

			setSizeFull();
			userListSelect.setCaption("Other players");
			userListSelect.setHeight(100, Unit.PERCENTAGE);
			userListSelect.setWidth(200, Unit.PIXELS);
			userListSelect.setItems(getOtherUsers());
			userListSelect.setStyleName(Styles.FadeIn);

			RadioButtonGroup<GameType> radioButtonGroupGameType = new RadioButtonGroup<>();
			radioButtonGroupGameType.setItems(GameType.SHORT, GameType.LONG);
			radioButtonGroupGameType.setSelectedItem(GameType.SHORT);
			radioButtonGroupGameType.setStyleName(Styles.WhiteBackground);
			radioButtonGroupGameType.setItemCaptionGenerator((item) -> item.toString());

			Button buttonChallenge = new Button();
			buttonChallenge.setCaption(DefaultLocale.getString(Strings.captionChallenge));

			buttonChallenge.addClickListener((e) -> {
				if (userListSelect.getSelectedItems().size() == 1) {
					User challenged = userListSelect.getSelectedItems().iterator().next();
					if (GameCoordinator.isUserInGame(challenged)) {
						Notification.show("User already in game", Notification.Type.HUMANIZED_MESSAGE);
						return;
					}
					User challenger = UserCoordinator.getCurrent(ui);
					if (!challenged.equals(challenger)) {
						UserBroadcaster.broadcastChallenge(challenger, challenged, radioButtonGroupGameType.getValue());
						crw = new ChallengerWindow(challenged, radioButtonGroupGameType.getValue(), (btnEvent) -> {
							UserBroadcaster.broadcastCancelChallenge(challenger, challenged, challenger);
							ui.removeWindow(crw);
						});
						ui.addWindow(crw);
					}
				}
			});

			addComponents(currentUser, userListSelect, radioButtonGroupGameType, buttonChallenge);
			setExpandRatio(userListSelect, 1);
		}
		Page.getCurrent().setTitle(DefaultLocale.getString(Strings.captionMainScreen));
	}

	@Override
	public User getUser() {
		return UserCoordinator.getCurrent(ui);
	}

	@Override
	public void receiveAddBroadcast(User user) {
		DefaultLogger.log("Received add user broadcast for " + user + " at " + UserCoordinator.getCurrent(ui));
		ui.access(new Runnable() {
			@Override
			public void run() {
				userListSelect.setItems(getOtherUsers());
			}
		});
	}

	@Override
	public void receiveRemoveBroadcast(User user) {
		DefaultLogger.log("Received remove user broadcast for " + user + " at " + UserCoordinator.getCurrent(ui));
		ui.access(new Runnable() {
			@Override
			public void run() {
				userListSelect.setItems(getOtherUsers());
			}
		});
	}

	@Override
	public void receiveChallengeBroadcast(User challenger, User challenged, GameType gameType) {
		DefaultLogger.log("Received challenge user broadcast from " + challenger + " to " + challenged + " at "
				+ UserCoordinator.getCurrent(ui));
		if (challenged.equals(UserCoordinator.getCurrent(ui))) {
			DefaultLogger.log("Creating challenge window");
			cdw = new ChallengedWindow(challenger, gameType, (btnEvent) -> {
				UserBroadcaster.broadcastAcceptChallenge(challenger, challenged, gameType);
				ui.removeWindow(cdw);
				ui.setCurrentGame(GameCoordinator.reserveGame(challenger, challenged, gameType));
				ui.getNavigator().navigateTo(Views.GameScreen);
			}, (btnEvent) -> {
				UserBroadcaster.broadcastCancelChallenge(challenger, challenged, challenged);
				ui.removeWindow(cdw);
			});
			ui.addWindow(cdw);
		}
	}

	@Override
	public void receiveCancelChallengeBroadcast(User challenger, User challenged, User canceler) {
		DefaultLogger.log("Received cancel challenge user broadcast from " + canceler + " at " + UserCoordinator.getCurrent(ui));

		if (challenger == canceler && ui.getWindows().contains(cdw)) {
			DefaultLogger.log("Removing CDW at " + UserCoordinator.getCurrent(ui));
			ui.removeWindow(cdw);
		}
		if (challenged == canceler && ui.getWindows().contains(crw)) {
			DefaultLogger.log("Removing CRW at " + UserCoordinator.getCurrent(ui));
			ui.removeWindow(crw);
		}
	}

	@Override
	public void receiveAcceptChallengeBroadcast(User challenger, User challenged, GameType gameType) {
		DefaultLogger.log("Received accept challenge user broadcast from " + challenged + " at " + UserCoordinator.getCurrent(ui));

		if (ui.getWindows().contains(crw)) {
			DefaultLogger.log("Removing CRW at " + UserCoordinator.getCurrent(ui));
			ui.removeWindow(crw);
			ui.setCurrentGame(GameCoordinator.reserveGame(challenger, challenged, gameType));
			ui.getNavigator().navigateTo(Views.GameScreen);
		}
	}
}
