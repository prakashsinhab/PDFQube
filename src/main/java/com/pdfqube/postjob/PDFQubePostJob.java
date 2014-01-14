package com.pdfqube.postjob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import org.sonar.wsclient.services.Violation;
import org.sonar.wsclient.services.ViolationQuery;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFQubePostJob implements PostJob {

	public void executeOn(Project project, SensorContext arg1) {
		// TODO Auto-generated method stub

		try {
			String url = "http://localhost:9000";
			String login = "admin";
			String password = "admin";
			Sonar sonar = new Sonar(new HttpClient4Connector(new Host(url)));

			Resource struts = sonar.find(ResourceQuery.createForMetrics(
					project.getKey(), "coverage", "lines", "violations",
					"duplicated_lines", "ncloc", "packages", "classes",
					"functions"));

			String fileName = "C:\\Temp\\" + project.getName() + ".pdf";
			OutputStream file = new FileOutputStream(new File(fileName));

			Document document = new Document();
			PdfWriter.getInstance(document, file);

			String lines = struts.getMeasure("lines").getIntValue().toString();
			String codelines = struts.getMeasure("ncloc").getValue().toString();
			String noofpackages = struts.getMeasure("packages").getValue()
					.toString();
			String noofclasses = struts.getMeasure("classes").getValue()
					.toString();
			String nooffunctions = struts.getMeasure("functions").getValue()
					.toString();
			String noofviolations = struts.getMeasure("violations").getValue()
					.toString();

			document.open();
			document.add(new Paragraph("Sonar PDF Report using PDF Qube Plugin"));
			document.add(new Paragraph(new Date().toString()));

			// Page 1

			document.newPage();
			document.add(new Paragraph("Total Number of Lines of Code:" + lines));
			document.add(new Paragraph("Total Number of Lines of Code: "
					+ codelines));
			document.add(new Paragraph("Total Number of Packages: "
					+ noofpackages));
			document.add(new Paragraph("Total Number of Classes: "
					+ noofclasses));
			document.add(new Paragraph("Total Number of Funtions: "
					+ nooffunctions));
			document.add(new Paragraph("Total Number of Violations: "
					+ noofviolations));

			document.newPage();
			document.add(new Paragraph("Violations: "));

			ViolationQuery violationQuery = ViolationQuery
					.createForResource(project.getKey());
			violationQuery.setDepth(-1);

			violationQuery.setSeverities("BLOCKER", "CRITICAL", "MAJOR",
					"MINOR");
			List<Violation> violations = sonar.findAll(violationQuery);

			for (Violation violation : violations) {
				document.add(new Paragraph(violation.getPriority() + " | "
						+ violation.getResourceKey().toString()));

			}

			document.close();
			file.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
