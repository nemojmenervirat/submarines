package com.nemojmenervirat.submarines.front.game;

import com.nemojmenervirat.submarines.back.Submarine;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmarineImage extends Image {

  private Submarine submarine;

  public SubmarineImage(Submarine submarine) {
    super(submarine.getImagePath(), "submarine");
    this.submarine = submarine;

    setWidth(submarine.getWidth(), Unit.PIXELS);
    setHeight(submarine.getHeight(), Unit.PIXELS);
    getStyle().set("transition-property", "transform").set("transition-duration", "100ms");
  }

}
