package com.nemojmenervirat.submarines.logic.utils;

import com.vaadin.server.VaadinService;

public class DefaultLogger {

	private static boolean isProductionMode() {
		try {
			return VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode();
		} catch (Exception ex) {
			return false;
		}
	}

	public static void log(String message) {
		if (!isProductionMode()) {
			System.out.println(message);
		}
	}
}
