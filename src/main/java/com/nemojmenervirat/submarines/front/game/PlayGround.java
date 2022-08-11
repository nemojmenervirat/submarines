package com.nemojmenervirat.submarines.front.game;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nemojmenervirat.submarines.back.Game;
import com.nemojmenervirat.submarines.back.Game.Direction;
import com.nemojmenervirat.submarines.back.GameCoordinator;
import com.nemojmenervirat.submarines.back.Submarine;
import com.nemojmenervirat.submarines.back.User;
import com.nemojmenervirat.submarines.front.common.AbsoluteLayout;
import com.nemojmenervirat.submarines.front.home.MatchmakingView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.dom.Element;

@JavaScript("/js/play-ground.js")
@CssImport("/css/play-ground.css")
public class PlayGround extends AbsoluteLayout {

  private Map<Submarine, SubmarineImage> submarineImages = new HashMap<>();
  private final UI ui;
  private final Game game;
  private Label border;
  private int height;
  private int width;
  private User currentUser;

  public PlayGround(UI ui, Game game, User currentUser) {
    this.ui = ui;
    this.game = game;
    this.currentUser = currentUser;
    getStyle().set("border", "2px dashed gray").set("margin", "auto");
    setId("playGround");
  }

  public void setWidth(int width) {
    this.width = width;
    setWidth(width + "px");
    setMaxWidth(width + "px");
  }

  public void setHeight(int height) {
    this.height = height;
    setHeight(height + "px");
    setMaxHeight(height + "px");
  }

  public void drawMiddleBorder() {
    if (border != null) {
      remove(border);
    }
    border = new Label();
    border.setWidth(0, Unit.PIXELS);
    border.setHeight(height, Unit.PIXELS);
    add(border, 0, width / 2, 30);
    border.getStyle().set("border", "1px dotted gray");
  }



  public synchronized void addSubmarine(ControlLayout controlLayout, Submarine submarine) {
    SubmarineImage image = new SubmarineImage(submarine);
    image.addClickListener((e) -> {
      System.out.println("Image selected " + image);
      controlLayout.setSelectedImage(image);
    });
    submarineImages.put(submarine, image);
    add(image, submarine.getStartY(), submarine.getStartX(), 10);
  }

  public synchronized void animateMove(Submarine submarine) {
    submarineImages.get(submarine).getStyle().set("transform",
        "translate(" + submarine.getX() + "px," + submarine.getY() + "px)");
  }

  public synchronized void animateHit(Rectangle rectangle, int delay) {
    Div hit = new Div();
    hit.setWidth((float) rectangle.getWidth(), Unit.PIXELS);
    hit.setHeight((float) rectangle.getHeight(), Unit.PIXELS);
    add(hit, (float) rectangle.getY(), (float) rectangle.getX(), 15);
    js_animateProjectileHit(hit.getElement(), delay);
  }

  public synchronized void animateHit(Submarine submarine, int delay) {
    Image img = submarineImages.get(submarine);
    js_animateSubmarineHit(img.getElement(), delay);
  }

  private void createExplosion(double x, double y, int delay) {
    Image explosion = new Image();
    explosion.setWidth("180px");
    explosion.setHeight("180px");
    x = x - 180 / 2;
    y = y - 180 / 2;
    add(explosion, (float) y, (float) x, 20);
    js_animateExplosion(explosion.getElement(), delay);
  }

  public synchronized void animateDeath(Rectangle rectangle, int delay) {
    animateHit(rectangle, delay);
    double x = rectangle.getX() + rectangle.getWidth() / 2;
    double y = rectangle.getY() + rectangle.getHeight() / 2;
    createExplosion(x, y, delay + 800);
  }

  public synchronized void animateDeath(Submarine submarine, int delay) {
    animateHit(submarine, delay);
    float x = submarine.getStartX() + submarine.getX() + submarine.getWidth() / 2;
    float y = submarine.getStartY() + submarine.getY() + submarine.getHeight() / 2;
    createExplosion(x, y, delay + 800);
    js_destroyAfter(submarineImages.get(submarine).getElement(), delay + 2100);
  }

