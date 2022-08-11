package com.nemojmenervirat.submarines.front.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class AbsoluteLayout extends Div {

  public AbsoluteLayout() {
    getElement().getStyle().set("position", "relative");
  }

  public void add(Component component, float top, float left) {
    add(component, top, left, null, null, null);
  }

  public void add(Component component, float top, float left, float zIndex) {
    add(component, top, left, null, null, zIndex);
  }

  public void add(Component component, float top, float left, float right, float zIndex) {
    add(component, top, left, right, null, zIndex);
  }

  public void add(Component component, float top, float left, Float right, Float bottom,
      Float zIndex) {
    add(component);
    component.getElement().getStyle().set("position", "absolute");
    component.getElement().getStyle().set("top", top + "px");
    component.getElement().getStyle().set("left", left + "px");
    if (right != null) {
      component.getElement().getStyle().set("right", right + "px");
    }
    if (bottom != null) {
      component.getElement().getStyle().set("bottom", bottom + "px");
    }
    if (zIndex != null) {
      component.getElement().getStyle().set("z-index", zIndex + "px");
    }
  }
}
