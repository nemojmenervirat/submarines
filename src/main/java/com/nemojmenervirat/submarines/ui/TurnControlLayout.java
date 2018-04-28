package com.nemojmenervirat.submarines.ui;

import com.nemojmenervirat.submarines.logic.Submarine;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class TurnControlLayout extends Panel {
	private SubmarinesUI ui;

	private Label labelTurn;
	private Label labelUser;

	private static final String CUR_TUR = "Current turn: ";
	private static final String CUR_USER = "Current turn user: ";

	public TurnControlLayout(ControlLayout controlLayout, PlayGround playGround, SubmarinesUI ui) {
		this.ui = ui;
		labelTurn = new Label(CUR_TUR);
		labelUser = new Label(CUR_USER);

		Button turnFinish = new Button("Enough for this turn");
		turnFinish.addClickListener((e) -> {
			ui.getCurrentGame().goNextMove();
			updateCurrentTurn();
			controlLayout.reselect();
		});

		Button surrender = new Button("Surrender");
		surrender.addClickListener((e) -> {
			int i = 1;
			for (Submarine submarine : ui.getCurrentGame().getSubmarines(ui.getCurrentUser())) {
				playGround.animateDeath(submarine, 500 + 100 * i);
			}
			ui.getCurrentGame().getSubmarines(ui.getCurrentUser()).clear();
			ui.getCurrentGame().checkStatus();
		});

		VerticalLayout layout = new VerticalLayout(labelTurn, labelUser, turnFinish, surrender);
		setContent(layout);
		setWidth("300px");

		setEnabled(false);
	}

	public void updateCurrentTurn() {
		labelTurn.setValue(CUR_TUR + ui.getCurrentGame().getCurrentTurn());
		labelUser.setValue(CUR_USER + ui.getCurrentGame().getCurrentTurnUser());
		setEnabled(ui.getCurrentGame().getCurrentTurnUser().equals(ui.getCurrentUser()));
	}
}
