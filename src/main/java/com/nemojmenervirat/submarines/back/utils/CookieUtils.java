package com.nemojmenervirat.submarines.back.utils;

import javax.servlet.http.Cookie;
import com.vaadin.flow.server.VaadinService;

public class CookieUtils {

  public enum PersistanceType {
    Session, Permanent
  }

  public static Cookie getByName(String name) {
    try {
      Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
      for (Cookie cookie : cookies) {
        if (name.equals(cookie.getName())) {
          System.out.println("COOKIE FOUND FOR NAME " + name);
          return cookie;
        }
      }
      System.out.println("COOKIE FOR NAME " + name + " NOT FOUND");
      return null;
    } catch (Exception ex) {
      return null;
    }
  }

  public static void save(String name, String value) {
    save(name, value, PersistanceType.Permanent);
  }

  public static void save(String name, String value, PersistanceType type) {
    try {
      Cookie cookie = getByName(name);
      if (cookie == null) {
        cookie = new Cookie(name, value);
      } else {
        cookie.setValue(value);
      }
      switch (type) {
        case Permanent:
          cookie.setMaxAge(60 * 60 * 24 * 365 * 10); // 10 years
        case Session:
          cookie.setMaxAge(-1); // until browser closes
      }
      cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
      VaadinService.getCurrentResponse().addCookie(cookie);
      System.out.println("COOKIE WITH NAME " + name + " SAVED WITH VALUE " + value);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void delete(String name) {
    Cookie cookie = getByName(name);
    if (cookie != null) {
      cookie.setMaxAge(0); // A zero value causes the cookie to be
                           // deleted.
      cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
      VaadinService.getCurrentResponse().addCookie(cookie);
      System.out.println("COOKIE WITH NAME " + name + " DELETED");
    }
  }

}
