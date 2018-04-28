package com.nemojmenervirat.submarines.ui;

import com.nemojmenervirat.submarines.logic.Game.GameType;
import com.nemojmenervirat.submarines.logic.User;
import com.nemojmenervirat.submarines.ui.utils.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class ChallengerWindow extends Window {

	public ChallengerWindow(User challenged, GameType gameType, Button.ClickListener onCancel) {
		setCaption("Challenging " + challenged);

		Image image = new Image("", new ThemeResource("img/loading.svg"));
		image.setWidth(70, Unit.PIXELS);
		image.setHeight(70, Unit.PIXELS);

		Label labelWait = new Label((gameType == GameType.SHORT ? "Short game." : "Long game.") + "Waiting for response...");

		Button button = new Button("Cancel");
		button.setStyleName(Styles.ButtonCancel);
		button.addClickListener(onCancel);

		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setSizeFull();
		layout.addComponent(image, "top:20px; left:20px;");
		layout.addComponent(labelWait, "top:45px; left:120px");
		layout.addComponent(button, "top:110px; left:310px;");
		setContent(layout);

		setWidth(400, Unit.PIXELS);
		setHeight(180, Unit.PIXELS);

		setModal(true);
		setClosable(false);
		setResizable(false);
		center();
	}
}
