package com.nemojmenervirat.submarines;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop
 * browsers.
 *
 */
@SpringBootApplication
@Theme(value = "submarines", variant = Lumo.DARK)
@PWA(name = "Submarines", shortName = "Submarines", offlineResources = {})
@Push
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
