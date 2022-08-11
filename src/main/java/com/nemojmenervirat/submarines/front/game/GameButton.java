package com.nemojmenervirat.submarines.front.game;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GameButton extends Button {

  public GameButton(VaadinIcon icon, Key key) {
    setIcon(icon.create());
    addClickShortcut(key);
    setWidth("40px");
    setHeight("40px");
  }

}
