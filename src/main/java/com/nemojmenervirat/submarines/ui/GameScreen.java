package com.nemojmenervirat.submarines.ui;

import java.util.List;

import com.nemojmenervirat.submarines.logic.Game;
import com.nemojmenervirat.submarines.logic.Submarine;
import com.nemojmenervirat.submarines.logic.User;
import com.nemojmenervirat.submarines.logic.UserCoordinator;
import com.nemojmenervirat.submarines.logic.Game.Direction;
import com.nemojmenervirat.submarines.logic.Game.GameBroadcastListener;
import com.nemojmenervirat.submarines.ui.utils.Styles;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class GameScreen extends HorizontalLayout implements View, GameBroadcastListener {

	private SubmarinesUI ui;
	private PlayGround playGround;
	private ControlLayout controlLayout;
	private TurnControlLayout turnControlLayout;
	private Panel playGroundPanel;

	public GameScreen(SubmarinesUI ui) {
		this.ui = ui;
		setStyleName(Styles.DefaultBackground);
		setSizeFull();
		setSpacing(false);
		setMargin(true);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		ui.getPage().setTitle("Battleground");
		Game game = ui.getCurrentGame();

		removeAllComponents();

		playGround = new PlayGround(ui);
		playGroundPanel = new Panel(playGround);

		controlLayout = new ControlLayout(playGround, ui);
		controlLayout.setHeight(100, Unit.PERCENTAGE);
		turnControlLayout = new TurnControlLayout(controlLayout, playGround, ui);
		turnControlLayout.setHeight(140, Unit.PIXELS);

		VerticalLayout sideLayout = new VerticalLayout(controlLayout, turnControlLayout);
		sideLayout.setMargin(false);
		sideLayout.setSpacing(false);
		sideLayout.setWidth(300, Unit.PIXELS);
		sideLayout.setHeight(100, Unit.PERCENTAGE);
		sideLayout.setExpandRatio(turnControlLayout, 0);
		sideLayout.setExpandRatio(controlLayout, 1);
		if (game.getDirection(UserCoordinator.getCurrent(ui)) == Direction.LTR) {
			addComponents(sideLayout, playGroundPanel);
		} else {
			addComponents(playGroundPanel, sideLayout);
		}
		setComponentAlignment(playGroundPanel, Alignment.MIDDLE_CENTER);
		playGround.setWidth(game.getPlaygroundWidth(), Unit.PIXELS);
		playGround.setHeight(game.getPlaygroundHeight(), Unit.PIXELS);
		playGround.drawMiddleBorder();
		if (ui.getPage().getBrowserWindowWidth() > game.getPlaygroundWidth() + sideLayout.getWidth() + 2 * 21 + 2) {
			playGroundPanel.setWidth(game.getPlaygroundWidth() + 2, Unit.PIXELS);
		} else {
			playGroundPanel.setWidth(100, Unit.PERCENTAGE);
		}
		if (ui.getPage().getBrowserWindowHeight() > game.getPlaygroundHeight() + 2 * 21 + 2) {
			playGroundPanel.setHeight(game.getPlaygroundHeight() + 2, Unit.PIXELS);
		} else {
			playGroundPanel.setHeight(100, Unit.PERCENTAGE);
		}
		setExpandRatio(sideLayout, 0);
		setExpandRatio(playGroundPanel, 1);
		sideLayout.addStyleName(Styles.FadeIn);
		playGroundPanel.addStyleName(Styles.FadeIn);
		playGroundPanel.addStyleName(Styles.PlaygroundPanel);

		game.join(ui.getCurrentUser(), this);
		List<Submarine> submarines = game.getSubmarines(ui.getCurrentUser());

		for (Submarine submarine : submarines) {
			playGround.addSubmarine(controlLayout, submarine);
		}
		turnControlLayout.updateCurrentTurn();
	}

	@Override
	public void receiveTurnEndBroadcast(User attacker) {
		turnControlLayout.updateCurrentTurn();
		controlLayout.reselect();
	}

	@Override
	public void receiveSubmarineHitBroadcast(Submarine attacked) {
		if (attacked.getCurrentHp() > 0) {
			playGround.animateHit(attacked, 500);
		} else {
			playGround.animateDeath(attacked, 500);
		}
		controlLayout.reselect();
	}

	@Override
	public void receiveGameFinishedBroadcast() {
		playGround.animateWinner(ui.getCurrentGame().getWinner());
	}
}
