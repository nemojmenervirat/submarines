package com.nemojmenervirat.submarines.ui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.Dom;
import org.vaadin.jouni.dom.client.Css;

import com.nemojmenervirat.submarines.logic.Game.Direction;
import com.nemojmenervirat.submarines.ui.utils.Styles;
import com.nemojmenervirat.submarines.ui.utils.Views;
import com.nemojmenervirat.submarines.logic.Submarine;
import com.nemojmenervirat.submarines.logic.User;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class PlayGround extends AbsoluteLayout {

	private Map<Submarine, Image> submarineImages = new HashMap<>();
	private UI ui;
	private Label border;

	public PlayGround(UI ui) {
		this.ui = ui;
		new Dom(this).getStyle().setProperty("background", "#57B3D8");
	}

	public void drawMiddleBorder() {
		if (border != null) {
			removeComponent(border);
		}
		border = new Label();
		border.setWidth(0, Unit.PIXELS);
		border.setHeight(getHeight(), Unit.PIXELS);
		addComponent(border, "top:0px; left:" + getWidth() / 2 + "px; z-index:30");
		new Dom(border).getStyle().setProperty("border", "1px dotted black");
	}

	private JavaScript getJavaScript() {
		return ui.getPage().getJavaScript();
	}

	public synchronized void addSubmarine(ControlLayout controlLayout, Submarine submarine) {
		Image image = new Image("", new ThemeResource(submarine.getImagePath()));
		image.setData(submarine);
		image.setWidth(submarine.getWidth(), Unit.PIXELS);
		image.setHeight(submarine.getHeight(), Unit.PIXELS);
		image.addClickListener((e) -> {
			controlLayout.setSelectedImage(image);
		});
		image.setId("img_" + System.identityHashCode(image));
		submarineImages.put(submarine, image);
		addComponent(image, "top:" + submarine.getStartY() + "px;left:" + submarine.getStartX() + "px;z-index:10");
	}

	public synchronized void animateMove(Submarine submarine) {
		Animator.animate(submarineImages.get(submarine),
				new Css().translateX(submarine.getX() + "px").translateY(submarine.getY() + "px")).duration(100);
	}

	public synchronized void animateHit(Rectangle rectangle, int delay) {
		Image hit = new Image();
		hit.setWidth((float) rectangle.getWidth(), Unit.PIXELS);
		hit.setHeight((float) rectangle.getHeight(), Unit.PIXELS);
		hit.setId("img_" + System.identityHashCode(hit));
		addComponent(hit, "top:" + rectangle.getY() + "px;left:" + rectangle.getX() + "px; z-index:15");
		String script = "function sleep(ms){return new Promise(resolve => setTimeout(resolve, ms));} "
				+ "async function animateHit(ms, id){var element = document.getElementById(id); var old = element.style.border; await sleep(ms); for(i=0; i<3; i++) { element.style.border = '2px solid red'; await sleep(400); element.style.border = old; await sleep(400); } element.parentNode.removeChild(element); } "
				+ "animateHit(%d,'%s');";
		script = String.format(script, delay, hit.getId());
		getJavaScript().execute(script);
	}

	public synchronized void animateHit(Submarine submarine, int delay) {
		Image img = submarineImages.get(submarine);
		String script = "function sleep(ms){return new Promise(resolve => setTimeout(resolve, ms));} "
				+ "async function animateHit(ms, id){var element = document.getElementById(id); var old = element.style.border; await sleep(ms); for(i=0; i<3; i++) { element.style.border = '2px solid red'; await sleep(400); element.style.border = old; await sleep(400); } } "
				+ "animateHit(%d,'%s');";
		script = String.format(script, delay, img.getId());
		getJavaScript().execute(script);
	}

	private void createExplosion(double x, double y, int delay) {
		Image explosion = new Image();
		explosion.setId("img_" + System.identityHashCode(explosion));
		explosion.setWidth("180px");
		explosion.setHeight("180px");
		x = x - explosion.getWidth() / 2;
		y = y - explosion.getHeight() / 2;
		addComponent(explosion, "top:" + y + "px; left:" + x + "px; z-index:20");
		String script = "function sleep(ms){return new Promise(resolve => setTimeout(resolve, ms));} "
				+ "async function removeExplosion(ms,id) {var element = document.getElementById(id); await sleep(ms); element.className += ' explosionSprite'; await sleep(1700); element.parentNode.removeChild(element); } "
				+ "removeExplosion(%d,'%s');";
		script = String.format(script, delay, explosion.getId());
		getJavaScript().execute(script);
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
		String script = "function sleep(ms){return new Promise(resolve => setTimeout(resolve, ms));} "
				+ "async function removeSubmarine(ms,id) {var element = document.getElementById(id); await sleep(ms); element.parentNode.removeChild(element); } "
				+ "removeSubmarine(%d,'%s');";
		script = String.format(script, delay + 2100, submarineImages.get(submarine).getId());
		getJavaScript().execute(script);
	}

	public void animateWinner(User winner) {
		ui.access(() -> {
			Label label = new Label(winner.getUsername() + " wins!");
			new Dom(label).getStyle().setProperty("font-size", "360%");
			Button button = new Button("Back to main screen and play again :)");
			button.addClickListener((e) -> {
				ui.getNavigator().navigateTo(Views.MainScreen);
			});
			addComponent(label, "top:0px; left:0px; right: 0px; z-index: 100");
			addComponent(button, "top:100px; left:0px; right: 0px; bottom: 0px; z-index: 100");
		});
	}

	public void animateProjectile(Submarine attacker, Submarine target) {
		Image projectile = new Image("", new ThemeResource(attacker.getProjectile().getImagePath()));
		projectile.setId("img_" + System.identityHashCode(projectile));
		projectile.setWidth(attacker.getProjectile().getWidth(), Unit.PIXELS);
		projectile.setHeight(attacker.getProjectile().getHeight(), Unit.PIXELS);
		Point startPosition = attacker.getProjectileStartPosition();
		addComponent(projectile, "top:" + startPosition.getY() + "px;left:" + startPosition.getX() + "px;");
		int translation = attacker.getDirection() == Direction.LTR ? attacker.getRange() : -attacker.getRange();
		if (target != null) {
			translation = (int) (target.getStartX() + target.getX()) - (int) startPosition.getX() + (int) (target.getWidth() / 2);
		}
		int duration = Math.abs(translation) * 4;
		Animator.animate(projectile, new Css().translateX(translation + "px")).delay(100).duration(duration);
		String script = "function sleep(ms){return new Promise(resolve => setTimeout(resolve, ms));} "
				+ "async function removeExplosion(ms,id) {var element = document.getElementById(id); await sleep(ms); element.parentNode.removeChild(element); } "
				+ "removeExplosion(%d, '%s');";
		script = String.format(script, duration + 110, projectile.getId());
		getJavaScript().execute(script);

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
			sonar.setId("img_" + System.identityHashCode(sonar));
			sonar.setWidth("1px");
			sonar.setHeight("1px");
			Rectangle submarineRectangle = submarine.getRectangle();
			String className = null;
			switch (submarine.getSonarRange()) {
			case 250:
				className = Styles.SonarEffectSmall;
				break;
			case 500:
				className = Styles.SonarEffectLarge;
				break;
			default:
				throw new RuntimeException("No existing resource to display sonar for range " + submarine.getSonarRange());
			}
			addComponent(sonar, "top:" + submarineRectangle.getCenterY() + "px; left:" + submarineRectangle.getCenterX()
					+ "px; z-index:5");
			String script = "function sleep(ms){return new Promise(resolve => setTimeout(resolve, ms));} "
					+ "async function sonar(ms,id,cn) {var element = document.getElementById(id); await sleep(ms); element.className += ' ' + cn; await sleep(2600); element.parentNode.removeChild(element); } "
					+ "sonar(%d,'%s','%s');";
			int randomNum = ThreadLocalRandom.current().nextInt(180, 300 + 1);
			script = String.format(script, 200 + i * randomNum, sonar.getId(), className);
			getJavaScript().execute(script);
		}
		for (Submarine visibleSubmarine : visibleSubmarines) {
			Rectangle rectangle = visibleSubmarine.getRectangle();
			Image hit = new Image();
			hit.setWidth((float) rectangle.getWidth(), Unit.PIXELS);
			hit.setHeight((float) rectangle.getHeight(), Unit.PIXELS);
			hit.setId("img_" + System.identityHashCode(hit));
			addComponent(hit, "top:" + rectangle.getY() + "px;left:" + rectangle.getX() + "px; z-index:15");
			String script = "function sleep(ms){return new Promise(resolve => setTimeout(resolve, ms));} "
					+ "async function animateHit(ms, id){var element = document.getElementById(id); await sleep(ms); element.style.border = '2px solid red'; await sleep(1000); element.parentNode.removeChild(element); } "
					+ "animateHit(%d,'%s');";
			script = String.format(script, 2500, hit.getId());
			getJavaScript().execute(script);
		}
	}

}
