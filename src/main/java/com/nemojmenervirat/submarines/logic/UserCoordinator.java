package com.nemojmenervirat.submarines.logic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nemojmenervirat.submarines.logic.utils.DefaultLogger;
import com.nemojmenervirat.submarines.ui.SubmarinesUI;
import com.vaadin.ui.UI;

public final class UserCoordinator {

	public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = UserCoordinator.class.getCanonicalName();

	private static List<User> activeUsers = new LinkedList<>();

	private UserCoordinator() {
	}

	public synchronized static void register(User user) {
		if (!activeUsers.contains(user)) {
			activeUsers.add(user);
			UserBroadcaster.broadcastAdd(user);
			DefaultLogger.log("User added " + user);
		}
	}

	public synchronized static void unregister(User user) {
		activeUsers.remove(user);
		UserBroadcaster.broadcastRemove(user);
		DefaultLogger.log("User removed " + user);
	}

	public static Collection<User> getActiveUsers() {
		return activeUsers;
	}

	public static User getCurrent(UI ui) {
		return ((SubmarinesUI) ui).getCurrentUser();
	}

	public static void setCurrent(User currentUser) {
		((SubmarinesUI) UI.getCurrent()).setCurrentUser(currentUser);
	}
}
