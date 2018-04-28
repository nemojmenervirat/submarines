package com.nemojmenervirat.submarines.ui;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;

import com.nemojmenervirat.submarines.logic.AccessControl;
import com.nemojmenervirat.submarines.logic.AccessControl.SignInStatus;
import com.nemojmenervirat.submarines.logic.User;
import com.nemojmenervirat.submarines.logic.UserCoordinator;
import com.nemojmenervirat.submarines.logic.utils.CookieUtils;
import com.nemojmenervirat.submarines.ui.language.DefaultLocale;
import com.nemojmenervirat.submarines.ui.language.Strings;
import com.nemojmenervirat.submarines.ui.utils.Styles;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class LoginScreen extends CssLayout implements View {

	private static final String LocaleCookie = "locale-cookie";
	private static final String UserNameCookie = "user-name-cookie";
	private static final String PasswordCookie = "password-cookie";

	private TextField usernameField;
	private PasswordField passwordField;
	private Button loginButton;
	private Button forgotPassword;
	private LoginListener loginListener;
	private AccessControl accessControl;
	private VerticalLayout centeringLayout;
	private VerticalLayout topLayout;
	private VerticalLayout bottomLayout;
	private boolean loaded;

	public LoginScreen(AccessControl accessControl, LoginListener loginListener) {
		this.loginListener = loginListener;
		this.accessControl = accessControl;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		if (!loaded) {
			buildUI();
			loaded = true;
		}
	}

	private void buildUI() {
		addStyleName(Styles.LoginScreen);

		topLayout = new VerticalLayout();
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		topLayout.setHeight(30, Unit.PIXELS);

		centeringLayout = new VerticalLayout();
		centeringLayout.setMargin(false);
		centeringLayout.setSpacing(false);

		bottomLayout = new VerticalLayout();
		bottomLayout.setMargin(false);
		bottomLayout.setSpacing(false);
		bottomLayout.setHeight(30, Unit.PIXELS);

		Cookie localeCookie = CookieUtils.getByName(LocaleCookie);
		if (localeCookie == null) {
			buildLocaleSelectionUI();
		} else {
			DefaultLocale.select(localeCookie.getValue());

			Cookie usernameCookie = CookieUtils.getByName(UserNameCookie);
			Cookie passwordCookie = CookieUtils.getByName(PasswordCookie);
			if (usernameCookie != null && passwordCookie != null) {
				boolean loggedIn = login(usernameCookie.getValue(), passwordCookie.getValue());
				if (!loggedIn) {
					buildLoginUI();
				}
			} else {
				buildLoginUI();
			}
		}

	}

	private void buildLocaleSelectionUI() {
		Component locale = buildLocaleSelectionForm();
		centeringLayout.setStyleName(Styles.LoginCenteringLayout);
		centeringLayout.addComponent(locale);
		centeringLayout.setComponentAlignment(locale, Alignment.MIDDLE_CENTER);
		addComponent(centeringLayout);
	}

	private Component buildLocaleSelectionForm() {
		FormLayout localeForm = new FormLayout();

		localeForm.setStyleName(Styles.LoginForm);
		localeForm.setSizeUndefined();
		localeForm.setMargin(false);

		ComboBox<Locale> locales = new ComboBox<>();
		locales.setItemCaptionGenerator((item) -> DefaultLocale.getString("captionLanguage_" + item.toString()));
		locales.setWidth(15, Unit.EM);
		locales.setItems(DefaultLocale.getAvailableLocales());
		locales.setSelectedItem(DefaultLocale.Get());
		locales.setEmptySelectionAllowed(false);

		CheckBox checkBoxRememberSelection = new CheckBox(DefaultLocale.getString(Strings.captionRememberCheckbox));

		Button buttonSelectLocale = new Button(DefaultLocale.getString(Strings.captionLocaleButton));
		buttonSelectLocale.addClickListener((e) -> {
			DefaultLocale.select(locales.getValue());
			if (checkBoxRememberSelection.getValue()) {
				CookieUtils.save(LocaleCookie, locales.getValue().toString());
			} else {
				CookieUtils.delete(LocaleCookie);
			}
			buildLoginUI();
			usernameField.focus();
		});
		buttonSelectLocale.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		buttonSelectLocale.addStyleName(ValoTheme.BUTTON_FRIENDLY);

		localeForm.addComponents(locales, checkBoxRememberSelection, buttonSelectLocale);
		locales.focus();

		return localeForm;
	}

	private void buildLoginUI() {
		centeringLayout.removeAllComponents();
		removeAllComponents();

		Component loginForm = buildLoginForm();
		centeringLayout.addComponent(loginForm);
		centeringLayout.setStyleName(Styles.LoginCenteringLayout);
		centeringLayout.addStyleName(Styles.LoginCenteringLayoutPadding);
		centeringLayout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

		Button returnToLocaleSelection = new Button();
		returnToLocaleSelection.addClickListener((e) -> {
			centeringLayout.removeAllComponents();
			topLayout.removeAllComponents();
			removeAllComponents();
			addComponent(topLayout);
			addComponent(centeringLayout);
			addComponent(bottomLayout);
			buildLocaleSelectionUI();
		});
		returnToLocaleSelection.addStyleName(ValoTheme.BUTTON_LINK);
		returnToLocaleSelection.setIcon(new ThemeResource("img/back.png"));
		returnToLocaleSelection.setCaption(DefaultLocale.getString(Strings.captionReturnToLocaleSelection));
		topLayout.addComponent(returnToLocaleSelection);
		topLayout.setComponentAlignment(returnToLocaleSelection, Alignment.MIDDLE_RIGHT);
		addComponent(topLayout);
		addComponent(centeringLayout);
		addComponent(bottomLayout);

		Component loginInfo = buildLoginInformation();
		addComponent(loginInfo);
	}

	private Component buildLoginForm() {
		FormLayout loginForm = new FormLayout();

		loginForm.addStyleName(Styles.LoginForm);
		loginForm.setSizeUndefined();
		loginForm.setMargin(false);

		loginForm.addComponent(usernameField = new TextField(DefaultLocale.getString(Strings.captionUsername)));
		usernameField.setWidth(15, Unit.EM);
		loginForm.addComponent(passwordField = new PasswordField(DefaultLocale.getString(Strings.captionPassword)));
		passwordField.setWidth(15, Unit.EM);

		CheckBox checkBoxRememberSelection = new CheckBox(DefaultLocale.getString(Strings.captionRememberCheckbox));
		loginForm.addComponent(checkBoxRememberSelection);

		CssLayout buttons = new CssLayout();
		buttons.setStyleName(Styles.LoginButtons);
		loginForm.addComponent(buttons);

		buttons.addComponent(loginButton = new Button(DefaultLocale.getString(Strings.captionLogin)));
		loginButton.setDisableOnClick(true);
		loginButton.addClickListener((e) -> {
			try {
				boolean loggedIn = login(usernameField.getValue(), passwordField.getValue());
				if (loggedIn && checkBoxRememberSelection.getValue()) {
					CookieUtils.save(UserNameCookie, usernameField.getValue());
					CookieUtils.save(PasswordCookie, passwordField.getValue());
				} else {
					CookieUtils.delete(UserNameCookie);
					CookieUtils.delete(PasswordCookie);
				}
			} finally {
				loginButton.setEnabled(true);
			}
		});
		loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		loginButton.addStyleName(Styles.ButtonAccept);

		buttons.addComponent(forgotPassword = new Button(DefaultLocale.getString(Strings.captionForgotPassword)));
		forgotPassword.addClickListener((e) -> showNotification(new Notification(DefaultLocale.getString(Strings.textContactAdministrator))));
		forgotPassword.addStyleName(Styles.ButtonLink);
		return loginForm;
	}

	private CssLayout buildLoginInformation() {
		CssLayout loginInformation = new CssLayout();
		loginInformation.setStyleName(Styles.LoginInformation);
		Label loginInfoText = new Label(DefaultLocale.getString(Strings.textLoginInfo), ContentMode.HTML);
		loginInfoText.setSizeFull();
		loginInformation.addComponent(loginInfoText);
		return loginInformation;
	}

	private boolean login(String username, String password) {
		SignInStatus signInStatus = accessControl.signIn(username, password);
		switch (signInStatus) {
		case OK: {
			User user = new User();
			user.setUsername(username);
			user.setLoginDate(new Date());
			user.setAddress(Page.getCurrent().getWebBrowser().getAddress());
			UserCoordinator.setCurrent(user);
			UserCoordinator.register(user);
			loginListener.loginSuccessful();
			return true;
		}
		case INVALID_USERNAME: {
			showNotification(new Notification(DefaultLocale.getString(Strings.captionLoginFailed),
					DefaultLocale.getString(Strings.textLoginFailed), Notification.Type.HUMANIZED_MESSAGE));
			if (usernameField != null) {
				usernameField.focus();
			}
			return false;
		}
		case ALREADY_LOGGED_IN: {
			showNotification(new Notification(DefaultLocale.getString(Strings.captionLoginFailed), "Vec postoji", Notification.Type.HUMANIZED_MESSAGE));
			if (usernameField != null) {
				usernameField.focus();
			}
			return false;
		}
		default: {
			// default needs to be implemented because of return value
			return false;
		}
		}
	}

	private void showNotification(Notification notification) {
		notification.setDelayMsec(2000);
		notification.show(Page.getCurrent());
	}

	public interface LoginListener extends Serializable {
		void loginSuccessful();
	}
}
