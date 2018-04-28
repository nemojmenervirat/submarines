package com.nemojmenervirat.submarines.ui;

import javax.servlet.annotation.WebServlet;

import com.nemojmenervirat.submarines.logic.AccessControl;
import com.nemojmenervirat.submarines.logic.Game;
import com.nemojmenervirat.submarines.logic.User;
import com.nemojmenervirat.submarines.logic.UserBroadcaster;
import com.nemojmenervirat.submarines.logic.UserCoordinator;
import com.nemojmenervirat.submarines.logic.utils.DefaultLogger;
import com.nemojmenervirat.submarines.ui.language.DefaultLocale;
import com.nemojmenervirat.submarines.ui.utils.Views;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("submarinestheme")
@Push
public class SubmarinesUI extends UI {

	private AccessControl accessControl = new AccessControl();
	private User currentUser;
	private Game currentGame;

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		Responsive.makeResponsive(this);
		setLocale(DefaultLocale.Get());
		DefaultLocale.select(DefaultLocale.Get());

		getPage().setTitle("Prijava");
		DefaultLogger.log("UI INIT CURRENT = " + UserCoordinator.getCurrent(this));

		addStyleName(ValoTheme.UI_WITH_MENU);
		LoginScreen loginScreen = new LoginScreen(accessControl, () -> showMainView());
		mainScreen = new MainScreen(this);
		GameScreen gameScreen = new GameScreen(this);

		Navigator navigator = new Navigator(this, this);
		navigator.addView(Views.LoginScreen, loginScreen);
		navigator.addView(Views.MainScreen, mainScreen);
		navigator.addView(Views.GameScreen, gameScreen);

		if (accessControl.isUserSignedIn()) {
			showMainView();
		} else {
			showLoginView();
		}
	}

	@Override
	public void detach() {
		UserBroadcaster.unregister(mainScreen);
		if (accessControl.isUserSignedIn()) {
			accessControl.signOut();
		}
		super.detach();
	}

	private MainScreen mainScreen;

	private void showMainView() {
		getNavigator().navigateTo(Views.MainScreen);
		UserBroadcaster.register(mainScreen);
		setPollInterval(1000);
	}

	private void showLoginView() {
		getNavigator().navigateTo(Views.LoginScreen);
	}

	public AccessControl getAccessControl() {
		return accessControl;
	}

	@WebServlet(urlPatterns = "/*", name = "SubmarinesUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = SubmarinesUI.class, productionMode = false)
	public static class SubmarinesUIServlet extends VaadinServlet {
	}
}
