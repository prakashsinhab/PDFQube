package com.pdfqube.plugin;

import com.pdfqube.postjob.PDFQubePostJob;
import com.pdfqube.ui.PDFQubeRubyWidget;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * This class is the entry point for all extensions
 */
@Properties({ @Property(key = PDFQubePlugin.MY_PROPERTY, name = "Plugin Property", description = "A property for the plugin", defaultValue = "Hello World!") })
public final class PDFQubePlugin extends SonarPlugin {

	public static final String MY_PROPERTY = "sonar.example.myproperty";

	// This is where you're going to declare all your Sonar extensions
	public List getExtensions() {
		return Arrays.asList(
		// This is where the magic happens
				PDFQubePostJob.class, PDFQubeRubyWidget.class

		);
	}
}
