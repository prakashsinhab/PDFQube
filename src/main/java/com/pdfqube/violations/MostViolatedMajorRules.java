package com.pdfqube.violations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.NodeList;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class MostViolatedMajorRules {
	public void getMostViolatedRules(Document document, String key) {
		getTopMajorViolatedRules(document, key, "major");
		getTopMajorViolatedRules(document, key, "minor");
	}

	private static void getTopMajorViolatedRules(Document document, String key,
			String violation) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(

				"http://localhost:9000/api/resources?resource="
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
			if (i != 0 && j != 0 && k != 0) {

				PdfPCell cell1 = new PdfPCell(new Paragraph("RULE"));
				PdfPCell cell2 = new PdfPCell(new Paragraph("COUNT"));
				PdfPCell cell3 = new PdfPCell(new Paragraph("PRIORITY"));

				PdfPCell cell;
				cell = new PdfPCell(new Phrase(
						"Top 6 most violated "+ violation +" rules", fontbold));
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
				PdfPCell cell = new PdfPCell(new Phrase(
						"No " + violation + " rules violated", fontbold));
				table.addCell(cell);
				document.add(table);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
