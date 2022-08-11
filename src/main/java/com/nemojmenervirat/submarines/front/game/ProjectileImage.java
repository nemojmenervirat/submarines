package com.nemojmenervirat.submarines.front.game;

import com.nemojmenervirat.submarines.back.Projectile;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Image;

public class ProjectileImage extends Image {

  public ProjectileImage(Projectile projectile) {
    super(projectile.getImagePath(), "projectile");
    setWidth(projectile.getWidth(), Unit.PIXELS);
    setHeight(projectile.getHeight(), Unit.PIXELS);
    getStyle().set("transition-property", "transform");
  }

}
