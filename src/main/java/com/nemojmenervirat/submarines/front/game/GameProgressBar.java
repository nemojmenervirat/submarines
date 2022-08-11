package com.nemojmenervirat.submarines.front.game;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;

public class GameProgressBar extends Div {

  private boolean showPercentage;

  private Span spanBefore = new Span();
  private ProgressBar progressBar = new ProgressBar();
  private Span spanAfter = new Span();

  public GameProgressBar(String label, ProgressBarVariant... variant) {
    this(label, false, variant);
  }

  public GameProgressBar(String label, boolean showPercentage, ProgressBarVariant... variant) {
    this.showPercentage = showPercentage;
    spanBefore.setText(label);
    spanBefore.getStyle().set("white-space", "nowrap");
    progressBar.getStyle().set("height", "16px").set("margin-top", "auto")
        .set("margin-bottom", "auto").set("margin-left", "0.5em");
    progressBar.addThemeVariants(variant);
    progressBar.setMinWidth("50px");
    spanAfter.getStyle().set("margin-left", "0.5em");
    setValue(0, 0);
    add(spanBefore, progressBar, spanAfter);
    getStyle().set("display", "flex").set("flex-direction", "row").set("align-items", "center");
  }

  public void setValue(int sofar, int total) {
    if (total != 0) {
      progressBar.setMax(total);
    }
    progressBar.setValue(sofar);
    if (showPercentage) {
      if (total != 0) {
        spanAfter.setText(String.format("%.0f%%", (float) sofar / total * 100));
      } else {
        spanAfter.setText("0%");
      }
    } else {
      spanAfter.setText(String.format("%d/%d", sofar, total));
    }
  }

}
