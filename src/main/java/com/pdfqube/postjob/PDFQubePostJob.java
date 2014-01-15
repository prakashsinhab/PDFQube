package com.pdfqube.postjob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.jfree.chart.block.CenterArrangement;
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

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFQubePostJob implements PostJob {

	private static void addTitlePage(Document document)
			throws DocumentException {
		Font fontbold = FontFactory.getFont("Times-Roman", 36, Font.BOLD);

		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Phrase("\n"));
		document.add(new Paragraph("Sonar PDF Report", fontbold));
		document.add(new Paragraph("Created by PDF Qube Plugin"));

	}

	public void executeOn(Project project, SensorContext arg1) {
		// TODO Auto-generated method stub

		try {
			String fileName = "SonarReport.pdf";

			Font fontHeading = FontFactory
					.getFont("Times-Roman", 24, Font.BOLD);

			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}

			String url = "http://localhost:9000";
			String login = "admin";
			String password = "admin";
			Sonar sonar = new Sonar(new HttpClient4Connector(new Host(url)));

			Resource struts = sonar.find(ResourceQuery.createForMetrics(
					project.getKey(), "coverage", "lines", "violations",
					"duplicated_lines", "ncloc", "packages", "classes",
					"functions", "sqale_index", "files", "tests",
					"test_execution_time", "test_errors", "test_failures",
					"test_success_density"));

			OutputStream report = new FileOutputStream(new File(fileName));

			Document document = new Document();
			PdfWriter.getInstance(document, report);

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
			String techDebt = struts.getMeasure("sqale_index")
					.getFormattedValue().toString();
			String noofFiles = struts.getMeasure("files").getValue().toString();

			document.open();
			/*			*/
			addTitlePage(document);

			// Page 1

			document.newPage();
			document.add(new Phrase("\n"));
			document.add(new Phrase("\n"));
			document.add(new Paragraph("Overview details", fontHeading));
			document.add(new Phrase("\n"));

			document.add(new Paragraph("Total Number of Lines:" + lines));
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
			document.add(new Paragraph("Technical Debt: " + techDebt));
			document.add(new Paragraph("Total Number of Files: " + noofFiles));

			document.newPage();
			document.add(new Paragraph("Violations", fontHeading));
			document.add(new Phrase("\n"));

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
			report.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
