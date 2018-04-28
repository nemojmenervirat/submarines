package com.nemojmenervirat.submarines.ui.language;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.nemojmenervirat.submarines.logic.utils.DefaultLogger;

public class DefaultLocale {

	private static DefaultLocale instance;

	private List<Locale> availableLocales;
	private Locale selectedLocale;
	private Locale defaultLocale;
	private ResourceBundle resourceBundle;

	private DefaultLocale() {
		availableLocales = new LinkedList<>();
		defaultLocale = new Locale("sr", "RS");
		availableLocales.add(defaultLocale);
		availableLocales.add(new Locale("sr_latn", "RS"));
		availableLocales.add(new Locale("en", "US"));
	}

	private static DefaultLocale getInstance() {
		if (instance == null) {
			instance = new DefaultLocale();
		}
		return instance;
	}

	public static List<Locale> getAvailableLocales() {
		return getInstance().availableLocales;
	}

	public static void select(Locale value) {
		if (getAvailableLocales().contains(value)) {
			getInstance().selectedLocale = value;
		} else {
			getInstance().selectedLocale = getInstance().defaultLocale;
		}
		getInstance().resourceBundle = null;
		DefaultLogger.log("Selected locale is " + getInstance().selectedLocale);
	}

	public static void select(String value) {
		try {
			Locale locale = new Locale(value);
			select(locale);
		} catch (Exception ex) {
			select(getInstance().defaultLocale);
		}
	}

	public static Locale getSelectedLocale() {
		if (getInstance().selectedLocale == null) {
			getInstance().selectedLocale = getInstance().defaultLocale;
		}
		return getInstance().selectedLocale;
	}

	public static String getString(String key) {
		if (getInstance().resourceBundle == null) {
			getInstance().resourceBundle = ResourceBundle.getBundle("DefaultBundle", getSelectedLocale(),
					new UTF8Control());
		}
		return getInstance().resourceBundle.getString(key);
	}

	public static Locale Get() {
		return getInstance().defaultLocale;
	}
}
