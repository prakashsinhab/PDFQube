package com.pdfqube.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
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

public class CycloComplexAction {
	public void getFunctionComplexity(Document document, String key) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(

				"http://localhost:9000/api/resources?resource="
						+ key
						+ "&metrics=function_complexity&scopes=FIL&depth=-1&limit=10&format=xml");
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
			PdfPTable table = new PdfPTable(2); // 2 columns.
			if (nodeList_rule_name.getLength() != 0
					&& nodeList_val.getLength() != 0) {

				PdfPCell cell1 = new PdfPCell(new Paragraph("Resource"));
				PdfPCell cell2 = new PdfPCell(new Paragraph("Complexity"));

				PdfPCell cell;
				cell = new PdfPCell(new Phrase("Top 10 most complex functions",
						fontbold));
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

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getClassComplexity(Document document, String key) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(

				"http://localhost:9000/api/resources?resource="
						+ key
						+ "&metrics=class_complexity&scopes=FIL&depth=-1&limit=10&format=xml");
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
			PdfPTable table = new PdfPTable(2); // 2 columns.
			if (nodeList_rule_name.getLength() != 0
					&& nodeList_val.getLength() != 0) {

				PdfPCell cell1 = new PdfPCell(new Paragraph("Resource"));
				PdfPCell cell2 = new PdfPCell(new Paragraph("Complexity"));

				PdfPCell cell;
				cell = new PdfPCell(new Phrase("Top 10 most complex classes",
						fontbold));
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

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getFileComplexity(Document document, String key) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(

				"http://localhost:9000/api/resources?resource="
						+ key
						+ "&metrics=file_complexity&scopes=FIL&depth=-1&limit=10&format=xml");
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
			PdfPTable table = new PdfPTable(2); // 2 columns.
			if (nodeList_rule_name.getLength() != 0
					&& nodeList_val.getLength() != 0) {

				PdfPCell cell1 = new PdfPCell(new Paragraph("Resource"));
				PdfPCell cell2 = new PdfPCell(new Paragraph("Complexity"));

				PdfPCell cell;
				cell = new PdfPCell(new Phrase("Top 10 most complex files",
						fontbold));
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

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
