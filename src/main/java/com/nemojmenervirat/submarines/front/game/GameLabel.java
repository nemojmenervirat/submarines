package com.nemojmenervirat.submarines.front.game;

import com.vaadin.flow.component.html.Label;

public class GameLabel extends Label {

  private final String label;

  public GameLabel(String label) {
    this.label = label;
    setText(label);
  }

  public void setValue(int value) {
    setText(label + " " + value);
  }

  public void clear() {
    setText(label);
  }

}
