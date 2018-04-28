package com.nemojmenervirat.submarines.ui.language;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class StringsCreator {

	public static void main(String[] args) {
		ResourceBundle rb = ResourceBundle.getBundle("DefaultBundle", DefaultLocale.Get(), new UTF8Control());
		Enumeration<String> keys = rb.getKeys();

		String outputDirectory = StringsCreator.class.getCanonicalName().replace(".", "/");
		outputDirectory = outputDirectory.substring(0, outputDirectory.lastIndexOf('/'));
		String packageName = outputDirectory.replace("/", ".");

		List<String> lines = new LinkedList<>();
		lines.add("package " + packageName + ";\n");
		lines.add("public class Strings {\n");
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			lines.add("\tpublic static final String " + key + " = \"" + key + "\";");
		}
		lines.add("\n}");

		Path file = Paths.get("src\\main\\java\\com\\nemojmenervirat\\submarines\\language\\Strings.java");
		try {
			Files.write(file.toAbsolutePath(), lines, Charset.forName("UTF-8"));
			System.out.println("Strings.java created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
