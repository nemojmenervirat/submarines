package com.nemojmenervirat.submarines.back;

import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class UserCoordinator {

  private final List<User> activeUsers = new LinkedList<>();

  public synchronized void register(User user) {
    if (!activeUsers.contains(user)) {
      activeUsers.add(user);
      UserBroadcaster.broadcastUpdate(user);
      log.debug("User added " + user);
    }
  }

  public synchronized void unregister(User user) {
    activeUsers.remove(user);
    UserBroadcaster.broadcastUpdate(user);
    log.debug("User removed " + user);
  }

  public List<User> getActiveUsers() {
    return activeUsers;
  }

  public boolean validateUsername(User user, String username) {
    return activeUsers.stream().filter(e -> !e.equals(user))
        .noneMatch(e -> e.getUsername().equalsIgnoreCase(username));
  }

}
