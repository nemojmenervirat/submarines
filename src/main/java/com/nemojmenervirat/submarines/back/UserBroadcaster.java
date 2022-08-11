package com.nemojmenervirat.submarines.back;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.nemojmenervirat.submarines.back.Game.GameType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UserBroadcaster {

  static ExecutorService executorService = Executors.newSingleThreadExecutor();
  static final List<UserBroadcastListener> listeners = new LinkedList<>();

  public synchronized static void register(UserBroadcastListener listener) {
    listeners.add(listener);
    log.debug("Register user broadcast at " + listener.getUser());
  }

  public synchronized static void unregister(UserBroadcastListener listener) {
    listeners.remove(listener);
    log.debug("Unregister user broadcast at " + listener.getUser());
  }

  public synchronized static void broadcastUpdate(final User user) {
    listeners.stream().filter(e -> !e.getUser().equals(user))
        .forEach((listener) -> executorService.execute(() -> listener.receiveActiveUsersChange()));

    log.debug("Add broadcast for " + user + " for " + listeners.size() + " listeners.");
  }

  public synchronized static void broadcastChallenge(final User challenger, final User challenged,
      final GameType gameType) {
    listeners.stream().filter(listener -> listener.getUser().equals(challenged))
        .forEach((listener) -> executorService
            .execute(() -> listener.receiveChallengeBroadcast(challenger, challenged, gameType)));

    log.debug("Challenge broadcast from " + challenger + " to " + challenged + " for "
        + listeners.size() + " listeners.");
  }

  public synchronized static void broadcastCancelChallenge(final User challenger,
      final User challenged) {
    listeners.stream().filter((listener) -> listener.getUser().equals(challenged))
        .forEach((listener) -> executorService
            .execute(() -> listener.receiveCancelChallengeBroadcast(challenger, challenged)));

    log.debug("Cancel challenge broadcast from " + challenger + " to " + challenged + " for "
        + listeners.size() + " listeners.");

  }

  public synchronized static void broadcastDeclineChallenge(final User challenger,
      final User challenged) {
    listeners.stream().filter((listener) -> listener.getUser().equals(challenger))
        .forEach((listener) -> executorService
            .execute(() -> listener.receiveDeclineChallengeBroadcast(challenger, challenged)));

    log.debug("Decline challenge broadcast from " + challenged + " to " + challenger + " for "
        + listeners.size() + " listeners.");
  }

  public synchronized static void broadcastAcceptChallenge(final User challenger,
      final User challenged, GameType gameType) {
    listeners.stream().filter((listener) -> listener.getUser().equals(challenger))
        .forEach((listener) -> executorService.execute(
            () -> listener.receiveAcceptChallengeBroadcast(challenger, challenged, gameType)));

    log.debug("Accept challenge broadcast from " + challenger + " to " + challenged + " for "
        + listeners.size() + " listeners.");
  }

  public interface UserBroadcastListener {
    public User getUser();

    public void receiveActiveUsersChange();

    public void receiveChallengeBroadcast(User challenger, User challenged, GameType gameType);

    public void receiveCancelChallengeBroadcast(User challenger, User challenged);

    public void receiveDeclineChallengeBroadcast(User challenger, User challenged);

    public void receiveAcceptChallengeBroadcast(User challenger, User challenged,
        GameType gameType);
  }
}
