package com.nemojmenervirat.submarines.logic;

import com.vaadin.server.Page;
import com.vaadin.ui.UI;

public class AccessControl {

	public enum SignInStatus {
		OK, INVALID_USERNAME, ALREADY_LOGGED_IN
	}

	public SignInStatus signIn(String username, String password) {
		if (username.isEmpty()) {
			return SignInStatus.INVALID_USERNAME;
		}
		if (UserCoordinator.getActiveUsers().stream().anyMatch((u) -> u.getUsername().equals(username))) {
			return SignInStatus.ALREADY_LOGGED_IN;
		}
		return SignInStatus.OK;
	}

	public boolean isUserSignedIn() {
		return UserCoordinator.getCurrent(UI.getCurrent()) != null;
	}

	public void signOut() {
		UserCoordinator.unregister(UserCoordinator.getCurrent(UI.getCurrent()));
		UserCoordinator.setCurrent(null);
		Page.getCurrent().reload();
	}

}
