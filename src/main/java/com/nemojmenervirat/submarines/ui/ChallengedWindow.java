package com.nemojmenervirat.submarines.ui;

import com.nemojmenervirat.submarines.logic.Game.GameType;
import com.nemojmenervirat.submarines.logic.User;
import com.nemojmenervirat.submarines.ui.utils.Styles;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class ChallengedWindow extends Window {

	public ChallengedWindow(User challenger, GameType gameType, Button.ClickListener onAccept, Button.ClickListener onCancel) {
		setCaption("Challenged by " + challenger);

		Label label = new Label((gameType == GameType.SHORT ? "Short game." : "Long game.") + "Do you accept challenge?");

		Button buttonAccept = new Button("Accept");
		buttonAccept.setStyleName(Styles.ButtonAccept);
		buttonAccept.addClickListener(onAccept);

		Button buttonCancel = new Button("I'm pussy");
		buttonCancel.setStyleName(Styles.ButtonCancel);
		buttonCancel.addClickListener(onCancel);

		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setSizeFull();
		layout.addComponent(label, "top:45px; left:20px;");
		layout.addComponent(buttonAccept, "top:110px; left:200px");
		layout.addComponent(buttonCancel, "top:110px; left:290px");
		setContent(layout);

		setWidth(400, Unit.PIXELS);
		setHeight(180, Unit.PIXELS);

		setModal(true);
		setClosable(false);
		setResizable(false);
		center();
	}

}
