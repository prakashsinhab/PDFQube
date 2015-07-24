package com.pdfqube.postjob;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
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
import org.w3c.dom.NodeList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdfqube.action.CycloComplexAction;
import com.pdfqube.constants.Constant;

public class PDFQubePostJob implements PostJob {

	private static void addTitlePage(Document document)
			throws DocumentException {
		Font fontbold = FontFactory.getFont("Times-Roman", 36, Font.BOLD);

		for (int i = 0; i < 12; i++) {
			document.add(new Phrase("\n"));
		}
		document.add(new Paragraph(Constant.HEADING, fontbold));
		document.add(new Paragraph(Constant.AUTHOR));

	}

	private static void addStanza(Document document) {
		try {
			for (int i = 0; i < 5; i++) {
				document.add(new Phrase("\n"));
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeOn(Project project, SensorContext arg1) {
		// TODO Auto-generated method stub

		try {

			Font fontHeading = FontFactory
					.getFont("Times-Roman", 24, Font.BOLD);

			File file = new File(Constant.FILENAME);
			if (file.exists()) {
				file.delete();
			}

			Sonar sonar = new Sonar(new HttpClient4Connector(new Host(
					Constant.URL)));

			Resource struts = sonar.find(ResourceQuery.createForMetrics(
					project.getKey(), "coverage", "lines", "violations",
					"duplicated_lines", "ncloc", "packages", "classes",
					"functions", "sqale_index", "files", "tests",
					"test_execution_time", "test_errors", "test_failures",
					"test_success_density"));

			OutputStream report = new FileOutputStream(new File(
					Constant.FILENAME));

			Document document = new Document();
			PdfWriter.getInstance(document, report);

			if(struts == null) return;
			
			String lines = struts.getMeasure("lines").getIntValue().toString();
			String codelines = struts.getMeasure("ncloc").getValue().toString();
			String noofpackages = "";

			/*String noofpackages = struts.getMeasure("packages").getValue()
					.toString();*/
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

			addStanza(document);
			
			document.add(new Paragraph("Overview details", fontHeading));
			document.add(new Phrase("\n"));

			document.add(new Paragraph("Total Lines:" + lines));
			document.add(new Paragraph("Lines of Code: " + codelines));
			document.add(new Paragraph("Packages: " + noofpackages));
			document.add(new Paragraph("Classes: " + noofclasses));
			document.add(new Paragraph("Funtions: " + nooffunctions));
			document.add(new Paragraph("Issues: " + noofviolations));
			document.add(new Paragraph("Technical Debt: " + techDebt));
			document.add(new Paragraph("Files: " + noofFiles));

			/*
			 * document.newPage(); document.add(new Paragraph("Violations",
			 * fontHeading)); document.add(new Phrase("\n"));
			 * 
			 * ViolationQuery violationQuery = ViolationQuery
			 * .createForResource(project.getKey());
			 * violationQuery.setDepth(-1);
			 * 
			 * violationQuery.setSeverities("BLOCKER", "CRITICAL", "MAJOR",
			 * "MINOR"); List<Violation> violations =
			 * sonar.findAll(violationQuery);
			 * 
			 * for (Violation violation : violations) { document.add(new
			 * Paragraph(violation.getPriority() + " | " +
			 * violation.getResourceKey().toString()));
			 * 
			 * }
			 */

			document.newPage();
			this.getTopViolatedRules(document, project.getKey(), "critical");
			document.newPage();
			this.getTopViolatedRules(document, project.getKey(), "blocker");
			document.newPage();
			this.getTopViolatedRules(document, project.getKey(), "major");
			document.newPage();
			this.getTopViolatedRules(document, project.getKey(), "minor");
			document.newPage();
			this.getTopViolatedRules(document, project.getKey(), "info");
			document.newPage();
			this.getMostViolatedResources(document, project.getKey());
			document.newPage();

			// Cyclomatic complexity
			CycloComplexAction cc = new CycloComplexAction();
			cc.getFunctionComplexity(document, project.getKey());
			document.newPage();
			cc.getClassComplexity(document, project.getKey());
			document.newPage();
			cc.getFileComplexity(document, project.getKey());

			document.close();
			report.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void getTopViolatedRules(Document document, String key,
			String violation) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(

				"http://localhost:9000/sonar/api/resources?resource="
						+ key
						+ "&depth=0&metrics="
						+ violation
						+ "_violations&filter_rules=false&filter_rules_cats=true&format=xml&limit=7");

		try {

			Font fontbold = FontFactory.getFont("Times-Roman", 18, Font.BOLD);

			client.executeMethod(method);
			String result = method.getResponseBodyAsString();

			InputStream stream = new ByteArrayInputStream(
					result.getBytes("UTF-8"));

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			org.w3c.dom.Document xmlDocument = builder.parse(stream);

			XPath xPath = XPathFactory.newInstance().newXPath();

			String expression_rule_name = "/resources/resource/msr/rule_name";
			NodeList nodeList_rule_name = (NodeList) xPath.compile(
					expression_rule_name).evaluate(xmlDocument,
					XPathConstants.NODESET);

			String expression_val = "/resources/resource/msr/frmt_val";
			NodeList nodeList_val = (NodeList) xPath.compile(expression_val)
					.evaluate(xmlDocument, XPathConstants.NODESET);

			String expression_rule_priority = "/resources/resource/msr/rule_priority";
			NodeList nodeList_rule_priority = (NodeList) xPath.compile(
					expression_rule_priority).evaluate(xmlDocument,
					XPathConstants.NODESET);

			// PDF Stuff starts here

			int i = 0, j = 0, k = 0;
			PdfPTable table = new PdfPTable(3); // 3 columns.
			if (nodeList_rule_name.getLength() != 0
					&& nodeList_val.getLength() != 0
					&& nodeList_rule_priority.getLength() != 0) {

				PdfPCell cell1 = new PdfPCell(new Paragraph("RULE"));
				PdfPCell cell2 = new PdfPCell(new Paragraph("COUNT"));
				PdfPCell cell3 = new PdfPCell(new Paragraph("PRIORITY"));

				PdfPCell cell;
				cell = new PdfPCell(new Phrase("Top 7 most violated "
						+ violation + " rules", fontbold));
				cell.setColspan(3); // an entire row

				table.addCell(cell);
				table.addCell(cell1);
				table.addCell(cell2);
				table.addCell(cell3);

				while (i < nodeList_rule_name.getLength()
						&& j < nodeList_val.getLength()
						&& k < nodeList_rule_priority.getLength()) {

					table.addCell(nodeList_rule_name.item(i).getFirstChild()
							.getNodeValue());
					table.addCell(nodeList_val.item(j).getFirstChild()
							.getNodeValue());
					table.addCell(nodeList_rule_priority.item(j)
							.getFirstChild().getNodeValue());

					++i;
					++j;
					++k;

				}
				document.add(table);

			}

			else {
				PdfPCell cell = new PdfPCell(new Phrase("No " + violation
						+ " rules violated", fontbold));
				table.addCell(cell);
				document.add(table);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getMostViolatedResources(Document document, String key) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(

				"http://localhost:9000/sonar/api/resources?resource="
						+ key
						+ "&metrics=weighted_violations&scopes=FIL&depth=-1&limit=10&format=xml");
		Font fontbold = FontFactory.getFont("Times-Roman", 18, Font.BOLD);
		try {
			client.executeMethod(method);
			String result = method.getResponseBodyAsString();
			InputStream stream = new ByteArrayInputStream(
					result.getBytes("UTF-8"));

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			org.w3c.dom.Document xmlDocument = builder.parse(stream);
			XPath xPath = XPathFactory.newInstance().newXPath();

			String expression_rule_name = "/resources/resource/lname";
			NodeList nodeList_rule_name = (NodeList) xPath.compile(
					expression_rule_name).evaluate(xmlDocument,
					XPathConstants.NODESET);

			String expression_val = "/resources/resource/msr/frmt_val";
			NodeList nodeList_val = (NodeList) xPath.compile(expression_val)
					.evaluate(xmlDocument, XPathConstants.NODESET);

			int i = 0, j = 0;
			PdfPTable table = new PdfPTable(2); // 3 columns.
			if (nodeList_rule_name.getLength() != 0
					&& nodeList_val.getLength() != 0) {

				PdfPCell cell1 = new PdfPCell(new Paragraph("Resource"));
				PdfPCell cell2 = new PdfPCell(new Paragraph("Count"));

				PdfPCell cell;
				cell = new PdfPCell(new Phrase(
						"Top 10 most violated resources", fontbold));
				cell.setColspan(2); // an entire row

				table.addCell(cell);
				table.addCell(cell1);
				table.addCell(cell2);

				while (i < nodeList_rule_name.getLength()
						&& j < nodeList_val.getLength()) {

					table.addCell(nodeList_rule_name.item(i).getFirstChild()
							.getNodeValue());
					table.addCell(nodeList_val.item(j).getFirstChild()
							.getNodeValue());

					++i;
					++j;

				}
				document.add(table);

			}

			else {
				PdfPCell cell = new PdfPCell(new Phrase(
						"No resources violated", fontbold));
				table.addCell(cell);
				document.add(table);
			}

		} catch (Exception e) {

		}
	}

	/*
	 * public void getAllViolatedRules(Document document, String key) {
	 * HttpClient client = new HttpClient(); HttpMethod method = new GetMethod(
	 * 
	 * "http://localhost:9000//api/resources?resource="+ key +
	 * "&depth=0&metrics=major_violations,minor_violations,critical_violations,blocker_violations,info_violations&filter_rules=false&filter_rules_cats=true&format=xml&limit=10&rule_priorities=INFO,MINOR,MAJOR,CRITICAL,BLOCKER"
	 * );
	 * 
	 * try {
	 * 
	 * Font fontbold = FontFactory.getFont("Times-Roman", 18, Font.BOLD);
	 * 
	 * client.executeMethod(method); String result =
	 * method.getResponseBodyAsString();
	 * 
	 * InputStream stream = new ByteArrayInputStream( result.getBytes("UTF-8"));
	 * 
	 * DocumentBuilderFactory builderFactory = DocumentBuilderFactory
	 * .newInstance();
	 * 
	 * DocumentBuilder builder = builderFactory.newDocumentBuilder();
	 * 
	 * org.w3c.dom.Document xmlDocument = builder.parse(stream);
	 * 
	 * XPath xPath = XPathFactory.newInstance().newXPath();
	 * 
	 * String expression_rule_name = "/resources/resource/msr/rule_priority";
	 * NodeList nodeList_rule_name = (NodeList) xPath.compile(
	 * expression_rule_name).evaluate(xmlDocument, XPathConstants.NODESET);
	 * 
	 * String expression_val = "/resources/resource/msr/rule_name"; NodeList
	 * nodeList_val = (NodeList) xPath.compile(expression_val)
	 * .evaluate(xmlDocument, XPathConstants.NODESET);
	 * 
	 * String expression_rule_priority = "/resources/resource/msr/frmt_val";
	 * NodeList nodeList_rule_priority = (NodeList) xPath.compile(
	 * expression_rule_priority).evaluate(xmlDocument, XPathConstants.NODESET);
	 * 
	 * // PDF Stuff starts here
	 * 
	 * int i = 0, j = 0, k = 0; PdfPTable table = new PdfPTable(3); // 3
	 * columns. if (nodeList_rule_name.getLength() != 0 &&
	 * nodeList_val.getLength() != 0 && nodeList_rule_priority.getLength() != 0)
	 * {
	 * 
	 * PdfPCell cell1 = new PdfPCell(new Paragraph("Priority")); PdfPCell cell2
	 * = new PdfPCell(new Paragraph("Rule")); PdfPCell cell3 = new PdfPCell(new
	 * Paragraph("Count"));
	 * 
	 * PdfPCell cell; cell = new PdfPCell(new
	 * Phrase("Top 10 Most Violated Rules", fontbold)); cell.setColspan(3); //
	 * an entire row
	 * 
	 * table.addCell(cell); table.addCell(cell1); table.addCell(cell2);
	 * table.addCell(cell3);
	 * 
	 * while (i < nodeList_rule_name.getLength() && j < nodeList_val.getLength()
	 * && k < nodeList_rule_priority.getLength()) {
	 * 
	 * table.addCell(nodeList_rule_name.item(i).getFirstChild()
	 * .getNodeValue()); table.addCell(nodeList_val.item(j).getFirstChild()
	 * .getNodeValue()); table.addCell(nodeList_rule_priority.item(j)
	 * .getFirstChild().getNodeValue());
	 * 
	 * ++i; ++j; ++k;
	 * 
	 * } document.add(table);
	 * 
	 * }
	 * 
	 * else { PdfPCell cell = new PdfPCell(new Phrase("No rules violated",
	 * fontbold)); table.addCell(cell); document.add(table); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
	public String Http_Access(String url) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);

		client.executeMethod(method);
		String response = method.getResponseBodyAsString();
		if (response != null) {
			return response;
		} else {
			return "NULL";
		}

	}
}
