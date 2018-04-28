package com.nemojmenervirat.submarines.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nemojmenervirat.submarines.logic.Game.GameType;
import com.nemojmenervirat.submarines.logic.utils.DefaultLogger;
import com.vaadin.ui.UI;

public class UserBroadcaster {

	static ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static final List<UserBroadcastListener> listeners = new LinkedList<>();

	public synchronized static void register(UserBroadcastListener listener) {
		listeners.add(listener);
		DefaultLogger.log("Register user broadcast at " + UserCoordinator.getCurrent(UI.getCurrent()));
	}

	public synchronized static void unregister(UserBroadcastListener listener) {
		listeners.remove(listener);
		DefaultLogger.log("Unregister user broadcast at " + UserCoordinator.getCurrent(UI.getCurrent()));
	}

	public synchronized static void broadcastAdd(final User user) {
		listeners.forEach((listener) -> executorService.execute(() -> listener.receiveAddBroadcast(user)));

		DefaultLogger.log("Add broadcast for " + user + " for " + listeners.size() + " listeners.");
	}

	public synchronized static void broadcastRemove(final User user) {
		listeners.forEach((listener) -> executorService.execute(() -> listener.receiveRemoveBroadcast(user)));

		DefaultLogger.log("Remove broadcast for " + user + " for " + listeners.size() + " listeners.");
	}

	public synchronized static void broadcastChallenge(final User challenger, final User challenged, final GameType gameType) {
		listeners.forEach((listener) -> executorService
				.execute(() -> listener.receiveChallengeBroadcast(challenger, challenged, gameType)));

		DefaultLogger.log("Challenge broadcast from " + challenger + " to " + challenged + " at "
				+ UserCoordinator.getCurrent(UI.getCurrent()) + " for " + listeners.size() + " listeners.");
	}

	public synchronized static void broadcastCancelChallenge(final User challenger, final User challenged,
			final User canceler) {
		listeners.stream().filter(
				(listener) -> listener.getUser().equals(challenged) || listener.getUser().equals(challenger))
				.forEach((listener) -> executorService.execute(
						() -> listener.receiveCancelChallengeBroadcast(challenger, challenged, canceler)));

		DefaultLogger.log("Cancel challenge broadcast from " + challenger + " to " + challenged + " at "
				+ UserCoordinator.getCurrent(UI.getCurrent()) + " for " + listeners.size() + " listeners.");

	}

	public synchronized static void broadcastAcceptChallenge(final User challenger, final User challenged, GameType gameType) {
		listeners.stream().filter(
				(listener) -> listener.getUser().equals(challenged) || listener.getUser().equals(challenger))
				.forEach((listener) -> executorService
						.execute(() -> listener.receiveAcceptChallengeBroadcast(challenger, challenged, gameType)));

		DefaultLogger.log("Accept challenge broadcast from " + challenger + " to " + challenged + " at "
				+ UserCoordinator.getCurrent(UI.getCurrent()) + " for " + listeners.size() + " listeners.");
	}

	public interface UserBroadcastListener {
		public User getUser();

		public void receiveAddBroadcast(User user);

		public void receiveRemoveBroadcast(User user);

		public void receiveChallengeBroadcast(User challenger, User challenged, GameType gameType);

		public void receiveCancelChallengeBroadcast(User challenger, User challenged, User canceler);

		public void receiveAcceptChallengeBroadcast(User challenger, User challenged, GameType gameType);
	}
}