  public void animateWinner(User winner) {
    ui.access(() -> {
      Label label = new Label(winner.getUsername() + " wins!");
      label.getStyle().set("font-size", "360%");
      Button button = new Button("Back to main screen and play again :)");
      button.addClickListener((e) -> {
        ui.navigate(MatchmakingView.class);
        game.leave(currentUser);
        GameCoordinator.gameFinished(game);
      });
      add(label, 0, 0, 0, 100);
      add(button, 100, 0, 0f, 0f, 100f);
    });
  }

  public void animateProjectile(Submarine attacker, Submarine target) {
    ProjectileImage projectile = new ProjectileImage(attacker.getProjectile());
    Point startPosition = attacker.getProjectileStartPosition();
    add(projectile, (float) startPosition.getY(), (float) startPosition.getX());
    int translation =
        attacker.getDirection() == Direction.LTR ? attacker.getRange() : -attacker.getRange();
    if (target != null) {
      translation = (int) (target.getStartX() + target.getX()) - (int) startPosition.getX()
          + (int) (target.getWidth() / 2);
    }
    int duration = Math.abs(translation) * 4;

    js_translate(projectile.getElement(), translation, 0, duration);
    js_destroyAfter(projectile.getElement(), duration + 110);

    if (target != null) {
      if (target.getCurrentHp() <= 0) {
        animateDeath(target.getRectangle(), duration + 100);
      } else {
        animateHit(target.getRectangle(), duration + 100);
      }
    }
  }

  public void animateSonar(Submarine submarine, List<Submarine> visibleSubmarines) {
    for (int i = 0; i < 5; i++) {
      Image sonar = new Image();
      sonar.setWidth("1px");
      sonar.setHeight("1px");
      sonar.getStyle().set("border-radius", "50%");
      Rectangle submarineRectangle = submarine.getRectangle();
      String className = null;
      switch (submarine.getSonarRange()) {
        case 250:
          className = "sonarEffect250";
          break;
        case 500:
          className = "sonarEffect500";
          break;
        default:
          throw new RuntimeException(
              "No existing resource to display sonar for range " + submarine.getSonarRange());
      }
      add(sonar, (float) submarineRectangle.getCenterY(), (float) submarineRectangle.getCenterX(),
          5);
      js_sonarEffect(sonar.getElement(), i * 200, className);
    }
    for (Submarine visibleSubmarine : visibleSubmarines) {
      Rectangle rectangle = visibleSubmarine.getRectangle();
      Div hit = new Div();
      hit.setWidth((float) rectangle.getWidth(), Unit.PIXELS);
      hit.setHeight((float) rectangle.getHeight(), Unit.PIXELS);
      add(hit, (float) rectangle.getY(), (float) rectangle.getX(), 15);
      js_animateSonarHit(hit.getElement());
    }
  }

  private void executeJs(String expression, Serializable... parameters) {
    ui.getPage().executeJs(expression, parameters);
  }

  private void js_translate(Element element, int x, int y, int duration) {
    executeJs("translate($0, $1, $2, $3);", element, x, y, duration);
  }

  private void js_destroyAfter(Element element, int wait) {
    executeJs("destroyAfter($0, $1);", element, wait);
  }

  private void js_sonarEffect(Element element, int wait, String className) {
    executeJs("sonarEffect($0, $1, $2);", element, wait, className);
  }

  private void js_animateSonarHit(Element element) {
    executeJs("animateSonarHit($0);", element);
  }

  private void js_animateProjectileHit(Element element, int wait) {
    executeJs("animateProjectileHit($0, $1);", element, wait);
  }

  private void js_animateSubmarineHit(Element element, int wait) {
    executeJs("animateSubmarineHit($0, $1);", element, wait);
  }

  private void js_animateExplosion(Element element, int wait) {
    executeJs("animateExplosion($0, $1);", element, wait);
  }

}
